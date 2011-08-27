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

public class HashGraph<T> implements WeightedGraph<T> {

    private final int defaultWeight;

    private final HashMap<T, Collection<WeightedEdge<T>>> edges;

    public HashGraph(int defaultWeight) {
        this.defaultWeight = defaultWeight;

        edges = new HashMap<T, Collection<WeightedEdge<T>>>();
    }

    public WeightedEdge<T> connect(T start, T end) {
        return connect(start, end, defaultWeight);
    }

    public WeightedEdge<T> connect(T start, T end, int weight) {
        if (!contains(end)) {
            throw new NoSuchElementException("end");

        } else {
            Collection<WeightedEdge<T>> edgesFromStart = edges(start);

            WeightedEdgeImpl<T> edge = new WeightedEdgeImpl<T>(start, end, weight);
            edgesFromStart.add(edge);
            return edge;
        }
    }

    public boolean disconnect(T start, T end) {
        if (!contains(end)) {
            throw new NoSuchElementException("end");

        } else {
            Collection<WeightedEdge<T>> edgesFromStart = edges(start);

            boolean result = false;
            for (Iterator<WeightedEdge<T>> i = edgesFromStart.iterator(); i.hasNext();) {
                WeightedEdge<T> edge = i.next();
                if (end.equals(edge.getEnd())) {
                    i.remove();
                    result = true;
                }
            }
            return result;
        }
    }

    public boolean connected(T start, T end) {
        return (edge(start, end) != null);
    }

    public WeightedEdge<T> edge(T start, T end) {
        for (WeightedEdge<T> edge : edges(start)) {
            if (end.equals(edge.getEnd())) {
                return edge;
            }
        }
        return null;
    }

    public Collection<WeightedEdge<T>> edges() {
        throw new UnsupportedOperationException();
    }

    public Collection<WeightedEdge<T>> edges(T start) {
        Collection<WeightedEdge<T>> result = edges.get(start);
        if (result == null) {
            throw new NoSuchElementException("start");
        } else {
            return result;
        }
    }

    public int weight(T start, T end) {
        return edge(start, end).getWeight();
    }

    public boolean add(T value) {
        return edges.put(value, new ArrayList<WeightedEdge<T>>()) == null;
    }

    public void clear() {
        edges.clear();
    }

    public boolean contains(T value) {
        return edges.containsKey(value);
    }

    public boolean isEmpty() {
        return edges.isEmpty();
    }

    public Collection<T> neighbours(T value) {
        return new NeighbourCollection(value);
    }

    public boolean remove(T value) {
        return edges.remove(value) != null;
    }

    public Collection<T> values() {
        return edges.keySet();
    }

    private class NeighbourCollection implements Collection<T> {
        private final T start;

        private final Collection<WeightedEdge<T>> edges;

        public NeighbourCollection(T value) {
            this.start = value;
            this.edges = HashGraph.this.edges.get(value);
        }

        public boolean add(T value) {
            return (connect(start, value) != null);
        }

        public boolean addAll(Collection<? extends T> values) {
            boolean changed = false;
            for (T value : values) {
                changed |= (connect(start, value) != null);
            }
            return changed;
        }

        public void clear() {
            edges.clear();
        }

        public boolean contains(Object value) {
            for (Edge<T> edge : edges) {
                if (value.equals(edge.getEnd())) {
                    return true;
                }
            }
            return false;
        }

        public boolean containsAll(Collection<?> values) {
            throw new UnsupportedOperationException();
        }

        public boolean isEmpty() {
            return edges.isEmpty();
        }

        public Iterator<T> iterator() {
            throw new UnsupportedOperationException();
        }

        public boolean remove(Object value) {
            for (Iterator<WeightedEdge<T>> i = edges.iterator(); i.hasNext();) {
                Edge<T> edge = i.next();
                if (value.equals(edge.getEnd())) {
                    i.remove();
                    return true;
                }
            }
            return false;
        }

        public boolean removeAll(Collection<?> c) {
            throw new UnsupportedOperationException();
        }

        public boolean retainAll(Collection<?> c) {
            throw new UnsupportedOperationException();
        }

        public int size() {
            return edges.size();
        }

        public Object[] toArray() {
            throw new UnsupportedOperationException();
        }

        public <E> E[] toArray(E[] a) {
            throw new UnsupportedOperationException();
        }
    }

    private static class WeightedEdgeImpl<T> implements WeightedEdge<T> {
        private final T start;

        private final T end;

        private int weight;

        public WeightedEdgeImpl(final T start, final T end, int weight) {
            super();
            this.start = start;
            this.end = end;
            this.weight = weight;
        }

        public T getStart() {
            return start;
        }

        public T getEnd() {
            return end;
        }

        public int getWeight() {
            return weight;
        }

        public void setWeight(int weight) {
            this.weight = weight;
        }
    }
}
