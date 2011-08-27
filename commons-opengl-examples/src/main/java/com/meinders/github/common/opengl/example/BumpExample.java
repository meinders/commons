package com.github.meinders.common.opengl.example;

import java.awt.event.*;
import java.nio.*;
import javax.media.opengl.*;
import javax.media.opengl.awt.*;
import javax.media.opengl.fixedfunc.*;
import javax.media.opengl.glu.*;

import com.github.meinders.common.animation.*;
import com.github.meinders.common.math.*;
import com.github.meinders.common.opengl.*;
import com.jogamp.opengl.util.texture.*;
import com.jogamp.opengl.util.texture.awt.*;

class BumpExample implements GLEventListener
{
	private FrameCounter counter = new FrameCounter();

	private Texture color;
	private Texture bump;
	private Texture normalizationCubeMap;

	GeoSphere shape = new GeoSphere(Vector3f.ZERO, 10.0f,
	        GeoSphere.Type.OCTAHEDRAL, 20);

	float cameraX = 0.0f;
	float cameraY = -20.0f;
	float cameraZ = 0.0f;

	public void init(final GLAutoDrawable drawable)
	{
		GLCanvas canvas = (GLCanvas)drawable;
		canvas.addMouseMotionListener(new MouseAdapter()
		{
			@Override
			public void mouseMoved(MouseEvent e)
			{
				float s = 10.0f * (e.getX() - drawable.getWidth() / 2.0f)
				        / drawable.getWidth();
				float t = 10.0f * (e.getY() - drawable.getHeight() / 2.0f)
				        / drawable.getHeight();

				cameraX = (float) Math.cos(s) * (float) Math.sin(t) * 20.0f;
				cameraY = (float) Math.sin(s) * (float) Math.sin(t) * 20.0f;
				cameraZ = (float) Math.cos(t) * 20.0f;
			}
		});

		color = AWTTextureIO.newTexture( GLProfile.getGL2GL3(), GLUtilities.createColorMapImage(), false );
		bump = AWTTextureIO.newTexture( GLProfile.getGL2GL3(), GLUtilities.createNormalMapFromBumpMap( GLUtilities.createBumpMapImage() ), false );

		GL gl = drawable.getGL();

		normalizationCubeMap = createNormalizationCubeMap(gl);

		// System.out.println("gl.glGetString(GL.GL_EXTENSIONS) = '" +
		// gl.glGetString(GL.GL_EXTENSIONS) + "'");

		int[] textureImageUnits = new int[1];
		gl.glGetIntegerv(GL2.GL_MAX_TEXTURE_IMAGE_UNITS, textureImageUnits, 0);

		System.out.println("gl.isExtensionAvailable(\"GL_ARB_multitexture\") = '"
		        + gl.isExtensionAvailable("GL_ARB_multitexture") + "'");
		System.out.println("textureImageUnits[0] = '"
		        + textureImageUnits[0] + "'");
		System.out.println("gl.isExtensionAvailable(\"GL_ARB_texture_env_combine\") = '"
		        + gl.isExtensionAvailable("GL_ARB_texture_env_combine")
		        + "'");
		System.out.println("gl.isExtensionAvailable(\"GL_ARB_texture_env_dot3\") = '"
		        + gl.isExtensionAvailable("GL_ARB_texture_env_dot3") + "'");

	}

