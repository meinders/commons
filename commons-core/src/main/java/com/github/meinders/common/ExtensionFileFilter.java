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
 * Provides a default implementation of a file filter that filters a specific
 * file name extension.
 *
 * @author Gerrit Meinders
 */
public class ExtensionFileFilter extends FileFilter {
    private String[] extensions;

    private boolean includeDirs;

    private boolean caseSensitive;

    /**
     * <p>
     * Constructs a new file filter for the specified extension.
     * </p>
     *
     * @param description the filter's description
     * @param extension the extension
     */
    public ExtensionFileFilter(String description, String extension) {
        this(description, new String[] { extension }, true, false);
    }

    /**
     * <p>
     * Constructs a new file filter for the specified extension.
     * </p>
     *
     * @param description the filter's description
     * @param extension the extension
     * @param includeDirs directories are included if set to <code>true</code>;
     *        otherwise, directories are omitted
     */
    public ExtensionFileFilter(String description, String extension,
            boolean includeDirs) {
        this(description, new String[] { extension }, includeDirs, false);
    }

    /**
     * <p>
     * Constructs a new file filter for the specified extensions.
     * </p>
     *
     * @param description the filter's description
     * @param extensions the extensions
     */
    public ExtensionFileFilter(String description, String[] extensions) {
        this(description, extensions, true, false);
    }

    /**
     * <p>
     * Constructs a new file filter for the specified extensions.
     * </p>
     *
     * @param description the filter's description
     * @param extensions the extensions
     * @param includeDirs directories are included if set to <code>true</code>;
     *        otherwise, directories are omitted
     */
    public ExtensionFileFilter(String description, String[] extensions,
            boolean includeDirs) {
        this(description, extensions, includeDirs, false);
    }

    /**
     * <p>
     * Constructs a new file filter for the specified extensions.
     * </p>
     *
     * @param description the filter's description
     * @param extensions the extensions
     * @param includeDirs directories are included if set to <code>true</code>;
     *        otherwise, directories are omitted
     * @param caseSensitive set whether the filter should be case-sensitive
     */
    public ExtensionFileFilter(String description, String[] extensions,
            boolean includeDirs, boolean caseSensitive) {
        super(description);
        assert extensions != null;
        this.extensions = extensions;
        this.includeDirs = includeDirs;
        this.caseSensitive = caseSensitive;
    }

    public String[] getExtensions() {
        return extensions;
    }

    public File getAppropriateFile(File file) {
        // check for a matching extension
        for (String extension : getExtensions()) {
            if (caseSensitive) {
                if (file.getName().endsWith("." + extension)) {
                    return file;
                }
            } else {
                extension = extension.toLowerCase();
                if (file.getName().toLowerCase().endsWith("." + extension)) {
                    return file;
                }
            }
        }

        // append the filter's first extension
        file = new File(file.getPath() + "." + getExtensions()[0]);
        return file;
    }

    @Override
    public boolean accept(File f) {
        if (includeDirs && f.isDirectory()) {
            return true;
        } else if (!f.isDirectory()) {
            for (String extension : extensions) {
                if (caseSensitive) {
                    if (f.getName().endsWith("." + extension)) {
                        return true;
                    }
                } else {
                    if (f.getName().toLowerCase().endsWith(
                            "." + extension.toLowerCase())) {
                        return true;
                    }
                }
            }
            return false;
        } else {
            return false;
        }
    }
}
