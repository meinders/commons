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

/**
 * Provides utility methods for working with {@link Enumeration}s.
 *
 * @author Gerrit Meinders
 */
public class Enumerations {
	/**
	 * Returns an iterator backed by the given enumeration.
	 *
	 * @param <T> The element type.
	 * @param enumeration The enumeration.
	 *
	 * @return The given enumeration as an iterator.
	 */
	public static <T> Iterator<T> iterator(
	        final Enumeration<? extends T> enumeration) {
		return new Iterator<T>() {
			@Override
			public boolean hasNext() {
				return enumeration.hasMoreElements();
			}

			@Override
			public T next() {
				return enumeration.nextElement();
			}

			@Override
			public void remove() {
				throw new UnsupportedOperationException();
			}
		};
	}

	/**
	 * Returns an enumeration backed by the given iterator.
	 *
	 * @param <T> The element type.
	 * @param iterator The iterator.
	 *
	 * @return The given iterator as an enumeration.
	 */
	public static <T> Enumeration<T> enumeration(
	        final Iterator<? extends T> iterator) {
		return new Enumeration<T>() {
			@Override
			public boolean hasMoreElements() {
				return iterator.hasNext();
			}

			@Override
			public T nextElement() {
				return iterator.next();
			}
		};
	}

	private Enumerations() {
		// This class MUST NOT be instantiated.
	}
}