	private Texture createNormalizationCubeMap(GL gl)
	{
		Texture result = TextureIO.newTexture(GL.GL_TEXTURE_CUBE_MAP);
		gl.glBindTexture(GL.GL_TEXTURE_CUBE_MAP, result.getTextureObject(gl));

		int size = 128;
		float offset = 0.5f;
		float halfSize = size / 2.0f;

		/*
		 * POSITIVE X
		 */
		ByteBuffer data = ByteBuffer.allocate(size * size * 3);
		for (int j = 0; j < size; j++)
		{
			for (int i = 0; i < size; i++)
			{
				float x = halfSize;
				float y = (j + offset - halfSize);
				float z = -(i + offset - halfSize);

				// scale to [0..1]
				float length = (float) Math.sqrt(x * x + y * y + z * z);
				x = ((x / length) + 1.0f) / 2.0f;
				y = ((y / length) + 1.0f) / 2.0f;
				z = ((z / length) + 1.0f) / 2.0f;

				data.put((byte) (x * 255.0f));
				data.put((byte) (y * 255.0f));
				data.put((byte) (z * 255.0f));
			}
		}
		data.rewind();
		gl.glTexImage2D(GL.GL_TEXTURE_CUBE_MAP_POSITIVE_X, 0, GL.GL_RGB8, size, size, 0, GL.GL_RGB, GL.GL_UNSIGNED_BYTE, data);

		/*
		 * NEGATIVE X
		 */
		data.rewind();
		for (int j = 0; j < size; j++)
		{
			for (int i = 0; i < size; i++)
			{
				float x = -halfSize;
				float y = (j + offset - halfSize);
				float z = (i + offset - halfSize);

				// scale to [0..1]
				float length = (float) Math.sqrt(x * x + y * y + z * z);
				x = ((x / length) + 1.0f) / 2.0f;
				y = ((y / length) + 1.0f) / 2.0f;
				z = ((z / length) + 1.0f) / 2.0f;

				data.put((byte) (x * 255.0f));
				data.put((byte) (y * 255.0f));
				data.put((byte) (z * 255.0f));
			}
		}
		data.rewind();
		gl.glTexImage2D(GL.GL_TEXTURE_CUBE_MAP_NEGATIVE_X, 0, GL.GL_RGB8, size, size, 0, GL.GL_RGB, GL.GL_UNSIGNED_BYTE, data);

		/*
		 * POSITIVE Y
		 */
		data.rewind();
		for (int j = 0; j < size; j++)
		{
			for (int i = 0; i < size; i++)
			{
				float x = i + offset - halfSize;
				float y = -halfSize;
				float z = j + offset - halfSize;

				// scale to [0..1]
				float length = (float) Math.sqrt(x * x + y * y + z * z);
				x = ((x / length) + 1.0f) / 2.0f;
				y = ((y / length) + 1.0f) / 2.0f;
				z = ((z / length) + 1.0f) / 2.0f;

				data.put((byte) (x * 255.0f));
				data.put((byte) (y * 255.0f));
				data.put((byte) (z * 255.0f));
			}
		}
		data.rewind();
		gl.glTexImage2D(GL.GL_TEXTURE_CUBE_MAP_POSITIVE_Y, 0, GL.GL_RGB8, size, size, 0, GL.GL_RGB, GL.GL_UNSIGNED_BYTE, data);

		/*
		 * NEGATIVE Y
		 */
		data.rewind();
		for (int j = 0; j < size; j++)
		{
			for (int i = 0; i < size; i++)
			{
				float x = i + offset - halfSize;
				float y = halfSize;
				float z = -(j + offset - halfSize);

				// scale to [0..1]
				float length = (float) Math.sqrt(x * x + y * y + z * z);
				x = ((x / length) + 1.0f) / 2.0f;
				y = ((y / length) + 1.0f) / 2.0f;
				z = ((z / length) + 1.0f) / 2.0f;

				data.put((byte) (x * 255.0f));
				data.put((byte) (y * 255.0f));
				data.put((byte) (z * 255.0f));
			}
		}
		data.rewind();
		gl.glTexImage2D(GL.GL_TEXTURE_CUBE_MAP_NEGATIVE_Y, 0, GL.GL_RGB8, size, size, 0, GL.GL_RGB, GL.GL_UNSIGNED_BYTE, data);

		/*
		 * POSITIVE Z
		 */
		data.rewind();
		for (int j = 0; j < size; j++)
		{
			for (int i = 0; i < size; i++)
			{
				float x = i + offset - halfSize;
				float y = (j + offset - halfSize);
				float z = halfSize;

				// scale to [0..1]
				float length = (float) Math.sqrt(x * x + y * y + z * z);
				x = ((x / length) + 1.0f) / 2.0f;
				y = ((y / length) + 1.0f) / 2.0f;
				z = ((z / length) + 1.0f) / 2.0f;

				data.put((byte) (x * 255.0f));
				data.put((byte) (y * 255.0f));
				data.put((byte) (z * 255.0f));
			}
		}
		data.rewind();
		gl.glTexImage2D(GL.GL_TEXTURE_CUBE_MAP_POSITIVE_Z, 0, GL.GL_RGB8, size, size, 0, GL.GL_RGB, GL.GL_UNSIGNED_BYTE, data);

		/*
		 * NEGATIVE Z
		 */
		data.rewind();
		for (int j = 0; j < size; j++)
		{
			for (int i = 0; i < size; i++)
			{
				float x = -(i + offset - halfSize);
				float y = (j + offset - halfSize);
				float z = -halfSize;

				// scale to [0..1]
				float length = (float) Math.sqrt(x * x + y * y + z * z);
				x = ((x / length) + 1.0f) / 2.0f;
				y = ((y / length) + 1.0f) / 2.0f;
				z = ((z / length) + 1.0f) / 2.0f;

				data.put((byte) (x * 255.0f));
				data.put((byte) (y * 255.0f));
				data.put((byte) (z * 255.0f));
			}
		}
		data.rewind();
		gl.glTexImage2D(GL.GL_TEXTURE_CUBE_MAP_NEGATIVE_Z, 0, GL.GL_RGB8, size, size, 0, GL.GL_RGB, GL.GL_UNSIGNED_BYTE, data);

		gl.glTexParameteri(GL.GL_TEXTURE_CUBE_MAP, GL.GL_TEXTURE_MIN_FILTER, GL.GL_LINEAR);
		gl.glTexParameteri(GL.GL_TEXTURE_CUBE_MAP, GL.GL_TEXTURE_MAG_FILTER, GL.GL_LINEAR);
		gl.glTexParameteri(GL.GL_TEXTURE_CUBE_MAP, GL.GL_TEXTURE_WRAP_S, GL.GL_CLAMP_TO_EDGE);
		gl.glTexParameteri(GL.GL_TEXTURE_CUBE_MAP, GL.GL_TEXTURE_WRAP_T, GL.GL_CLAMP_TO_EDGE);
		gl.glTexParameteri(GL.GL_TEXTURE_CUBE_MAP, GL2ES2.GL_TEXTURE_WRAP_R, GL.GL_CLAMP_TO_EDGE);

		return result;
	}

