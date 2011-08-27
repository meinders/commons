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

public class Vector2d implements DoubleVector<Vector2d>
{
	public double x;
	public double y;

	public Vector2d()
	{
		this(0.0, 0.0);
	}

	public Vector2d(Vector2d original)
	{
		this(original.x, original.y);
	}

	public Vector2d(double[] original)
	{
		this(original[0], original[1]);
	}

	public Vector2d(double x, double y)
	{
		super();
		this.x = x;
		this.y = y;
	}

	public double length()
	{
		return Math.sqrt(lengthSq());
	}

	public double lengthSq()
	{
		return x * x + y * y;
	}

	public void normalize()
	{
		double length = length();
		x /= length;
		y /= length;
	}

	public Vector2d normalized()
	{
		double length = length();
		return new Vector2d(x / length, y / length);
	}

	public void invert()
	{
		x = -x;
		y = -y;
	}

	public double dot(Vector2d operand)
	{
		return x * operand.x + y * operand.y;
	}

	public double cross(Vector2d operand)
	{
		return x * operand.y - y * operand.x;
	}

	public void add(double operand)
	{
		x += operand;
		y += operand;
	}

	public void add(Vector2d operand)
	{
		x += operand.x;
		y += operand.y;
	}

	public Vector2d sum(Vector2d operand)
	{
		return new Vector2d(x + operand.x, y + operand.y);
	}

	public Vector2d sum(Vector2d... operands)
	{
		Vector2d result = new Vector2d(x, y);
		for (Vector2d operand : operands)
		{
			result.add(operand);
		}
		return result;
	}

	public void subtract(double operand)
	{
		x -= operand;
		y -= operand;
	}

	public void subtract(Vector2d operand)
	{
		x -= operand.x;
		y -= operand.y;
	}

	public Vector2d difference(Vector2d operand)
	{
		return new Vector2d(x - operand.x, y - operand.y);
	}

	public Vector2d difference(Vector2d... operands)
	{
		Vector2d result = new Vector2d(x, y);
		for (Vector2d operand : operands)
		{
			result.subtract(operand);
		}
		return result;
	}

	public void multiply(double operand)
	{
		x *= operand;
		y *= operand;
	}

	public void multiply(Vector2d operand)
	{
		x *= operand.x;
		y *= operand.y;
	}

	public void divide(double operand)
	{
		x /= operand;
		y /= operand;
	}

	public void divide(Vector2d operand)
	{
		x /= operand.x;
		y /= operand.y;
	}

	public void rotateCCW()
	{
		double t;
		t = x;
		x = -y;
		y = t;
	}

	public void rotateCW()
	{
		double t;
		t = x;
		x = y;
		y = -t;
	}

	public void rotate(double a)
	{
		double cos = Math.cos(a);
		double sin = Math.sin(a);

		double t;
		t = x * cos - y * sin;
		y = x * sin + y * cos;
		x = t;
	}

	@Override
	public Vector2d product(double scalar)
	{
		return new Vector2d(x * scalar, y * scalar);
	}

	@Override
	public double[] toArray()
	{
		return new double[] { x, y };
	}

	@Override
	public void maximum(Vector2d other)
	{
		// NOTE: Don't use Math.min because of its NaN handling!
		x = x >= other.x ? x : other.x;
		y = y >= other.y ? y : other.y;
	}

	@Override
	public void minimum(Vector2d other)
	{
		// NOTE: Don't use Math.min because of its NaN handling!
		x = x <= other.x ? x : other.x;
		y = y <= other.y ? y : other.y;
	}

	@Override
	public Vector2d product(Vector2d vector)
	{
		return new Vector2d(x * vector.x, y * vector.y);
	}

	@Override
	public Vector2d toCartesian()
	{
		return new Vector2d(x * Math.cos(y), x * Math.sin(y));
	}

	@Override
	public Vector2d toSpherical()
	{
		return new Vector2d(length(), Math.atan2(y, x));
	}
}
