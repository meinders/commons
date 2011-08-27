package com.github.meinders.common.opengl.example;

import java.awt.*;
import java.awt.event.*;
import java.text.*;
import java.util.*;
import javax.media.opengl.*;
import javax.media.opengl.awt.*;
import javax.media.opengl.glu.*;

import com.github.meinders.common.animation.*;
import com.github.meinders.common.math.*;
import com.github.meinders.common.opengl.*;
import com.jogamp.opengl.util.awt.*;

class AccumulationBufferExample implements GLEventListener
{
	private FrameCounter counter = new FrameCounter();
	private int vertexCount = 0;

	private GeoSphere shape = new GeoSphere(Vector3f.ZERO, 2.0f,
	        GeoSphere.Type.OCTAHEDRAL, 5);

	private Vector3f camera = new Vector3f(0.0, -30.0, 15.0);

	private float rotationPerFrame = 1.0f;
	private boolean motionBlur = false;
	private boolean depthOfField = false;

	private int orb = 0;
	private int scene = 0;

	private TextRenderer textRenderer = null;
	private boolean displayLists = false;

	public void init(final GLAutoDrawable drawable)
	{
		GL gl = drawable.getGL();

		final int[] accumBits = new int[4];
		gl.glGetIntegerv(GL2.GL_ACCUM_RED_BITS, accumBits, 0);
		gl.glGetIntegerv(GL2.GL_ACCUM_GREEN_BITS, accumBits, 1);
		gl.glGetIntegerv(GL2.GL_ACCUM_BLUE_BITS, accumBits, 2);
		gl.glGetIntegerv(GL2.GL_ACCUM_ALPHA_BITS, accumBits, 3);
		System.out.println("Accumulation buffer bits: "
		        + Arrays.toString(accumBits));

		final GLCanvas canvas = (GLCanvas)drawable;
		canvas.addKeyListener( new KeyAdapter()
		{
			@Override
			public void keyReleased(KeyEvent e)
			{
				switch (e.getKeyCode())
				{
				case KeyEvent.VK_M:
					motionBlur = !motionBlur;
					break;
				case KeyEvent.VK_D:
					depthOfField = !depthOfField;
					break;
				case KeyEvent.VK_L:
					displayLists = !displayLists;
					break;
				}
			}

			@Override
			public void keyPressed(KeyEvent e)
			{
				switch (e.getKeyCode())
				{
				case KeyEvent.VK_ADD:
					rotationPerFrame = Math.min(5.0f, rotationPerFrame * 1.1f);
					break;
				case KeyEvent.VK_SUBTRACT:
					rotationPerFrame = Math.max(0.1f, rotationPerFrame / 1.1f);
					break;
				}
			}
		});

		System.out.println("gl.glGetString(GL.GL_EXTENSIONS) = '"
		        + gl.glGetString(GL.GL_EXTENSIONS) + "'");

		int[] textureImageUnits = new int[1];
		gl.glGetIntegerv(GL2.GL_MAX_TEXTURE_IMAGE_UNITS, textureImageUnits, 0);

		System.out.println("gl.isExtensionAvailable(\"GL_ARB_multitexture\") = '"
		        + gl.isExtensionAvailable("GL_ARB_multitexture") + "'");
		System.out.println("textureImageUnits[0] = '" + textureImageUnits[0]
		        + "'");
		System.out.println("gl.isExtensionAvailable(\"GL_ARB_texture_env_combine\") = '"
		        + gl.isExtensionAvailable("GL_ARB_texture_env_combine") + "'");
		System.out.println("gl.isExtensionAvailable(\"GL_ARB_texture_env_dot3\") = '"
		        + gl.isExtensionAvailable("GL_ARB_texture_env_dot3") + "'");

		textRenderer = new TextRenderer(new Font(Font.SANS_SERIF, Font.PLAIN,
		        10));

		/*
		 * Compile display lists.
		 */
		final GL2 gl2 = gl.getGL2();
		orb = gl2.glGenLists(2);
		scene = orb + 1;

		gl2.glNewList(orb, GL2.GL_COMPILE);
		renderPrimitive(gl);
		gl2.glEndList();

		gl2.glNewList(scene, GL2.GL_COMPILE);
		for (int x = -2; x <= 2; x++)
		{
			for (int y = -2; y <= 2; y++)
			{
				gl2.glTranslated(5.0 * x, 5.0 * y, 0.0);
				gl2.glCallList(orb);
				gl2.glTranslated(-5.0 * x, -5.0 * y, 0.0);
			}
		}
		gl2.glEndList();
	}

