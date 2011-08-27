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

import javax.media.opengl.*;

import com.github.meinders.common.math.*;

public class GeoSphere extends GLShape
{
	public enum Type
	{
		OCTAHEDRAL, DODECAHEDRAL, ICOSAHEDRAL
	}

	public GeoSphere(Vector3f center, float radius, Type type, int subdivisions)
	{
		switch (type)
		{
		case OCTAHEDRAL:
			createOctahedralGeometry(center, radius, subdivisions);
			break;
		default:
			throw new UnsupportedOperationException("not implemented");
		}
	}

	private void createOctahedralGeometry(Vector3f center, float radius,
	        int subdivisions)
	{
		Primitive primitive = new Primitive(GL.GL_TRIANGLES);

		Vector3f p0 = new Vector3f(center.x, center.y, center.z + radius);
		Vector3f p1 = new Vector3f(center.x - radius, center.y, center.z);
		Vector3f p2 = new Vector3f(center.x, center.y - radius, center.z);
		Vector3f p3 = new Vector3f(center.x + radius, center.y, center.z);
		Vector3f p4 = new Vector3f(center.x, center.y + radius, center.z);
		Vector3f p5 = new Vector3f(center.x, center.y, center.z - radius);

		addSubdividedFace(primitive, p0, p1, p2, center, radius, subdivisions);
		addSubdividedFace(primitive, p0, p2, p3, center, radius, subdivisions);
		addSubdividedFace(primitive, p0, p3, p4, center, radius, subdivisions);
		addSubdividedFace(primitive, p0, p4, p1, center, radius, subdivisions);

		addSubdividedFace(primitive, p5, p2, p1, center, radius, subdivisions);
		addSubdividedFace(primitive, p5, p3, p2, center, radius, subdivisions);
		addSubdividedFace(primitive, p5, p4, p3, center, radius, subdivisions);
		addSubdividedFace(primitive, p5, p1, p4, center, radius, subdivisions);

		addPrimitive(primitive);
	}

	private void addSubdividedFace(Primitive primitive, Vector3f p0,
	        Vector3f p1, Vector3f p2, Vector3f center, float radius,
	        int subdivisions)
	{

		Vector3f dx = p2.difference(p1).product(1.0f / (subdivisions + 1));
		Vector3f dy = p1.difference(p0).product(1.0f / (subdivisions + 1));

		for (int y = 0; y < subdivisions + 1; y++)
		{
			for (int x = 0; x <= y; x++)
			{
				Vector3f s0 = dx.product(x);
				Vector3f s1 = dx.product(x);
				Vector3f s2 = dx.product(x + 1);
				s0.add(dy.product(y));
				s1.add(dy.product(y + 1));
				s2.add(dy.product(y + 1));
				s0.add(p0);
				s1.add(p0);
				s2.add(p0);

				Vector3f n0 = s0.difference(center);
				Vector3f n1 = s1.difference(center);
				Vector3f n2 = s2.difference(center);
				n0.normalize();
				n1.normalize();
				n2.normalize();

				s0 = n0.product(radius);
				s1 = n1.product(radius);
				s2 = n2.product(radius);
				s0.add(center);
				s1.add(center);
				s2.add(center);

				TexCoord2f t0 = new TexCoord2f((s0.x + radius) / (2 * radius),
				        (s0.y + radius) / (2 * radius));
				TexCoord2f t1 = new TexCoord2f((s1.x + radius) / (2 * radius),
				        (s1.y + radius) / (2 * radius));
				TexCoord2f t2 = new TexCoord2f((s2.x + radius) / (2 * radius),
				        (s2.y + radius) / (2 * radius));

				primitive.addVertex(new Vertex(s0, n0, t0));
				primitive.addVertex(new Vertex(s1, n1, t1));
				primitive.addVertex(new Vertex(s2, n2, t2));
			}

			for (int x = 1; x <= y; x++)
			{
				Vector3f s0 = dx.product(x - 1);
				Vector3f s1 = dx.product(x);
				Vector3f s2 = dx.product(x);
				s0.add(dy.product(y));
				s1.add(dy.product(y));
				s2.add(dy.product(y + 1));
				s0.add(p0);
				s1.add(p0);
				s2.add(p0);

				Vector3f n0 = s0.difference(center);
				Vector3f n1 = s1.difference(center);
				Vector3f n2 = s2.difference(center);
				n0.normalize();
				n1.normalize();
				n2.normalize();

				s0 = n0.product(radius);
				s1 = n1.product(radius);
				s2 = n2.product(radius);
				s0.add(center);
				s1.add(center);
				s2.add(center);

				TexCoord2f t0 = new TexCoord2f((s0.x + radius) / (2 * radius),
				        (s0.y + radius) / (2 * radius));
				TexCoord2f t1 = new TexCoord2f((s1.x + radius) / (2 * radius),
				        (s1.y + radius) / (2 * radius));
				TexCoord2f t2 = new TexCoord2f((s2.x + radius) / (2 * radius),
				        (s2.y + radius) / (2 * radius));

				primitive.addVertex(new Vertex(s0, n0, t0));
				primitive.addVertex(new Vertex(s2, n2, t2));
				primitive.addVertex(new Vertex(s1, n1, t1));
			}
		}
	}
}
