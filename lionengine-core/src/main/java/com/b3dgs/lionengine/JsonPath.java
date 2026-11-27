/*
 * Copyright (C) 2013-2026 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
 * JSON path segments of error messages (ADR-0032): {@code $}, {@code .name}, {@code ['name']} and {@code [index]}.
 */
final class JsonPath
{
    /** Path of the root value. */
    static final String ROOT = "$";

    /** Opening of a quoted member name. */
    private static final String QUOTED_OPEN = "['";
    /** Closing of a quoted member name. */
    private static final String QUOTED_CLOSE = "']";
    /** Escape character inside a quoted member name. */
    private static final char ESCAPE = '\\';
    /** Quote character of a quoted member name. */
    private static final char QUOTE = '\'';

    /**
     * Private constructor.
     */
    private JsonPath()
    {
        throw new UnsupportedOperationException();
    }

    /**
     * Appends a member name: {@code .name} for an ASCII identifier, {@code ['name']} otherwise.
     *
     * @param path The path to complete.
     * @param key The member name.
     */
    static void appendKey(StringBuilder path, String key)
    {
        if (isIdentifier(key))
        {
            path.append('.').append(key);
        }
        else
        {
            path.append(QUOTED_OPEN);
            final int length = key.length();
            for (int i = 0; i < length; i++)
            {
                final char c = key.charAt(i);
                if (c == QUOTE || c == ESCAPE)
                {
                    path.append(ESCAPE);
                }
                path.append(c);
            }
            path.append(QUOTED_CLOSE);
        }
    }

    /**
     * Appends an array index: {@code [index]}.
     *
     * @param path The path to complete.
     * @param index The element index.
     */
    static void appendIndex(StringBuilder path, int index)
    {
        path.append('[').append(index).append(']');
    }

    /**
     * Tells whether a member name is an ASCII identifier: a letter or {@code _}, then letters, digits or {@code _}.
     *
     * @param key The member name.
     * @return {@code true} if the name can follow a dot.
     */
    private static boolean isIdentifier(String key)
    {
        final int length = key.length();
        boolean identifier = length > 0 && !isDigit(key.charAt(0));
        for (int i = 0; identifier && i < length; i++)
        {
            final char c = key.charAt(i);
            identifier = isLetter(c) || isDigit(c) || c == '_';
        }
        return identifier;
    }

    /**
     * Tells whether a character is an ASCII letter.
     *
     * @param c The character.
     * @return {@code true} for {@code a-z} and {@code A-Z}.
     */
    static boolean isLetter(char c)
    {
        return c >= 'a' && c <= 'z' || c >= 'A' && c <= 'Z';
    }

    /**
     * Tells whether a character is an ASCII digit.
     *
     * @param c The character.
     * @return {@code true} for {@code 0-9}.
     */
    static boolean isDigit(char c)
    {
        return c >= '0' && c <= '9';
    }
}
