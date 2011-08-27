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
 * An abstract base class for LL(k) parser implementations.
 *
 * @since 2005
 * @author Gerrit Meinders
 */
public abstract class LLParser<K> extends Parser<K>
{
	/** The last scanned token */
	protected List<Token<K>> lookaheads;

	/**
	 * Constructs a new LL-parser with the given number of lookaheads and no
	 * scanner yet. Subclasses should initialize the scanner by calling
	 * {@link #initScanner(Scanner)}.
	 *
	 * @param lookaheads the number of lookaheads
	 */
	protected LLParser(int lookaheads)
	{
		super();
		this.lookaheads = new ArrayList<Token<K>>();
		for (int i = 0; i < lookaheads; i++)
		{
			this.lookaheads.add(null);
		}
	}

	/**
	 * Constructs a new LL-parser with the given number of lookaheads and using
	 * the given scanner.
	 *
	 * @param scanner the scanner
	 * @param lookaheads the number of lookaheads
	 */
	public LLParser(Scanner<K> scanner, int lookaheads) throws IOException
	{
		this(lookaheads);
		initScanner(scanner);
	}

	/**
	 * Returns the lookahead at the given index (0 being the first lookahead).
	 *
	 * @param index the index
	 */
	public Token<K> getLookahead(int index)
	{
		return lookaheads.get(index);
	}

	/**
	 * Returns the parser's current lookaheads.
	 */
	public List<Token<K>> getLookaheads()
	{
		return Collections.unmodifiableList(lookaheads);
	}

	protected void initTokens() throws IOException
	{
		Scanner<K> scanner = getScanner();
		for (int i = 0; i < lookaheads.size(); i++)
		{
			lookaheads.set(i, scanner.nextToken());
		}
		token = lookaheads.get(0);
	}

	/**
	 * Accepts the current token.
	 */
	protected void accept() throws ParserException
	{
		fireTokenAccepted(token);
		lookaheads.remove(0);
		try
		{
			lookaheads.add(nextToken());
			token = lookaheads.get(0);
		}
		catch (IOException e)
		{
			throw new ParserException(e);
		}
	}

	/**
	 * Ensures that the given lookahead matches on of the the given kinds and
	 * throws an exception if it doesn't.
	 *
	 * @param lookahead the lookahead index
	 * @param kinds the expected token kinds
	 * @throws ParserException if the token is not of the expected kind
	 * @throws IllegalArgumentException if lookahead is greater or equal to the
	 *             number of lookaheads
	 */
	protected void ensure(int lookahead, K... kinds) throws ParserException
	{
		if (!check(lookahead, kinds))
		{
			throw new UnexpectedTokenException(token, kinds[0]);
		}
	}

	/**
	 * Ensures that the given lookahead matches on of the the given strings and
	 * throws an exception if it doesn't.
	 *
	 * @param lookahead the lookahead index
	 * @param strings the expected token kinds
	 * @throws ParserException if the token is not of the expected kind
	 * @throws IllegalArgumentException if lookahead is greater or equal to the
	 *             number of lookaheads
	 */
	protected void ensure(int lookahead, String... strings)
	        throws ParserException
	{
		if (!check(lookahead, strings))
		{
			Token<K> expectedToken = new Token<K>(null, strings[0]);
			throw new UnexpectedTokenException(token, expectedToken);
		}
	}

	/**
	 * Checks whether the given lookahead matches any of the given kinds.
	 *
	 * @param lookahead the lookahead index
	 * @param kinds the expected token kinds
	 * @return <code>true</code> if and only if the token is of one of the given
	 *         kinds
	 * @throws IllegalArgumentException if lookahead is greater or equal to the
	 *             number of lookaheads
	 */
	protected boolean check(int lookahead, K... kinds) throws ParserException
	{
		if (lookahead > lookaheads.size())
		{
			throw new IllegalArgumentException("kinds");
		}
		K matchingKind = lookaheads.get(lookahead).getKind();
		for (K kind : kinds)
		{
			if (kind == matchingKind)
			{
				return true;
			}
		}
		return false;
	}

	/**
	 * Checks whether the given lookahead matches any of the given strings.
	 *
	 * @param lookahead the lookahead index
	 * @param strings the expected strings
	 * @return <code>true</code> if and only if the token is equal to one of the
	 *         strings
	 * @throws IllegalArgumentException if lookahead is greater or equal to the
	 *             number of lookaheads
	 */
	protected boolean check(int lookahead, String... strings)
	        throws ParserException
	{
		if (lookahead > lookaheads.size())
		{
			throw new IllegalArgumentException("strings");
		}
		String matchString = lookaheads.get(lookahead).getText();
		for (String string : strings)
		{
			if (matchString.equals(string))
			{
				return true;
			}
		}
		return false;
	}
}
