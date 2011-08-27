package com.github.meinders.common.opengl.example;

import java.util.*;
import javax.media.opengl.*;
import javax.media.opengl.fixedfunc.*;
import javax.media.opengl.glu.*;

import com.github.meinders.common.math.*;
import com.github.meinders.common.opengl.*;

public class ShadowMapExample implements GLEventListener
{
	private static final double[] PROJECTION_TO_TEXTURE = {
	        0.5, 0.0, 0.0, 0.0,//
	        0.0, 0.5, 0.0, 0.0,//
	        0.0, 0.0, 0.5, 0.0,//
	        0.5, 0.5, 0.5, 1.0
	};

	private Vector3f camera;

	private Vector3f light1Position = new Vector3f(5.0f, 0.0f, 5.0f);

	private Vector3f light1Target = new Vector3f(0.0f, 0.0f, 0.0f);

	private int framebuffer;

	private int colorTexture;

	private int depthTexture;

	int shadowSize = 512;

	ShaderProgram shadowProgram;

	public ShadowMapExample()
	{
		camera = new Vector3f(0.0, -30.0, 15.0);
	}

	@Override
	public void init(GLAutoDrawable drawable)
	{
		GL gl = drawable.getGL();

		gl.glEnable(GL.GL_CULL_FACE);
		gl.glCullFace(GL.GL_BACK);

		int[] textures = new int[2];
		gl.glGenTextures(textures.length, textures, 0);
		colorTexture = textures[0];
		depthTexture = textures[1];

		// Create color texture
		gl.glBindTexture(GL.GL_TEXTURE_2D, colorTexture);
		gl.glTexParameterf(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MIN_FILTER,
		        GL.GL_LINEAR);
		gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MAG_FILTER,
		        GL.GL_LINEAR);
		gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_WRAP_S,
		        GL.GL_CLAMP_TO_EDGE);
		gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_WRAP_T,
		        GL.GL_CLAMP_TO_EDGE);
		gl.glTexImage2D(GL.GL_TEXTURE_2D, 0, GL.GL_RGBA, shadowSize,
		        shadowSize, 0, GL.GL_RGBA, GL.GL_UNSIGNED_INT, null);

		// Create depth texture
		gl.glBindTexture(GL.GL_TEXTURE_2D, depthTexture);
		gl.glTexParameterf(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MIN_FILTER,
		        GL.GL_LINEAR);
		gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MAG_FILTER,
		        GL.GL_LINEAR);
		gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_WRAP_S,
		        GL.GL_CLAMP_TO_EDGE);
		gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_WRAP_T,
		        GL.GL_CLAMP_TO_EDGE);
		gl.glTexParameteri(GL.GL_TEXTURE_2D, GL2ES2.GL_TEXTURE_COMPARE_MODE, GL2.GL_COMPARE_R_TO_TEXTURE);
		gl.glTexImage2D(GL.GL_TEXTURE_2D, 0, GL.GL_DEPTH_COMPONENT24, shadowSize, shadowSize, 0, GL2ES2.GL_DEPTH_COMPONENT,GL.GL_UNSIGNED_INT, null);

		gl.glBindTexture(GL.GL_TEXTURE_2D, 0);

		// Create and bind framebuffer
		int[] framebuffers = new int[1];
		gl.glGenFramebuffers( framebuffers.length, framebuffers, 0 );
		framebuffer = framebuffers[0];
		gl.glBindFramebuffer( GL.GL_FRAMEBUFFER, framebuffer );

		// Attach color texture.
		gl.glFramebufferTexture2D( GL.GL_FRAMEBUFFER, GL.GL_COLOR_ATTACHMENT0, GL.GL_TEXTURE_2D, colorTexture, 0 );

		// Attach depth texture.
		gl.glFramebufferTexture2D( GL.GL_FRAMEBUFFER, GL.GL_DEPTH_ATTACHMENT, GL.GL_TEXTURE_2D, depthTexture, 0 );

		// // Disable color attachments.
		// gl.glDrawBuffer(GL.GL_NONE);
		// gl.glReadBuffer(GL.GL_NONE);

		checkFramebufferStatus(gl);
		gl.glBindFramebuffer( GL.GL_FRAMEBUFFER, 0 );

		shadowProgram = new ShaderProgram(gl);
		shadowProgram.attach(GL2ES2.GL_VERTEX_SHADER, "vertex.glsl", getClass().getResourceAsStream("vertex.glsl"));
		shadowProgram.attach(GL2ES2.GL_FRAGMENT_SHADER, "fragment.glsl", getClass().getResourceAsStream("fragment.glsl"));
	}

	@Override
	public void display(GLAutoDrawable drawable)
	{
		GL gl = drawable.getGL();
		GL2 gl2 = gl.getGL2();
		GLU glu = new GLU();

		gl.glEnable(GL.GL_DEPTH_TEST);

		// Render to shadow map
		gl2.glMatrixMode( GLMatrixFunc.GL_MODELVIEW );
		gl2.glLoadIdentity();

		gl.glViewport(0, 0, shadowSize, shadowSize);
		GLUtilities.perspectiveProjection(gl, 90, shadowSize, shadowSize);
		gl.glBindFramebuffer( GL.GL_FRAMEBUFFER, framebuffer );

		gl.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
		gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);

		glu.gluLookAt(light1Position.x, light1Position.y, light1Position.z,
		        light1Target.x, light1Target.y, light1Target.z, 0.0, 0.0, 1.0);

		setTextureMatrix(gl);
		setupLights(gl);
		renderObjects(gl, true);

		// Render to window
		gl.glViewport(0, 0, drawable.getWidth(), drawable.getHeight());
		gl.glBindFramebuffer( GL.GL_FRAMEBUFFER, 0 );

		// - Render scene from camera
		gl.glEnable(GLLightingFunc.GL_LIGHTING);
		gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);

		GLUtilities.perspectiveProjection(gl, 45, drawable.getWidth(),
		        drawable.getHeight());
		gl2.glLoadIdentity();
		glu.gluLookAt(camera.x, camera.y, camera.z, 0.0, 0.0, 0.0, 0.0, 0.0,
		        1.0);

		updateTextureMatrix(gl);

		// glu.gluLookAt(light1Position.x, light1Position.y, light1Position.z,
		// light1Target.x, light1Target.y, light1Target.z, 0.0, 0.0, 1.0);
		// camera = camera.rotate(Vector3f.Z_AXIS, (float) Math.toRadians(0.5));
		light1Position = light1Position.rotate(Vector3f.Z_AXIS,
		        (float) Math.toRadians(5.0));

		setupLights(gl);

		gl.glActiveTexture(GL.GL_TEXTURE6);
		gl.glBindTexture(GL.GL_TEXTURE_2D, colorTexture);
		gl.glActiveTexture(GL.GL_TEXTURE7);
		gl.glBindTexture(GL.GL_TEXTURE_2D, depthTexture);
		gl.glActiveTexture(GL.GL_TEXTURE0);

		startShaders(gl, GL.GL_TEXTURE7);
		renderObjects(gl, false);
		stopShaders(gl);

		// - Render shadow map to screen
		gl.glDisable( GLLightingFunc.GL_LIGHTING);
		gl.glClear(GL.GL_DEPTH_BUFFER_BIT);
		renderToScreen(gl, colorTexture, -1.0f, -1.0f, -0.5f, -0.5f);
		renderToScreen(gl, depthTexture, 0.5f, -1.0f, 1.0f, -0.5f);
	}

	private void startShaders(GL gl, int texture)
	{
		shadowProgram.enable();
		shadowProgram.setUniform("shadowMap", texture - GL.GL_TEXTURE0);
	}

	private void stopShaders(GL gl)
	{
		ShaderProgram.disable(gl);
	}

	void setTextureMatrix(GL gl)
	{
		GL2 gl2 = gl.getGL2();

		double[] modelView = new double[16];
		double[] projection = new double[16];

		// Moving from unit cube [-1,1] to [0,1]
		double[] bias = PROJECTION_TO_TEXTURE;

		// Grab modelview and transformation matrices
		gl2.glGetDoublev( GLMatrixFunc.GL_MODELVIEW_MATRIX, modelView, 0 );
		gl2.glGetDoublev( GLMatrixFunc.GL_PROJECTION_MATRIX, projection, 0 );

		gl2.glMatrixMode( GL.GL_TEXTURE );
		gl.glActiveTexture(GL.GL_TEXTURE7);

		// concatating all matrices into one.
		gl2.glLoadIdentity();
		gl2.glMultMatrixd( bias, 0 );
		gl2.glMultMatrixd( projection, 0 );
		gl2.glMultMatrixd( modelView, 0 );

		// Go back to normal matrix mode
		gl.glActiveTexture(GL.GL_TEXTURE0);
		gl2.glMatrixMode( GLMatrixFunc.GL_MODELVIEW );
	}

	private void updateTextureMatrix(GL gl)
	{
		GL2 gl2 = gl.getGL2();

		// Determine model view inverse matrix.
		double[] modelViewInverse = new double[16];
		gl2.glGetDoublev( GLMatrixFunc.GL_MODELVIEW_MATRIX, modelViewInverse, 0 );
		Matrix3d modelViewMatrix = new Matrix3d(modelViewInverse);
		modelViewMatrix.inverse();
		modelViewInverse = modelViewMatrix.toArray();

		// Apply model view inverse to shadow matrix.
		gl2.glMatrixMode(GL.GL_TEXTURE);
		gl.glActiveTexture(GL.GL_TEXTURE7);
		gl2.glMultMatrixd(modelViewInverse, 0);
		gl.glActiveTexture(GL.GL_TEXTURE0);
		gl2.glMatrixMode(GLMatrixFunc.GL_MODELVIEW);
	}

	private void renderToScreen(GL gl, int texture, float x1, float y1,
	        float x2, float y2)
	{
		GL2 gl2 = gl.getGL2();

		gl2.glMatrixMode(GLMatrixFunc.GL_PROJECTION);
		gl2.glPushMatrix();
		gl2.glLoadIdentity();
		gl2.glMatrixMode(GLMatrixFunc.GL_MODELVIEW);
		gl2.glLoadIdentity();

		gl.glBindTexture(GL.GL_TEXTURE_2D, texture);
		gl.glEnable(GL.GL_TEXTURE_2D);

		gl2.glColor3f(1, 1, 1);
		plane(gl, x1, y1, x2, y2);

		gl.glDisable(GL.GL_TEXTURE_2D);

		gl2.glMatrixMode(GLMatrixFunc.GL_PROJECTION);
		gl2.glPopMatrix();
		gl2.glMatrixMode(GLMatrixFunc.GL_MODELVIEW);
	}

	private void plane(GL gl, float x1, float y1, float x2, float y2)
	{
		GL2 gl2 = gl.getGL2();

		gl2.glBegin(GL2.GL_QUADS);
		gl2.glTexCoord2f(0.0f, 0.0f);
		gl2.glVertex2f(x1, y1);
		gl2.glTexCoord2f(1.0f, 0.0f);
		gl2.glVertex2f(x2, y1);
		gl2.glTexCoord2f(1.0f, 1.0f);
		gl2.glVertex2f(x2, y2);
		gl2.glTexCoord2f(0.0f, 1.0f);
		gl2.glVertex2f(x1, y2);
		gl2.glEnd();
	}

	void checkFramebufferStatus(GL gl)
	{
		int status;
		status = gl.glCheckFramebufferStatus(GL.GL_FRAMEBUFFER);
		switch (status)
		{
		case GL.GL_FRAMEBUFFER_COMPLETE:
			break;
		case GL.GL_FRAMEBUFFER_UNSUPPORTED:
			/* choose different formats */
			break;
		default:
			/* programming error; will fail on all hardware */
			throw new AssertionError(status);
		}
	}

	private void renderObjects(GL gl, boolean shadowPass)
	{
		GL2 gl2 = gl.getGL2();

		gl2.glPushMatrix();

		gl2.glTranslatef(0.0f, 0.0f, -5.0f);
		float size = 10f;
		gl2.glNormal3f(0.0f, 0.0f, 1.0f);

		gl2.glMaterialfv(GL.GL_FRONT_AND_BACK, GLLightingFunc.GL_DIFFUSE, new float[] {
		        0.9f, 0.9f, 0.9f, 1.0f
		}, 0);
		plane(gl, -size, -size, 0.0f, 0.0f);
		plane(gl, 0.0f, 0.0f, size, size);

		gl2.glMaterialfv(GL.GL_FRONT_AND_BACK, GLLightingFunc.GL_DIFFUSE, new float[] {
		        0.3f, 0.3f, 0.3f, 1.0f
		}, 0);
		plane(gl, -size, 0.0f, 0.0f, size);
		plane(gl, 0.0f, -size, size, 0.0f);

		gl2.glMaterialfv(GL.GL_FRONT_AND_BACK, GLLightingFunc.GL_DIFFUSE, new float[] {
		        0.4f, 0.8f, 0.4f, 1.0f
		}, 0);

		Random random = new Random(0L);
		GeoSphere sphere = new GeoSphere(Vector3f.ZERO, 1.0f,
		        GeoSphere.Type.OCTAHEDRAL, 2);
		for (int i = 0; i < 4; i++)
		{
			float x = 10.0f * (random.nextFloat() - 0.5f);
			float y = 10.0f * (random.nextFloat() - 0.5f);
			gl2.glTranslatef(x, y, i);
			GLUtilities.render(gl, sphere);
			gl2.glTranslatef(-x, -y, -i);
		}

		if (!shadowPass)
		{
			gl2.glMaterialfv(GL.GL_FRONT_AND_BACK, GLLightingFunc.GL_DIFFUSE, new float[] { 1.0f, 0.8f, 0.4f, 1.0f }, 0);
			gl2.glTranslatef( light1Position.x, light1Position.y, light1Position.z );
			GLUtilities.render(gl, sphere);
			gl2.glTranslatef( -light1Position.x, -light1Position.y, -light1Position.z );
		}

		gl2.glPopMatrix();
	}

	private void setupLights(GL gl)
	{
		GL2 gl2 = gl.getGL2();

		gl.glEnable(GLLightingFunc.GL_LIGHTING);
		gl2.glLightModeli(GL2.GL_LIGHT_MODEL_COLOR_CONTROL, GL2.GL_SEPARATE_SPECULAR_COLOR);
		gl2.glLightModeli(GL2.GL_LIGHT_MODEL_LOCAL_VIEWER, 1);

		gl.glEnable(GLLightingFunc.GL_LIGHT0);
		gl2.glLightfv(GLLightingFunc.GL_LIGHT0, GLLightingFunc.GL_AMBIENT, new float[] { 0.1f, 0.1f, 0.1f, 1.0f }, 0);
		gl2.glLightfv(GLLightingFunc.GL_LIGHT0, GLLightingFunc.GL_DIFFUSE, new float[] { 1.0f, 1.0f, 1.0f, 1.0f }, 0);
		gl2.glLightfv(GLLightingFunc.GL_LIGHT0, GLLightingFunc.GL_SPECULAR, new float[] { 1.0f, 1.0f, 1.0f, 1.0f }, 0);
		gl2.glLightfv(GLLightingFunc.GL_LIGHT0, GLLightingFunc.GL_POSITION, new float[] { light1Position.x, light1Position.y, light1Position.z, 1.0f }, 0);
	}

	@Override
	public void reshape(GLAutoDrawable drawable, int x, int y, int width,
	        int height)
	{
	}

	@Override
	public void dispose( GLAutoDrawable drawable )
	{
	}
}
