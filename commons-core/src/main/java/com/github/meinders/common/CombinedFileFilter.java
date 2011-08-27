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

import java.io.*;
import java.util.*;

/**
 * A file filter that combines one or more filters, creating either a union or
 * an intersection of the those filters.
 *
 * @version 0.9 (2006.02.20)
 * @author Gerrit Meinders
 */
public class CombinedFileFilter extends FileFilter
{
	/** The method used to combine the underlying file filters. */
	public enum Method
	{
		/**
		 * Union: files accepted by any file filter are accept by the combined
		 * filter.
		 */
		UNION,
		/**
		 * Intersection: only files accepted by all file filters are accept by
		 * the combined filter.
		 */
		INTERSECTION
	}

	/** The first file filter. */
	private List<FileFilter> filters;

	/** The combination method. */
	private Method method;

	/**
	 * Constructs a new file filter with the given description which combines
	 * the given filters using the given method. The first filter defines the
	 * behavior of the getAppropriateFile method in the combined filter.
	 *
	 * @param description the description
	 * @param method the method used to combine the filters
	 * @param filter the first filter
	 * @param filters the other filters, if any
	 */
	public CombinedFileFilter(String description, Method method,
	        FileFilter filter, FileFilter... filters)
	{
		super(description);
		if (filter == null)
		{
			throw new NullPointerException("filter");
		}
		if (method == null)
		{
			throw new NullPointerException("method");
		}
		this.filters = new ArrayList<FileFilter>(filters.length + 1);
		this.filters.add(filter);
		this.filters.addAll(Arrays.asList(filters));
		this.method = method;
	}

	/**
	 * Constructs a new file filter with the given description which combines
	 * the given filters using the given method. The first filter defines the
	 * behavior of the getAppropriateFile method in the combined filter.
	 *
	 * @param description the description
	 * @param method the method used to combine the filters
	 * @param filters the filters
	 */
	public CombinedFileFilter(String description, Method method,
	        List<FileFilter> filters)
	{
		super(description);
		if (filters == null)
		{
			throw new NullPointerException("filters");
		}
		if (filters.isEmpty())
		{
			throw new IllegalArgumentException("filters: must not be empty");
		}
		if (method == null)
		{
			throw new NullPointerException("method");
		}
		this.filters = new ArrayList<FileFilter>(filters);
		this.method = method;
	}

	/**
	 * Accepts files based on the underlying file filters.
	 *
	 * @param file the file
	 * @return <code>true</code> if the file is accepted; <code>false</code>
	 *         otherwise
	 */
	@Override
	public synchronized boolean accept(File file)
	{
		switch (method)
		{
		case UNION:
			for (FileFilter filter : filters)
			{
				if (filter.accept(file))
				{
					return true;
				}
			}
			return false;
		case INTERSECTION:
			for (FileFilter filter : filters)
			{
				if (!filter.accept(file))
				{
					return false;
				}
			}
			return true;
		default:
			throw new AssertionError("unknown method: " + method);
		}
	}

	/**
	 * Returns a the given file or a more appropriate alternative, for example
	 * with the proper extension for this file filter.
	 *
	 * @param file the file
	 * @return the file or a more appropriate alternative
	 */
	public File getAppropriateFile(File file)
	{
		return filters.get(0).getAppropriateFile(file);
	}
}
