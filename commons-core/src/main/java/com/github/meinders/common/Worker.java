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

package com.github.meinders.common;

import java.util.*;

/**
 * This interface describes the methods required by a Worker class, used to
 * safely perform tasks in a separate thread. It's based on the 3rd version of
 * SwingWorker, with added generics for improved type-safety.
 *
 * @see SwingWorker
 *
 * @author Gerrit Meinders
 * @version 0.2 (2005.02.01)
 */
public interface Worker<T> {
    /**
     * Compute the value to be returned by the <code>get</code> method.
     */
    public T construct();

    /**
     * Called on the event dispatching thread (not on the worker thread)
     * after the <code>construct</code> method has returned.
     */
    public void finished();

    /**
     * Start the worker thread.
     */
    public void start();

    /**
     * A new method that interrupts the worker thread.  Call this method
     * to force the worker to stop what it's doing.
     */
    public void interrupt();

    /**
     * Return the value created by the <code>construct</code> method.
     * Returns null if either the constructing thread or the current
     * thread was interrupted before a value was produced.
     *
     * @return the value created by the <code>construct</code> method
     */
    public T get();

    /**
     * Adds a worker listener.
     */
    public void addWorkerListener(WorkerListener<T> listener);

    /**
     * Removes a worker listener.
     */
    public void removeWorkerListener(WorkerListener<T> listener);

    /**
     * Returns the set of worker listeners for this worker.
     */
    public Set<WorkerListener<T>> getWorkerListeners();
}
