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

public interface Vector<VectorImpl extends Vector<VectorImpl>>
{
	void add(VectorImpl other);

	void subtract(VectorImpl other);

	VectorImpl difference(VectorImpl other);

	void multiply(VectorImpl vector);

	VectorImpl product(VectorImpl vector);

	void normalize();

	VectorImpl normalized();

	/**
	 * Converts the vector from cartesian to spherical coordinates, returning
	 * the result.
	 *
	 * @return A representation of the vector using spherical coordinates
	 *         [&rho;, &phi;, &theta;], i.e. the radius, zenith and azimuth,
	 *         respectively.
	 */
	// http://en.wikipedia.org/wiki/Spherical_coordinate_system
	VectorImpl toSpherical();

	// {x}=\rho \, \sin\phi \, \cos\theta \quad
	// {y}=\rho \, \sin\phi \, \sin\theta \quad
	// {z}=\rho \, \cos\phi \quad
	VectorImpl toCartesian();

	void maximum(VectorImpl other);

	void minimum(VectorImpl other);
}
