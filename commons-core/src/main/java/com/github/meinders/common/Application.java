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
import java.net.*;
import java.util.concurrent.atomic.*;
import javax.xml.bind.*;

public class Application {
	private static AtomicReference<Application> instance = new AtomicReference<Application>();

	public static Application getInstance() {
		Application result = instance.get();
		if (result == null) {
			result = new Application();
			if (instance.compareAndSet(null, result)) {
				result.initialize();
			} else {
				result = instance.get();
			}
		}
		return result;
	}

	private ApplicationDescriptor descriptor;

	protected Application() {
		descriptor = null;
	}

	protected void initialize() {
		try {
			readApplicationDescriptor();
		} catch (JAXBException e) {
			throw new IOError(e);
		}
	}

	private void readApplicationDescriptor() throws JAXBException {
		JAXBContext context = JAXBContext.newInstance(ApplicationDescriptor.class);
		Unmarshaller unmarshaller = context.createUnmarshaller();
		ClassLoader classLoader = Application.class.getClassLoader();
		URL source = classLoader.getResource("META-INF/application.xml");
		if (source == null) {
			source = classLoader.getResource("application.xml");
		}
		if (source == null) {
			throw new RuntimeException("Can't find 'application.xml'");
		}
		descriptor = (ApplicationDescriptor) unmarshaller.unmarshal(source);
	}

	public ApplicationDescriptor getDescriptor() {
		return descriptor;
	}
}
