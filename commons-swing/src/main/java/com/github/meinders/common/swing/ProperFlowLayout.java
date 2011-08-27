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

package com.github.meinders.common.swing;

import java.awt.*;

/**
 * A layout manager that behaves like a FlowLayout, except that the component's
 * size gets extended it multiple lines are needed for layout.
 *
 * @author Gerrit Meinders
 */
public class ProperFlowLayout extends FlowLayout {
	public ProperFlowLayout() {
		super();
	}

	public ProperFlowLayout(int align) {
		super(align);
	}

	public ProperFlowLayout(int align, int hgap, int vgap) {
		super(align, hgap, vgap);
	}

	public Dimension preferredLayoutSize(Container target) {
		synchronized (target.getTreeLock()) {
			Dimension dim = new Dimension(0, 0);
			if (target.getSize().width == 0) {
				return dim;
			}

			int nmembers = target.getComponentCount();
			boolean firstVisibleComponent = true;

			Insets insets = target.getInsets();
			int maxLineWidth = target.getWidth() - insets.left - insets.right;
			maxLineWidth -= getHgap() * 2;
			int lineWidth = 0;
			int lineHeight = 0;
			boolean firstLine = true;
			// System.out.println("< " + maxLineWidth);
			for (int i = 0; i < nmembers; i++) {
				Component m = target.getComponent(i);
				if (m.isVisible()) {
					Dimension d = m.getPreferredSize();
					// System.out.println(". " + d);
					if (lineWidth == 0 || lineWidth + d.width <= maxLineWidth) {
						// continue on current line
						if (lineWidth > 0) {
							d.width += getHgap();
						}
						lineWidth += d.width;
						lineHeight = Math.max(lineHeight, d.height);
					} else {
						// new line
						dim.width = Math.max(dim.width, lineWidth);
						dim.height += lineHeight + getVgap();
						lineWidth = d.width;
						lineHeight = d.height;
					}
				}
			}
			dim.height += lineHeight;

			dim.width += insets.left + insets.right + getHgap() * 2;
			dim.height += insets.top + insets.bottom + getVgap() * 2;
			// System.out.println(dim);

			return dim;
		}
	}

	public void layoutContainer(Container target) {
		synchronized (target.getTreeLock()) {
			int nmembers = target.getComponentCount();
			Dimension d = new Dimension(0, 0);
			for (int i = 0; i < nmembers; i++) {
				Dimension d2 = target.getComponent(i).getMinimumSize();
				d.width = Math.max(d.width, d2.width);
				d.height = Math.max(d.height, d2.height);
			}
			target.setMinimumSize(d);
			super.layoutContainer(target);
		}
	}
}
