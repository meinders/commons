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
 * Accurately keeps track of the time that has passed since a predefined
 * start time.
 *
 * @author Gerrit Meinders
 */
public class Timer {
	/**
	 * Start time, in nanoseconds.
	 */
	private long start;

	/**
	 * Constructs a new timer, counting from the current time.
	 */
	public Timer() {
		restart();
	}

	/**
	 * Resets the timer's current time to zero.
	 */
	public void restart() {
		start = System.nanoTime();
	}

	/**
	 * Returns the current time relative to the start time.
	 *
	 * @return The current time, in seconds.
	 */
	public double currentTime() {
		return (System.nanoTime() - start) / 1000000000.0;
	}
}
