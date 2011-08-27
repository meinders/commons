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

public class Matrix3f
{
	public static final Matrix3f IDENTITY = new Matrix3f();

	private float m00; // scale x

	private float m01; // shear x by y

	private float m02; // shear x by z

	private float m03; // translate x

	private float m10; // shear y by x

	private float m11; // scale y

	private float m12; // shear y by z

	private float m13; // translate y

	private float m20; // shear z by x

	private float m21; // shear z by y

	private float m22; // scale z

	private float m23; // translate z

	private float[] array;

	private boolean arrayNeedsUpdate;

	public Matrix3f()
	{
		this(1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f,
		        0.0f);
	}

	/**
	 *
	 * <pre>
	 *   | m00 , m01 , m02 , 0.0f |
	 *   | m10 , m11 , m12 , 0.0f |
	 *   | m20 , m21 , m22 , 0.0f |
	 *   | 0.0f , 0.0f , 0.0f , 1.0f |
	 * </pre>
	 */
	public Matrix3f(float m00, float m01, float m02, float m10, float m11,
	        float m12, float m20, float m21, float m22)
	{
		this(m00, m01, m02, 0.0f, m10, m11, m12, 0.0f, m20, m21, m22, 0.0f);
	}

	/**
	 *
	 * <pre>
	 *   | m00 , m01 , m02 , m03 |
	 *   | m10 , m11 , m12 , m13 |
	 *   | m20 , m21 , m22 , m23 |
	 *   | 0.0f , 0.0f , 0.0f , 1.0f |
	 * </pre>
	 */
	public Matrix3f(float m00, float m01, float m02, float m03, float m10,
	        float m11, float m12, float m13, float m20, float m21, float m22,
	        float m23)
	{
		super();
		this.m00 = m00;
		this.m01 = m01;
		this.m02 = m02;
		this.m03 = m03;
		this.m10 = m10;
		this.m11 = m11;
		this.m12 = m12;
		this.m13 = m13;
		this.m20 = m20;
		this.m21 = m21;
		this.m22 = m22;
		this.m23 = m23;

		array = null;
		arrayNeedsUpdate = false;
	}

	public Vector3f multiply(Vector3f v)
	{
		return new Vector3f(v.x * m00 + v.y * m01 + v.z * m02 + m03,//
		        v.x * m10 + v.y * m11 + v.z * m12 + m13,//
		        v.x * m20 + v.y * m21 + v.z * m22 + m23);
	}

	public Bounds3f multiply(Bounds3f bounds)
	{
		return new Bounds3f(multiply(bounds.minimum), multiply(bounds.maximum));
	}

	public Vector3f translation()
	{
		return new Vector3f(m03, m13, m23);
	}

	public void add(Vector3f translation)
	{
		m03 += translation.x;
		m13 += translation.y;
		m23 += translation.z;
		arrayNeedsUpdate = true;
	}

	/**
	 * Returns the matrix's coefficients as an array, sorted by column index
	 * (primary) and row index (secondary). This ordering is consistent with
	 * OpenGL.
	 *
	 * @return Matrix as an array, consistent with OpenGL.
	 */
	public float[] toArray()
	{
		if (array == null)
		{
			array = new float[] {
			        m00, m10, m20, 0.0f, m01, m11, m21, 0.0f, m02, m12, m22,
			        0.0f, m03, m13, m23, 1.0f
			};
		}
		else if (arrayNeedsUpdate)
		{
			array[0] = m00;
			array[1] = m10;
			array[2] = m20;
			array[3] = 0.0f;
			array[4] = m01;
			array[5] = m11;
			array[6] = m21;
			array[7] = 0.0f;
			array[8] = m02;
			array[9] = m12;
			array[10] = m22;
			array[11] = 0.0f;
			array[12] = m03;
			array[13] = m13;
			array[14] = m23;
			array[15] = 1.0f;
		}
		return array;
	}

