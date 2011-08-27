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

package com.github.meinders.common.parser;

import java.io.*;

/**
 * An interface for a lexical analyzer. The interface is based on the one used
 * by JFlex.
 *
 * @since 2005
 * @author Gerrit Meinders
 */
public interface Scanner<K>
{
	public Token<K> nextToken() throws IOException;

	public void close() throws IOException;
}
