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
import javax.swing.*;

/**
 * TODO: class javadoc
 *
 * @version 0.9 (2005.01.31)
 * @author Gerrit Meinders
 */
public class ColoredListCellRenderer extends JLabel
        implements ListCellRenderer {
    /** The interface for colored list elements. */
    public static interface ColoredElement {
        public Color getColor();
        public Object getValue();
    }

    public ColoredListCellRenderer() {
        super();
        setOpaque(true);
    }

    public Component getListCellRendererComponent(JList list, Object value,
            int index, boolean isSelected, boolean cellHasFocus) {
        ColoredElement coloredElement = null;
        if (value instanceof ColoredElement) {
            coloredElement = (ColoredElement) value;
            value = coloredElement.getValue();
        }
        if (isSelected) {
            setBackground(list.getSelectionBackground());
            setForeground(list.getSelectionForeground());
        } else {
            setBackground(list.getBackground());
            if (coloredElement == null) {
                setForeground(list.getForeground());
            } else {
                setForeground(coloredElement.getColor());
            }
        }

        setText(value.toString());
        setEnabled(list.isEnabled());
        setFont(list.getFont());

        return this;
    }
}

