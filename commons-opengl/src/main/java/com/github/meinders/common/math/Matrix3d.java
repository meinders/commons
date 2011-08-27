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

public class Matrix3d
{
	public static final Matrix3f IDENTITY = new Matrix3f();

	private double m00; // scale x

	private double m01; // shear x by y

	private double m02; // shear x by z

	private double m03; // translate x

	private double m10; // shear y by x

	private double m11; // scale y

	private double m12; // shear y by z

	private double m13; // translate y

	private double m20; // shear z by x

	private double m21; // shear z by y

	private double m22; // scale z

	private double m23; // translate z

	private double[] array;

	private boolean arrayNeedsUpdate;

	public Matrix3d()
	{
		this(1.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0);
	}

	/**
	 *
	 * <pre>
	 *   | m00 , m01 , m02 , 0.0 |
	 *   | m10 , m11 , m12 , 0.0 |
	 *   | m20 , m21 , m22 , 0.0 |
	 *   | 0.0 , 0.0 , 0.0 , 1.0 |
	 * </pre>
	 */
	public Matrix3d(double m00, double m01, double m02, double m10, double m11,
	        double m12, double m20, double m21, double m22)
	{
		this(m00, m01, m02, 0.0, m10, m11, m12, 0.0, m20, m21, m22, 0.0);
	}

	/**
	 *
	 * <pre>
	 *   | m00 , m01 , m02 , m03 |
	 *   | m10 , m11 , m12 , m13 |
	 *   | m20 , m21 , m22 , m23 |
	 *   | 0.0 , 0.0 , 0.0 , 1.0 |
	 * </pre>
	 */
	public Matrix3d(double m00, double m01, double m02, double m03, double m10,
	        double m11, double m12, double m13, double m20, double m21,
	        double m22, double m23)
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

	/**
	 * <pre>
	 *  | A[0] , A[4] , A[8] , A[12] |
	 *  | A[1] , A[5] , A[9] , A[13] |
	 *  | A[2] , A[6] , A[10], A[14] |
	 *  | 0.0  , 0.0  , 0.0  , 1.0   |
	 * </pre>
	 *
	 * @param array Array to create the matrix from, as specified above. The
	 *            array is reserved for reuse by {@link #toArray()} and should
	 *            not be modified externally.
	 */
	public Matrix3d(double[] array)
	{
		m00 = array[0];
		m10 = array[1];
		m20 = array[2];
		array[3] = 0.0;
		m01 = array[4];
		m11 = array[5];
		m21 = array[6];
		array[7] = 0.0;
		m02 = array[8];
		m12 = array[9];
		m22 = array[10];
		array[11] = 0.0;
		m03 = array[12];
		m13 = array[13];
		m23 = array[14];
		array[15] = 1.0;

		this.array = array;
		arrayNeedsUpdate = false;
	}

	public Matrix3d product(Matrix3d m)
	{
		double m00 = this.m00 * m.m00 + this.m10 * m.m01 + this.m20 * m.m02;
		double m01 = this.m01 * m.m00 + this.m11 * m.m01 + this.m21 * m.m02;
		double m02 = this.m02 * m.m00 + this.m12 * m.m01 + this.m22 * m.m02;
		double m03 = this.m03 * m.m00 + this.m13 * m.m01 + this.m23 * m.m02
		        + m.m03;
		double m10 = this.m00 * m.m10 + this.m10 * m.m11 + this.m20 * m.m12;
		double m11 = this.m01 * m.m10 + this.m11 * m.m11 + this.m21 * m.m12;
		double m12 = this.m02 * m.m10 + this.m12 * m.m11 + this.m22 * m.m12;
		double m13 = this.m03 * m.m10 + this.m13 * m.m11 + this.m23 * m.m12
		        + m.m13;
		double m20 = this.m00 * m.m20 + this.m10 * m.m21 + this.m20 * m.m22;
		double m21 = this.m01 * m.m20 + this.m11 * m.m21 + this.m21 * m.m22;
		double m22 = this.m02 * m.m20 + this.m12 * m.m21 + this.m22 * m.m22;
		double m23 = this.m03 * m.m20 + this.m13 * m.m21 + this.m23 * m.m22
		        + m.m23;
		return new Matrix3d(m00, m01, m02, m03, m10, m11, m12, m13, m20, m21,
		        m22, m23);
	}

