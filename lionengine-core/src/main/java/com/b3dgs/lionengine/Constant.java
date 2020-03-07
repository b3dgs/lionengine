/*
 * Copyright (C) 2013-2020 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */
package com.b3dgs.lionengine;

/**
 * List of common constants.
 * <p>
 * This class is Thread-Safe.
 * </p>
 */
public final class Constant
{
    /** Engine name. */
    public static final String ENGINE_NAME = "LionEngine";
    /** Engine author. */
    public static final String ENGINE_AUTHOR = "Pierre-Alexandre";
    /** Engine website. */
    public static final String ENGINE_WEBSITE = "http://lionengine.b3dgs.com";
    /** Engine version. */
    public static final Version ENGINE_VERSION = Version.create(9, 0, 3);
    /** Header XML. */
    public static final String XML_HEADER = "xmlns:lionengine";
    /** Prefix XML node. */
    public static final String XML_PREFIX = "lionengine:";
    /** Empty string. */
    public static final String EMPTY_STRING = "";
    /** Tabulation. */
    public static final String TAB = "\t";
    /** Quote. */
    public static final String QUOTE = "\"";
    /** Slash. */
    public static final String SLASH = "/";
    /** Dot. */
    public static final String DOT = ".";
    /** Double dot with space after. */
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
    public static final int MEGA_BYTE = 1_048_576;
    /** Maximum port value. */
    public static final int MAX_PORT = 65_535;
    /** Maximum degree value (excluded). */
    public static final int MAX_DEGREE = 360;
    /** Thousand. */
    public static final int THOUSAND = 1_000;
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
    /** Extrapolation standard. */
    public static final double EXTRP = 1.0;
    /** Half value. */
    public static final double HALF = 0.5;
    /** One second in milli. */
    public static final long ONE_SECOND_IN_MILLI = 1_000L;
    /** One second in nano. */
    public static final long ONE_SECOND_IN_NANO = 1_000_000_000L;
    /** Nano to milli. */
    public static final double NANO_TO_MILLI = 1_000_000.0;
    /** Gravity of Earth (in m/s²). */
    public static final double GRAVITY_EARTH = 9.80665;
    /** Gravity of Mars (in m/s²). */
    public static final double GRAVITY_MARS = 3.71;
    /** Gravity of the Moon (in m/s²). */
    public static final double GRAVITY_MOON = 1.624;
    /** Standard text font sans serif. */
    public static final String FONT_SANS_SERIF = "SansSerif";
    /** Standard text font serif. */
    public static final String FONT_SERIF = "Serif";
    /** Standard text dialog. */
    public static final String FONT_DIALOG = "Dialog";
    /** Error system property. */
    private static final String ERROR_PROPERTY = "Unable to get system property: ";

    /**
     * Get the system property. If the property is not valid due to a {@link SecurityException}, an empty string is
     * returned.
     * 
     * @param property The system property (must not be <code>null</code>).
     * @param def The default value used if property is not available (can be <code>null</code>).
     * @return The system property value.
     */
    public static String getSystemProperty(String property, String def)
    {
        Check.notNull(property);

        try
        {
            return System.getProperty(property, def);
        }
        catch (final SecurityException exception)
        {
            Verbose.exception(exception,
                              new StringBuilder(Constant.HUNDRED).append(ERROR_PROPERTY)
                                                                 .append(property)
                                                                 .append(" (")
                                                                 .append(exception.getClass().getName())
                                                                 .append(')')
                                                                 .toString());
            return def;
        }
    }

    /**
     * Private constructor.
     */
    private Constant()
    {
        throw new LionEngineException(LionEngineException.ERROR_PRIVATE_CONSTRUCTOR);
    }
}
