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

public class Bounds3f {
	public Vector3f minimum;

	public Vector3f maximum;

	public Bounds3f(Vector3f minimum, Vector3f maximum) {
		assert minimum.x <= maximum.x : minimum.x + " <= " + maximum.x;
		assert minimum.y <= maximum.y : minimum.y + " <= " + maximum.y;
		assert minimum.z <= maximum.z : minimum.z + " <= " + maximum.z;

		this.minimum = minimum;
		this.maximum = maximum;
	}

	public static Bounds3f createBounds(Vector3f... points) {
		Vector3f minimum = points[0];
		Vector3f maximum = points[0];

		for (Vector3f point : points) {
			minimum.minimum(point);
			maximum.maximum(point);
		}

		return new Bounds3f(minimum, maximum);
	}

	public boolean contains(Vector3f point) {
		return (minimum.x <= point.x) && (point.x <= maximum.x)
		        && (minimum.y <= point.y) && (point.y <= maximum.y)
		        && (minimum.z <= point.z) && (point.z <= maximum.z);
	}

	public boolean intersects(Bounds3f other) {
		return (minimum.x <= other.maximum.x) && (maximum.x >= other.minimum.x)
		        && (minimum.y <= other.maximum.y)
		        && (maximum.y >= other.minimum.y)
		        && (minimum.z <= other.maximum.z)
		        && (maximum.z >= other.minimum.z);
	}

	public Vector3f size() {
		return maximum.difference(minimum);
	}

	@Override
	public String toString() {
		return "Bounds[ " + minimum + " , " + maximum + " ]";
	}

	public Vector3f center() {
		return new Vector3f((maximum.x - minimum.x) / 2.0f,
		        (maximum.y - minimum.y) / 2.0f, (maximum.z - minimum.z) / 2.0f);
	}
}
