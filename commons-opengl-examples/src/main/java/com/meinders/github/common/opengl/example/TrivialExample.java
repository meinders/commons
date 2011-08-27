package com.github.meinders.common.opengl.example;

import javax.media.opengl.*;
import javax.media.opengl.fixedfunc.*;
import javax.media.opengl.glu.*;

import com.github.meinders.common.math.*;
import com.github.meinders.common.opengl.*;

public class TrivialExample implements GLEventListener
{
	private Vector3f camera;

	private Vector3f light1Position = new Vector3f(30.0f, 0.0f, 30.0f);

	private Vector3f light1Target = new Vector3f(0.0f, 0.0f, 0.0f);

	public TrivialExample()
	{
		camera = new Vector3f(0.0, -30.0, 15.0);
	}

	@Override
	public void init(GLAutoDrawable drawable)
	{
		System.out.println("TrivialExample.init(drawable)");
		GL gl = drawable.getGL();
		GL2 gl2 = gl.getGL2();

		gl.glEnable(GLLightingFunc.GL_LIGHTING);
		gl2.glLightModeli(GL2.GL_LIGHT_MODEL_COLOR_CONTROL, GL2.GL_SEPARATE_SPECULAR_COLOR);

		gl.glEnable(GLLightingFunc.GL_LIGHT0);
		gl2.glLightfv(GLLightingFunc.GL_LIGHT0, GLLightingFunc.GL_AMBIENT, new float[] {
		        0.1f, 0.1f, 0.1f, 1.0f
		}, 0);
		gl2.glLightfv(GLLightingFunc.GL_LIGHT0, GLLightingFunc.GL_DIFFUSE, new float[] {
		        1.0f, 1.0f, 1.0f, 1.0f
		}, 0);
		gl2.glLightfv(GLLightingFunc.GL_LIGHT0, GLLightingFunc.GL_SPECULAR, new float[] {
		        1.0f, 1.0f, 1.0f, 1.0f
		}, 0);
		gl2.glLightfv(GLLightingFunc.GL_LIGHT0, GLLightingFunc.GL_POSITION, light1Position.toArray(), 0);
	}

	@Override
	public void display(GLAutoDrawable drawable)
	{
		System.out.println("TrivialExample.display(drawable)");
		GL gl = drawable.getGL();
		GL2 gl2 = gl.getGL2();
		GLU glu = new GLU();

		gl.glEnable( GL.GL_DEPTH_TEST );

		gl.glClear( GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT );
		gl2.glMatrixMode( GLMatrixFunc.GL_MODELVIEW );
		gl2.glLoadIdentity();
		glu.gluLookAt( camera.x, camera.y, camera.z, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0 );
		camera = camera.rotate(Vector3f.Z_AXIS, (float) Math.toRadians(1.0));

		GeoSphere sphere = new GeoSphere(Vector3f.ZERO, 5.0f,
		        GeoSphere.Type.OCTAHEDRAL, 2);
		GLUtilities.render( gl, sphere );

		gl2.glTranslatef( 0.0f, 0.0f, -5.0f );
		float size = 10f;
		gl2.glColor3f( 1.0f, 1.0f, 1.0f );
		gl2.glNormal3f( 0.0f, 0.0f, 1.0f );
		gl2.glBegin( GL2.GL_QUADS );
		gl2.glVertex2f( -size, -size );
		gl2.glVertex2f( size, -size );
		gl2.glVertex2f( size, size );
		gl2.glVertex2f( -size, size );
		gl2.glEnd();
	}

	@Override
	public void reshape(GLAutoDrawable drawable, int x, int y, int width,
	        int height)
	{
		System.out.println("TrivialExample.reshape(drawable, x, y, width, height)");
		GL gl = drawable.getGL();
		GLUtilities.perspectiveProjection(gl, 45, drawable.getWidth(),
		        drawable.getHeight());
	}

	@Override
	public void dispose( GLAutoDrawable drawable )
	{
	}
}
