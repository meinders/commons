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
 * An abstract base class for parser implementations.
 *
 * @since 2005
 * @author Gerrit Meinders
 */
public abstract class Parser<K>
{
	/** The lexical analyzer used to scan tokens */
	private Scanner<K> scanner;

	/** The last scanned token */
	protected Token<K> token;

	/** A set of all parser listeners registered with the parser */
	private Set<ParserListener<K>> listeners;

	/**
	 * Constructs a new parser with no scanner yet. Subclasses should initialize
	 * the scanner by calling {@link #initScanner(Scanner)}
	 */
	protected Parser()
	{
	}

	/**
	 * Constructs a new parser using the given scanner.
	 *
	 * @param scanner the scanner
	 */
	public Parser(Scanner<K> scanner) throws IOException
	{
		initScanner(scanner);
	}

	/**
	 * Returns the lexical analyzer used to scan tokens
	 */
	public Scanner<K> getScanner()
	{
		return scanner;
	}

	/**
	 * Sets the scanner to the given scanner and calls {@link #initTokens} to
	 * read the first token(s) from it.
	 *
	 * @param scanner the scanner
	 */
	protected void initScanner(Scanner<K> scanner) throws IOException
	{
		if (scanner == null)
		{
			throw new NullPointerException("scanner");
		}
		this.scanner = scanner;
		initTokens();
	}

	protected abstract void initTokens() throws IOException;

	/**
	 * Accepts the current token.
	 */
	protected void accept() throws ParserException
	{
		fireTokenAccepted(token);
		try
		{
			token = nextToken();
		}
		catch (IOException e)
		{
			throw new ParserException(e);
		}
	}

	/**
	 * Returns the next token from the scanner.
	 */
	protected Token<K> nextToken() throws IOException
	{
		return scanner.nextToken();
	}

	/**
	 * Accepts the current token if it's of one of the given kinds.
	 *
	 * @param kinds the expected token kinds
	 * @throws ParserException if the token is not of the expected kind
	 */
	protected void accept(K... kinds) throws ParserException
	{
		ensure(kinds);
		accept();
	}

	/**
	 * Accepts the current token if it's one of the given strings.
	 *
	 * @param strings the expected strings
	 * @throws ParserException if the token is not equal to one of the strings
	 */
	protected void accept(String... strings) throws ParserException
	{
		ensure(strings);
		accept();
	}

	/**
	 * Checks whether the current token is of one of the given kinds.
	 *
	 * @param kinds the expected token kinds
	 * @throws ParserException if the token is not of the expected kind
	 */
	protected void ensure(K... kinds) throws ParserException
	{
		if (!check(kinds))
		{
			K kind = (kinds.length == 0) ? null : kinds[0];
			throw new UnexpectedTokenException(token, kind);
		}
	}

	/**
	 * Checks whether the current token is equal to one of the given strings.
	 *
	 * @param strings the expected strings
	 * @throws ParserException if the token is equal to one of the strings
	 */
	protected void ensure(String... strings) throws ParserException
	{
		if (!check(strings))
		{
			String string = (strings.length == 0) ? null : strings[0];
			Token<K> expectedToken = new Token<K>(null, string);
			throw new UnexpectedTokenException(token, expectedToken);
		}
	}

	/**
	 * Checks whether the current token is of one of the given kinds.
	 *
	 * @param kinds the expected token kinds
	 * @return <code>true</code> if and only if the token is of one of the given
	 *         kinds
	 */
	protected boolean check(K... kinds) throws ParserException
	{
		for (K kind : kinds)
		{
			if (token.getKind() == kind)
			{
				return true;
			}
		}
		return false;
	}

	/**
	 * Checks whether the current token is equal to one of the given strings.
	 *
	 * @param strings the expected strings
	 * @return <code>true</code> if and only if the token is equal to one of the
	 *         given strings
	 */
	protected boolean check(String... strings) throws ParserException
	{
		for (String string : strings)
		{
			if (token.getText().equals(string))
			{
				return true;
			}
		}
		return false;
	}

	/**
	 * Accepts the current token if it's of one of the given kinds.
	 *
	 * @param kinds the token kinds to ignore
	 */
	protected void ignore(K... kinds) throws ParserException
	{
		if (contains(kinds, token.getKind()))
		{
			accept();
		}
	}

	/**
	 * Accepts all tokens as long as the current token is one of the given
	 * kinds.
	 *
	 * @param kinds the token kinds to ignore
	 */
	protected void ignoreAll(K... kinds) throws ParserException
	{
		while (contains(kinds, token.getKind()))
		{
			accept();
		}
	}

	/**
	 * Accepts all tokens until the current token is one of the given kinds.
	 *
	 * @param kinds the token kinds to ignore
	 */
	protected void ignoreUntil(K... kinds) throws ParserException
	{
		while (!contains(kinds, token.getKind()))
		{
			accept();
		}
	}

	/**
	 * Returns whether the given array contains the given value.
	 *
	 * @param array the array
	 * @param value the value
	 * @return <code>true</code> if the array contains value, <code>false</code>
	 *         otherwise
	 */
	private <T> boolean contains(T[] array, T value)
	{
		for (int i = 0; i < array.length; i++)
		{
			if (array[i] == value)
			{
				return true;
			}
		}
		return false;
	}

	public boolean hasParserListeners()
	{
		return listeners != null;
	}

	/**
	 * Adds the given listener from the set of listeners.
	 *
	 * @param l the listener
	 */
	public void addParserListener(ParserListener<K> l)
	{
		if (listeners == null)
		{
			listeners = new LinkedHashSet<ParserListener<K>>();
		}
		listeners.add(l);
	}

	/**
	 * Removes the given listener from the set of listeners.
	 *
	 * @param l the listener
	 */
	public void removeParserListener(ParserListener<K> l)
	{
		if (listeners == null)
		{
			return;
		}
		listeners.remove(l);
		if (listeners.isEmpty())
		{
			listeners = null;
		}
	}

	/**
	 * Notifies all registered parser listeners that the parser has started.
	 */
	protected void fireParserStarted()
	{
		if (!hasParserListeners())
			return;
		for (ParserListener<K> l : listeners)
		{
			l.parserStarted();
		}
	}

	/**
	 * Notifies all registered parser listeners that the given token has been
	 * accepted.
	 *
	 * @param token the token
	 */
	protected void fireTokenAccepted(Token<K> token)
	{
		if (!hasParserListeners())
			return;
		for (ParserListener<K> l : listeners)
		{
			l.tokenAccepted(token);
		}
	}

	/**
	 * Notifies all registered parser listeners that the parser has completed.
	 */
	protected void fireParserCompleted()
	{
		if (!hasParserListeners())
			return;
		for (ParserListener<K> l : listeners)
		{
			l.parserCompleted();
		}
	}
}
