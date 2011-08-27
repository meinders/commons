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

public class HashGraphTest extends TestCase {
    private static final int DEFAULT_WEIGHT = -1;

    private HashGraph<String> graph;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        graph = new HashGraph<String>(DEFAULT_WEIGHT);
    }

    @Override
    protected void tearDown() throws Exception {
        graph = null;
        super.tearDown();
    }

    public void testAdd() {
        assertTrue("Value not added.", graph.add("Hello"));
        assertTrue("Value not added.", graph.add("World"));
        assertTrue("Value not added.", graph.values().contains("Hello"));
        assertTrue("Value not added.", graph.values().contains("World"));
        assertFalse("Duplicate value added.", graph.add("World"));
    }

    public void testRemove() {
        testAdd();
        assertTrue("Value not removed.", graph.remove("World"));
        assertFalse("Value not removed.", graph.values().contains("World"));
        assertFalse("Removed value not in graph.", graph.remove("World"));
    }

    public void testClear() {
        testAdd();
        graph.clear();
        assertTrue("Graph not empty.", graph.isEmpty());
        assertTrue("Graph not empty.", graph.values().isEmpty());
    }

    public void testContains() {
        testAdd();
        assertTrue("Contains failed.", graph.contains("Hello"));
        assertTrue("Contains failed.", graph.contains("World"));
        assertFalse("Contains gave false positive.", graph.contains("!!!"));
    }

    public void testIsEmpty() {
        assertTrue("Expected empty graph.", graph.isEmpty());
        testAdd();
        assertFalse("Expected non-empty graph.", graph.isEmpty());
        graph.clear();
        assertTrue("Expected empty graph.", graph.isEmpty());
    }

    public void testValues() {
        testAdd();
        assertEquals("Incorrect size.", 2, graph.values().size());
        assertTrue("Missing value.", graph.values().contains("Hello"));
        assertTrue("Missing value.", graph.values().contains("World"));
    }

    public void testConnect() {
        testAdd();
        graph.connect("Hello", "World");
        assertTrue("Connect failed.", graph.connected("Hello", "World"));
        assertFalse("Connect must not be bi-directional.", graph.connected("World", "Hello"));
        graph.connect("World", "Hello");
        assertTrue("Connect failed.", graph.connected("World", "Hello"));
    }

    public void testConnected() {
        graph.add("Hello");
        graph.add("World");
        assertFalse("Connected gave false positive.", graph.connected("Hello", "World"));
        assertFalse("Connected gave false positive.", graph.connected("Hello", "World"));

        graph.connect("Hello", "World");
        assertTrue("Connected gave false negative.", graph.connected("Hello", "World"));
        assertFalse("Connected gave false positive.", graph.connected("World", "Hello"));

        graph.connect("World", "Hello");
        assertTrue("Connected gave false negative.", graph.connected("World", "Hello"));
        assertTrue("Connected gave false negative.", graph.connected("Hello", "World"));

        graph.connect("World", "World");
        assertFalse("Connected gave false positive.", graph.connected("Hello", "Hello"));
        assertTrue("Connected gave false negative.", graph.connected("World", "World"));

        try {
            graph.connect("World", "Non-existining");
            fail("Expected NoSuchElementException.");
        } catch (NoSuchElementException e) {
            // expected
        }

        try {
            graph.connect("Non-existining", "Hello");
            fail("Expected NoSuchElementException.");
        } catch (NoSuchElementException e) {
            // expected
        }
    }

    // public Edge<T> edge(T start, T end);
    //
    // public Collection<? extends Edge<T>> edges();
    //
    // public Collection<? extends Edge<T>> edges(T start);
    //
    public void testNeighbours() {
        testConnect();
        assertEquals("Unexpected number of neighbours.", 1, graph.neighbours("Hello").size());
        assertTrue("Missing neighbour.", graph.neighbours("Hello").contains("World"));
        assertEquals("Unexpected number of neighbours.", 1, graph.neighbours("World").size());
        assertTrue("Missing neighbour.", graph.neighbours("World").contains("Hello"));

        graph.add("Other");
        graph.add("YetAnother");
        graph.connect("Other", "YetAnother");

        assertEquals("Unexpected number of neighbours.", 1, graph.neighbours("Other").size());
        assertTrue("Missing neighbour.", graph.neighbours("Other").contains("YetAnother"));

        assertTrue("Expected no neighbours.", graph.neighbours("YetAnother").isEmpty());
    }

    // public static interface Edge<T> {
    // public T getStart();
    //
    // public T getEnd();
    // }

}
