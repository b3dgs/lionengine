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

import java.util.ArrayList;
import java.util.List;

/**
 * JSON array: elements in order (ADR-0032, REQ-DEF-001).
 *
 * <p>
 * Elements are read by index, so iterating allocates nothing. Immutable once read.
 * </p>
 *
 * @since 11.0.0
 */
public final class JsonArray extends JsonValue
{
    /** Elements in order. */
    private final List<JsonValue> values = new ArrayList<>();

    /**
     * Creates an empty array, filled by the reader.
     *
     * @param source The source of the document.
     * @param line The line where the array starts, from 1.
     * @param column The column where the array starts, from 1.
     * @param parent The enclosing object or array, {@code null} for the root.
     */
    JsonArray(String source, int line, int column, JsonValue parent)
    {
        super(source, line, column, parent);
    }

    @Override
    public Kind getKind()
    {
        return Kind.ARRAY;
    }

    @Override
    public JsonArray asArray()
    {
        return this;
    }

    /**
     * Returns the number of elements.
     *
     * @return The number of elements.
     */
    public int size()
    {
        return values.size();
    }

    /**
     * Returns an element.
     *
     * @param index The element index, in {@code [0, size())}.
     * @return The element (never {@code null}; the JSON literal {@code null} is a value).
     * @throws IndexOutOfBoundsException If the index is outside {@code [0, size())}.
     */
    public JsonValue get(int index)
    {
        return values.get(index);
    }

    /**
     * Adds an element.
     *
     * @param value The element.
     */
    void add(JsonValue value)
    {
        values.add(value);
    }

    @Override
    void appendSegment(StringBuilder path, JsonValue child)
    {
        final int count = values.size();
        for (int i = 0; i < count; i++)
        {
            if (values.get(i) == child)
            {
                JsonPath.appendIndex(path, i);
            }
        }
    }
}
