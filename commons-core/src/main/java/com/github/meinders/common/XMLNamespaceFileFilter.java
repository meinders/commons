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
import javax.xml.parsers.*;

import org.xml.sax.*;
import org.xml.sax.helpers.*;

/**
 * A file filter that accepts XML files based on the namespace URI of the
 * document element.
 *
 * @version 0.9 (2006.02.20)
 * @author Gerrit Meinders
 */
public class XMLNamespaceFileFilter extends ExtensionFileFilter {
    private String namespaceURI;

    private SAXParser parser;

    public XMLNamespaceFileFilter(String description, String namespaceURI) {
        super(description, "xml");
        if (namespaceURI == null) {
            throw new NullPointerException("namespaceURI");
        }
        this.namespaceURI = namespaceURI;
        SAXParserFactory factory = SAXParserFactory.newInstance();
        factory.setNamespaceAware(true);
        try {
            parser = factory.newSAXParser();
        } catch (Exception e) {
            throw new AssertionError(e);
        }
    }

    public String getNamespaceURI() {
        return namespaceURI;
    }

    @Override
    public boolean accept(File f) {
        if (super.accept(f)) {
            if (!f.isFile()) {
                return true;
            }
            String otherNamespaceURI = getNamespaceURI(f);
            return namespaceURI.equals(otherNamespaceURI);
        } else {
            return false;
        }
    }

    /**
     * Returns the namespace URI of the document element.
     */
    private String getNamespaceURI(File file) {
        NamespaceRetrievalHandler handler = new NamespaceRetrievalHandler();
        try {
            parser.parse(file, handler);
        } catch (IOException e) {
            System.err.println("WARNING: " + e);
            return null;
        } catch (SAXException e) {
            // thrown to abort the parser; no action required
            parser.reset();
        }
        return handler.getNamespaceURI();
    }

    /**
     * XMLNamespaceFileFilters are considered equal if they match the same
     * namespace URI.
     */
    @Override
    public boolean equals(Object o) {
        if (o != null && o instanceof XMLNamespaceFileFilter) {
            XMLNamespaceFileFilter other = (XMLNamespaceFileFilter) o;
            return namespaceURI.equals(other.namespaceURI);

        } else {
            return false;
        }
    }

    /**
     * A SAX2 event handler that retrieves the namespace URI of the document
     * element.
     */
    private class NamespaceRetrievalHandler extends DefaultHandler {
        /** The namespace URI. */
        private String namespaceURI = null;

        /**
         * Returns the namespace URI retrieved by the handler.
         *
         * @return the namespace URI
         */
        public String getNamespaceURI() {
            return namespaceURI;
        }

        /**
         * Sets the namespaceURI property and aborts the parser by throwing an
         * exception.
         */
        @Override
        public void startElement(String uri, String localName, String qName,
                Attributes attributes) throws SAXException {
            namespaceURI = uri;
            throw new SAXException("aborting parser");
        }
    }
}
