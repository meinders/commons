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

package com.github.meinders.common.diff;

import java.util.*;

/**
 * Allows for the comparison of two collections, resulting in a list of changes
 * (additions and/or removals).
 *
 * <p>
 * <strong>Thread safety is not guaranteed.</strong> The results of using the
 * same diff instance from multiple threads are undefined. In general, each
 * thread should use its own diff instance.
 *
 * @author Gerrit Meinders
 */
public interface Diff
{
	/**
	 * Compares the given collections and returns the changes needed to change
	 * the first collection into the second collection.
	 *
	 * @param first First collection.
	 * @param second Second collection.
	 *
	 * @return Resulting changes.
	 */
	public List<Change> diff(Collection<?> first, Collection<?> second);
}
