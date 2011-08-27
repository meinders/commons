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

package com.github.meinders.common;

import org.w3c.dom.*;

/**
 * This class provdes methods to add whitespace to a DOM document to improve
 * human-readability. Using the methods in this class on documents that are
 * already formatted with extra whitespace may cause problems.
 *
 * @version 0.9 (2006.03.11)
 * @author Gerrit Meinders
 */
@Deprecated
public class XMLFormatter {
	/**
	 * Formats an XML document such that it becomes humanly-readable by adding
	 * whitespace. Text content is formatted (by indenting each line).
	 *
	 * FIXME: add a newline before the document element (if needed)
	 *
	 * @param document the document to be formatted
	 */
	public static void formatXML(Document document) {
		formatXML(document, true);
	}

	/**
	 * Formats an XML document such that it becomes humanly-readable by adding
	 * whitespace.
	 *
	 * FIXME: add a newline before the document element (if needed)
	 *
	 * @param document the document to be formatted
	 * @param formatText indicates whether text should be formatted
	 */
	public static void formatXML(Document document, boolean formatText) {
		Element root = document.getDocumentElement();
		// document.insertBefore(document.createTextNode("\n"), root);
		/*
		 * The line above sometimes causes the following exception:
		 * org.w3c.dom.DOMException: HIERARCHY_REQUEST_ERR: An attempt was made
		 * to insert a node where it is not permitted.
		 */
		formatXML(document, root, 0, formatText);
	}

	/**
	 * Formats an XML document such that it becomes humanly-readable by adding
	 * whitespace. This method is intended to be used on documents generated
	 * from code, and may result in excessive whitespace when used on a document
	 * that already contains whitespace.
	 *
	 * @see #formatXML(Document)
	 *
	 * @param document the document to be formatted
	 * @param element the element from <code>document</code> that is to be
	 *            formatted
	 * @param depth the depth of <code>element</code> in the document tree
	 * @param formatText indicates whether text should be formatted
	 */
	private static void formatXML(Document document, Element element,
	        int depth, boolean formatText) {
		if (element == null)
			return;
		NodeList children = element.getChildNodes();

		boolean containsElements = false;

		int maxTextLength = 80 - 2 * (depth + 1);
		for (int i = 0; i < children.getLength(); i += 2) {
			Node childNode = children.item(i);
			if (childNode instanceof Element) {
				Element child = (Element) childNode;

				containsElements = true;
				if (child.hasChildNodes()) {
					formatXML(document, child, depth + 1, formatText);
				}

			} else if (childNode instanceof Text) {
				if (!formatText) {
					continue;
				}

				Text child = (Text) childNode;
				String text = child.getData();
				if (text.length() > maxTextLength) {
					// break up text in lines of no more than maxLength chars
					String[] words = text.split(" ");
					StringBuffer lineBuffer = new StringBuffer();
					int lineLength = 0;
					for (String word : words) {
						if (lineLength > 0) {
							lineLength++;
						}
						lineLength += word.length();
						if (lineLength > maxTextLength) {
							// newline and indent
							lineBuffer.append("\n");
							for (int j = 0; j <= depth; j++) {
								lineBuffer.append("  ");
							}
							lineLength = word.length();
						} else {
							if (lineLength > word.length()) {
								lineBuffer.append(" ");
							}
						}
						lineBuffer.append(word);
					}
					child.setData(lineBuffer.toString());
				}
			}

			StringBuffer whitespace = new StringBuffer("\n");
			for (int j = 0; j <= depth; j++) {
				whitespace.append("  ");
			}
			element.insertBefore(
			        document.createTextNode(whitespace.toString()), childNode);
		}

		if (containsElements) {
			StringBuffer whitespace = new StringBuffer("\n");
			for (int j = 0; j < depth; j++) {
				whitespace.append("  ");
			}
			element.insertBefore(
			        document.createTextNode(whitespace.toString()), null);
		}
	}
}
