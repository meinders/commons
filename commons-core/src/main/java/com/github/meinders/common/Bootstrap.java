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

import java.lang.reflect.*;
import java.net.*;

/**
 * <p>
 * An abstract base class for creating a 'Main-class' that loads a splash screen
 * as soon as possible, and then delegates application startup to another class'
 * main method.
 * </p>
 *
 * <p>
 * The following code illustrates the typical usage of this class:
 *
 * <pre>
 * public class Bootstrap extends com.github.meinders.common.Bootstrap
 * {
 * 	public static void main(String[] args)
 * 	{
 * 		new Bootstrap(args).run();
 * 	}
 *
 * 	protected Bootstrap(String[] args)
 * 	{
 * 		super(args);
 * 	}
 *
 * 	// implementations for all abstract methods
 * }
 * </pre>
 *
 * </p>
 *
 * @version 0.9 (2006.02.23)
 * @author Gerrit Meinders
 */
public abstract class Bootstrap implements Runnable
{
	private String[] args;

	protected Bootstrap(String[] args)
	{
		this.args = args;
	}

	/**
	 * Returns the class on which the main method will be called after loading
	 * the splash screen.
	 *
	 * @return the full name of the class
	 */
	protected abstract String getMainClassName();

	/**
	 * Returns the title to be given to the frame created for the splash screen.
	 *
	 * @return the title
	 */
	protected abstract String getSplashTitle();

	/**
	 * Returns the URL of the image to be shown in the splash screen.
	 *
	 * @return the image's URL
	 */
	protected abstract URL getSplashImage();

	/**
	 * Invokes the main method of the class returned by
	 * <code>getMainClassName</code>, after creating and showing a splash
	 * screen.
	 *
	 * @see #getMainClassName()
	 * @see #createSplashInstance()
	 */
	public final void run()
	{
		if (getSplashImage() != null)
		{
			createSplashInstance();
			// Splash.getInstance().setVisible(true);
		}

		try
		{
			// perform "Main.main(args)" using reflection
			Class mainClass = Class.forName(getMainClassName());
			Method mainMethod = mainClass.getMethod("main", String[].class);
			mainMethod.invoke(null, (Object) args);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * Creates a splash screen instance using the title and image returned by
	 * the <code>getSplashTitle</code> and <code>getSplashImage</code> methods.
	 * This method is a shorthand for calling
	 * <code>createSplashInstance(null)</code>.
	 *
	 * @see #createSplashInstance(String)
	 */
	public void createSplashInstance()
	{
		createSplashInstance(null);
	}

	/**
	 * Creates a splash screen instance using the title and image returned by
	 * the <code>getSplashTitle</code> and <code>getSplashImage</code> methods.
	 *
	 * @see #getSplashTitle()
	 * @see #getSplashImage()
	 *
	 * @param tag the tag value passed to <code>Splash.getInstance</code>
	 */
	public void createSplashInstance(String tag)
	{
		// Splash instance = Splash.getInstance(tag);
		// instance.setTitle(getSplashTitle());
		// instance.setSplashImage(getSplashImage());
	}
}
