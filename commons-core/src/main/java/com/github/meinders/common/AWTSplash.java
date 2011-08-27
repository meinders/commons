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

import java.awt.*;
import java.awt.event.*;
import java.net.*;
import java.util.*;

/**
 * This class displays a splash screen while the rest of the application is
 * being loaded.
 *
 * @deprecated as of Java 1.6
 *
 * @author Gerrit Meinders
 */
public class AWTSplash extends Frame implements StatusListener
{
	private static Map<String, AWTSplash> instances;

	static
	{
		instances = Collections.synchronizedMap(new HashMap<String, AWTSplash>());
	}

	public static AWTSplash getInstance()
	{
		return getInstance(null);
	}

	public static AWTSplash getInstance(String tag)
	{
		AWTSplash instance = instances.get(tag);
		if (instance == null)
		{
			instance = new AWTSplash(tag);
			instances.put(tag, instance);
		}
		return instance;
	}

	private static void disposeInstance(String tag)
	{
		AWTSplash instance = instances.remove(tag);
	}

	private final String tag;

	private Image splashImage = null;
	private String status = "";
	private Font font;
	private FontMetrics fontMetrics;

	private boolean consoleOutputEnabled = true;

	private AWTSplash(String tag)
	{
		super();
		this.tag = tag;

		font = new Font("SansSerif", Font.PLAIN, 10);
		fontMetrics = getFontMetrics(font);

		addWindowListener(new WindowAdapter()
		{
			public void windowClosing(WindowEvent e)
			{
				dispose();
			}
		});

		addMouseListener(new MouseAdapter()
		{
			public void mouseClicked(MouseEvent e)
			{
				if (e.getClickCount() == 2)
				{
					dispose();
				}
			}
		});

		DragToMoveListener.addListener(this);

		setUndecorated(true);
	}

	public void setSplashImage(URL splashImageURL)
	{
		// load splash image
		Toolkit toolkit = Toolkit.getDefaultToolkit();
		MediaTracker tracker = new MediaTracker(this);
		splashImage = toolkit.createImage(splashImageURL);
		tracker.addImage(splashImage, 0);
		try
		{
			tracker.waitForAll();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

		if (splashImage != null)
		{
			setSize(splashImage.getWidth(this), splashImage.getHeight(this));
		}

		Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
		setLocation((screen.width - getWidth()) / 2,
		        (screen.height - getHeight()) / 2);
	}

	public void dispose()
	{
		AWTSplash.disposeInstance(tag);
		super.dispose();
	}

	public void setVisible(boolean visible)
	{
		super.setVisible(visible);
		if (visible)
		{
			paintNow();
		}
	}

	public void setStatus(String status)
	{
		this.status = status;
		if (isConsoleOutputEnabled())
			System.out.println(status);
		paintNow();
	}

	public boolean isConsoleOutputEnabled()
	{
		return consoleOutputEnabled;
	}

	public void setConsoleOutputEnabled(boolean consoleOutputEnabled)
	{
		this.consoleOutputEnabled = consoleOutputEnabled;
	}

	private void paintNow()
	{
		paint(getGraphics());
	}

	public void paint(Graphics g)
	{
		if (g == null)
			return;

		if (splashImage == null)
		{
			g.setColor(getBackground());
			g.drawRect(0, 0, getWidth(), getHeight());

		}
		else
		{
			g.drawImage(splashImage, 0, 0, this);
		}

		g.setFont(font);
		g.setColor(Color.WHITE);
		g.drawString(status, 10, getHeight() - fontMetrics.getDescent() - 1);
	}

	/**
	 * This class allows the user to move a component by dragging it around.
	 */
	private static class DragToMoveListener extends MouseAdapter implements
	        MouseMotionListener
	{
		/**
		 * Creates a new listener and adds it to the given component.
		 *
		 * @param component the component
		 */
		public static void addListener(Component component)
		{
			DragToMoveListener listener = new DragToMoveListener(component);
			component.addMouseListener(listener);
			component.addMouseMotionListener(listener);
		}

		private Component component;
		private int x;
		private int y;

		public DragToMoveListener(Component component)
		{
			this.component = component;
		}

		public void mousePressed(MouseEvent e)
		{
			x = e.getX();
			y = e.getY();
		}

		public void mouseDragged(MouseEvent e)
		{
			component.setLocation(component.getX() + e.getX() - x,
			        component.getY() + e.getY() - y);
		}

		public void mouseMoved(MouseEvent e)
		{
		}
	}
}
