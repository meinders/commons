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

public class Vector3d implements DoubleVector<Vector3d>
{
	public static final Vector3d ZERO = new Vector3d(0.0, 0.0, 0.0);

	public static final Vector3d X_AXIS = new Vector3d(1.0, 0.0, 0.0);
	public static final Vector3d Y_AXIS = new Vector3d(0.0, 1.0, 0.0);
	public static final Vector3d Z_AXIS = new Vector3d(0.0, 0.0, 1.0);

	public static final Vector3d X_AXIS_NEG = new Vector3d(-1.0, 0.0, 0.0);
	public static final Vector3d Y_AXIS_NEG = new Vector3d(0.0, -1.0, 0.0);
	public static final Vector3d Z_AXIS_NEG = new Vector3d(0.0, 0.0, -1.0);

	public double x;

	public double y;

	public double z;

	private double[] array = null;

	public Vector3d()
	{
		this(0.0, 0.0, 0.0);
	}

	public Vector3d(double x, double y, double z)
	{
		super();
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public Vector3d(Vector3d other)
	{
		this(other.x, other.y, other.z);
	}

	public Vector3d(double[] values)
	{
		this(values[0], values[1], values[2]);
		this.array = values;
	}

	public Vector3d(double[] values, int offset)
	{
		this(values[offset + 0], values[offset + 1], values[offset + 2]);
	}

	public double length()
	{
		return Math.sqrt(lengthSq());
	}

	public double lengthSq()
	{
		return x * x + y * y + z * z;
	}

	public void add(Vector3d other)
	{
		x += other.x;
		y += other.y;
		z += other.z;
	}

	public Vector3d sum(Vector3d other)
	{
		return new Vector3d(x + other.x, y + other.y, z + other.z);
	}

	public void subtract(Vector3d other)
	{
		x -= other.x;
		y -= other.y;
		z -= other.z;
	}

	public Vector3d difference(Vector3d other)
	{
		return new Vector3d(x - other.x, y - other.y, z - other.z);
	}

	public void multiply(double scalar)
	{
		x *= scalar;
		y *= scalar;
		z *= scalar;
	}

	public void multiply(Vector3d vector)
	{
		x *= vector.x;
		y *= vector.y;
		z *= vector.z;
	}

	public Vector3d product(double scalar)
	{
		return new Vector3d(x * scalar, y * scalar, z * scalar);
	}

	public Vector3d product(Vector3d vector)
	{
		return new Vector3d(x * vector.x, y * vector.y, z * vector.z);
	}

	// http://en.wikipedia.org/wiki/Cross_product
	public void cross(Vector3d other)
	{
		double tempX = x;
		x = y * other.z - z * other.y;
		double tempY = y;
		y = z * other.x - tempX * other.z;
		z = tempX * other.y - tempY * other.x;
	}

	// http://en.wikipedia.org/wiki/Cross_product
	public Vector3d crossProduct(Vector3d other)
	{
		return new Vector3d(y * other.z - z * other.y, z * other.x - x
		        * other.z, x * other.y - y * other.x);
	}

	public double dot(Vector3d other)
	{
		return x * other.x + y * other.y + z * other.z;
	}

	public void normalize()
	{
		double length = length();
		if (length != 0.0)
		{
			x /= length;
			y /= length;
			z /= length;
		}
	}

	public Vector3d normalized()
	{
		double length = length();
		if (length == 0.0)
		{
			return this;
		}
		else
		{
			return new Vector3d(x / length, y / length, z / length);
		}
	}

	/**
	 * Returns the result of rotating the vector around the given axis.
	 *
	 * @param axis the unit axis to rotate around
	 * @param angle the angle of rotation, in radians
	 *
	 * @return vector rotated around the axis by the given angle
	 */
	public Vector3d rotate(Vector3d axis, double angle)
	{
		Matrix3d rotation = Matrix3d.rotationMatrix(axis, angle);
		return rotation.multiply(this);
	}

	public Vector3d rotateX(double angle)
	{
		double cos = Math.cos(angle);
		double sin = Math.sin(angle);
		return new Vector3d(x, y * cos + z * sin, z * cos - y * sin);
	}

	public Vector3d rotateY(double angle)
	{
		double cos = Math.cos(angle);
		double sin = Math.sin(angle);
		return new Vector3d(x * cos - z * sin, y, x * sin + z * cos);
	}

	public Vector3d rotateZ(double angle)
	{
		double cos = Math.cos(angle);
		double sin = Math.sin(angle);
		return new Vector3d(x * cos + y * sin, y * cos - x * sin, z);
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
	public Vector3d toSpherical()
	{
		double rho = length();
		double phi = Math.toDegrees(Math.acos(z / rho));
		double theta = Math.toDegrees(Math.atan2(y, x));
		return new Vector3d(rho, phi, theta);
	}

	// {x}=\rho \, \sin\phi \, \cos\theta \quad
	// {y}=\rho \, \sin\phi \, \sin\theta \quad
	// {z}=\rho \, \cos\phi \quad
	public Vector3d toCartesian()
	{
		double phi = Math.toRadians(y);
		double cosPhi = Math.cos(phi);
		double sinPhi = Math.sin(phi);

		double theta = Math.toRadians(z);
		double cosTheta = Math.cos(theta);
		double sinTheta = Math.sin(theta);

		return new Vector3d(x * sinPhi * cosTheta, x * sinPhi * sinTheta, x
		        * cosPhi);
	}

	public double[] toArray()
	{
		double[] array = this.array;
		if (array == null)
		{
			array = new double[] { x, y, z };
			this.array = array;
		}
		return array;
	}

	public void maximum(Vector3d other)
	{
		// NOTE: Don't use Math.min because of its NaN handling!
		x = x >= other.x ? x : other.x;
		y = y >= other.y ? y : other.y;
		z = z >= other.z ? z : other.z;
	}

	public void minimum(Vector3d other)
	{
		// NOTE: Don't use Math.min because of its NaN handling!
		x = x <= other.x ? x : other.x;
		y = y <= other.y ? y : other.y;
		z = z <= other.z ? z : other.z;
	}

	@Override
	public String toString()
	{
		return "[ " + x + " , " + y + " , " + z + " ]";
	}

	@Override
	public boolean equals(Object obj)
	{
		if (obj == this)
		{
			return true;
		}
		else if (obj instanceof Vector3d)
		{
			Vector3d other = (Vector3d) obj;
			return (x == other.x) && (y == other.y) && (z == other.z);
		}
		else
		{
			return false;
		}
	}

	@Override
	public int hashCode()
	{
		long hash = Double.doubleToLongBits(x) ^ Double.doubleToLongBits(y)
		        ^ Double.doubleToLongBits(z);
		return (int) hash ^ (int) (hash >> 32);
	}
}
