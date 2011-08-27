/*
 * Copyright 2018 Gerrit Meinders
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.github.meinders.common.opengl;

import java.awt.*;
import java.awt.RenderingHints.*;
import java.awt.font.*;
import java.awt.geom.*;
import java.awt.image.*;
import java.awt.image.renderable.*;
import java.text.*;
import java.util.*;
import javax.media.opengl.*;
import javax.media.opengl.fixedfunc.*;
import javax.media.opengl.glu.*;

import com.github.meinders.common.math.*;
import com.github.meinders.common.util.*;

import static javax.media.opengl.GL.*;

/**
 * Implementation of the {@link Graphics2D} class that performs its operations
 * on a {@link GLAutoDrawable}.
 *
 * @author Gerrit Meinders
 */
public class GLGraphics extends Graphics2D
{

	private static final double DEFAULT_FLATNESS = 1.0;

	protected final GLU glu = new GLU();

	private GLAutoDrawable drawable;

	private double flatness = DEFAULT_FLATNESS;

	private Map<Shape, GLShape> shapeCache;

	private Map<TextKey, GLShape> textCache;

	private Color color = Color.WHITE;

	private Color background = Color.BLACK;

	private Stroke stroke = null;

	private static final Font DEFAULT_FONT = new Font("SansSerif", Font.PLAIN,
	        12);

	private Font font;

	private final FontRenderContext fontRenderContext;

	private final boolean derived;

	private boolean disposed = false;

	public GLGraphics(GLAutoDrawable drawable)
	{
		super();
		this.drawable = drawable;

		shapeCache = new WeakHashMap<Shape, GLShape>();
		textCache = new SoftHashMap<TextKey, GLShape>();

		fontRenderContext = new FontRenderContext(null, false, false);

		reset();

		derived = false;
	}

	public GLGraphics(GLGraphics graphics)
	{
		this.drawable = graphics.drawable;
		this.flatness = graphics.flatness;
		this.shapeCache = graphics.shapeCache;
		this.textCache = graphics.textCache;
		this.color = graphics.color;
		this.background = graphics.background;
		this.stroke = graphics.stroke;
		this.font = graphics.font;
		this.fontRenderContext = graphics.fontRenderContext;

		// store current state for retrieval upon graphics disposal
		GL gl = drawable.getGL();
		GL2 gl2 = gl.getGL2();
		gl2.glPushMatrix();
		gl2.glPushAttrib( GL2.GL_ALL_ATTRIB_BITS );

		derived = true;
	}

	public void reset()
	{
		font = DEFAULT_FONT;
		stroke = null;

		GL gl = drawable.getGL();
		resetMatrices(drawable);

		setClip(0, 0, drawable.getWidth(), drawable.getHeight());

		GL2 gl2 = gl.getGL2();
		gl2.glColor3d(1.0, 1.0, 1.0);

		gl.glEnable(GL.GL_LINE_SMOOTH);
	}

	protected void resetMatrices(GLAutoDrawable drawable)
	{
		int width = drawable.getWidth();
		int height = drawable.getHeight();

		GL gl = drawable.getGL();
		GL2 gl2 = gl.getGL2();

		gl2.glMatrixMode( GLMatrixFunc.GL_PROJECTION);
		gl2.glLoadMatrixd(new double[] {
		        2.0 / width, 0.0, 0.0, 0.0, 0.0, -2.0 / height, 0.0, 0.0, 0.0,
		        0.0, 1.0, 0.0, -1.0, 1.0, 0.0, 1.0,
		}, 0);
		gl2.glMatrixMode(GLMatrixFunc.GL_MODELVIEW);
		gl2.glLoadIdentity();
	}

	public GLAutoDrawable getGLAutoDrawable()
	{
		return drawable;
	}

	public double getFlatness()
	{
		return flatness;
	}

	public void setFlatness(double flatness)
	{
		this.flatness = flatness;
	}

	@Override
	public void addRenderingHints(Map<?, ?> hints)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void clip(Shape shape)
	{
		// TODO Auto-generated method stub
		System.out.println("clip(Shape)");
	}

