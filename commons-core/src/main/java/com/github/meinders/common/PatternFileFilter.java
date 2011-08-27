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
import java.net.*;
import java.util.*;
import java.util.regex.*;

public class PatternFileFilter extends FileFilter {
    private static final Pattern WILCARD_PATTERN = Pattern
            .compile("\\?|(\\*\\*?)");

    private final URI path;

    private final Set<Pattern> includes;

    private final Set<Pattern> excludes;

    public PatternFileFilter(String description, File path) {
        super(description);
        this.path = path.toURI();

        includes = new LinkedHashSet<Pattern>();
        excludes = new LinkedHashSet<Pattern>();
    }

    public void addInclude(String include) {
        includes.add(createPattern(include));
    }

    public void addExclude(String exclude) {
        excludes.add(createPattern(exclude));
    }

    private Pattern createPattern(String pattern) {
        final Matcher matcher = WILCARD_PATTERN.matcher(pattern);
        final StringBuffer buffer = new StringBuffer();
        while (matcher.find()) {
            final String wildcard = matcher.group();
            final String wildcardRegEx;
            if (wildcard.charAt(0) == '?') {
                wildcardRegEx = ".";
            } else if (wildcard.length() == 1) {
                wildcardRegEx = "[^\\" + File.separatorChar + "]*";
            } else {
                wildcardRegEx = ".*";
            }

            buffer.append("\\Q");
            matcher.appendReplacement(buffer, "");
            buffer.append("\\E");
            buffer.append(wildcardRegEx);
        }

        buffer.append("\\Q");
        matcher.appendTail(buffer);
        buffer.append("\\E");

        return Pattern.compile(buffer.toString());
    }

    public void addIncludes(Collection<String> includes) {
        for (String include : includes) {
            addInclude(include);
        }
    }

    public void addExcludes(Collection<String> excludes) {
        for (String exclude : excludes) {
            addExclude(exclude);
        }
    }

    @Override
    public boolean accept(File file) {
        final File relative = new File(file.toURI().relativize(path));
        final String relativeName = relative.toString();

        boolean included = includes.isEmpty();
        for (Iterator<Pattern> i = includes.iterator(); i.hasNext()
                && !included;) {
            Pattern include = i.next();
            included = include.matcher(relativeName).matches();
        }

        if (included) {
            for (Iterator<Pattern> i = excludes.iterator(); i.hasNext()
                    && included;) {
                Pattern exclude = i.next();
                included = !exclude.matcher(relativeName).matches();
            }
        }

        return included;
    }
}
