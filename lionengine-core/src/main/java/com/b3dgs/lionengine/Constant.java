/*
 * Copyright (C) 2013-2016 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
 */
package com.b3dgs.lionengine;

import java.nio.charset.Charset;

/**
 * List of common constants.
 */
public final class Constant
{
    /** Charset UTF-8. */
    public static final Charset UTF_8 = Charset.forName("UTF-8");
    /** Empty string. */
    public static final String EMPTY_STRING = "";
    /** New line. */
    public static final String NEW_LINE = "\n";
    /** Tabulation. */
    public static final String TAB = "\t";
    /** Quote. */
    public static final String QUOTE = "\"";
    /** Slash. */
    public static final String SLASH = "/";
    /** Dot. */
    public static final String DOT = ".";
    /** Double dot. */
    public static final String DOUBLE_DOT = ": ";
    /** Space string. */
    public static final String SPACE = " ";
    /** Underscore string. */
    public static final String UNDERSCORE = "_";
    /** Star string. */
    public static final String STAR = "*";
    /** At string. */
    public static final String AT = "@";
    /** Percent string. */
    public static final String PERCENT = "%";
    /** Rate unit. */
    public static final String UNIT_RATE = "Hz";
    /** Jar type. */
    public static final String TYPE_JAR = ".jar";
    /** One megabyte. */
    public static final int MEGA_BYTE = 1048576;
    /** Maximum port value. */
    public static final int MAX_PORT = 65535;
    /** Maximum degree value (excluded). */
    public static final int MAX_DEGREE = 360;
    /** Thousand. */
    public static final int THOUSAND = 1000;
    /** Hundred. */
    public static final int HUNDRED = 100;
    /** Decade. */
    public static final int DECADE = 10;
    /** Unsigned byte max value (excluded). */
    public static final int UNSIGNED_BYTE = 256;
    /** Byte 4. */
    public static final int BYTE_4 = 24;
    /** Byte 3. */
    public static final int BYTE_3 = 16;
    /** Byte 2. */
    public static final int BYTE_2 = 8;
    /** Byte 1. */
    public static final int BYTE_1 = 0;
    /** Half value. */
    public static final double HALF = 0.5;
    /** Error system property. */
    private static final String ERROR_PROPERTY = "Unable to get system property: ";

    /**
     * Get the system property. If the property is not valid due to a {@link SecurityException}, an empty string is
     * returned.
     * 
     * @param property The system property.
     * @param def The default value used if property is not available.
     * @return The system property value (<code>null</code> if there is not any corresponding property).
     */
    public static String getSystemProperty(String property, String def)
    {
        try
        {
            return System.getProperty(property);
        }
        catch (final SecurityException exception)
        {
            final StringBuilder builder = new StringBuilder(ERROR_PROPERTY);
            builder.append(property).append(" (").append(exception.getClass().getName()).append(")");
            Verbose.exception(exception, builder.toString());
            return def;
        }
    }

    /**
     * Private constructor.
     */
    private Constant()
    {
        super();
    }
}
