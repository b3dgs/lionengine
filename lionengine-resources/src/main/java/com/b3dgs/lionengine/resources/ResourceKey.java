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
package com.b3dgs.lionengine.resources;

/**
 * Identifier of a resource: a logical path, the same for every origin (ADR-0005, ADR-0007, REQ-RES-005).
 *
 * <p>
 * A valid path uses {@code /} as separator, is case-sensitive, does not start with {@code /}, and has no empty,
 * {@code .} or {@code ..} segment; a {@code \} is rejected, never converted. Nothing is converted, so the path given is
 * the path kept, and two keys are equal when their paths are equal. A resource is never assumed to be a file.
 * </p>
 *
 * <p>
 * Immutable and thread-safe.
 * </p>
 *
 * @param path The logical path, for example {@code sprites/hero.png}.
 * @since 11.0.0
 */
public record ResourceKey(String path)
{
    /** Path separator. */
    private static final char SEPARATOR = '/';
    /** Rejected separator. */
    private static final char BACKSLASH = '\\';
    /** Character of the current and parent segments. */
    private static final char DOT = '.';
    /** Error message of an invalid path. */
    private static final String ERROR_PATH = "Invalid resource key: ";

    /**
     * Checks a path.
     *
     * @param path The path to check.
     * @throws IllegalArgumentException If the path is not valid.
     */
    private static void validate(String path)
    {
        if (path == null || path.isEmpty() || path.indexOf(BACKSLASH) >= 0)
        {
            throw new IllegalArgumentException(ERROR_PATH + path);
        }
        final int length = path.length();
        int start = 0;
        while (start <= length)
        {
            final int separator = path.indexOf(SEPARATOR, start);
            final int end = separator < 0 ? length : separator;
            if (!isValidSegment(path, start, end))
            {
                throw new IllegalArgumentException(ERROR_PATH + path);
            }
            start = end + 1;
        }
    }

    /**
     * Tells whether a segment is valid: not empty, not {@code .} and not {@code ..}.
     *
     * @param path The path.
     * @param start The first index of the segment (included).
     * @param end The last index of the segment (excluded).
     * @return {@code true} if the segment is valid.
     */
    private static boolean isValidSegment(String path, int start, int end)
    {
        final int size = end - start;
        if (size == 0)
        {
            return false;
        }
        final boolean dots = size <= 2 && path.charAt(start) == DOT && path.charAt(end - 1) == DOT;
        return !dots;
    }

    /**
     * Creates a key.
     *
     * @param path The logical path (never {@code null}).
     * @throws IllegalArgumentException If the path is {@code null}, empty or not valid.
     */
    public ResourceKey
    {
        validate(path);
    }

    /**
     * Returns the logical path.
     *
     * @return The logical path.
     */
    @Override
    public String toString()
    {
        return path;
    }
}
