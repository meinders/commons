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
 * <p>
 * The function for a cubic b�zier curve, shown below, is used to interpolate
 * between the start and end values. The variable t represents the current time
 * relative to the start and end time, in the range [0..1].
 *
 * <blockquote> f(t) = (1 - t)<sup>3</sup> p<sub>start</sub> + 3t (1 - t)<sup>2</sup>
 * p<sub>control 1</sub> + 3(1 - t) t<sup>2</sup> p<sub>control 2</sub> +
 * t<sup>3</sup> p<sub>end</sub> </blockquote>
 *
 * The control points p<sub>control 1</sub> and p<sub>control 2</sub> are
 * not specified directly. Instead, the values of the function g' are given for
 * the start and end points, in absolute-time units. The resulting controls
 * points are given by the following equations.
 *
 * <blockquote> p<sub>control 1</sub> = p<sub>start</sub> + g'(0) / 3</sub><br>
 * p<sub>control 2</sub> = p<sub>end</sub> - g'(1) / 3 </blockquote>
 *
 * With the function g' denoting the derivative of f in absolute-time units, as
 * shown below.
 *
 * <blockquote> f'(t) = df(t) / dt<br>
 * g'(t) = f'(t) dT / dt</blockquote>
 *
 * @author Gerrit Meinders
 */
public class Cubic1D implements Variable1D {
	private double startValue;

	private double startTime;

	private double controlPoint1;

	private double controlPoint2;

	private double endValue;

	private double endTime;

	public Cubic1D(double startValue, double startRate, double startTime,
	        double endValue, double endRate, double endTime) {
		super();
		this.startValue = startValue;
		this.startTime = startTime;
		this.endValue = endValue;
		this.endTime = endTime;

		double dt = endTime - startTime;
		controlPoint1 = startValue + startRate * dt / 3.0;
		controlPoint2 = endValue - endRate * dt / 3.0;
	}

	public double get(double t) {
		if (t <= startTime) {
			return startValue;

		} else if (t >= endTime) {
			return endValue;

		} else {
			double dt = endTime - startTime;
			if (dt == 0) {
				return endValue;

			} else {
				double ta = (t - startTime) / dt;
				double tb = 1.0 - ta;

				return tb * tb * tb * startValue + 3 * ta * tb * tb
				        * controlPoint1 + 3 * ta * ta * tb * controlPoint2 + ta
				        * ta * ta * endValue;
			}
		}
	}

	public static Cubic1D branch(Variable1D start, double branchTime,
	        double endValue, double endRate, double endTime) {

		double dt = 0.001;
		double dv = (start.get(branchTime + dt) - start.get(branchTime - dt));
		double dvdt = dv / (2.0 * dt);

		double branchValue = start.get(branchTime);
		double branchRate = dvdt;

		return new Cubic1D(branchValue, branchRate, branchTime, endValue,
		        endRate, endTime);
	}

	public double getStartValue() {
		return startValue;
	}

	public double getStartTime() {
		return startTime;
	}

	public double getEndValue() {
		return endValue;
	}

	public double getEndTime() {
		return endTime;
	}
}