	public void reshape(GLAutoDrawable drawable, int x, int y, int width,
	        int height)
	{
		GL gl = drawable.getGL();
		GL2 gl2 = gl.getGL2();
		GLU glu = new GLU();

		gl2.glMatrixMode( GLMatrixFunc.GL_PROJECTION );
		double fovy = 90.0;
		double aspect = (double) width / (double) height;
		double zNear = 1.0;
		double zFar = 100.0;
		gl2.glLoadIdentity();
		glu.gluPerspective(fovy, aspect, zNear, zFar);
	}

	public void display(GLAutoDrawable drawable)
	{
		GL gl = drawable.getGL();
		GL2 gl2 = gl.getGL2();
		GLU glu = new GLU();

		gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT
		        | GL.GL_STENCIL_BUFFER_BIT);

		gl2.glMatrixMode(GLMatrixFunc.GL_MODELVIEW);
		gl2.glLoadIdentity();
		// glu.gluLookAt(-2.0, -5.0, 25.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0);
		glu.gluLookAt(cameraX, cameraY, cameraZ, 0.0, 0.0, 0.0, 0.0, 0.0,
		        1.0);

		gl.glEnable(GL.GL_DEPTH_TEST);

		Primitive plane = new Primitive(GL2.GL_QUADS);
		plane.addVertex(new Vertex(new Vector3f(-10.0, -10.0, 0.0),
		        new TexCoord2f(0.0, 0.0)));
		plane.addVertex(new Vertex(new Vector3f(10.0, -10.0, 0.0),
		        new TexCoord2f(1.0, 0.0)));
		plane.addVertex(new Vertex(new Vector3f(10.0, 10.0, 0.0),
		        new TexCoord2f(1.0, 1.0)));
		plane.addVertex(new Vertex(new Vector3f(-10.0, 10.0, 0.0),
		        new TexCoord2f(0.0, 1.0)));
		for (int x = 0; x < 2; x++)
		{
			for (int y = 0; y < 2; y++)
			{
				plane.addVertex(new Vertex(new Vector3f(-10.0 + x * 10.0,
				        -10.0 + y * 10.0, 0.0), new TexCoord2f(x / 2.0,
				        y / 2.0)));
				plane.addVertex(new Vertex(new Vector3f(x * 10.0, -10.0 + y
				        * 10.0, 0.0),
				        new TexCoord2f((x + 1) / 2.0, y / 2.0)));
				plane.addVertex(new Vertex(new Vector3f(x * 10.0, y * 10.0,
				        0.0), new TexCoord2f((x + 1) / 2.0, (y + 1) / 2.0)));
				plane.addVertex(new Vertex(new Vector3f(-10.0 + x * 10.0,
				        y * 10.0, 0.0), new TexCoord2f(x / 2.0,
				        (y + 1) / 2.0)));
			}
		}

