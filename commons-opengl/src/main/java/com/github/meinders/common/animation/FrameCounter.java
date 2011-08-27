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

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.*;
import javax.swing.event.*;

/**
 * A thread-safe frame counter.
 *
 * @author Gerrit Meinders
 */
public class FrameCounter
{
	/**
	 * Number of frames rendered during the previous second.
	 */
	private volatile int value;

	/**
	 * Number of frames rendered during the current second.
	 */
	private final AtomicInteger counter = new AtomicInteger();

	/**
	 * Value of the system timer at the start of the current second, in
	 * nanoseconds.
	 */
	private final AtomicLong start = new AtomicLong();

	/**
	 * Listeners to be notified each second.
	 */
	private final List<ChangeListener> changeListeners;

	/**
	 * Constructs a new frame counter.
	 */
	public FrameCounter()
	{
		reset();
		changeListeners = new ArrayList<ChangeListener>(1);
	}

	public void countFrame()
	{
		counter.incrementAndGet();
		update();
	}

	public void reset()
	{
		value = 0;
		counter.set(0);
		start.set(System.nanoTime());
	}

	public int getFramesPerSecond()
	{
		update();
		return value;
	}

	private void update()
	{
		long start = this.start.get();

		long current = System.nanoTime();
		long seconds = TimeUnit.NANOSECONDS.toSeconds(current - start);

		if (seconds > 1L)
		{
			long newStart = start + TimeUnit.SECONDS.toNanos(seconds);
			if (this.start.compareAndSet(start, newStart))
			{
				value = this.counter.getAndSet(0);
			}

			fireChangeEvent();
		}
	}

	public void addChangeListener(ChangeListener listener)
	{
		changeListeners.add(listener);
	}

	protected void fireChangeEvent()
	{
		ChangeEvent event = null;
		for (ChangeListener listener : changeListeners)
		{
			if (event == null)
			{
				event = new ChangeEvent(this);
			}
			listener.stateChanged(event);
		}
	}
}
