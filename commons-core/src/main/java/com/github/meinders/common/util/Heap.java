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

package com.github.meinders.common.util;

import java.util.*;

/**
 * Provides an interface for a heap. A heap is a balanced tree in which for each
 * path from the root to a leaf, the nodes in that path are ordered according to
 * some predefined ordering.
 *
 * @param <T> The type of values stored in the heap.
 *
 * @author Gerrit Meinders
 */
public interface Heap<T> {
    /**
     * Adds the given value to the heap.
     *
     * @param value The value to be added.
     */
    public void add(T value);

    /**
     * Returns the value stored in the root node from the heap and returns it.
     * This is the first value as defined by the ordering of values.
     *
     * @return The first value in the heap.
     *
     * @throws NoSuchElementException if the heap is empty.
     */
    public T getFirst();

    /**
     * Removes the value stored in the root node from the heap and returns it.
     * This is the first value as defined by the ordering of values.
     *
     * @return The removed value.
     *
     * @throws NoSuchElementException if the heap is empty.
     */
    public T removeFirst();

    /**
     * Returns the number of values stored in the heap.
     *
     * @return The size of the heap.
     */
    public int size();

    /**
     * Returns whether the heap contains no values.
     *
     * @return <code>true</code> if the heap is empty; <code>false</code>
     *         otherwise.
     */
    public boolean isEmpty();
}
