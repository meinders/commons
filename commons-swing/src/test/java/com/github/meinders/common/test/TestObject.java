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

package com.github.meinders.common.test;

/**
 * An example object used for testing. It holds an integer value
 * representing the result of some operation and the start and end time for
 * the operation.
 *
 * @see Main
 */
class TestObject {
    private long value;
    private long start;
    private long end;

    public TestObject(long value, long start, long end) {
        this.value = value;
        this.start = start;
        this.end = end;
    }

    public long getValue() {
        return value;
    }

    public long getStart() {
        return start;
    }

    public long getEnd() {
        return end;
    }

    /**
     * @return the duration in seconds
     */
    public double getDuration() {
        return (getEnd() - getStart()) / 1000000000d;
    }
}

