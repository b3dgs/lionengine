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
package com.b3dgs.lionengine.runtime;

/**
 * Checks of the names of a {@link SceneFlow}: scene ids {@code namespace:name} and trigger names, each part matching
 * {@code [a-z][a-z0-9_-]*}, as the registry ids of ADR-0032 (ADR-0036).
 */
final class FlowNames
{
    /** Separator between the namespace and the name of an id. */
    private static final char SEPARATOR = ':';

    /**
     * Tells whether a scene id has the {@code namespace:name} form.
     *
     * @param id The id, may be {@code null}.
     * @return {@code true} if the id is valid.
     */
    static boolean isValidId(String id)
    {
        boolean valid = id != null;
        if (valid)
        {
            final int separator = id.indexOf(SEPARATOR);
            valid = separator > 0 && isValidPart(id, 0, separator) && isValidPart(id, separator + 1, id.length());
        }
        return valid;
    }

    /**
     * Tells whether a trigger name matches {@code [a-z][a-z0-9_-]*}.
     *
     * @param trigger The trigger name, may be {@code null}.
     * @return {@code true} if the name is valid.
     */
    static boolean isValidTrigger(String trigger)
    {
        return trigger != null && isValidPart(trigger, 0, trigger.length());
    }

    /**
     * Tells whether a part of a name matches {@code [a-z][a-z0-9_-]*}.
     *
     * @param text The name.
     * @param start The index of the first character of the part.
     * @param end The index after the last character of the part.
     * @return {@code true} if the part is valid.
     */
    private static boolean isValidPart(String text, int start, int end)
    {
        boolean valid = end > start && isLowerCase(text.charAt(start));
        for (int i = start + 1; valid && i < end; i++)
        {
            final char c = text.charAt(i);
            valid = isLowerCase(c) || isDigit(c) || c == '_' || c == '-';
        }
        return valid;
    }

    /**
     * Tells whether a character is an ASCII digit.
     *
     * @param c The character.
     * @return {@code true} for {@code 0-9}.
     */
    private static boolean isDigit(char c)
    {
        return c >= '0' && c <= '9';
    }

    /**
     * Tells whether a character is an ASCII lower-case letter.
     *
     * @param c The character.
     * @return {@code true} for {@code a-z}.
     */
    private static boolean isLowerCase(char c)
    {
        return c >= 'a' && c <= 'z';
    }

    /**
     * Private constructor.
     */
    private FlowNames()
    {
        throw new UnsupportedOperationException();
    }
}
