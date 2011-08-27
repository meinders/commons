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

package com.github.meinders.common.opengl;

import java.io.*;
import java.util.*;
import java.util.concurrent.*;
import javax.media.opengl.*;

import com.jogamp.opengl.util.texture.*;

/**
 * Loads texture data asynchronously.
 *
 * @author Gerrit Meinders
 */
public class TextureLoader
{
	private final Map<File, Future<TextureData>> scheduled;

	private final ExecutorService executor;

	private int preloadLimit;

	private TexturePreloadModel model;

	public TextureLoader(TexturePreloadModel model)
	{
		super();
		this.model = model;
		this.preloadLimit = 5;

		scheduled = new HashMap<File, Future<TextureData>>();
		executor = Executors.newSingleThreadExecutor();
	}

	public Texture getTexture(final File source)
	{
		Texture result = null;

		Future<TextureData> future = scheduled.get(source);
		if (future == null)
		{
			schedule(source);
		}
		else if (future.isDone())
		{
			scheduled.remove(source);

			TextureData textureData;
			try
			{
				textureData = future.get();
				if (textureData != null)
				{
					try
					{
						result = TextureIO.newTexture(textureData);
					}
					finally
					{
						textureData.flush();
					}
				}
			}
			catch (InterruptedException e)
			{
				e.printStackTrace();
			}
			catch (ExecutionException e)
			{
				e.printStackTrace();
			}
		}

		/*
		Collection<File> schedule = model.getPreloadFiles(source, preloadLimit);
		Collection<File> unschedule = new HashSet<File>(scheduled.keySet());
		unschedule.remove(source);
		unschedule.removeAll(schedule);

		for (File file : schedule)
		{
			schedule(file);
		}

		for (File file : unschedule)
		{
			scheduled.get(file).cancel(true);
		}

		scheduled.keySet().removeAll(unschedule);
		*/

		return result;
	}

	private void schedule(final File source)
	{
		if (!scheduled.containsKey(source))
		{
			System.out.println( "Scheduling " + source );
			scheduled.put(source, executor.submit(new Callable<TextureData>()
			{
				public TextureData call()
				{
					String name = source.getName();
					String suffix = name.substring(name.lastIndexOf('.') + 1);
					try
					{
						return TextureIO.newTextureData( GLProfile.getGL2GL3(), source, false, suffix );
					}
					catch (IOException e1)
					{
						e1.printStackTrace();
						return null;
					}
				}
			}));
		}
	}

	public void dispose()
	{
		executor.shutdownNow();
	}

	public int getScheduledCount()
	{
		return scheduled.size();
	}

	public int getLoadedCount()
	{
		int result = 0;
		for (File file : scheduled.keySet())
		{
			if (scheduled.get(file).isDone())
			{
				result++;
			}
		}
		return result;
	}
}
