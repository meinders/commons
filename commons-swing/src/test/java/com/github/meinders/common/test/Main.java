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

package com.github.meinders.common.test;

import java.awt.*;
import javax.swing.*;

import com.github.meinders.common.*;
import com.github.meinders.common.swing.*;

/**
 * TODO: javadoc
 *
 * @version 0.8 (2005.02.01)
 * @author Gerrit Meinders
 */
public class Main implements Runnable
{
	public static void main(String[] args)
	{
		SwingUtilities.invokeLater(new Main());
	}

	public Main()
	{
	}

	public void run()
	{
		testSplash();
		testWorker();
	}

	private void testWorker()
	{
		Worker<TestObject> slowWorker = new TestWorker( 143, true);
		final Worker<TestObject> fastWorker = new TestWorker(3729751, false);
		final Worker<TestObject> consoleWorker = new TestWorker(3729751, false);

		final ProgressDialog<TestObject> dialog = new ProgressDialog<TestObject>(
		        (Frame) null, "com.github.meinders.common.test - Worker classes");
		dialog.setWorker(slowWorker);
		dialog.setVisible(true);

		slowWorker.addWorkerListener(new WorkerListener<TestObject>()
		{
			public void stateChanged(WorkerEvent<TestObject> e)
			{
				if (e.getType() == WorkerEvent.EventType.FINISHED)
				{
					dialog.setWorker(fastWorker);
					dialog.setMinimumProgressInterval(100);
					fastWorker.start();
				}
			}
		});
		fastWorker.addWorkerListener(new WorkerListener<TestObject>()
		{
			public void stateChanged(WorkerEvent<TestObject> e)
			{
				if (e.getType() == WorkerEvent.EventType.FINISHED)
				{
					consoleWorker.start();
				}
			}
		});
		consoleWorker.addWorkerListener(new WorkerListener<TestObject>()
		{
			public void stateChanged(WorkerEvent<TestObject> e)
			{
				if (e.getType() != WorkerEvent.EventType.PROGRESS)
				{
					System.out.println(e.getMessage());
				}
				if (e.getType() == WorkerEvent.EventType.FINISHED)
				{
					dialog.dispose();
					// Splash.getInstance().dispose();
				}
			}
		});

		slowWorker.start();
	}

	private void testSplash()
	{
		// Splash.getInstance().setVisible(false);
		//
		// final String tag = "testSplash";
		// Splash.getInstance(tag).setSplashImage(
		// getClass().getResource("/images/splash.png"));
		// Splash.getInstance(tag).setTitle("com.github.meinders.common.test - Splash class");
		// Splash.getInstance(tag).setVisible(true);
		// try
		// {
		// for (int i = 10; i > 0; i--)
		// {
		// Splash.getInstance(tag).setStatus(createTextMessage());
		// Thread.currentThread().sleep((int) (Math.random() * 200 + 300));
		// }
		// }
		// catch (Exception e)
		// {
		// }
		// Splash.getInstance(tag).dispose();
		//
		// Splash.getInstance().setVisible(true);
	}

	private String createTextMessage()
	{
		String[] verbs = {
		        "Initializing", "Capitalizing", "Extracting", "Researching",
		        "Loading", "Transforming", "Scanning", "Paraphrasing",
		        "Resizing", "Scaling", "Locating", "Internationalizing",
		        "Localizing"
		};

		String[] nouns = {
		        "services", "objects", "classes", "interfaces",
		        "task scheduler", "microwave", "mass storage devices",
		        "internet connection", "virtual machine",
		        "operating system resources", "device drivers",
		        "voice analyzer", "rethorical subroutines",
		        "transphasic modulator", "Bussard collectors",
		        "spatial curvature algorithms"
		};

		String verb = verbs[(int) (Math.random() * verbs.length)];
		String noun = nouns[(int) (Math.random() * nouns.length)];
		return verb + " " + noun + "...";
	}
}
