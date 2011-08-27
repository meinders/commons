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

import java.awt.*;
import java.awt.font.*;
import java.awt.geom.*;
import java.text.*;
import java.util.*;

public class Text extends GLShape {
	public Text(String text, Font font, float width) {
		super();
		long start = System.nanoTime();

		System.out.println("Subdividing into lines...");
		String[] paragraphs = text.split("\r|\n|(\r\n)");

		System.out.println("Creating text shape...");
		Collection<Shape> textShapes = getTextShapes(font, width, paragraphs);

		System.out.println("Tesselating...");
		Tesselator tesselator = new Tesselator(this);
		for (Shape textShape : textShapes) {
			tesselator.tesselate(textShape);
		}

		long end = System.nanoTime();
		System.out.println("Done. (" + ((end - start) / 1000000) + "ms)");
	}

	private Collection<Shape> getTextShapes(Font font, float width,
	        final String[] lines) {
		Collection<Shape> result = new ArrayList<Shape>();

		FontRenderContext context = new FontRenderContext(
		        new AffineTransform(), true, true);

		float y = 0.0f;
		boolean firstLine = true;

		float paragraphSpacing = 0;
		for (String line : lines) {
			if (line.isEmpty()) {
				y += paragraphSpacing;
			} else {
				AttributedString attributedText = new AttributedString(line);
				attributedText.addAttribute(TextAttribute.FONT, font);

				AttributedCharacterIterator attributedTextIterator = attributedText.getIterator();
				LineBreakMeasurer measurer = new LineBreakMeasurer(
				        attributedTextIterator, context);

				while (measurer.getPosition() < attributedTextIterator.getEndIndex()) {
					TextLayout textLayout = measurer.nextLayout(width);

					if (!firstLine) {
						y += textLayout.getLeading();
					} else {
						firstLine = false;
					}

					y += textLayout.getAscent();

					AffineTransform transform = new AffineTransform(1.0, 0.0,
					        0.0, 1.0, 0.0, y);
					result.add(textLayout.getOutline(transform));

					y += textLayout.getDescent();

					paragraphSpacing = textLayout.getAscent()
					        + textLayout.getDescent();
				}
			}
		}

		return result;
	}
}
