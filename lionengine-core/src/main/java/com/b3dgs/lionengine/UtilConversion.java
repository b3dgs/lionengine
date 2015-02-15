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

import java.nio.ByteBuffer;
import java.util.Locale;

/**
 * Conversion class utility.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public final class UtilConversion
{
    /**
     * Convert an integer to an array of byte.
     * 
     * @param value The integer value.
     * @return The byte array.
     */
    public static byte[] intToByteArray(int value)
    {
        return new byte[]
        {
                (byte) (value >>> 24), (byte) (value >>> 16), (byte) (value >>> 8), (byte) value
        };
    }

    /**
     * Convert a byte array to an integer.
     * 
     * @param bytes The byte array.
     * @return The integer value.
     */
    public static int byteArrayToInt(byte[] bytes)
    {
        return ByteBuffer.wrap(bytes).getInt();
    }

    /**
     * Convert a short to an array of byte.
     * 
     * @param value The short value.
     * @return The byte array.
     */
    public static byte[] shortToByteArray(short value)
    {
        return new byte[]
        {
                (byte) (value >>> 8), (byte) value
        };
    }

    /**
     * Convert a byte array to an integer.
     * 
     * @param bytes The byte array.
     * @return The integer value.
     */
    public static short byteArrayToShort(byte[] bytes)
    {
        return ByteBuffer.wrap(bytes).getShort();
    }

    /**
     * Return the java byte value [-128|127] from an unsigned byte [0|255].
     * 
     * @param value The unsigned byte value [0|255].
     * @return The java byte value [-128|127].
     */
    public static byte fromUnsignedByte(short value)
    {
        return (byte) (value - (Byte.MAX_VALUE + 1));
    }

    /**
     * Return the unsigned byte value [0|255] from the java byte value [-128|127].
     * 
     * @param value The java byte value [-128|127].
     * @return The unsigned byte value [0|255].
     */
    public static short toUnsignedByte(byte value)
    {
        return (short) (value - Byte.MIN_VALUE);
    }

    /**
     * Return the java short value [-32768|32767] from an unsigned short [0|65535].
     * 
     * @param value The unsigned short value [0|65535].
     * @return The java short value [-32768|32767].
     */
    public static short fromUnsignedShort(int value)
    {
        return (short) (value - (Short.MAX_VALUE + 1));
    }

    /**
     * Return the unsigned short value [0|65535] from the java short value [-32768|32767].
     * 
     * @param value The java short value [-32768|32767].
     * @return The unsigned short value [0|65535].
     */
    public static int toUnsignedShort(short value)
    {
        return value - Short.MIN_VALUE;
    }

    /**
     * Convert a string to title case.
     * 
     * @param string The string to convert.
     * @return The string in title case.
     */
    public static String toTitleCase(String string)
    {
        final StringBuilder result = new StringBuilder();
        for (int i = 0; i < string.length(); i++)
        {
            final String next = string.substring(i, i + 1);
            if (i == 0)
            {
                result.append(next.toUpperCase(Locale.ENGLISH));
            }
            else
            {
                result.append(next.toLowerCase(Locale.ENGLISH));
            }
        }
        return result.toString();
    }

    /**
     * Convert a string to a pure title case for each word, replacing special characters by space.
     * 
     * @param string The string to convert.
     * @return The string in title case.
     */
    public static String toTitleCaseWord(String string)
    {
        final String[] words = string.replaceAll("\\W|_", " ").split(" ");
        final StringBuilder title = new StringBuilder();
        for (int i = 0; i < words.length; i++)
        {
            title.append(toTitleCase(words[i]));
            if (i < words.length - 1)
            {
                title.append(" ");
            }
        }
        return title.toString();
    }

    /**
     * Private constructor.
     */
    private UtilConversion()
    {
        throw new RuntimeException();
    }
}
