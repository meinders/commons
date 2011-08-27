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

/**
 * The event class used by the Worker interface to inform listeners of any
 * changes in the Worker.
 *
 * @see Worker
 *
 * @author Gerrit Meinders
 * @version 0.2 (2005.01.30)
 */
public class WorkerEvent<T> {
    /** Defines the different types of WorkerEvents */
    public enum EventType {
        /** Indicates that the worker was started. */
        STARTED,
        /**
         * Indicates that the worker has made progress in performing its task.
         */
        PROGRESS,
        /** Indicates that event contains a textual message from the worker. */
        MESSAGE,
        /**
         * Indicates that the worker was interrupted before it could finish its
         * task.
         */
        INTERRUPTED,
        /** Indicates that the worker has successfully completed its task. */
        FINISHED,
        /** Indicates that the worker has detected an error. */
        ERROR,
        /** Indicates that the worker has issued a warning. */
        WARNING;
    }

    /**
     * A constant indicating that the length of the worker's task is unknown.
     */
    public static final long UNKNOWN_LENGTH = -1;

    private Worker<T> source;
    private EventType type;
    private String message;
    private Exception cause;
    private long length = 0;
    private long progress = 1;

    public WorkerEvent(Worker<T> source, EventType type) {
        this(source, type, null, null);
    }

    public WorkerEvent(Worker<T> source, EventType type, String message) {
        this(source, type, message, null);
    }

    public WorkerEvent(Worker<T> source, EventType type, String message,
            Exception cause) {
        this.source = source;
        this.type = type;
        this.message = message;
        this.cause = cause;
    }

    public WorkerEvent(Worker<T> source, long progress, String message) {
        this(source, progress, UNKNOWN_LENGTH, message);
    }

    public WorkerEvent(Worker<T> source, long progress, long length,
            String message) {
        this(source, EventType.PROGRESS, message);
        this.length = length;
        this.progress = progress;
    }

    public WorkerEvent(Worker<T> source, String message) {
        this(source, EventType.MESSAGE, message);
    }

    /**
     * Returns the worker that fired the event.
     */
    public Worker<T> getSource() {
        return source;
    }

    /**
     * Returns the type of this event.
     */
    public EventType getType() {
        return type;
    }

    /**
     * Returns the message set by the worker.
     */
    public String getMessage() {
        return message;
    }

    /**
     * Returns the exception that caused the event, in the case of warning and
     * error events.
     */
    public Exception getCause() {
        return cause;
    }

    /**
     * Returns the length of the worker's task.
     */
    public long getLength() {
        return length;
    }

    /**
     * <p>Returns the current progress of the worker's task.</p>
     *
     * <p><code>0</code> &lt;= <code>getProgress()</code> &lt;=
     * <code>getLength()</code></p>
     */
    public long getProgress() {
        return progress;
    }

    /**
     * Returns the progress in terms of the given scale, such that:
     *
     * <p><code>0.0</code> &lt;= <code>getProgress(scale)</code> &lt;=
     * <code>scale</code></p>
     */
    public double getProgress(double scale) {
        return progress * scale / length;
    }
}
