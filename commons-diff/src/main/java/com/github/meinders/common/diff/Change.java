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

/**
 * Specifies a single change that is needed in the process of changing one
 * collection into another, as determined by {@link Diff}. A change may be an
 * insertion, a deletion or a combination of the two.
 *
 * @author Gerrit Meinders
 */
public interface Change
{
	/**
	 * Returns the index where a deletion occurs.
	 *
	 * @return Index of the first deleted element.
	 */
	public int getDeletedIndex();

	/**
	 * Returns the number of deleted elements.
	 *
	 * @return Number of deleted elements.
	 */
	public int getDeletedCount();

	/**
	 * Returns the index where an insertion occurs.
	 *
	 * @return Index of the first inserted element.
	 */
	public int getInsertedIndex();

	/**
	 * Returns the number of inserted elements.
	 *
	 * @return Number of inserted elements.
	 */
	public int getInsertedCount();
}
