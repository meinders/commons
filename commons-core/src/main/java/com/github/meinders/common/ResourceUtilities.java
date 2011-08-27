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

import java.text.*;
import java.util.*;

/**
 * A utility class and wrapper for resource bundles to provide better access to
 * resources.
 *
 * @version 0.9 (2005.02.22)
 * @author Gerrit Meinders
 */
public class ResourceUtilities {
    public static String getString(ResourceBundle bundle, String key) {
        return getString(bundle, key, getMissingResourceString(key));
    }

    public static String getRawString(ResourceBundle bundle, String key) {
        return getString(bundle, key, (String) null);
    }

    private static String getString(ResourceBundle bundle, String key,
            String fallback) {
        String value = null;
        try {
            value = bundle.getString(key);
        } catch (Exception e) {
        }
        if (value == null) {
            return fallback;
        } else {
            return value;
        }
    }

    public static String getString(ResourceBundle bundle, String key,
            Object... args) {
        String value = getString(bundle, key, (String) null);
        if (value == null) {
            return getMissingResourceString(key);
        } else {
            return MessageFormat.format(getString(bundle, key), args);
        }
    }

    public static Integer getMnemonic(ResourceBundle bundle, String key) {
        return getMnemonic(bundle, key, null);
    }

    public static Integer getMnemonic(ResourceBundle bundle, String key,
            Integer fallback) {
        String value = getString(bundle, getKey(key, "mnemonic"));
        if (value == null) {
            return fallback;
        } else {
            return new Integer(Character.toUpperCase(value.charAt(0)));
        }
    }

    public static Integer getInteger(ResourceBundle bundle, String key) {
        return getInteger(bundle, key, null);
    }

    public static Integer getInteger(ResourceBundle bundle, String key,
            Integer fallback) {
        Integer value = null;
        try {
            value = Integer.parseInt(bundle.getString(key));
        } catch (Exception e) {
        }
        if (value == null) {
            return fallback;
        } else {
            return value;
        }
    }

    public static String[] getStringArray(ResourceBundle bundle, String arrayKey) {
        // get array length
        String key = getArrayLengthKey(arrayKey);
        Integer length = getInteger(bundle, key, null);
        if (length == null) {
            // unknown length
            return new String[0];

        } else {
            // known length: build array
            String[] values = new String[length];
            for (int i = 0; i < length; i++) {
                values[i] = getStringArrayElement(bundle, arrayKey, i);
            }
            return values;
        }
    }

    public static String getStringArrayElement(ResourceBundle bundle,
            String arrayKey, int index) {
        String key = getArrayElementKey(arrayKey, index);
        return getString(bundle, key);
    }

    public static String[] getStringArrayCS(ResourceBundle bundle, String arrayKey) {
        return getString(bundle, arrayKey).split(",");
    }

    public static String getKey(String... keyElements) {
        if (keyElements.length == 0) {
            return null;

        } else {
            StringBuffer buffer = new StringBuffer(keyElements[0]);
            for (int i = 1; i < keyElements.length; i++) {
                buffer.append('.');
                buffer.append(keyElements[i]);
            }
            return buffer.toString();
        }
    }

    public static String getArrayElementKey(String arrayKey, int index) {
        return arrayKey + "[" + index + "]";
    }

    private static String getArrayLengthKey(String arrayKey) {
        return getKey(arrayKey, "length");
    }

    private static String getMissingResourceString(String key) {
        return "???" + key + "???";
    }

    private final ResourceBundle bundle;

    private final Object prefix;

    public ResourceUtilities(String bundleName) {
        this(ResourceBundle.getBundle(bundleName));
    }

    public ResourceUtilities(ResourceBundle bundle) {
        assert bundle != null : "Invalid bundle: " + bundle;
        this.bundle = bundle;
        prefix = null;
    }

    public ResourceUtilities(ResourceUtilities utilities, String prefix) {
        this.bundle = utilities.bundle;
        this.prefix = prefix;
    }

    private String resolve(String key) {
        return prefix == null ? key : prefix + "." + key;
    }

    public String getString(String key) {
        return ResourceUtilities.getString(bundle, resolve(key));
    }

    public String getString(String key, Object... args) {
        return ResourceUtilities.getString(bundle, resolve(key), args);
    }

    public Integer getMnemonic(String key) {
        return ResourceUtilities.getMnemonic(bundle, resolve(key));
    }

    public Integer getMnemonic(String key, Integer fallback) {
        return ResourceUtilities.getInteger(bundle, resolve(key), fallback);
    }

    public Integer getInteger(String key) {
        return ResourceUtilities.getInteger(bundle, resolve(key));
    }

    public Integer getInteger(String key, Integer fallback) {
        return ResourceUtilities.getInteger(bundle, resolve(key), fallback);
    }

    public String[] getStringArray(String arrayKey) {
        return ResourceUtilities.getStringArray(bundle, arrayKey);
    }

    public String getStringArrayElement(String arrayKey, int index) {
        return ResourceUtilities.getStringArrayElement(bundle,
                resolve(arrayKey), index);
    }

    public String getLabel(String key) {
        return ResourceUtilities.getString(bundle, "label",
                (Object) getString(key));
    }
}
