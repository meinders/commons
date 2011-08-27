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
 * Switches between two transitions at a predefined time, returning values from
 * the first transition before that point and values from the second afterwards.
 *
 * @author Gerrit Meinders
 */
public class Switch1D implements Variable1D {
	private Variable1D first;

	private Variable1D second;

	private double switchTime;

	public Switch1D(Variable1D first, Variable1D second, double switchTime) {
		super();
		this.first = first;
		this.second = second;
		this.switchTime = switchTime;
	}

//	public double getEndTime() {
//		return Math.max(switchTime, Math.max(first.getEndTime(),
//		        second.getEndTime()));
//	}

//	public double getEndValue() {
//		return get(getEndTime());
//	}

//	public double getStartTime() {
//		return Math.min(switchTime, Math.min(first.getEndTime(),
//		        second.getEndTime()));
//	}

//	public double getStartValue() {
//		return get(getStartTime());
//	}

	@Override
	public double get(double t) {
		return (t < switchTime) ? first.get(t) : second.get(t);
	}

	@Override
	public String toString() {
		return "Switch ( " + first + " , " + second + " at " + switchTime
		        + " )";
	}
}