		gl.glDepthFunc(GL.GL_LEQUAL);
		gl.glEnable(GL2.GL_LIGHTING);

		float lightR = (float) Math.sin(System.currentTimeMillis() / 1000.0) * 10.0f;
		float lightZ = 15.0f;

		/*
		 * Render first light
		 */
		Vector3f lightPosition;
		{
			double lightT = System.currentTimeMillis() / 500.0;
			float lightX = (float) (Math.cos(lightT) % (Math.PI * 2.0))
			        * lightR;
			float lightY = (float) (Math.sin(lightT) % (Math.PI * 2.0))
			        * lightR;
			lightPosition = new Vector3f(lightX, lightY, lightZ);
		}

		gl2.glLightfv(GLLightingFunc.GL_LIGHT0, GLLightingFunc.GL_AMBIENT, new float[] { 0.0f, 0.0f, 0.0f }, 0);
		gl2.glLightfv(GLLightingFunc.GL_LIGHT0, GLLightingFunc.GL_DIFFUSE, new float[] { 0.0f, 1.0f, 0.0f }, 0);
		gl2.glLightfv(GLLightingFunc.GL_LIGHT0, GLLightingFunc.GL_SPECULAR, new float[] { 1.0f, 1.0f, 1.0f }, 0);
		gl2.glLightfv(GLLightingFunc.GL_LIGHT0, GLLightingFunc.GL_POSITION, new float[] { lightPosition.x, lightPosition.y, lightPosition.z, 1.0f }, 0);
		gl.glEnable(GLLightingFunc.GL_LIGHT0);

		renderPrimitive(gl, shape, color, bump, lightPosition, false);

		/*
		 * Render second light
		 */
		Vector3f lightPosition2;
		{
			double lightT = System.currentTimeMillis() / 500.0 + 2.0
			        * Math.PI / 3.0;
			float lightX = (float) (Math.cos(lightT) % (Math.PI * 2.0))
			        * lightR;
			float lightY = (float) (Math.sin(lightT) % (Math.PI * 2.0))
			        * lightR;
			lightPosition2 = new Vector3f(lightX, lightY, lightZ);
		}

		gl2.glLightfv(GLLightingFunc.GL_LIGHT1, GLLightingFunc.GL_AMBIENT, new float[] { 0.0f, 0.0f, 0.0f }, 0);
		gl2.glLightfv(GLLightingFunc.GL_LIGHT1, GLLightingFunc.GL_DIFFUSE, new float[] { 0.0f, 0.0f, 1.0f }, 0);
		gl2.glLightfv(GLLightingFunc.GL_LIGHT1, GLLightingFunc.GL_SPECULAR, new float[] { 1.0f, 1.0f, 1.0f }, 0);
		gl2.glLightfv(GLLightingFunc.GL_LIGHT1, GLLightingFunc.GL_POSITION, new float[] { lightPosition2.x, lightPosition2.y, lightPosition2.z, 1.0f }, 0);
		gl.glEnable(GLLightingFunc.GL_LIGHT1);

