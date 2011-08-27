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

package com.github.meinders.common.animation;

public class MinMax1D implements Variable1D {
	private Variable1D variable;

	private double minimum;

	private double maximum;

	public MinMax1D(Variable1D variable, double minimum, double maximum) {
		super();
		this.variable = variable;
		this.minimum = minimum;
		this.maximum = maximum;
	}

	@Override
	public double get(double t) {
		return Math.max(minimum, Math.min(maximum, variable.get(t)));
	}
}
