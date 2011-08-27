package com.github.meinders.common.opengl.example;

import java.awt.*;
import java.awt.image.*;
import javax.media.opengl.*;
import javax.media.opengl.fixedfunc.*;
import javax.media.opengl.glu.*;

import com.github.meinders.common.math.*;
import com.github.meinders.common.opengl.*;

public class GLUtilities
{
	public static BufferedImage createNormalMapFromBumpMap(BufferedImage bumpMap)
	{
		int width = bumpMap.getWidth();
		int height = bumpMap.getHeight();
		BufferedImage result = new BufferedImage(width, height,
		        BufferedImage.TYPE_INT_RGB);

		for (int y = 1; y < height - 1; y++)
		{
			for (int x = 1; x < width - 1; x++)
			{
				int corner1 = bumpMap.getRGB(x - 1, y - 1);
				int corner2 = bumpMap.getRGB(x + 1, y - 1);
				int corner3 = bumpMap.getRGB(x - 1, y + 1);
				int corner4 = bumpMap.getRGB(x + 1, y + 1);

				/*
				 * Apply sobel operation in x and y directions to determine the
				 * approximate normal vector.
				 */
				int sobelX = ((corner1 & 0xff)
				        + (bumpMap.getRGB(x - 1, y) & 0xff) * 2
				        + (corner3 & 0xff) - (corner2 & 0xff)
				        - (bumpMap.getRGB(x + 1, y) & 0xff) * 2 - (corner4 & 0xff)) / 8;

				int sobelY = ((corner3 & 0xff)
				        + (bumpMap.getRGB(x, y + 1) & 0xff) * 2
				        + (corner4 & 0xff) - (corner1 & 0xff)
				        - (bumpMap.getRGB(x, y - 1) & 0xff) * 2 - (corner2 & 0xff)) / 8;

				int z = (int) Math.sqrt(0xfe01 - sobelX * sobelX - sobelY
				        * sobelY) / 2 + 0x80;

				sobelX = sobelX + 0x80;
				sobelY = sobelY + 0x80;
				result.setRGB(x, y, sobelX << 16 | sobelY << 8 | z);
			}
		}

		for (int x = 0; x < width; x++)
		{
			result.setRGB(x, 0, result.getRGB(x, 1));
			result.setRGB(x, height - 1, result.getRGB(x, height - 2));
		}

		for (int y = 0; y < height; y++)
		{
			result.setRGB(0, y, result.getRGB(1, y));
			result.setRGB(width - 1, y, result.getRGB(width - 2, y));
		}

		return result;
	}

	public static BufferedImage createColorMapImage()
	{
		int width = 512;
		int height = 512;
		BufferedImage result = new BufferedImage(width, height,
		        BufferedImage.TYPE_INT_RGB);

		Graphics2D g = result.createGraphics();
		g.setColor(Color.WHITE);
		g.fillRect(0, 0, width, height);
		g.setColor(new Color(0x4080c0));
		g.fillRect(width / 4, height / 4, width / 2, height / 2);
		g.setColor(new Color(0xc08040));
		g.fillRect(width / 3, height / 3, width / 3, height / 3);
		g.dispose();

		return result;
	}

	public static BufferedImage createBumpMapImage()
	{
		int width = 512;
		int height = 512;
		BufferedImage result = new BufferedImage(width, height,
		        BufferedImage.TYPE_INT_RGB);

		int centerX1 = width / 2;
		int centerY1 = height / 2;// 3;
		int centerX2 = width / 5;
		int centerY2 = height * 3 / 4;
		int centerX3 = width * 5 / 6;
		int centerY3 = height * 3 / 5;

		for (int y = 0; y < height; y++)
		{
			for (int x = 0; x < width; x++)
			{
				int dx1 = x - centerX1;
				int dy1 = y - centerY1;
				int dx2 = x - centerX2;
				int dy2 = y - centerY2;
				int dx3 = x - centerX3;
				int dy3 = y - centerY3;

				double ripple1 = Math.max(0.0, Math.min(1.0,
				        Math.pow(Math.cos(0.03 * Math.sqrt(dx1 * dx1 + dy1
				                * dy1)), 2.0) * 0.8 + 0.5));
				double ripple2 = Math.max(0.0, Math.min(1.0,
				        Math.pow(Math.cos(0.05 * Math.sqrt(dx2 * dx2 + dy2
				                * dy2)), 2.0) * 0.4 + 0.5));
				double ripple3 = Math.max(0.0, Math.min(1.0,
				        Math.pow(Math.cos(0.08 * Math.sqrt(dx3 * dx3 + dy3
				                * dy3)), 2.0) * 1.2 + 0.5));

				int gray = 255 - 2 * Math.max(128, (int) Math.round(Math.min(
				        Math.min(ripple1, ripple2), ripple3) * 255.0));

				int rgb = gray | gray << 8 | gray << 16;
				result.setRGB(x, y, rgb);
			}
		}

		return result;
	}

	public static void perspectiveProjection(GL gl, double fovy, int width,
	        int height)
	{
		double aspect = (double) width / (double) height;
		double zNear = 1.0;
		double zFar = 100.0;

		GL2 gl2 = gl.getGL2();
		GLU glu = new GLU();
		gl2.glMatrixMode( GLMatrixFunc.GL_PROJECTION );
		gl2.glLoadIdentity();
		glu.gluPerspective(fovy, aspect, zNear, zFar);
		gl2.glMatrixMode( GLMatrixFunc.GL_MODELVIEW );
	}

	public static void render(GL gl, GLShape shape)
	{
		GL2 gl2 = gl.getGL2();
		for (Primitive primitive : shape.getPrimitives())
		{
			gl2.glBegin(primitive.getType());
			for (Vertex vertex : primitive.getVertices())
			{
				Vector3f normal = vertex.getNormal();
				gl2.glNormal3d(normal.x, normal.y, normal.z);

				Vector3f position = vertex.getPosition();
				gl2.glVertex3d(position.x, position.y, position.z);
			}
			gl2.glEnd();
		}
	}
}
