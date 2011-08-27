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
import javax.swing.table.*;

/**
 * A table cell renderer that wraps another renderer, adding padding to the
 * rendered table cells.
 *
 * @author Gerrit Meinders
 */
public class PaddedCellRenderer implements TableCellRenderer {
    /** The wrapped tabel cell renderer. */
    private TableCellRenderer renderer;

    /** The horizontal padding amount. */
    private int padX;

    /** The vertical padding amount. */
    private int padY;

    /**
     * Constructs a new padded cell renderer that wraps the given table cell
     * renderer and adds the given amount of padding.
     *
     * @param renderer the renderer to be wrapped
     * @param padX the horizontal padding
     * @param padY the vertical padding
     */
    public PaddedCellRenderer(TableCellRenderer renderer, int padX, int padY) {
        this.renderer = renderer;
        this.padX = padX;
        this.padY = padY;
    }

    /**
     * Returns the component used for drawing the cell.
     *
     * @see TableCellRenderer#getTableCellRendererComponent
     */
    public Component getTableCellRendererComponent(JTable table, Object value,
            boolean isSelected, boolean hasFocus, int row, int column) {
        JComponent component = (JComponent) renderer
                .getTableCellRendererComponent(table, value, isSelected,
                        hasFocus, row, column);
        component.setBorder(BorderFactory.createCompoundBorder(component
                .getBorder(), BorderFactory.createEmptyBorder(padY, padX, padY,
                padX)));
        return component;
    }
}
