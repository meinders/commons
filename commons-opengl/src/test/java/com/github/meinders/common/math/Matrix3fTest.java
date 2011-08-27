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

import org.junit.*;

import static org.junit.Assert.*;

public class Matrix3fTest
{
	@Test
	public void inverse()
	{
		Vector3f axis = new Vector3f(1.0, 2.0, 3.0);
		axis.normalize();

		Matrix3f matrix = Matrix3f.rotationMatrix(axis, 1.0f);
		Matrix3f expected = Matrix3f.rotationMatrix(axis, -1.0f);
		Matrix3f actual = matrix.inversed();

		assertMatrixEquals(expected, actual);

		Vector3f translation = new Vector3f(1.0, 2.0, 3.0);
		matrix.add(translation);
		expected = matrix;
		actual = matrix.inversed();
		actual.inverse();
		assertMatrixEquals(expected, actual);
	}

	private void assertMatrixEquals(Matrix3f expected, Matrix3f actual)
	{
		float[] expectedValues = expected.toArray();
		float[] actualValues = actual.toArray();
		for (int i = 0; i < expectedValues.length; i++)
		{
			assertEquals(expectedValues[i], actualValues[i], 1.0e-6);
		}
		assertEquals(expectedValues.length, actualValues.length);
	}
}
