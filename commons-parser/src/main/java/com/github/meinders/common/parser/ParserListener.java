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

/**
 * A listener that is notified by a {@link Parser} of the parser's status.
 *
 * @since 2005
 * @author Gerrit Meinders
 */
public interface ParserListener<K>
{
	public void parserStarted();

	public void tokenAccepted(Token<K> token);

	public void parserCompleted();
}
