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

package com.github.meinders.common.math;

public class Plane {
	/**
	 * Returns the intersection point of the specified plane and ray.
	 *
	 * @param normal Normal of the plane.
	 * @param point Any point on the plane.
	 * @param from Source of the ray.
	 * @param to Target of the ray.
	 * @param infinite If <code>true</code>, the ray extends beyond
	 *            <code>to</code> towards infinity; otherwise, the ray ends in
	 *            <code>to</code>.
	 *
	 * @return Intersection point, if any.
	 *
	 * @see <a href="http://local.wasp.uwa.edu.au/~pbourke/geometry/linefacet/">
	 *      Determining whether a line segment intersects a 3 vertex facet</a>
	 */
	public static Vector3d rayIntersection(Vector3d normal, Vector3d point,
	        Vector3d from, Vector3d to, boolean infinite) {
		// System.out.println("normal = '" + normal + "'");
		// System.out.println("point = '" + point + "'");
		// System.out.println("from = '" + from + "'");
		// System.out.println("to = '" + to + "'");

		Vector3d p = from.difference(to);

		if (Vector3d.ZERO.equals(p))
			throw new IllegalArgumentException(
			        "from and to must not be equal: " + from + "," + to + ","
			                + p);

		double denominator = normal.dot(p);
		if (denominator == 0.0) { // coplanar
			return null;
		}

		double mu = (normal.dot(from) - normal.dot(point)) / denominator;
		// System.out.println("mu = '" + mu + "'");
		if (!infinite && (mu <= 0.0 || mu > 1.0)) {
			return null;
		}

		p.multiply(-mu);
		p.add(from);

		// System.out.println("p = '" + p + "'");

		return p;
	}

	public static boolean contains(Vector3d normal, Vector3d point,
	        Vector3d other) {
		return normal.dot(point) == normal.dot(other);
	}
}
