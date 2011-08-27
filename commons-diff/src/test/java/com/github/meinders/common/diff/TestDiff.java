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

import java.io.*;
import java.util.*;

import org.junit.*;

import static junit.framework.Assert.*;

public class TestDiff
{
	@Test
	public void testDiff() throws Exception
	{
		List<String> a = Arrays.asList("a", "b", "c", "d", "e", "f", "g");
		List<String> b = Arrays.asList("c", "d", "a", "b", "c", "e", "h", "i");

		Diff diff = DiffFactory.newDiff();
		List<Change> result = diff.diff(a, b);

		writeDiff(System.out, a, b, result);

		fail("Nothing's actually tested here...");
	}

	private void writeDiff(PrintStream out, List<?> first, List<?> second,
	        Collection<Change> changes)
	{
		for (Change change : changes)
		{
			if (change.getDeletedCount() > 0 && change.getInsertedCount() > 0)
			{
				out.print(change.getDeletedIndex() + 1);
				if (change.getDeletedCount() > 1)
				{
					out.print(',');
					out.print(change.getDeletedIndex()
					        + change.getDeletedCount());
				}
				out.print('c');
				out.print(change.getInsertedIndex() + 1);
				if (change.getInsertedCount() > 1)
				{
					out.print(',');
					out.print(change.getInsertedIndex()
					        + change.getInsertedCount());
				}
				out.println();

				for (int i = change.getDeletedIndex(); i < change.getDeletedIndex()
				        + change.getDeletedCount(); i++)
				{
					out.print("< ");
					out.println(first.get(i));
				}

				out.println("---");

				for (int i = change.getInsertedIndex(); i < change.getInsertedIndex()
				        + change.getInsertedCount(); i++)
				{
					out.print("> ");
					out.println(second.get(i));
				}

			}
			else if (change.getDeletedCount() > 0)
			{
				out.print(change.getDeletedIndex() + 1);
				if (change.getDeletedCount() > 1)
				{
					out.print(',');
					out.print(change.getDeletedIndex()
					        + change.getDeletedCount());
				}
				out.print('d');
				out.println(change.getInsertedIndex());

				for (int i = change.getDeletedIndex(); i < change.getDeletedIndex()
				        + change.getDeletedCount(); i++)
				{
					out.print("< ");
					out.println(first.get(i));
				}

			}
			else
			{
				out.print(change.getDeletedIndex());
				out.print('a');
				out.print(change.getInsertedIndex() + 1);
				if (change.getInsertedCount() > 1)
				{
					out.print(',');
					out.print(change.getInsertedIndex()
					        + change.getInsertedCount());
				}
				out.println();

				for (int i = change.getInsertedIndex(); i < change.getInsertedIndex()
				        + change.getInsertedCount(); i++)
				{
					out.print("> ");
					out.println(second.get(i));
				}
			}
		}
	}
}
