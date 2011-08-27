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

import java.io.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.regex.*;

public class FileIterator implements Iterator<File>, FileFilter, Runnable
{
	public static void main(String[] args)
	{
		FileIterator i = new FileIterator(new File("E:\\asdf\\"),
		        new FileFilter()
		        {
			        final Pattern pattern = Pattern.compile("(jpe?g)$");

			        @Override
			        public boolean accept(File pathname)
			        {
				        return pattern.matcher(pathname.getName()).find();
			        }
		        });

		System.out.println("Busy..");
		int j = 0;
		while (i.hasNext())
		{
			i.next();
			j++;
		}
		System.out.println(j + " <- j");
		i.dispose();
	}

	private final FileFilter filter;

	private final LinkedList<File> folders = new LinkedList<File>();

	private final ConcurrentLinkedQueue<File> buffer = new ConcurrentLinkedQueue<File>();

	private ExecutorService executor = Executors.newSingleThreadExecutor();

	private volatile boolean submitted = false;

	private volatile boolean finished = false;

	private Semaphore semaphore = new Semaphore(0);

	public FileIterator(File folder, FileFilter filter)
	{
		this.filter = filter;
		folders.add(folder);
	}

	@Override
	public void run()
	{
		while (!folders.isEmpty())
		{
			File folder = folders.removeFirst();
			folder.listFiles(this);
		}
		finished = true;
		semaphore.release();
	}

	private void dispose()
	{
		executor.shutdown();
	}

	@Override
	public boolean accept(File file)
	{
		if (file.isDirectory())
		{
			folders.add(file);
		}

		if (filter.accept(file))
		{
			buffer.add(file);
			semaphore.release();
		}

		return false;
	}

	private void ensureSubmitted()
	{
		if (!submitted)
		{
			executor.submit(this);
			submitted = true;
		}
	}

	@Override
	public boolean hasNext()
	{
		ensureSubmitted();

		try
		{
			semaphore.acquire();
		}
		catch (InterruptedException e)
		{
			return false;
		}
		semaphore.release();
		return !buffer.isEmpty();
	}

	@Override
	public File next()
	{
		ensureSubmitted();

		if (!finished)
		{
			try
			{
				semaphore.acquire();
			}
			catch (InterruptedException e)
			{
				throw new NoSuchElementException();
			}
		}

		return buffer.remove();
	}

	@Override
	public void remove()
	{
		throw new UnsupportedOperationException();
	}
}
