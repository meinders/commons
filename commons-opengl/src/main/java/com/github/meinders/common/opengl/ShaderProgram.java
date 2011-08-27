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
import javax.media.opengl.*;

public class ShaderProgram
{
	private GL2ES2 gl;

	private int program;

	private final Map<String, Integer> shaders;

	private boolean linked = false;

	public ShaderProgram(GL gl)
	{
		GL2ES2 gl2 = gl.getGL2ES2();
		this.gl = gl2;
		program = gl2.glCreateProgram();
		shaders = new HashMap<String, Integer>();
	}

	public void attach(int type, String name, InputStream stream)
	{
		String source = loadResource(stream);

		int shader = gl.glCreateShader(type);
		shaders.put(name, shader);

		gl.glShaderSource(shader, 1, new String[] {
			source.toString()
		}, new int[] {
			source.length()
		}, 0);

		gl.glCompileShader(shader);
		printShaderLog(gl, shader);

		gl.glAttachShader(program, shader);
		linked = false;
	}

	private void printShaderLog(GL gl, int shader)
	{
		GL2ES2 gl2 = gl.getGL2ES2();

		int[] length = new int[1];
		gl2.glGetShaderiv( shader, GL2ES2.GL_INFO_LOG_LENGTH, length, 0 );

		if (length[0] > 0)
		{
			byte[] infoLog = new byte[length[0]];
			gl2.glGetShaderInfoLog(shader, length[0], length, 0, infoLog, 0);

			System.err.print(new String(infoLog));
		}
	}

	private void printProgramLog(GL gl, int shader)
	{
		GL2ES2 gl2 = gl.getGL2ES2();

		int[] length = new int[1];
		gl2.glGetProgramiv(shader, GL2ES2.GL_INFO_LOG_LENGTH, length, 0);

		if (length[0] > 0)
		{
			byte[] infoLog = new byte[length[0]];
			gl2.glGetProgramInfoLog(shader, length[0], length, 0, infoLog, 0);

			System.err.print(new String(infoLog));
		}
	}

	private String loadResource(InputStream source)
	{
		InputStreamReader in = new InputStreamReader(source);
		StringWriter out = new StringWriter();

		char[] buffer = new char[10000];
		int read;
		try
		{
			while ((read = in.read(buffer)) != -1)
			{
				out.write(buffer, 0, read);
			}
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}

		return out.toString().replaceAll("\r\n?", "\n");
	}

	public void enable()
	{
		if (!linked)
		{
			gl.glLinkProgram(program);
			printProgramLog(gl, program);
			linked = true;
		}

		gl.glUseProgram(program);
	}

	public void setUniform(String name, int value)
	{
		int location = gl.glGetUniformLocation(program, name);
		gl.glUniform1i(location, value);
	}

	public void setUniform(String name, double value)
	{
		int location = gl.glGetUniformLocation(program, name);
		gl.glUniform1f(location, (float) value);
	}

	public void setUniform(String name, double x, double y)
	{
		int location = gl.glGetUniformLocation(program, name);
		gl.glUniform2f(location, (float) x, (float) y);
	}

	public void disable()
	{
		disable(gl);
	}

	public static void disable(GL gl)
	{
		GL2ES2 gl2 = gl.getGL2ES2();
		gl2.glUseProgram(0);
	}
}
