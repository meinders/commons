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
 * A file filter that accepts XML files with of specific document type.
 *
 * @version 0.8 (2004.12.18)
 * @author Gerrit Meinders
 */
public class XMLFileFilter extends ExtensionFileFilter {
    private String publicId;

    public XMLFileFilter(String description, String publicId) {
        super(description, "xml");
        assert publicId != null;
        this.publicId = publicId;
    }

    public String getPublicId() {
        return publicId;
    }

    public boolean accept(File f) {
        if (super.accept(f)) {
            if (!f.isFile()) {
                return true;
            }

            // check the file's public id
            /*
             * NOTE: This should be done using a SAX parser that supports the
             * DeclHandler interface, but that's currently not supported by the
             * J2SE. A simple home-made solution is used in stead...
             */

            try {
                Reader in = new BufferedReader(new FileReader(f));

                // read the first part of the file
                char[] buffer = new char[0x200];
                int off = 0;
                int len = buffer.length;
                while (len > 0) {
                    int charsRead = in.read(buffer, off, len);
                    if (charsRead == -1) {
                        break;
                    }
                    off += charsRead;
                    len -= charsRead;
                }

                // fix UTF-16... (stupid Reader classes :/ -- )
                // TODO: hack
                /* How about checking for UTF-16 *before* reading the file? */
                if (buffer[0] == 0xff && buffer[1] == 0xfe) {
                    // little-endian
                    char[] fixedBuffer = new char[buffer.length / 2 - 1];
                    for (int i = 0; i < buffer.length / 2 - 1; i++) {
                        fixedBuffer[i] = (char) (buffer[(i + 1) * 2] + buffer[(i + 1) * 2 + 1] * 0x100);
                    }
                    buffer = fixedBuffer;
                } else if (buffer[0] == 0xfe && buffer[1] == 0xff) {
                    // big-endian
                    char[] fixedBuffer = new char[buffer.length / 2 - 1];
                    for (int i = 0; i < buffer.length / 2 - 1; i++) {
                        fixedBuffer[i] = (char) (buffer[(i + 1) * 2 + 1] + buffer[(i + 1) * 2] * 0x100);
                    }
                    buffer = fixedBuffer;
                }

                // reject UTF-8 with BOM (unsupported by Crimson JAXP parser)
                if (buffer[0] == 0xef && buffer[1] == 0xbb && buffer[2] == 0xbf) {
                    return false;
                }

                // return whether public id was found
                String content = new String(buffer);
                return content.indexOf(publicId) > -1;

            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        } else {
            return false;
        }
    }

    /**
     * XMLFileFilters are considered equal if they match the same public id.
     */
    public boolean equals(Object o) {
        if (o != null && o instanceof XMLFileFilter) {
            XMLFileFilter x = (XMLFileFilter) o;
            return publicId.equals(x.publicId);

        } else {
            return false;
        }
    }
}
