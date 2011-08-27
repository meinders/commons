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
 * A one-dimensional value that is variable during a specific period of time.
 * The value of the variable is derived from an underlying function.
 *
 * @author Gerrit Meinders
 */
public class Transition1D implements Variable1D {
	private Variable1D variable;

	private double startTime;

	private double endTime;

	public Transition1D() {
		super();
	}

	public Transition1D(Variable1D variable, double startTime, double endTime) {
		super();
		this.variable = variable;
		this.startTime = startTime;
		this.endTime = endTime;
	}

	public double get(double t) {
		if (t < startTime) {
			return variable.get(startTime);
		} else if (t > endTime) {
			return variable.get(endTime);
		} else {
			return variable.get(t);
		}
	}

	public void set(Variable1D variable, double startTime, double endTime) {
		setVariable(variable);
		setStartTime(startTime);
		setEndTime(endTime);
	}

	public void setRange(double startTime, double endTime) {
		setStartTime(startTime);
		setEndTime(endTime);
	}

	public Variable1D getVariable() {
		return variable;
	}

	public void setVariable(Variable1D variable) {
		this.variable = variable;
	}

	public double getStartTime() {
		return startTime;
	}

	public void setStartTime(double startTime) {
		this.startTime = startTime;
	}

	public double getEndTime() {
		return endTime;
	}

	public void setEndTime(double endTime) {
		this.endTime = endTime;
	}

	public double getStartValue() {
		return get(getStartTime());
	}

	public double getEndValue() {
		return get(getEndTime());
	}
}
