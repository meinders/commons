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

import java.util.*;

/**
 * Complex OpenGL shape, made up of one or more primitives ({@link Primitive}).
 *
 * @author Gerrit Meinders
 */
public class GLShape {
	private final List<Primitive> primitives;

	public GLShape() {
		primitives = new ArrayList<Primitive>(1);
	}

	public void addPrimitive(Primitive primitive) {
		primitives.add(primitive);
	}

	public List<Primitive> getPrimitives() {
		return primitives;
	}

	/**
	 * Adds all primitives that make up the given shape to this one.
	 *
	 * @param shape Shape to be added.
	 */
	public void addShape(GLShape shape) {
		primitives.addAll(shape.primitives);
	}
}
