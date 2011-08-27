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
import javax.swing.*;

/**
 * <p>A generic SwingWorker class with additional support for monitoring a tasks
 * progress, as specified by the Worker interface.</p>
 *
 * <p>Based on SwingWorker 3. For more information about the SwingWorker
 * class:</p>
 *
 * <p>http://java.sun.com/docs/books/tutorial/uiswing/misc/threads.html</p>
 *
 * @see Worker
 *
 * @author Gerrit Meinders
 * @version 0.3 (2006.02.17)
 */
public abstract class GenericWorker<T> extends SwingWorker
        implements Worker<T> {
    private LinkedHashSet<WorkerListener<T>> listeners;

    /**
     * Start a thread that will call the <code>construct</code> method
     * and then exit.
     */
    public GenericWorker() {
        super();
        listeners = new LinkedHashSet<WorkerListener<T>>();
    }

    /**
     * Compute the value to be returned by the <code>get</code> method.
     */
    public abstract T construct();

    /**
     * Return the value created by the <code>construct</code> method.
     * Returns null if either the constructing thread or the current
     * thread was interrupted before a value was produced.
     *
     * @return the value created by the <code>construct</code> method
     */
    public T get() {
        return super.get() == null ? null : getValue();
    }

    /**
     * Get the value produced by the worker thread, or null if it
     * hasn't been constructed yet.
     */
    @SuppressWarnings("unchecked")
    protected synchronized T getValue() {
        // cast can't be checked but is guaranteed by the construct method
        return (T) super.getValue();
    }

    public void addWorkerListener(WorkerListener<T> listener) {
        listeners.add(listener);
    }

    public void removeWorkerListener(WorkerListener<T> listener) {
        listeners.remove(listener);
    }

    public Set<WorkerListener<T>> getWorkerListeners() {
        return Collections.unmodifiableSet(listeners);
    }

    protected void fireWorkerStarted() {
        fireWorkerStarted(null);
    }

    protected void fireWorkerStarted(String message) {
        fireWorkerEvent(new WorkerEvent<T>(this,
                WorkerEvent.EventType.STARTED, message));
    }

    protected void fireWorkerFinished() {
        fireWorkerFinished(null);
    }

    protected void fireWorkerFinished(String message) {
        fireWorkerEvent(new WorkerEvent<T>(this,
                WorkerEvent.EventType.FINISHED, message));
    }

    protected void fireWorkerInterrupted() {
        fireWorkerInterrupted(null);
    }

    protected void fireWorkerInterrupted(String message) {
        fireWorkerEvent(new WorkerEvent<T>(this,
                WorkerEvent.EventType.INTERRUPTED, message));
    }

    protected void fireWorkerProgress(long progress) {
        fireWorkerProgress(progress, WorkerEvent.UNKNOWN_LENGTH, null);
    }

    protected void fireWorkerProgress(long progress, long length) {
        fireWorkerProgress(progress, length, null);
    }

    protected void fireWorkerProgress(long progress, long length,
            String message) {
        fireWorkerEvent(new WorkerEvent<T>(this, progress, length, message));
    }

    protected void fireWorkerMessage(String message) {
        fireWorkerEvent(new WorkerEvent<T>(this, message));
    }

    protected void fireWorkerError(Exception cause) {
        fireWorkerError(null, cause);
    }

    protected void fireWorkerError(String message) {
        fireWorkerError(message, null);
    }

    protected void fireWorkerError(String message, Exception cause) {
        fireWorkerEvent(new WorkerEvent<T>(this,
                WorkerEvent.EventType.ERROR, message, cause));
    }

    protected void fireWorkerWarning(Exception cause) {
        String message = cause.getClass().getSimpleName() + ": " +
                cause.getLocalizedMessage();
        fireWorkerWarning(message, cause);
    }

    protected void fireWorkerWarning(String message) {
        fireWorkerWarning(message, null);
    }

    protected void fireWorkerWarning(String message, Exception cause) {
        fireWorkerEvent(new WorkerEvent<T>(this,
                WorkerEvent.EventType.WARNING, message, cause));
    }

    protected void fireWorkerEvent(final WorkerEvent<T> e) {
        if (SwingUtilities.isEventDispatchThread()) {
            for (WorkerListener<T> listener : getWorkerListeners()) {
                listener.stateChanged(e);
            }
        } else {
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    fireWorkerEvent(e);
                }});
        }
    }
}