	public void reshape(GLAutoDrawable drawable, int x, int y, int width,
	        int height)
	{
		GL gl = drawable.getGL();
		GLUtilities.perspectiveProjection(gl, 45, width, height);
	}

	public void display(GLAutoDrawable drawable)
	{
		GL gl = drawable.getGL();
		final GL2 gl2 = gl.getGL2();
		GLU glu = new GLU();

		gl.glEnable(GL2.GL_LIGHTING);
		gl2.glLightModeli(GL2.GL_LIGHT_MODEL_COLOR_CONTROL,
		        GL2.GL_SEPARATE_SPECULAR_COLOR);
		// gl.glLightModeli(GL_LIGHT_MODEL_LOCAL_VIEWER, 1);

		gl.glEnable(GL2.GL_LIGHT0);
		gl2.glLightfv(GL2.GL_LIGHT0, GL2.GL_AMBIENT, new float[] { 0.1f, 0.1f, 0.1f, 1.0f }, 0);
		gl2.glLightfv(GL2.GL_LIGHT0, GL2.GL_DIFFUSE, new float[] { 1.0f, 0.0f, 0.0f, 1.0f }, 0);
		gl2.glLightfv(GL2.GL_LIGHT0, GL2.GL_SPECULAR, new float[] { 1.0f, 1.0f, 1.0f, 1.0f }, 0);
		gl2.glLightfv(GL2.GL_LIGHT0, GL2.GL_POSITION, new float[] { 30.0f, 0.0f, 30.0f, 1.0f }, 0);

		gl.glEnable(GL2.GL_LIGHT1);
		gl2.glLightfv(GL2.GL_LIGHT1, GL2.GL_DIFFUSE, new float[] { 0.0f, 1.0f, 0.0f, 1.0f }, 0);
		gl2.glLightfv(GL2.GL_LIGHT1, GL2.GL_SPECULAR, new float[] { 1.0f, 1.0f, 1.0f, 1.0f }, 0);
		gl2.glLightfv(GL2.GL_LIGHT1, GL2.GL_POSITION, new float[] { 30.0f, -30.0f, 30.0f, 1.0f }, 0);

		gl.glEnable(GL2.GL_LIGHT2);
		gl2.glLightfv(GL2.GL_LIGHT2, GL2.GL_DIFFUSE, new float[] { 0.0f, 0.0f, 1.0f, 1.0f }, 0);
		gl2.glLightfv(GL2.GL_LIGHT2, GL2.GL_SPECULAR, new float[] { 1.0f, 1.0f, 1.0f, 1.0f }, 0);
		gl2.glLightfv(GL2.GL_LIGHT2, GL2.GL_POSITION, new float[] { -30.0f, 30.0f, -30.0f, 1.0f}, 0);

		gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT | GL2.GL_ACCUM_BUFFER_BIT);

		gl.glEnable(GL.GL_DEPTH_TEST);

		float rotationPerFrameRadians = (float) Math.toRadians(rotationPerFrame);

		camera = camera.rotate(Vector3f.Z_AXIS, rotationPerFrameRadians);
		vertexCount = 0;

		float depthOfFieldRadius = 1.0f;
		float motionBlurDuration = 5.0f;

		int passes = 1;
		if (motionBlur || depthOfField)
		{
			passes = 8 * 8;
		}

		float totalWeight = 0.0f;

		if (motionBlur)
		{
			camera = camera.rotate(Vector3f.Z_AXIS, -motionBlurDuration
			        * rotationPerFrameRadians);
		}

		Random random = new Random(0);

