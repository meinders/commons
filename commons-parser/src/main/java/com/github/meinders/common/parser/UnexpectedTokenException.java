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
 * An expection thrown when a parser encounters an unexpected kind of token.
 *
 * @since 2005
 * @author Gerrit Meinders
 */
public class UnexpectedTokenException extends ParserException
{
	private Token<?> found;
	private Object expected;

	public <K> UnexpectedTokenException(Token<K> found)
	{
		this(found, (K) null);
	}

	public <K> UnexpectedTokenException(Token<K> found, K expected)
	{
		this(found, new Token<K>(expected, null));
	}

	public <K> UnexpectedTokenException(Token<K> found, Token<K> expected)
	{
		super(createMessage(found, expected));
		if (found == null)
		{
			throw new NullPointerException("found");
		}
		this.found = found;
		this.expected = expected;
	}

	public Token<?> getFound()
	{
		return found;
	}

	public Object getExpected()
	{
		return expected;
	}

	private static <K> String createMessage(Token<K> found, Token<K> expected)
	{
		if (found == null)
		{
			throw new NullPointerException("found");
		}
		else
		{
			String message = "Unexpected token \"" + found.getText() + "\"";
			if (expected == null)
			{
				return message;
			}
			else
			{
				if (expected.getText() == null)
				{
					return message + "; expected " + expected.getKind();
				}
				else
				{
					return message + "; expected \"" + expected.getText()
					        + "\"";
				}
			}
		}
	}
}