	@Override
	public void draw(Shape shape)
	{
		GL gl = drawable.getGL();
		GL2 gl2 = gl.getGL2();

		shapeCache.get(shape);
		PathIterator pathIterator = shape.getPathIterator(null,
		        DEFAULT_FLATNESS);
		if (!pathIterator.isDone())
		{
			double[] start = null;
			double[] coords = new double[6];
			do
			{
				int type = pathIterator.currentSegment(coords);

				switch (type)
				{
				case PathIterator.SEG_CLOSE:
					gl2.glVertex2d(start[0], start[1]);
					gl2.glEnd();
					start = null;
					break;
				case PathIterator.SEG_MOVETO:
					if (start != null)
					{
						gl2.glEnd();
					}
					start = coords.clone();
					gl2.glBegin(GL_LINE_STRIP);
					gl2.glVertex2d(coords[0], coords[1]);
					break;
				case PathIterator.SEG_LINETO:
					gl2.glVertex2d(coords[0], coords[1]);
					break;
				case PathIterator.SEG_QUADTO:
					gl2.glVertex2d(coords[2], coords[3]);
					break;
				case PathIterator.SEG_CUBICTO:
					gl2.glVertex2d(coords[4], coords[5]);
					break;
				}

				pathIterator.next();
			}
			while (!pathIterator.isDone());

			if (start != null)
			{
				gl2.glEnd();
			}
		}
	}

