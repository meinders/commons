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

public interface WeightedGraph<T> extends Graph<T> {
    public WeightedEdge<T> connect(T start, T end);

    public WeightedEdge<T> connect(T start, T end, int weight);

    public WeightedEdge<T> edge(T start, T end);

    public int weight(T from, T to);

    public Collection<WeightedEdge<T>> edges();

    public Collection<WeightedEdge<T>> edges(T start);

    public static interface WeightedEdge<T> extends Edge<T> {
        public int getWeight();

        public void setWeight(int weight);
    }
}
