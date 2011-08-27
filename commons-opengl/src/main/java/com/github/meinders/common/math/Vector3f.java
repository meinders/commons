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

public class Vector3f {
	public static final Vector3f ZERO = new Vector3f();

	public static final Vector3f X_AXIS = new Vector3f(1.0f, 0.0f, 0.0f);
	public static final Vector3f Y_AXIS = new Vector3f(0.0f, 1.0f, 0.0f);
	public static final Vector3f Z_AXIS = new Vector3f(0.0f, 0.0f, 1.0f);

	public static final Vector3f X_AXIS_NEG = new Vector3f(-1.0f, 0.0f, 0.0f);
	public static final Vector3f Y_AXIS_NEG = new Vector3f(0.0f, -1.0f, 0.0f);
	public static final Vector3f Z_AXIS_NEG = new Vector3f(0.0f, 0.0f, -1.0f);

	public float x;

	public float y;

	public float z;

	private float[] array = null;

	public Vector3f() {
		this(0.0f, 0.0f, 0.0f);
	}

	public Vector3f(float x, float y, float z) {
		super();
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public Vector3f(Vector3f other) {
		this(other.x, other.y, other.z);
	}

	public Vector3f(float[] values) {
		this(values[0], values[1], values[2]);
		this.array = values;
	}

	public Vector3f(float[] values, int offset) {
		this(values[offset + 0], values[offset + 1], values[offset + 2]);
	}

	public Vector3f(double x, double y, double z) {
		super();
		this.x = (float) x;
		this.y = (float) y;
		this.z = (float) z;
	}

	public Vector3f(double[] values) {
		this(values[0], values[1], values[2]);
	}

	public Vector3f(double[] values, int offset) {
		this(values[offset + 0], values[offset + 1], values[offset + 2]);
	}

	public float length() {
		return (float) Math.sqrt(x * x + y * y + z * z);
	}

	public void add(Vector3f other) {
		x += other.x;
		y += other.y;
		z += other.z;
	}

	public Vector3f sum(Vector3f other) {
		return new Vector3f(x + other.x, y + other.y, z + other.z);
	}

	public void subtract(Vector3f other) {
		x -= other.x;
		y -= other.y;
		z -= other.z;
	}

	public Vector3f difference(Vector3f other) {
		return new Vector3f(x - other.x, y - other.y, z - other.z);
	}

	public void multiply(float scalar) {
		x *= scalar;
		y *= scalar;
		z *= scalar;
	}

	public void multiply(Vector3f vector) {
		x *= vector.x;
		y *= vector.y;
		z *= vector.z;
	}

	public Vector3f product(float scalar) {
		return new Vector3f(x * scalar, y * scalar, z * scalar);
	}

	public Vector3f product(Vector3f vector) {
		return new Vector3f(x * vector.x, y * vector.y, z * vector.z);
	}

	// http://en.wikipedia.org/wiki/Cross_product
	public void cross(Vector3f other) {
		float tempX = x;
		x = y * other.z - z * other.y;
		float tempY = y;
		y = z * other.x - tempX * other.z;
		z = tempX * other.y - tempY * other.x;
	}

	// http://en.wikipedia.org/wiki/Cross_product
	public Vector3f crossProduct(Vector3f other) {
		return new Vector3f(y * other.z - z * other.y, z * other.x - x
		        * other.z, x * other.y - y * other.x);
	}

	public float dot(Vector3f other) {
		return x * other.x + y * other.y + z * other.z;
	}

	public void normalize() {
		float length = length();
		if (length != 0.0) {
			x /= length;
			y /= length;
			z /= length;
		}
	}

	public Vector3f normalized() {
		float length = length();
		if (length == 0.0) {
			return this;
		} else {
			return new Vector3f(x / length, y / length, z / length);
		}
	}

	public Vector3f rotate(Vector3f axis, float angle) {
		Matrix3f rotation = Matrix3f.rotationMatrix(axis, angle);
		return rotation.multiply(this);
	}

	public Vector3f rotateX(float angle) {
		float cos = (float) Math.cos(Math.toRadians(angle));
		float sin = (float) Math.sin(Math.toRadians(angle));
		return new Vector3f(x, y * cos + z * sin, z * cos - y * sin);
	}

	public Vector3f rotateY(float angle) {
		float cos = (float) Math.cos(Math.toRadians(angle));
		float sin = (float) Math.sin(Math.toRadians(angle));
		return new Vector3f(x * cos - z * sin, y, x * sin + z * cos);
	}

	public Vector3f rotateZ(float angle) {
		float cos = (float) Math.cos(Math.toRadians(angle));
		float sin = (float) Math.sin(Math.toRadians(angle));
		return new Vector3f(x * cos + y * sin, y * cos - x * sin, z);
	}

	/**
	 * Converts the vector from cartesian to spherical coordinates, returning
	 * the result.
	 *
	 * @return A representation of the vector using spherical coordinates
	 *         [&rho;, &phi;, &theta;], i.e. the radius, zenith and azimuth,
	 *         respectively.
	 */
	// http://en.wikipedia.org/wiki/Spherical_coordinate_system
	public Vector3f toSpherical() {
		float rho = length();
		float phi = (float) Math.toDegrees(Math.acos(z / rho));
		float theta = (float) Math.toDegrees(Math.atan2(y, x));
		return new Vector3f(rho, phi, theta);
	}

	// {x}=\rho \, \sin\phi \, \cos\theta \quad
	// {y}=\rho \, \sin\phi \, \sin\theta \quad
	// {z}=\rho \, \cos\phi \quad
	public Vector3f toCartesian() {
		float phi = (float) Math.toRadians(y);
		float cosPhi = (float) Math.cos(phi);
		float sinPhi = (float) Math.sin(phi);

		float theta = (float) Math.toRadians(z);
		float cosTheta = (float) Math.cos(theta);
		float sinTheta = (float) Math.sin(theta);

		return new Vector3f(x * sinPhi * cosTheta, x * sinPhi * sinTheta, x
		        * cosPhi);
	}

	public float[] toArray() {
		float[] array = this.array;
		if (array == null) {
			array = new float[] { x, y, z };
			this.array = array;
		}
		return array;
	}

	public void maximum(Vector3f other) {
		// NOTE: Don't use Math.min because of its NaN handling!
		x = x >= other.x ? x : other.x;
		y = y >= other.y ? y : other.y;
		z = z >= other.z ? z : other.z;
	}

	public void minimum(Vector3f other) {
		// NOTE: Don't use Math.min because of its NaN handling!
		x = x <= other.x ? x : other.x;
		y = y <= other.y ? y : other.y;
		z = z <= other.z ? z : other.z;
	}

	@Override
	public String toString() {
		return "[ " + x + " , " + y + " , " + z + " ]";
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this) {
			return true;
		} else if (obj instanceof Vector3f) {
			Vector3f other = (Vector3f) obj;
			return (x == other.x) && (y == other.y) && (z == other.z);
		} else {
			return false;
		}
	}

	@Override
	public int hashCode() {
		return Float.floatToIntBits(x) ^ Float.floatToIntBits(y)
		        ^ Float.floatToIntBits(z);
	}
}