	public Vector3d multiply(Vector3d v)
	{
		return new Vector3d(v.x * m00 + v.y * m01 + v.z * m02 + m03,//
		        v.x * m10 + v.y * m11 + v.z * m12 + m13,//
		        v.x * m20 + v.y * m21 + v.z * m22 + m23);
	}

	public Bounds3d multiply(Bounds3d bounds)
	{
		return new Bounds3d(multiply(bounds.minimum), multiply(bounds.maximum));
	}

	public Vector3d translation()
	{
		return new Vector3d(m03, m13, m23);
	}

	public void add(Vector3d translation)
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
	public double[] toArray()
	{
		if (array == null)
		{
			array = new double[] {
			        m00, m10, m20, 0.0, m01, m11, m21, 0.0, m02, m12, m22, 0.0,
			        m03, m13, m23, 1.0
			};
		}
		else if (arrayNeedsUpdate)
		{
			array[0] = m00;
			array[1] = m10;
			array[2] = m20;
			array[3] = 0.0;
			array[4] = m01;
			array[5] = m11;
			array[6] = m21;
			array[7] = 0.0;
			array[8] = m02;
			array[9] = m12;
			array[10] = m22;
			array[11] = 0.0;
			array[12] = m03;
			array[13] = m13;
			array[14] = m23;
			array[15] = 1.0;
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
	public static Matrix3d rotationMatrix(Vector3d axis, double angle)
	{
		double c = Math.cos(angle);
		double s = Math.sin(angle);
		double t = 1.0 - c;

		double xs = s * axis.x;
		double ys = s * axis.y;
		double zs = s * axis.z;

		double txx = t * axis.x * axis.x;
		double txy = t * axis.x * axis.y;
		double txz = t * axis.x * axis.z;
		double tyy = t * axis.y * axis.y;
		double tyz = t * axis.y * axis.z;
		double tzz = t * axis.z * axis.z;

		return new Matrix3d(//
		        txx + c, txy - zs, txz + ys,//
		        txy + zs, tyy + c, tyz - xs,//
		        txz - ys, tyz + xs, tzz + c);
	}

	public void inverse2()
	{
		double x00 = m00;
		double x01 = m10;
		double x02 = m20;
		double x03 = -m03 * x00 - m13 * x01 - m23 * x02;
		double x10 = m01;
		double x11 = m11;
		double x12 = m21;
		double x13 = -m03 * x10 - m13 * x11 - m23 * x12;
		double x20 = m02;
		double x21 = m12;
		double x22 = m22;
		double x23 = -m03 * x20 - m13 * x21 - m23 * x22;

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
		double det = determinant();

		double x00 = (m11 * m22 - m12 * m21) / det;
		double x01 = (m02 * m21 - m01 * m22) / det;
		double x02 = (m01 * m12 - m02 * m11) / det;
		double x03 = -x00 * m03 - x01 * m13 - x02 * m23;
		double x10 = (m12 * m20 - m10 * m22) / det;
		double x11 = (m00 * m22 - m02 * m20) / det;
		double x12 = (m02 * m10 - m00 * m12) / det;
		double x13 = -x10 * m03 - x11 * m13 - x12 * m23;
		double x20 = (m10 * m21 - m11 * m20) / det;
		double x21 = (m01 * m20 - m00 * m21) / det;
		double x22 = (m00 * m11 - m01 * m10) / det;
		double x23 = -x20 * m03 - x21 * m13 - x22 * m23;

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

	public double determinant()
	{
		double result;
		result = m00 * (m11 * m22 - m12 * m21);
		result += m01 * (m12 * m20 - m10 * m22);
		result += m02 * (m10 * m21 - m11 * m20);
		return result;
	}

	public Matrix3d inversed()
	{
		Matrix3d result = new Matrix3d(m00, m01, m02, m03, m10, m11, m12, m13,
		        m20, m21, m22, m23);
		result.inverse();
		return result;
	}

	@Override
	public boolean equals(Object other)
	{
		return (other instanceof Matrix3d) && equals((Matrix3d) other);
	}

	public boolean equals(Matrix3d other)
	{
		return m00 == other.m00 && m01 == other.m01 && m02 == other.m02
		        && m03 == other.m03 && m10 == other.m10 && m11 == other.m11
		        && m12 == other.m12 && m13 == other.m13 && m20 == other.m20
		        && m21 == other.m21 && m22 == other.m22 && m23 == other.m23;
	}
}
