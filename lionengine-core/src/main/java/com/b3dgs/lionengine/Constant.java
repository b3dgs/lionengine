/*
 * Copyright (C) 2013-2015 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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

/**
 * List of common constants.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public final class Constant
{
    /** Empty string. */
    public static final String EMPTY_STRING = "";
    /** New line. */
    public static final String NEW_LINE = "\n";
    /** Tabulation. */
    public static final String TAB = "\t";
    /** Quote. */
    public static final String QUOTE = "\"";
    /** Dot. */
    public static final String DOT = ".";
    /** Double dot. */
    public static final String DOUBLE_DOT = ": ";
    /** Space string. */
    public static final String SPACE = " ";
    /** Star string. */
    public static final String STAR = "*";
    /** At string. */
    public static final String AT = "@";
    /** Rate unit. */
    public static final String UNIT_RATE = "Hz";
    /** Maximum port value. */
    public static final int MAX_PORT = 65535;
    /** Thousand. */
    public static final int THOUSAND = 1000;
    /** Hundred. */
    public static final int HUNDRED = 100;
    /** Decade. */
    public static final int DECADE = 10;
    /** Byte 4. */
    public static final int BYTE_4 = 24;
    /** Byte 3. */
    public static final int BYTE_3 = 16;
    /** Byte 2. */
    public static final int BYTE_2 = 8;
    /** Byte 1. */
    public static final int BYTE_1 = 0;

    /**
     * Private constructor.
     */
    private Constant()
    {
        throw new RuntimeException(LionEngineException.ERROR_PRIVATE_CONSTRUCTOR);
    }
}
