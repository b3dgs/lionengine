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
package com.b3dgs.lionengine.render;

/**
 * Identifier of an output filter: a registry id and its place in the chain (ADR-0006, ADR-0025, ADR-0033,
 * REQ-FILTER-001, REQ-FILTER-003).
 *
 * <p>
 * The standard filters are {@link #NONE}, {@link #BILINEAR}, {@link #HQ2X} and {@link #CRT}. A custom filter uses its
 * own id in the {@code namespace:name} form (each part starts with a lower-case letter, followed by lower-case
 * letters, digits, {@code _} or {@code -}) and is registered on the render backend through a {@link CustomFilter}.
 * Fallbacks: CRT is skipped; {@code HQ2X} falls back to bilinear, bilinear to {@code NONE}; each is reported.
 * </p>
 *
 * <p>
 * Immutable and thread-safe.
 * </p>
 *
 * @param id The registry id in the {@code namespace:name} form.
 * @param kind The place of the filter in the chain.
 * @since 11.0.0
 */
public record OutputFilter(String id, FilterKind kind)
{
    /** Nearest-neighbor sampling when the logical frame is scaled. */
    public static final OutputFilter NONE = new OutputFilter("lionengine:none", FilterKind.SCALING);
    /** Bilinear sampling. */
    public static final OutputFilter BILINEAR = new OutputFilter("lionengine:bilinear", FilterKind.SCALING);
    /** {@code HQ2X} pixel-art scaler. */
    public static final OutputFilter HQ2X = new OutputFilter("lionengine:hq2x", FilterKind.SCALING);
    /** CRT effect (scanlines and mask), GPU only. */
    public static final OutputFilter CRT = new OutputFilter("lionengine:crt", FilterKind.POST);

    /** Separator between the namespace and the name. */
    private static final char NAMESPACE_SEPARATOR = ':';
    /** Error message of an invalid id. */
    private static final String ERROR_ID = "Invalid filter id: ";

    /**
     * Checks an id.
     *
     * @param id The id to check.
     * @throws IllegalArgumentException If the id is not valid.
     */
    private static void validate(String id)
    {
        final int separator = id == null ? -1 : id.indexOf(NAMESPACE_SEPARATOR);
        if (separator < 0
            || !isValidPart(id, 0, separator)
            || !isValidPart(id, separator + 1, id.length()))
        {
            throw new IllegalArgumentException(ERROR_ID + id);
        }
    }

    /**
     * Tells whether a part of an id is valid: a lower-case letter, then lower-case letters, digits, {@code _} or
     * {@code -}.
     *
     * @param id The id.
     * @param start The first index of the part (included).
     * @param end The last index of the part (excluded).
     * @return {@code true} if the part is valid.
     */
    private static boolean isValidPart(String id, int start, int end)
    {
        boolean valid = end > start && isLowerLetter(id.charAt(start));
        for (int i = start + 1; valid && i < end; i++)
        {
            valid = isPartCharacter(id.charAt(i));
        }
        return valid;
    }

    /**
     * Tells whether a character may follow the first letter of a part.
     *
     * @param c The character.
     * @return {@code true} if the character is a lower-case letter, a digit, {@code _} or {@code -}.
     */
    private static boolean isPartCharacter(char c)
    {
        return isLowerLetter(c) || isDigit(c) || c == '_' || c == '-';
    }

    /**
     * Tells whether a character is an ASCII digit.
     *
     * @param c The character.
     * @return {@code true} if the character is in {@code [0-9]}.
     */
    private static boolean isDigit(char c)
    {
        return c >= '0' && c <= '9';
    }

    /**
     * Tells whether a character is a lower-case ASCII letter.
     *
     * @param c The character.
     * @return {@code true} if the character is in {@code [a-z]}.
     */
    private static boolean isLowerLetter(char c)
    {
        return c >= 'a' && c <= 'z';
    }

    /**
     * Creates a filter identifier.
     *
     * @param id The registry id in the {@code namespace:name} form (never {@code null}).
     * @param kind The place of the filter in the chain (never {@code null}).
     * @throws IllegalArgumentException If {@code id} is not valid or {@code kind} is {@code null}.
     */
    public OutputFilter
    {
        validate(id);
        if (kind == null)
        {
            throw new IllegalArgumentException(ERROR_ID + id);
        }
    }
}
