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

import java.io.*;

/**
 * This class is a union of both the FileFilter class and the FileFilter
 * interface found in the J2SE, and can be used in place of either of those
 * classes. Through the added getAppropriateFile method, subclasses can correct
 * file names that don't match the filter, for example by adding the proper
 * extension.
 *
 * @version 0.8 (2006.02.20)
 * @author Gerrit Meinders
 */
public abstract class FileFilter extends javax.swing.filechooser.FileFilter
        implements java.io.FileFilter {
    private String description;

    /**
     * Constructs a new file filter with the given description.
     *
     * @param description the description
     */
    public FileFilter(String description) {
        setDescription(description);
    }

    /**
     * Sets the description of the file filter.
     *
     * @param description the description
     */
    public void setDescription(String description) {
        assert description != null;
        this.description = description;
    }

    /**
     * Returns the description of the file filter.
     *
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Returns a the given file or a more appropriate alternative, for example
     * with the proper extension for this file filter.
     *
     * @param file the file
     * @return the file or a more appropriate alternative
     */
    public File getAppropriateFile(File file) {
        return file;
    }
}

