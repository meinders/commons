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
import java.util.*;

/**
 * An abstract class for building simple parser implementations with the added
 * ability to 'push back' tokens to be parsed again before new tokens are
 * scanned.
 *
 * @since 2005
 * @author Gerrit Meinders
 */
public abstract class PushParser<K> extends Parser<K>
{
	private LinkedList<Token<K>> pushbackStack;

	/**
	 * Constructs a new parser using the given scanner.
	 *
	 * @param scanner the scanner
	 */
	public PushParser(Scanner<K> scanner) throws IOException
	{
		super(scanner);
		pushbackStack = new LinkedList<Token<K>>();
	}

	/**
	 * Accepts the current token.
	 */
	protected void accept() throws ParserException
	{
		if (pushbackStack.isEmpty())
		{
			super.accept();
		}
		else
		{
			token = pushbackStack.removeFirst();
		}
	}

	/**
	 * Pushes the given tokens back, such that the tokens will be read back in
	 * the order in which they are given. The current token will be pushed back
	 * first and the first given token will become the current token.
	 *
	 * @param tokens the tokens
	 */
	protected void pushback(Token<K>... tokens)
	{
		pushbackStack.addFirst(token);
		for (int i = tokens.length - 1; i > 0; i--)
		{
			pushbackStack.addFirst(tokens[i]);
		}
		token = tokens[0];
	}
}
