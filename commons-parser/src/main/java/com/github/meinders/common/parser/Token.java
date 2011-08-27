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
 * A token scanned by a lexical analyzer. Generics are used to support any type
 * for token kinds, while preserving type safety.
 *
 * @since 2005
 * @author Gerrit Meinders
 */
public class Token<K>
{
	private K kind;
	private String text;
	private int position;
	private int line;
	private int column;

	/**
	 * Constructs a new token with the given kind and text.
	 *
	 * @param kind the kind of token
	 * @param text the scanned text
	 */
	public Token(K kind, String text)
	{
		this(kind, text, -1);
	}

	/**
	 * Constructs a new token with the given kind, text and position.
	 *
	 * @param kind the kind of token
	 * @param text the scanned text
	 * @param position the character position at which the token was scanned
	 */
	public Token(K kind, String text, int position)
	{
		this(kind, text, position, -1);
	}

	/**
	 * Constructs a new token with the given kind, text, position and line.
	 *
	 * @param kind the kind of token
	 * @param text the scanned text
	 * @param position the character position at which the token was scanned
	 * @param line the line number of the line where the token was scanned
	 */
	public Token(K kind, String text, int position, int line)
	{
		this(kind, text, position, line, -1);
	}

	/**
	 * Constructs a new token with the given kind, text, position and line.
	 *
	 * @param kind the kind of token
	 * @param text the scanned text
	 * @param position the character position at which the token was scanned
	 * @param line the line number of the line where the token was scanned
	 * @param column the column number of the line where the token was scanned
	 */
	public Token(K kind, String text, int position, int line, int column)
	{
		this.kind = kind;
		this.text = text;
		this.position = position;
		this.line = line;
		this.column = column;
	}

	/**
	 * Returns the token's kind.
	 *
	 * @return the token kind
	 */
	public K getKind()
	{
		return kind;
	}

	/**
	 * Returns the token's text.
	 *
	 * @return the text
	 */
	public String getText()
	{
		return text;
	}

	/**
	 * Returns the character position at which the token was scanned, starting
	 * at 0 for the first character.
	 *
	 * @return the line number
	 */
	public int getPosition()
	{
		return position;
	}

	/**
	 * Returns the line number at which the token was scanned, starting at 0 for
	 * the first line.
	 *
	 * @return the line number
	 */
	public int getLine()
	{
		return line;
	}

	/**
	 * Returns the column number at which the token was scanned, starting at 0
	 * for the first column.
	 *
	 * @return the column number
	 */
	public int getColumn()
	{
		return column;
	}

	public String toString()
	{
		if (getPosition() == -1)
		{
			return "Token[kind=" + getKind() + ",text=" + getText() + "]";
		}
		else
		{
			if (getLine() == -1)
			{
				return "Token[kind=" + getKind() + ",text=" + getText()
				        + ",position=" + getPosition() + "]";
			}
			else
			{
				if (getColumn() == -1)
				{
					return "Token[kind=" + getKind() + ",text=" + getText()
					        + ",position=" + getPosition() + ",line="
					        + getLine() + "]";
				}
				else
				{
					return "Token[kind=" + getKind() + ",text=" + getText()
					        + ",position=" + getPosition() + ",line="
					        + getLine() + ",column=" + getColumn() + "]";
				}
			}
		}
	}
}
