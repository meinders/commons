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

public class Vector2f {
	public float x;
	public float y;

	public Vector2f(Vector2f original) {
		super();
		x = original.x;
		y = original.y;
	}

	public Vector2f(float[] original) {
		super();
		x = original[0];
		y = original[1];
	}

	public Vector2f(float x, float y) {
		super();
		this.x = x;
		this.y = y;
	}

	public Vector2f(Vector2d original) {
		super();
		x = (float) original.x;
		y = (float) original.y;
	}

	public Vector2f(double[] original) {
		super();
		x = (float) original[0];
		y = (float) original[1];
	}

	public Vector2f(double x, double y) {
		this((float) x, (float) y);
	}

	public float length() {
		return (float) Math.sqrt(lengthSq());
	}

	public float lengthSq() {
		return x * x + y * y;
	}

	public void normalize() {
		float length = length();
		x /= length;
		y /= length;
	}

	public Vector2f normalized() {
		float length = length();
		return new Vector2f(x / length, y / length);
	}

	public float dot(Vector2f operand) {
		return x * operand.x + y * operand.y;
	}

	public float cross(Vector2f operand) {
		return x * operand.y - y * operand.x;
	}

	public void add(float operand) {
		x += operand;
		y += operand;
	}

	public void add(Vector2f operand) {
		x += operand.x;
		y += operand.y;
	}

	public Vector2f sum(Vector2f operand) {
		return new Vector2f(x + operand.x, y + operand.y);
	}

	public Vector2f sum(Vector2f... operands) {
		Vector2f result = new Vector2f(x, y);
		for (Vector2f operand : operands) {
			result.add(operand);
		}
		return result;
	}

	public void subtract(float operand) {
		x -= operand;
		y -= operand;
	}

	public void subtract(Vector2f operand) {
		x -= operand.x;
		y -= operand.y;
	}

	public Vector2f difference(Vector2f operand) {
		return new Vector2f(x - operand.x, y - operand.y);
	}

	public Vector2f difference(Vector2f... operands) {
		Vector2f result = new Vector2f(x, y);
		for (Vector2f operand : operands) {
			result.subtract(operand);
		}
		return result;
	}

	public void multiply(float operand) {
		x *= operand;
		y *= operand;
	}

	public void multiply(Vector2f operand) {
		x *= operand.x;
		y *= operand.y;
	}

	public void divide(float operand) {
		x /= operand;
		y /= operand;
	}

	public void divide(Vector2f operand) {
		x /= operand.x;
		y /= operand.y;
	}

	public void rotateCCW() {
		float t;
		t = x;
		x = -y;
		y = t;
	}

	public void rotateCW() {
		float t;
		t = x;
		x = y;
		y = -t;
	}

	public void rotate(float a) {
		float cos = (float) Math.cos(a);
		float sin = (float) Math.sin(a);

		float t;
		t = x * cos - y * sin;
		y = x * sin + y * cos;
		x = t;
	}
}
