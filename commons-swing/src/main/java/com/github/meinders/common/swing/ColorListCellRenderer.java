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

import com.github.meinders.common.*;

/**
 * A list cell renderer displaying a list of colors by using each color as the
 * background color for its cell.
 *
 * @version 0.9 (2006.02.22)
 * @author Gerrit Meinders
 */
public class ColorListCellRenderer extends JLabel implements ListCellRenderer {
    /** The localized resources used by this renderer. */
    private ResourceUtilities resources = new ResourceUtilities(
            "com.github.meinders.common.swing.ColorListCellRenderer");

    /** The background color of the renderer when it was created. */
    private Color background;

    /** Constructs a new color list cell renderer. */
    public ColorListCellRenderer() {
        setOpaque(true);
        background = getBackground();
    }

    /**
     * <p>Sets the background color.
     *
     * <p>This method is customized to change the UI's default behavior
     * slightly. <strong>Use {@link #setBackground(Color, boolean)} in stead.
     * </strong>
     *
     * @param background the background color
     */
    public void setBackground(Color background) {
        if (getBackground() == this.background ||
                getBackground().equals(this.background)) {
            super.setBackground(background);
        } else {
            setForegroundForBackground(background);
            super.setBackground(getBackground());
        }
    }

    /**
     * Sets the background color.
     *
     * @param background the background color
     * @param ignored (ignored)
     */
    public void setBackground(Color background, boolean ignored) {
        super.setBackground(background);
        this.background = background;
    }

    /**
     * Sets the foreground color to either white or black, depending on the
     * luminance of the background color.
     *
     * @param background the background color
     */
    private void setForegroundForBackground(Color background) {
        // set foreground color
        int luminance = (int) (background.getRed() * 0.2126f +
                background.getGreen() * 0.7152f +
                background.getBlue() * 0.0724f);
        if (luminance >= 128) {
            setForeground(Color.BLACK);
        } else {
            setForeground(Color.WHITE);
        }
    }

    /**
     * Return a component that has been configured to display the specified
     * value.
     *
     * @param list the JList being painted
     * @param value the value returned by list.getModel().getElementAt(index)
     * @param index the cell's index
     * @param isSelected {@code true} if the specified cell is selected
     * @param cellHasFocus {@code true} if the specified cell has the focus
     */
    public Component getListCellRendererComponent(JList list, Object value,
            int index, boolean isSelected, boolean cellHasFocus) {
        if (value == null || value instanceof Color) {
            if (value == null) {
                super.setBackground(background);
                setText(resources.getString(
                        "ColorListCellRenderer.customColor"));
            } else {
                Color color = (Color) value;
                // setForegroundForBackground(color);
                super.setBackground(color);
                setText(" ");
                // setText(color.toString());
            }
        } else {
            throw new AssertionError("ColorListCellRenderer can only render " +
                    "java.awt.Color objects and null, but a " +
                    value.getClass() + " object was found.");
        }
        if (isSelected) {
            setBorder(BorderFactory.createMatteBorder(2, 2, 2, 2,
                    list.getSelectionBackground()));
        } else {
            setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
        }
        return this;
    }
}

