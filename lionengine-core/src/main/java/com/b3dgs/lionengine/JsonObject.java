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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * JSON object: members with unique names, in document order (ADR-0032, REQ-DEF-001).
 *
 * <p>
 * Members are read by name ({@link #get(String)}) or by index in document order ({@link #getKey(int)},
 * {@link #getValue(int)}), so iterating allocates nothing. Immutable once read.
 * </p>
 *
 * @since 11.0.0
 */
public final class JsonObject extends JsonValue
{
    /** Member names in document order. */
    private final List<String> keys = new ArrayList<>();
    /** Member values in document order. */
    private final List<JsonValue> values = new ArrayList<>();
    /** Index of each member by name. */
    private final Map<String, Integer> indexes = new HashMap<>();

    /**
     * Creates an empty object, filled by the reader.
     *
     * @param source The source of the document.
     * @param line The line where the object starts, from 1.
     * @param column The column where the object starts, from 1.
     * @param parent The enclosing object or array, {@code null} for the root.
     */
    JsonObject(String source, int line, int column, JsonValue parent)
    {
        super(source, line, column, parent);
    }

    @Override
    public Kind getKind()
    {
        return Kind.OBJECT;
    }

    @Override
    public JsonObject asObject()
    {
        return this;
    }

    /**
     * Returns the number of members.
     *
     * @return The number of members.
     */
    public int size()
    {
        return keys.size();
    }

    /**
     * Returns the name of a member.
     *
     * @param index The member index, in document order, in {@code [0, size())}.
     * @return The member name (never {@code null}).
     * @throws IndexOutOfBoundsException If the index is outside {@code [0, size())}.
     */
    public String getKey(int index)
    {
        return keys.get(index);
    }

    /**
     * Returns the value of a member.
     *
     * @param index The member index, in document order, in {@code [0, size())}.
     * @return The member value (never {@code null}; the JSON literal {@code null} is a value).
     * @throws IndexOutOfBoundsException If the index is outside {@code [0, size())}.
     */
    public JsonValue getValue(int index)
    {
        return values.get(index);
    }

    /**
     * Tells whether a member exists.
     *
     * @param key The member name.
     * @return {@code true} if the object has this member, {@code false} otherwise.
     */
    public boolean has(String key)
    {
        return indexes.containsKey(key);
    }

    /**
     * Returns the value of a member.
     *
     * @param key The member name.
     * @return The member value, {@code null} if the object has no such member.
     */
    public JsonValue get(String key)
    {
        final int index = indexOf(key);
        JsonValue value = null;
        if (index >= 0)
        {
            value = values.get(index);
        }
        return value;
    }

    /**
     * Returns the index of a member.
     *
     * @param key The member name.
     * @return The member index in document order, {@code -1} if absent.
     */
    int indexOf(String key)
    {
        final Integer index = indexes.get(key);
        int result = -1;
        if (index != null)
        {
            result = index.intValue();
        }
        return result;
    }

    /**
     * Adds a member. The reader has checked that the name is new.
     *
     * @param key The member name.
     * @param value The member value.
     */
    void add(String key, JsonValue value)
    {
        indexes.put(key, Integer.valueOf(keys.size()));
        keys.add(key);
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
                JsonPath.appendKey(path, keys.get(i));
            }
        }
    }
}