	/**
	 * Returns a matrix representing the specified axis-angle rotation.
	 *
	 * @param axis the unit axis to rotate around
	 * @param angle the angle of rotation, in radians
	 *
	 * @return matrix representing the specified rotation
	 */
	// http://en.wikipedia.org/wiki/Rotation_matrix
	public static Matrix3f rotationMatrix(Vector3f axis, float angle)
	{
		float c = (float) Math.cos(angle);
		float s = (float) Math.sin(angle);
		float t = 1.0f - c;

		float xs = s * axis.x;
		float ys = s * axis.y;
		float zs = s * axis.z;

		float txx = t * axis.x * axis.x;
		float txy = t * axis.x * axis.y;
		float txz = t * axis.x * axis.z;
		float tyy = t * axis.y * axis.y;
		float tyz = t * axis.y * axis.z;
		float tzz = t * axis.z * axis.z;

		return new Matrix3f(//
		        txx + c, txy - zs, txz + ys,//
		        txy + zs, tyy + c, tyz - xs,//
		        txz - ys, tyz + xs, tzz + c);
	}

	public void inverse2()
	{
		float x00 = m00;
		float x01 = m10;
		float x02 = m20;
		float x03 = -m03 * x00 - m13 * x01 - m23 * x02;
		float x10 = m01;
		float x11 = m11;
		float x12 = m21;
		float x13 = -m03 * x10 - m13 * x11 - m23 * x12;
		float x20 = m02;
		float x21 = m12;
		float x22 = m22;
		float x23 = -m03 * x20 - m13 * x21 - m23 * x22;

		m00 = x00;
		m01 = x01;
		m02 = x02;
		m03 = x03;
		m10 = x10;
		m11 = x11;
		m12 = x12;
		m13 = x13;
		m20 = x20;
		m21 = x21;
		m22 = x22;
		m23 = x23;

		arrayNeedsUpdate = true;
	}

	public void inverse()
	{
		float det = determinant();

		float x00 = (m11 * m22 - m12 * m21) / det;
		float x01 = (m02 * m21 - m01 * m22) / det;
		float x02 = (m01 * m12 - m02 * m11) / det;
		float x03 = -x00 * m03 - x01 * m13 - x02 * m23;
		float x10 = (m12 * m20 - m10 * m22) / det;
		float x11 = (m00 * m22 - m02 * m20) / det;
		float x12 = (m02 * m10 - m00 * m12) / det;
		float x13 = -x10 * m03 - x11 * m13 - x12 * m23;
		float x20 = (m10 * m21 - m11 * m20) / det;
		float x21 = (m01 * m20 - m00 * m21) / det;
		float x22 = (m00 * m11 - m01 * m10) / det;
		float x23 = -x20 * m03 - x21 * m13 - x22 * m23;

		m00 = x00;
		m01 = x01;
		m02 = x02;
		m03 = x03;
		m10 = x10;
		m11 = x11;
		m12 = x12;
		m13 = x13;
		m20 = x20;
		m21 = x21;
		m22 = x22;
		m23 = x23;

		arrayNeedsUpdate = true;
	}

	public float determinant()
	{
		float result;
		result = m00 * (m11 * m22 - m12 * m21);
		result += m01 * (m12 * m20 - m10 * m22);
		result += m02 * (m10 * m21 - m11 * m20);
		return result;
	}

	public Matrix3f inversed()
	{
		Matrix3f result = new Matrix3f(m00, m01, m02, m03, m10, m11, m12, m13,
		        m20, m21, m22, m23);
		result.inverse();
		return result;
	}

	@Override
	public boolean equals(Object other)
	{
		return (other instanceof Matrix3f) && equals((Matrix3f) other);
	}

	public boolean equals(Matrix3f other)
	{
		return m00 == other.m00 && m01 == other.m01 && m02 == other.m02
		        && m03 == other.m03 && m10 == other.m10 && m11 == other.m11
		        && m12 == other.m12 && m13 == other.m13 && m20 == other.m20
		        && m21 == other.m21 && m22 == other.m22 && m23 == other.m23;
	}
}
