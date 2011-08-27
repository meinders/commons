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

public class HashBidiMap<K, V> implements BidiMap<K, V> {
	private Map<K, V> left;
	private Map<V, K> right;

	public HashBidiMap() {
		left = new HashMap<K, V>();
		right = new HashMap<V, K>();
	}

	@Override
	public void clear() {
		left.clear();
		right.clear();
	}

	@Override
	public boolean containsKey(Object key) {
		return left.containsKey(key);
	}

	@Override
	public boolean containsValue(Object value) {
		return right.containsKey(value);
	}

	@Override
	public Set<Map.Entry<K, V>> entrySet() {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public V get(Object key) {
		return left.get(key);
	}

	@Override
	public K getKey(V value) {
		return right.get(value);
	}

	@Override
	public boolean isEmpty() {
		return left.isEmpty();
	}

	@Override
	public Set<K> keySet() {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public V put(K key, V value) {
		V result = left.put(key, value);
		right.put(value, key);
		return result;
	}

	@Override
	public void putAll(Map<? extends K, ? extends V> m) {
		left.putAll(m);
		for (Map.Entry<? extends K, ? extends V> entry : m.entrySet()) {
			right.put(entry.getValue(), entry.getKey());
		}
	}

	@Override
	public V remove(Object key) {
		if (left.containsKey(key)) {
			V result = left.remove(key);
			right.remove(result);
			return result;
		} else {
			return null;
		}
	}

	@Override
	public int size() {
		return left.size();
	}

	@Override
	public Collection<V> values() {
		throw new UnsupportedOperationException("not implemented");
	}
}
