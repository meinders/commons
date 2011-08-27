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

import org.junit.*;

import static org.junit.Assert.*;

public class TestPatch
{
	@Test
	public void testForward()
	{
		// List<String> a = Arrays.asList("a", "b", "c", "d", "e", "f", "g");
		// List<String> b = Arrays.asList("c", "d", "a", "b", "c", "e", "h",
		// "i");
		List<String> a = characters("Hello world! This is a more complex test.");
		List<String> b = characters("Halo or Ulduar is complex but unrelated.");

		System.out.println(a + " <- a");
		System.out.println(b + " <- b");

		Diff diff = DiffFactory.newDiff();

		Patch<String> patch = new Patch<String>(a, b, diff.diff(a, b));
		assertEquals("patch(a, diff(a, b, false), false) = b", b, patch.patch(
		        a, false));
		assertEquals("patch(b, diff(a, b, false), true) = a", a, patch.patch(b,
		        true));
	}

	private List<String> characters(String string)
	{
		List<String> result = new ArrayList<String>(string.length());
		for (int i = 0; i < string.length(); i++)
		{
			result.add(String.valueOf(string.charAt(i)));
		}
		return result;
	}
}