		renderPrimitive(gl, shape, color, bump, lightPosition2, false);

		/*
		 * Render third light
		 */
		Vector3f lightPosition3;
		{
			double lightT = System.currentTimeMillis() / 500.0 + 4.0
			        * Math.PI / 3.0;
			float lightX = (float) (Math.cos(lightT) % (Math.PI * 2.0))
			        * lightR;
			float lightY = (float) (Math.sin(lightT) % (Math.PI * 2.0))
			        * lightR;
			lightPosition3 = new Vector3f(lightX, lightY, lightZ);
		}

		gl2.glLightfv(GLLightingFunc.GL_LIGHT2, GLLightingFunc.GL_AMBIENT, new float[] { 0.0f, 0.0f, 0.0f }, 0);
		gl2.glLightfv(GLLightingFunc.GL_LIGHT2, GLLightingFunc.GL_DIFFUSE, new float[] { 1.0f, 0.0f, 0.0f }, 0);
		gl2.glLightfv(GLLightingFunc.GL_LIGHT2, GLLightingFunc.GL_SPECULAR, new float[] { 1.0f, 1.0f, 1.0f }, 0);
		gl2.glLightfv(GLLightingFunc.GL_LIGHT2, GLLightingFunc.GL_POSITION, new float[] { lightPosition3.x, lightPosition3.y, lightPosition3.z, 1.0f }, 0);
		gl.glEnable(GLLightingFunc.GL_LIGHT2);

		renderPrimitive(gl, shape, color, bump, lightPosition3, false);
		render(gl, shape);

		double lightX = lightPosition.x;
		double lightY = lightPosition.y;
		gl2.glTranslated(lightX, lightY, lightZ);
		GLUquadric sphere = glu.gluNewQuadric();
		glu.gluSphere(sphere, 1.0, 4, 4);
		gl2.glTranslated(-lightX, -lightY, -lightZ);

