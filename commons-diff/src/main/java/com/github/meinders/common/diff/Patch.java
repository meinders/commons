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
 * Specifies the difference between two collections as a list of changes and
 * provides the information need to apply and undo those changes.
 *
 * @param <T> Element type of the collections.
 *
 * @author Gerrit Meinders
 */
public class Patch<T>
{
	private final List<PatchChange<T>> changes;

	/**
	 * Constructs a new patch from the given two collections and the result of a
	 * diff operation between the two.
	 *
	 * @param first First collection.
	 * @param second Second collection.
	 */
	public Patch(Collection<? extends T> first, Collection<? extends T> second)
	{
		this(first, second, DiffFactory.newDiff().diff(first, second));
	}

	/**
	 * Constructs a new patch from the given two collections and the result of a
	 * diff operation between the two.
	 *
	 * @param first First collection.
	 * @param second Second collection.
	 * @param changes Result of a diff between the two collection.
	 */
	public Patch(Collection<? extends T> first, Collection<? extends T> second,
	        List<Change> changes)
	{
		int firstIndex = 0;
		int secondIndex = 0;

		Iterator<? extends T> firstIterator = first.iterator();
		Iterator<? extends T> secondIterator = second.iterator();

		List<PatchChange<T>> patchChanges = new ArrayList<PatchChange<T>>(
		        changes.size());

		for (Change change : changes)
		{
			PatchChange<T> patchChange = new PatchChange<T>();
			patchChange.deletedIndex = change.getDeletedIndex();
			patchChange.insertedIndex = change.getDeletedIndex();

			{
				int skip = change.getDeletedIndex() - firstIndex;
				int count = change.getDeletedCount();
				patchChange.deletedElements = extractList(firstIterator, skip,
				        count);
				firstIndex += skip + count;
			}

			{
				int skip = change.getInsertedIndex() - secondIndex;
				int count = change.getInsertedCount();
				patchChange.insertedElements = extractList(secondIterator,
				        skip, count);
				secondIndex += skip + count;
			}

			patchChanges.add(patchChange);
		}

		this.changes = patchChanges;
	}

	/**
	 * Returns the result of applying the patch to the given input. The given
	 * collection is not modified in the process.
	 *
	 * @param input Collection to apply the patch to. (Will not be modified.)
	 * @param reverse If {@code true}, the patch is reversed.
	 *
	 * @return Result of applying the patch.
	 */
	public List<T> patch(Collection<? extends T> input, boolean reverse)
	{
		List<T> result = new ArrayList<T>();

		Iterator<? extends T> iterator = input.iterator();
		int index = 0;

		for (PatchChange<T> change : changes)
		{
			System.out.println(change.insertedElements
			        + " <- change.insertedElements");
			System.out.println(change.deletedElements
			        + " <- change.deletedElements");

			if (!reverse)
			{
				int unchanged = change.deletedIndex - index;
				int deleted = change.deletedElements.size();
				index += unchanged + deleted;

				for (int i = 0; i < unchanged; i++)
				{
					result.add(iterator.next());
				}

				for (int i = 0; i < deleted; i++)
				{
					iterator.next();
				}

				result.addAll(change.insertedElements);
			}
			else
			{
				int unchanged = change.deletedIndex - index;
				int deleted = change.insertedElements.size();
				System.out.println(" - " + unchanged + " <- unchanged");
				System.out.println(" - " + deleted + " <- deleted");
				System.out.println(" - " + index + " <- index (before)");
				index += unchanged + change.deletedElements.size();
				System.out.println(" - " + index + " <- index (after)");

				for (int i = 0; i < unchanged; i++)
				{
					result.add(iterator.next());
				}

				for (int i = 0; i < deleted; i++)
				{
					iterator.next();
				}

				result.addAll(change.deletedElements);
			}

			System.out.println(" - " + result + " <- result");
		}

		while (iterator.hasNext())
		{
			result.add(iterator.next());
		}

		return result;
	}

	/**
	 * Returns a list of a given size with elements retrieved from the given
	 * iterator, after skipping the specified number of elements.
	 *
	 * @param iterator Iterator to get elements from.
	 * @param skip Number of elements to be skipped.
	 * @param count Number of elements to be retrieved after skipping.
	 *
	 * @return List of elements retrieved from the iterator.
	 */
	private List<T> extractList(Iterator<? extends T> iterator, int skip,
	        int count)
	{
		final List<T> result;

		for (int i = 0; i < skip; i++)
		{
			iterator.next();
		}

		if (count == 0)
		{
			result = Collections.emptyList();
		}
		else
		{
			if (count == 1)
			{
				result = Collections.singletonList((T) iterator.next());
			}
			else
			{
				result = new ArrayList<T>(count);
				for (int i = 0; i < count; i++)
				{
					result.add(iterator.next());
				}
			}
		}

		return result;
	}

	@Override
	public String toString()
	{
		return "patch" + changes;
	}

	/**
	 * A change that includes the elements that were inserted and/or deleted.
	 */
	private static class PatchChange<T>
	{
		private int insertedIndex;

		private List<T> insertedElements;

		private int deletedIndex;

		private List<T> deletedElements;

		@Override
		public String toString()
		{
			return "change[inserted@" + insertedIndex + "=" + insertedElements
			        + ",deleted@" + deletedIndex + "=" + deletedElements + "]";
		}
	}
}
