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
 * Handles instantiation of {@link Diff} implementations.
 *
 * @author Gerrit Meinders
 */
public class DiffFactory
{
	/**
	 * Creates a new {@link Diff} instance.
	 *
	 * @return Created object.
	 */
	public static Diff newDiff()
	{
		try
		{
			Class<? extends Diff> implClass = (Class<? extends Diff>) Class.forName( "com.github.meinders.common.diff.GNUDiff" );
			return implClass.newInstance();
		}
		catch (Exception e)
		{
			throw new AssertionError(e);
		}
	}
}