		gl2.glMaterialfv(GL.GL_FRONT_AND_BACK, GL2.GL_AMBIENT, new float[] { 0.2f, 0.2f, 0.2f, 1.0f }, 0);
		gl2.glMaterialfv(GL.GL_FRONT_AND_BACK, GL2.GL_DIFFUSE, new float[] { 0.7f, 0.7f, 0.7f, 1.0f }, 0);
		gl2.glMaterialfv(GL.GL_FRONT_AND_BACK, GL2.GL_SPECULAR, new float[] { 0.1f, 0.1f, 0.1f, 1.0f }, 0);
		gl2.glMaterialf(GL.GL_FRONT_AND_BACK, GL2.GL_SHININESS, 32.0f);

		for (int i = 0; i < passes; i++)
		{
			if (motionBlur)
			{
				camera = camera.rotate(Vector3f.Z_AXIS, motionBlurDuration
				        * rotationPerFrameRadians / passes);
			}

			gl2.glMatrixMode(GL2.GL_MODELVIEW);
			gl2.glLoadIdentity();

			float weight = 1.0f;
			if (motionBlur)
			{
				weight = i + 1;
			}

			if (depthOfField)
			{
				float offsetX = (2.0f * random.nextFloat() - 1.0f)
				        * depthOfFieldRadius;
				float offsetY = (2.0f * random.nextFloat() - 1.0f)
				        * depthOfFieldRadius;
				float offsetZ = (2.0f * random.nextFloat() - 1.0f)
				        * depthOfFieldRadius;
				glu.gluLookAt(camera.x + offsetX, camera.y + offsetY, camera.z
				        + offsetZ, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0);

				weight = 4.0f / (Math.abs(offsetX) + Math.abs(offsetY)
				        + Math.abs(offsetZ) + 1.0f);
			}
			else
			{
				glu.gluLookAt(camera.x, camera.y, camera.z, 0.0, 0.0, 0.0, 0.0,
				        0.0, 1.0);
			}

			if (displayLists)
			{
				gl2.glCallList(scene);
			}
			else
			{
				for (int x = -2; x <= 2; x++)
				{
					for (int y = -2; y <= 2; y++)
					{
						gl2.glTranslated(5.0 * x, 5.0 * y, 0.0);
						renderPrimitive(gl);
						gl2.glTranslated(-5.0 * x, -5.0 * y, 0.0);
					}
				}
			}

			if (motionBlur)
			{
				gl2.glAccum(GL2.GL_ACCUM, weight / 2080f);
				gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
			}
			else if (depthOfField)
			{
				gl2.glAccum( GL2.GL_ACCUM, weight / passes );
				gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
			}
			totalWeight += weight;
		}

		if (motionBlur || depthOfField)
		{
			gl2.glAccum(GL2.GL_RETURN, 1.0f);
		}

		counter.countFrame();
		textRenderer.beginRendering(drawable.getWidth(), drawable.getHeight());
		textRenderer.setColor(Color.WHITE);
		textRenderer.draw("Motion blur: " + (motionBlur ? "on" : "off"), 10, 50);
		textRenderer.draw("Depth of field: " + (depthOfField ? "on" : "off"),
		        10, 40);
		textRenderer.draw("Display lists: " + (displayLists ? "on" : "off"),
		        10, 30);
		NumberFormat instance = NumberFormat.getInstance(Locale.ENGLISH);
		instance.setMaximumFractionDigits(1);
		instance.setMinimumFractionDigits(1);
		textRenderer.draw("Speed: " + instance.format(rotationPerFrame)
		        + "�/frame", 10, 20);
		textRenderer.draw(counter.getFramesPerSecond() + " fps (" + vertexCount
		        + " vertices)", 10, 10);
		textRenderer.endRendering();
	}

	private void renderPrimitive(GL gl)
	{
		final GL2 gl2 = gl.getGL2();
		for (Primitive primitive : shape.getPrimitives())
		{
			gl2.glBegin( primitive.getType() );
			for (Vertex vertex : primitive.getVertices())
			{
				Vector3f normal = vertex.getNormal();
				gl2.glNormal3d( normal.x, normal.y, normal.z );

				Vector3f position = vertex.getPosition();
				gl2.glVertex3d( position.x, position.y, position.z );
				vertexCount++;
			}
			gl2.glEnd();
		}
	}

	@Override
	public void dispose( GLAutoDrawable drawable )
	{
	}
}
