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

public interface Graph<T> {
    public boolean add(T value);

    public boolean remove(T value);

    public void clear();

    public boolean contains(T value);

    public boolean isEmpty();

    public Collection<T> values();

    public Collection<T> neighbours(T start);

    public Edge<T> connect(T start, T end);

    public boolean disconnect(T start, T end);

    public boolean connected(T start, T end);

    public Edge<T> edge(T start, T end);

    public Collection<? extends Edge<T>> edges();

    public Collection<? extends Edge<T>> edges(T start);

    public static interface Edge<T> {
        public T getStart();

        public T getEnd();
    }
}
