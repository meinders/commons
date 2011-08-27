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

import junit.framework.*;

/**
 * Unit test for the {@link TreeHeap} class.
 *
 * @author Gerrit Meinders
 */
public class TreeHeapTest extends TestCase {
    /**
     * Tests that adding unsorted nodes and then removing them results in the
     * values being returned in sorted order, verifying the heap property.
     */
    public void testHeapProperty() {
        Heap<Integer> heap = new TreeHeap<Integer>(new Comparator<Integer>() {
            public int compare(Integer o1, Integer o2) {
                return o1.compareTo(o2);
            }
        });

        int[] values = { 5, 3, 8, 11, 4, 2, 6, 3 };

        for (int value : values) {
            heap.add(value);
        }
        assertEquals("Heap is too small.", values.length, heap.size());

        int previous = heap.removeFirst();
        while (!heap.isEmpty()) {
            int current = heap.removeFirst();
            assertTrue("Heap property violation.", current >= previous);
            previous = current;
        }

        assertTrue("Expected empty heap.", heap.isEmpty());
    }
}
