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

import com.github.meinders.common.math.*;

public class Vertex {
	private final Vector3f position;
	private final Vector3f normal;
	private final TexCoord2f texCoord;

	public Vertex(Vector3f position) {
		this(position, null, null);
	}

	public Vertex(Vector3f position, TexCoord2f texCoord) {
		this(position, null, texCoord);
	}

	public Vertex(Vector3f position, Vector3f normal, TexCoord2f texCoord) {
		super();
		this.position = position;
		this.normal = normal;
		this.texCoord = texCoord;
	}

	public Vector3f getPosition() {
		return position;
	}

	public Vector3f getNormal() {
		return normal;
	}

	public TexCoord2f getTexCoord() {
		return texCoord;
	}
}