		counter.countFrame();
		System.out.println("FPS " + counter.getFramesPerSecond());
	}

	private void renderPrimitive(GL gl, GLShape shape, Texture color,
	        Texture bump, Vector3f lightPosition, boolean first)
	{
		final GL2 gl2 = gl.getGL2();

		/*
		 * Set The First Texture Unit To Normalize Our Vector From The
		 * Surface To The Light. Set The Texture Environment Of The First
		 * Texture Unit To Replace It With The Sampled Value Of The
		 * Normalization Cube Map.
		 */
		gl.glActiveTexture( GL.GL_TEXTURE0 );
		gl.glEnable(GL.GL_TEXTURE_CUBE_MAP);
		gl.glBindTexture(GL.GL_TEXTURE_CUBE_MAP,
		        normalizationCubeMap.getTextureObject(gl));
		gl2.glTexEnvi(GL2ES1.GL_TEXTURE_ENV, GL2ES1.GL_TEXTURE_ENV_MODE, GL2ES1.GL_COMBINE);
		gl2.glTexEnvi(GL2ES1.GL_TEXTURE_ENV, GL2ES1.GL_COMBINE_RGB, GL2ES1.GL_REPLACE);
		gl2.glTexEnvi(GL2ES1.GL_TEXTURE_ENV, GL2.GL_SOURCE0_RGB, GL2ES1.GL_TEXTURE);

		/*
		 * Set The Second Unit To The Bump Map. Set The Texture Environment
		 * Of The Second Texture Unit To Perform A Dot3 Operation With The
		 * Value Of The Previous Texture Unit (The Normalized Vector Form
		 * The Surface To The Light) And The Sampled Texture Value (The
		 * Normalized Normal Vector Of Our Bump Map).
		 */
		gl.glActiveTexture(GL.GL_TEXTURE1);
		gl.glEnable(GL.GL_TEXTURE_2D);
		gl.glBindTexture(GL.GL_TEXTURE_2D, bump.getTextureObject(gl));
		gl2.glTexEnvi(GL2ES1.GL_TEXTURE_ENV, GL2ES1.GL_TEXTURE_ENV_MODE, GL2ES1.GL_COMBINE);
		gl2.glTexEnvi(GL2ES1.GL_TEXTURE_ENV, GL2ES1.GL_COMBINE_RGB, GL2ES1.GL_DOT3_RGB);
		gl2.glTexEnvi(GL2ES1.GL_TEXTURE_ENV, GL2.GL_SOURCE0_RGB, GL2ES1.GL_PREVIOUS);
		gl2.glTexEnvi(GL2ES1.GL_TEXTURE_ENV, GL2.GL_SOURCE1_RGB, GL2ES1.GL_TEXTURE);

		gl.glActiveTexture(GL.GL_TEXTURE2);
		gl.glEnable(GL.GL_TEXTURE_2D);
		gl.glBindTexture(GL.GL_TEXTURE_2D, bump.getTextureObject(gl));
		gl2.glTexEnvi(GL2ES1.GL_TEXTURE_ENV, GL2ES1.GL_TEXTURE_ENV_MODE, GL2ES1.GL_COMBINE);
		gl2.glTexEnvi(GL2ES1.GL_TEXTURE_ENV, GL2ES1.GL_COMBINE_RGB, GL2ES1.GL_MODULATE);
		gl2.glTexEnvi(GL2ES1.GL_TEXTURE_ENV, GL2.GL_SOURCE0_RGB, GL2ES1.GL_PRIMARY_COLOR);
		gl2.glTexEnvi(GL2ES1.GL_TEXTURE_ENV, GL2.GL_SOURCE1_RGB, GL2ES1.GL_PREVIOUS);

		{
			/*
			 * Set The Third Texture Unit To Our Texture. Set The Texture
			 * Environment Of The Third Texture Unit To Modulate (Multiply)
			 * The Result Of Our Dot3 Operation With The Texture Value.
			 */
			gl.glActiveTexture(GL.GL_TEXTURE3);
			gl.glEnable(GL.GL_TEXTURE_2D);
			gl.glBindTexture(GL.GL_TEXTURE_2D, color.getTextureObject(gl));
			gl2.glTexEnvi(GL2ES1.GL_TEXTURE_ENV, GL2ES1.GL_TEXTURE_ENV_MODE, GL2ES1.GL_MODULATE);
		}

		for (Primitive primitive : shape.getPrimitives())
		{
			gl2.glBegin(primitive.getType());
			for (Vertex vertex : primitive.getVertices())
			{
				Vector3f position = vertex.getPosition();
				Vector3f relativeLightPosition = lightPosition.difference(position);
				TexCoord2f texCoord = vertex.getTexCoord();

				Vector3f normal = vertex.getNormal();
				if (normal != null)
				{
					gl2.glNormal3f(normal.x, normal.y, normal.z);
				}

				gl2.glMultiTexCoord3f(GL.GL_TEXTURE0, relativeLightPosition.x, relativeLightPosition.y, relativeLightPosition.z);
				if (texCoord != null)
				{
					gl2.glMultiTexCoord2f(GL.GL_TEXTURE1, texCoord.x, texCoord.y);
					gl2.glMultiTexCoord2f(GL.GL_TEXTURE2, texCoord.x, texCoord.y);
					gl2.glMultiTexCoord2f(GL.GL_TEXTURE3, texCoord.x, texCoord.y);
				}

				gl2.glVertex3f(position.x, position.y, position.z);
			}
			gl2.glEnd();
		}

		gl.glActiveTexture(GL.GL_TEXTURE0);
		gl.glDisable(GL.GL_TEXTURE_CUBE_MAP);
		gl.glActiveTexture(GL.GL_TEXTURE1);
		gl.glDisable(GL.GL_TEXTURE_2D);
		gl.glActiveTexture(GL.GL_TEXTURE2);
		gl.glDisable(GL.GL_TEXTURE_2D);
	}

	private void render(GL gl, GLShape shape)
	{
		GL2 gl2 = gl.getGL2();
		for (Primitive primitive : shape.getPrimitives())
		{
			gl2.glBegin( primitive.getType() );
			for (Vertex vertex : primitive.getVertices())
			{
				Vector3f position = vertex.getPosition();

				Vector3f normal = vertex.getNormal();
				if (normal != null)
					gl2.glNormal3f(normal.x, normal.y, normal.z);

				TexCoord2f texCoord = vertex.getTexCoord();
				if (texCoord != null)
					gl2.glTexCoord2f(texCoord.x, texCoord.y);

				gl2.glVertex3f(position.x, position.y, position.z);
			}
			gl2.glEnd();
		}
	}

	private void setTextureMulti(GL gl, Texture color, Texture bump)
	{
		GL2 gl2 = gl.getGL2();

		/*
		 * Set The First Texture Unit To Normalize Our Vector From The
		 * Surface To The Light. Set The Texture Environment Of The First
		 * Texture Unit To Replace It With The Sampled Value Of The
		 * Normalization Cube Map.
		 */
		gl.glActiveTexture( GL.GL_TEXTURE0 );
		gl.glEnable( GL.GL_TEXTURE_CUBE_MAP );
		gl.glBindTexture( GL.GL_TEXTURE_CUBE_MAP, normalizationCubeMap.getTextureObject(gl) );
		gl2.glTexEnvi(GL2ES1.GL_TEXTURE_ENV, GL2ES1.GL_TEXTURE_ENV_MODE, GL2ES1.GL_COMBINE);
		gl2.glTexEnvi(GL2ES1.GL_TEXTURE_ENV, GL2ES1.GL_COMBINE_RGB, GL2ES1.GL_REPLACE);
		gl2.glTexEnvi(GL2ES1.GL_TEXTURE_ENV, GL2.GL_SOURCE0_RGB, GL2ES1.GL_TEXTURE);

		/*
		 * Set The Second Unit To The Bump Map. Set The Texture Environment
		 * Of The Second Texture Unit To Perform A Dot3 Operation With The
		 * Value Of The Previous Texture Unit (The Normalized Vector Form
		 * The Surface To The Light) And The Sampled Texture Value (The
		 * Normalized Normal Vector Of Our Bump Map).
		 */
		gl.glActiveTexture( GL.GL_TEXTURE1 );
		gl.glEnable( GL.GL_TEXTURE_2D );
		gl.glBindTexture( GL.GL_TEXTURE_2D, bump.getTextureObject(gl) );
		gl2.glTexEnvi(GL2ES1.GL_TEXTURE_ENV, GL2ES1.GL_TEXTURE_ENV_MODE, GL2ES1.GL_COMBINE);
		gl2.glTexEnvi(GL2ES1.GL_TEXTURE_ENV, GL2ES1.GL_COMBINE_RGB, GL2ES1.GL_DOT3_RGB);
		gl2.glTexEnvi(GL2ES1.GL_TEXTURE_ENV, GL2.GL_SOURCE0_RGB, GL2ES1.GL_PREVIOUS);
		gl2.glTexEnvi(GL2ES1.GL_TEXTURE_ENV, GL2.GL_SOURCE1_RGB, GL2ES1.GL_TEXTURE);

		/*
		 * Set The Third Texture Unit To Our Texture. Set The Texture
		 * Environment Of The Third Texture Unit To Modulate (Multiply) The
		 * Result Of Our Dot3 Operation With The Texture Value.
		 */
		gl.glActiveTexture( GL.GL_TEXTURE2 );
		gl.glEnable( GL.GL_TEXTURE_2D );
		gl.glBindTexture(GL.GL_TEXTURE_2D, color.getTextureObject(gl));
		gl2.glTexEnvi( GL2ES1.GL_TEXTURE_ENV, GL2ES1.GL_TEXTURE_ENV_MODE, GL2ES1.GL_MODULATE );
	}

	@Override
	public void dispose( GLAutoDrawable drawable )
	{
	}
}