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
import java.awt.geom.*;
import javax.media.opengl.glu.*;

import com.github.meinders.common.math.*;

public class Tesselator {
	private GLShape result;

	private GLShapeBuilder shapeBuilder;

	public Tesselator() {
		this(new GLShape());
	}

	public Tesselator(GLShape result) {
		this.result = result;
		shapeBuilder = new GLShapeBuilder();
	}

	public GLShape getResult() {
		return result;
	}

	public void clear() {
		result = new GLShape();
	}

	public void tesselate(Shape shape) {
		PathIterator pathIterator = shape.getPathIterator(null, 0.5);

		if (!pathIterator.isDone()) {
			GLU glu = new GLU();
			GLUtessellator tessellator = glu.gluNewTess();
			glu.gluTessCallback(tessellator, GLU.GLU_TESS_BEGIN, shapeBuilder);
			glu.gluTessCallback(tessellator, GLU.GLU_TESS_END, shapeBuilder);
			glu.gluTessCallback(tessellator, GLU.GLU_TESS_VERTEX, shapeBuilder);
			glu.gluTessCallback(tessellator, GLU.GLU_TESS_COMBINE, shapeBuilder);
			glu.gluTessCallback(tessellator, GLU.GLU_TESS_ERROR, shapeBuilder);

			// Shape is a 2D shape in the XY plane, so its normal is always Z+.
			glu.gluTessNormal(tessellator, 0.0, 0.0, 1.0);

			switch (pathIterator.getWindingRule()) {
			default:
			case PathIterator.WIND_EVEN_ODD:
				glu.gluTessProperty(tessellator, GLU.GLU_TESS_WINDING_RULE,
				        GLU.GLU_TESS_WINDING_ODD);
				break;
			case PathIterator.WIND_NON_ZERO:
				glu.gluTessProperty(tessellator, GLU.GLU_TESS_WINDING_RULE,
				        GLU.GLU_TESS_WINDING_NONZERO);
			}

			glu.gluTessBeginPolygon(tessellator, null);

			double[] vertex;
			double[] start = null;
			double[] coords = new double[6];
			do {
				int type = pathIterator.currentSegment(coords);

				switch (type) {
				case PathIterator.SEG_CLOSE:
					glu.gluTessVertex(tessellator, start, 0, start);
					glu.gluTessEndContour(tessellator);
					start = null;
					break;

				case PathIterator.SEG_MOVETO:
					if (start != null) {
						glu.gluTessEndContour(tessellator);
					}
					start = new double[3];
					start[0] = coords[0];
					start[1] = coords[1];
					start[2] = 0.0;

					glu.gluTessBeginContour(tessellator);

					vertex = new double[3];
					vertex[0] = coords[0];
					vertex[1] = coords[1];
					vertex[2] = 0.0;
					glu.gluTessVertex(tessellator, vertex, 0, vertex);
					break;

				case PathIterator.SEG_LINETO:
					vertex = new double[3];
					vertex[0] = coords[0];
					vertex[1] = coords[1];
					vertex[2] = 0.0;
					glu.gluTessVertex(tessellator, vertex, 0, vertex);
					break;
				}

				pathIterator.next();
			} while (!pathIterator.isDone());

			if (start != null) {
				glu.gluTessEndContour(tessellator);
			}

			glu.gluTessEndPolygon(tessellator);
			glu.gluDeleteTess(tessellator);
		}
	}

	private class GLShapeBuilder extends GLUtessellatorCallbackAdapter {
		private Primitive primitive;

		public GLShapeBuilder() {
			primitive = null;
		}

		@Override
		public void begin(int type) {
			primitive = new Primitive(type);
		}

		@Override
		public void end() {
			if (!primitive.getVertices().isEmpty()) {
				result.addPrimitive(primitive);
			}
		}

		@Override
		public void vertex(Object vertexData) {
			double[] position = (double[]) vertexData;
			primitive.addVertex(new Vertex(new Vector3f((float) position[0],
			        (float) position[1], 0.0f)));
		}

		@Override
		public void combine(double[] coords, Object[] data, float[] weights,
		        Object[] outData) {
			double[] result = new double[3];
			for (int i = 0; i < data.length; i++) {
				double[] vertexCoords = (double[]) data[i];
				if (vertexCoords != null) {
					double weight = weights[i];
					result[0] += weight * vertexCoords[0];
					result[1] += weight * vertexCoords[1];
					result[2] += weight * vertexCoords[2];
				}
			}
			outData[0] = result;
		}

		@Override
		public void error(int errnum) {
			System.err.println("tessellator error "
			        + new GLU().gluErrorString(errnum));
		}
	}
}
