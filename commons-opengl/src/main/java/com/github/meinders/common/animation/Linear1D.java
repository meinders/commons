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

/**
 * Provides smooth interpolation in 1 dimension using cubic b�zier
 * interpolation.
 *
 * @author Gerrit Meinders
 */
public class Linear1D implements Variable1D {
	private double startValue;

	private double startTime;

	private double rate;

	public Linear1D(double startValue, double startTime, double rate) {
		super();
		this.startValue = startValue;
		this.startTime = startTime;
		this.rate = rate;
	}

	public double get(double t) {
		if (t <= startTime) {
			return startValue;

		} else {
			return startValue + (t - startTime) * rate;
		}
	}

	public void set(double startTime, double startValue) {
		this.startTime = startTime;
		this.startValue = startValue;
	}

	public void set(double startTime, double startValue, double rate) {
		this.startTime = startTime;
		this.startValue = startValue;
		this.rate = rate;
	}

	public double getStartValue() {
		return startValue;
	}

	public double getStartTime() {
		return startTime;
	}

	public double getRate() {
		return rate;
	}

	public static Linear1D branch(Variable1D start, double branchTime,
	        double rate) {
		return new Linear1D(start.get(branchTime), branchTime, rate);
	}
}