	@Override
	public void drawGlyphVector(GlyphVector g, float x, float y)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public boolean drawImage(Image img, AffineTransform xform, ImageObserver obs)
	{
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void drawImage(BufferedImage img, BufferedImageOp op, int x, int y)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void drawRenderableImage(RenderableImage img, AffineTransform xform)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void drawRenderedImage(RenderedImage img, AffineTransform xform)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void drawString(String str, int x, int y)
	{
		drawString(str, (float) x, (float) y);
	}

	@Override
	public void drawString(String str, float x, float y)
	{
		GLShape textShape = getGLShape(str);

		translate(x, y);
		fillGL(textShape);
		translate(-x, -y);
	}

	@Override
	public void drawString(AttributedCharacterIterator iterator, int x, int y)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void drawString(AttributedCharacterIterator iterator, float x,
	        float y)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void fill(Shape shape)
	{
		fillGL(getGLShape(shape));
	}

	private void fillGL(GLShape shape)
	{
		GL gl = drawable.getGL();
		GL2 gl2 = gl.getGL2();
		for (Primitive primitive : shape.getPrimitives())
		{
			gl2.glBegin(primitive.getType());
			for (Vertex vertex : primitive.getVertices())
			{
				Vector3f position = vertex.getPosition();
				gl2.glVertex3f(position.x, position.y, position.z);
			}
			gl2.glEnd();
		}
	}

	private GLShape getGLShape(Shape shape)
	{
		GLShape result = shapeCache.get(shape);
		if (result == null)
		{
			result = createGLShape(shape);
			shapeCache.put(shape, result);
		}
		return result;
	}

	/**
	 * Returns the shape of the given text in the current font, as a
	 * {@link GLShape}.
	 *
	 * @param text Text to get the shape of.
	 * @return Shape of the given text in the current font.
	 */
	private GLShape getGLShape(String text)
	{
		TextKey textKey = new TextKey(text, getFont());
		GLShape result = textCache.get(textKey);

		if (result == null)
		{
			GlyphVector glyphVector = font.createGlyphVector(
			        getFontRenderContext(), text);
			result = new GLShape();
			for (int i = 0; i < glyphVector.getNumGlyphs(); i++)
			{
				Shape glyphShape = glyphVector.getGlyphOutline(i);
				result.addShape(createGLShape(glyphShape));
			}
			textCache.put(textKey, result);
		}

		return result;
	}

	/**
	 * Creates a {@link GLShape} from an arbitrary Java2D {@link Shape}. The
	 * created {@link GLShape} can't contain curves, so any curved segments are
	 * flattened (approximated by one or more line segments).
	 *
	 * @param shape Shape to be converted to a {@link GLShape}.
	 *
	 * @return {@link GLShape} that approximates the given shape.
	 */
	private GLShape createGLShape(Shape shape)
	{
		PathIterator pathIterator = shape.getPathIterator(null, flatness);

		Tesselator tesselator = new Tesselator();
		tesselator.tesselate(shape);

		return tesselator.getResult();
	}

	@Override
	public Color getBackground()
	{
		return background;
	}

	@Override
	public Composite getComposite()
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public GraphicsConfiguration getDeviceConfiguration()
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public FontRenderContext getFontRenderContext()
	{
		return fontRenderContext;
	}

	@Override
	public Paint getPaint()
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object getRenderingHint(Key hintKey)
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public RenderingHints getRenderingHints()
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Stroke getStroke()
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public AffineTransform getTransform()
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean hit(Rectangle rect, Shape shape, boolean onStroke)
	{
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void rotate(double theta)
	{
		GL gl = drawable.getGL();
		GL2 gl2 = gl.getGL2();
		gl2.glMatrixMode(GLMatrixFunc.GL_MODELVIEW);
		gl2.glRotated(Math.toDegrees(theta), 0.0, 0.0, 1.0);
	}

	@Override
	public void rotate(double theta, double x, double y)
	{
		GL gl = drawable.getGL();
		GL2 gl2 = gl.getGL2();
		gl2.glMatrixMode(GLMatrixFunc.GL_MODELVIEW);
		gl2.glTranslated(x, y, 0.0);
		gl2.glRotated(Math.toDegrees(theta), 0.0, 0.0, 1.0);
		gl2.glTranslated(-x, -y, 0.0);
	}

	@Override
	public void scale(double sx, double sy)
	{
		GL gl = drawable.getGL();
		GL2 gl2 = gl.getGL2();
		gl2.glMatrixMode(GLMatrixFunc.GL_MODELVIEW);
		gl2.glScaled(sx, sy, 1.0);
	}

	@Override
	public void setBackground(Color color)
	{
		if (!background.equals(color))
		{
			background = color;
			GL gl = drawable.getGL();
			float[] components = color.getRGBComponents(new float[4]);
			gl.glClearColor(components[0], components[1], components[2],
			        components[3]);
		}
	}

	@Override
	public void setComposite(Composite comp)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void setPaint(Paint paint)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void setRenderingHint(Key hintKey, Object hintValue)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void setRenderingHints(Map<?, ?> hints)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void setStroke(Stroke stroke)
	{
		if (stroke != this.stroke)
		{
			this.stroke = stroke;

			if (stroke instanceof BasicStroke)
			{
				BasicStroke basicStroke = (BasicStroke) stroke;

				GL gl = drawable.getGL();
				GL2 gl2 = gl.getGL2();
				gl.glLineWidth(basicStroke.getLineWidth());

				float[] dashArray = basicStroke.getDashArray();
				if (dashArray != null)
				{
					/*
					 * Create 16-bit dash/stipple bitmask.
					 */
					short stipple = 0;

					float totalLength = 0.0f;
					for (float length : dashArray)
					{
						totalLength += length;
					}

					float start = 0.0f;
					boolean visibleSegment = true;

					for (float length : dashArray)
					{
						int startBit = (int) (16.0f * start / totalLength);
						int endBit = (int) (16.0f * (start + length) / totalLength);

						short mask = 0;
						for (int i = startBit; i < endBit; i++)
						{
							mask |= 1 << i;
						}

						if (visibleSegment)
						{
							stipple |= mask;
						}
						else
						{
							stipple &= ~mask;
						}
						start += length;
						visibleSegment = !visibleSegment;
					}

					int factor = Math.max(1, (int) totalLength / 16);

					gl.glEnable(GL2.GL_LINE_STIPPLE);
					gl2.glLineStipple(factor, stipple);
				}
				else
				{
					gl.glDisable(GL2.GL_LINE_STIPPLE);
				}

			}
			else if (stroke == null)
			{
				GL gl = drawable.getGL();
				gl.glDisable(GL2.GL_LINE_STIPPLE);

			}
			else
			{
				// TODO: Use createStrokedShape in draw methods!
				throw new IllegalArgumentException("Unsupported stroke: "
				        + stroke);
			}
		}
	}

	@Override
	public void setTransform(AffineTransform transform)
	{
		GL gl = drawable.getGL();
		GL2 gl2 = gl.getGL2();
		resetMatrices(drawable);
		gl2.glMultMatrixd(getMatrix(transform), 0);
	}

	@Override
	public void shear(double shx, double shy)
	{
		GL gl = drawable.getGL();
		GL2 gl2 = gl.getGL2();
		gl2.glMultMatrixd(new double[] {
		        1.0, shy, 0.0, 0.0, shx, 1.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0,
		        0.0, 0.0, 0.0, 1.0
		}, 0);
	}

	@Override
	public void transform(AffineTransform transform)
	{
		GL gl = drawable.getGL();
		GL2 gl2 = gl.getGL2();
		gl2.glMultMatrixd(getMatrix(transform), 0);
	}

	/**
	 * Returns the 3D matrix representing the given (2D) affine transform.
	 *
	 * @param transform Transform to get a 3D transformation matrix for
	 * @return 3D transformation matrix, as used by OpenGL operations
	 */
	private double[] getMatrix(AffineTransform transform)
	{
		double[] result = new double[16];
		transform.getMatrix(result);

		// xo, yo, 0, 1
		result[12] = result[4];
		result[13] = result[5];
		result[14] = 0.0;
		result[15] = 1.0;

		// xy, yy, 0, 0
		result[4] = result[2];
		result[5] = result[3];
		result[6] = 0.0;
		result[7] = 0.0;

		// xx, yx, 0, 0
		result[2] = 0.0;
		result[3] = 0.0;

		// 0, 0, 1, 0
		result[8] = 0.0;
		result[9] = 0.0;
		result[10] = 1.0;
		result[11] = 0.0;

		return result;
	}

	@Override
	public void translate(int x, int y)
	{
		translate((double) x, (double) y);
	}

	@Override
	public void translate(double tx, double ty)
	{
		GL gl = drawable.getGL();
		GL2 gl2 = gl.getGL2();
		gl2.glTranslated(tx, ty, 0.0);
	}

	@Override
	public void clearRect(int x, int y, int width, int height)
	{
		// TODO Auto-generated method stub
		System.out.println("clearRect");
	}

	@Override
	public void clipRect(int x, int y, int width, int height)
	{
		// TODO Auto-generated method stub
		System.out.println("clipRect");
	}

	@Override
	public void copyArea(int x, int y, int width, int height, int dx, int dy)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public Graphics create()
	{
		return new GLGraphics(this);
	}

	@Override
	public void dispose()
	{
		if (derived && !disposed)
		{
			disposed = true;
			// assume we're going back to the previous state
			GL gl = drawable.getGL();
			GL2 gl2 = gl.getGL2();
			gl2.glPopAttrib();
			gl2.glPopMatrix();
		}
	}

	@Override
	public void drawArc(int x, int y, int width, int height, int startAngle,
	        int arcAngle)
	{
		draw(new Arc2D.Float(x, y, width, height, startAngle, arcAngle,
		        Arc2D.OPEN));
	}

	@Override
	public boolean drawImage(Image img, int x, int y, ImageObserver observer)
	{
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean drawImage(Image img, int x, int y, Color bgcolor,
	        ImageObserver observer)
	{
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean drawImage(Image img, int x, int y, int width, int height,
	        ImageObserver observer)
	{
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean drawImage(Image img, int x, int y, int width, int height,
	        Color bgcolor, ImageObserver observer)
	{
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean drawImage(Image img, int dx1, int dy1, int dx2, int dy2,
	        int sx1, int sy1, int sx2, int sy2, ImageObserver observer)
	{
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean drawImage(Image img, int dx1, int dy1, int dx2, int dy2,
	        int sx1, int sy1, int sx2, int sy2, Color bgcolor,
	        ImageObserver observer)
	{
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void drawLine(int x1, int y1, int x2, int y2)
	{
		draw(new Line2D.Float(x1, y1, x2, y2));
	}

	@Override
	public void drawOval(int x, int y, int width, int height)
	{
		// TODO Auto-generated method stub
		System.out.println("drawOval");
	}

	@Override
	public void drawPolygon(int[] points, int[] points2, int points3)
	{
		// TODO Auto-generated method stub
		System.out.println("drawPolygon");
	}

	@Override
	public void drawPolyline(int[] points, int[] points2, int points3)
	{
		// TODO Auto-generated method stub
		System.out.println("drawPolyline");
	}

	@Override
	public void drawRoundRect(int x, int y, int width, int height,
	        int arcWidth, int arcHeight)
	{
		// TODO Auto-generated method stub
		System.out.println("drawRoundRect");
	}

	@Override
	public void fillArc(int x, int y, int width, int height, int startAngle,
	        int arcAngle)
	{
		// TODO Auto-generated method stub
		System.out.println("drawArc");
	}

	@Override
	public void fillOval(int x, int y, int width, int height)
	{
		// TODO Auto-generated method stub
		System.out.println("drawOval");
	}

	@Override
	public void fillPolygon(int[] points, int[] points2, int points3)
	{
		// TODO Auto-generated method stub
		System.out.println("fillPolygon");
	}

	@Override
	public void fillRect(int x, int y, int width, int height)
	{
		fill(new Rectangle2D.Float(x, y, width, height));
	}

	@Override
	public void fillRoundRect(int x, int y, int width, int height,
	        int arcWidth, int arcHeight)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public Shape getClip()
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Rectangle getClipBounds()
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Color getColor()
	{
		return color;
	}

	@Override
	public Font getFont()
	{
		return font;
	}

	@Override
	public FontMetrics getFontMetrics(Font f)
	{
		// TODO Auto-generated method stub
		return null;
	}

	public void setClip(Collection<Shape> clips, Shape safeFrame)
	{
		GL gl = drawable.getGL();
		gl.glClear(GL_STENCIL_BUFFER_BIT);

		if (clips != null)
		{
			gl.glEnable(GL_STENCIL_TEST);

			final int mask = 3;
			int reference = 1;

			gl.glColorMask(false, false, false, false);
			gl.glStencilFunc(GL_ALWAYS, 1, mask);
			gl.glStencilOp(GL_KEEP, GL_KEEP, GL_REPLACE);

			for (Shape clip : clips)
			{
				fill(clip);
			}

			if (safeFrame != null)
			{
				gl.glStencilOp(GL_KEEP, GL_INCR, GL_INCR);
				gl.glStencilFunc(GL_ALWAYS, 0, mask);

				fill(safeFrame);
				reference++;
			}

			gl.glStencilOp(GL_KEEP, GL_KEEP, GL_KEEP);
			gl.glStencilFunc(GL_EQUAL, reference, mask);
			gl.glColorMask(true, true, true, true);

		}
		else
		{
			gl.glDisable(GL_STENCIL_TEST);
		}
	}

	@Override
	public void setClip(Shape clip)
	{
		setClip((clip == null) ? null : Collections.singleton(clip), null);
	}

	@Override
	public void setClip(int x, int y, int width, int height)
	{
		setClip(new Rectangle2D.Double(x, y, width, height));
	}

	@Override
	public void setColor(Color color)
	{
		if (!this.color.equals(color))
		{
			this.color = color;
			GL gl = drawable.getGL();
			GL2 gl2 = gl.getGL2();
			gl2.glColor4fv(color.getRGBComponents(new float[4]), 0);
		}
	}

	@Override
	public void setFont(Font font)
	{
		this.font = font;
	}

	@Override
	public void setPaintMode()
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void setXORMode(Color c1)
	{
		// TODO Auto-generated method stub

	}

	private static class TextKey
	{
		private String text;

		private Font font;

		public TextKey(String text, Font font)
		{
			super();
			this.text = text;
			this.font = font;
		}

		@Override
		public int hashCode()
		{
			return text.hashCode() ^ font.hashCode();
		}

		@Override
		public boolean equals(Object object)
		{
			if (object == this)
			{
				return true;
			}
			else if (object instanceof TextKey)
			{
				TextKey key = (TextKey) object;
				return text.equals(key.text) && font.equals(key.font);
			}
			else
			{
				return false;
			}
		}
	}
}
