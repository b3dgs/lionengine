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
 * JSON number value: an integer literal that fits a {@code long}, or a real number.
 */
final class JsonNumber extends JsonValue
{
    /** Error message of an integer outside the {@code int} range. */
    static final String ERROR_INT_RANGE = "Integer outside the int range";

    /** Kind: {@link JsonValue.Kind#LONG} or {@link JsonValue.Kind#DOUBLE}. */
    private final Kind kind;
    /** Value of a {@link JsonValue.Kind#LONG} number. */
    private final long integer;
    /** Value of a {@link JsonValue.Kind#DOUBLE} number. */
    private final double real;

    /**
     * Creates an integer value.
     *
     * @param source The source of the document.
     * @param line The line where the value starts, from 1.
     * @param column The column where the value starts, from 1.
     * @param parent The enclosing object or array, {@code null} for the root.
     * @param integer The value.
     */
    JsonNumber(String source, int line, int column, JsonValue parent, long integer)
    {
        super(source, line, column, parent);
        kind = Kind.LONG;
        this.integer = integer;
        real = (double) integer;
    }

    /**
     * Creates a real value.
     *
     * @param source The source of the document.
     * @param line The line where the value starts, from 1.
     * @param column The column where the value starts, from 1.
     * @param parent The enclosing object or array, {@code null} for the root.
     * @param real The value, finite.
     */
    JsonNumber(String source, int line, int column, JsonValue parent, double real)
    {
        super(source, line, column, parent);
        kind = Kind.DOUBLE;
        integer = 0L;
        this.real = real;
    }

    @Override
    public Kind getKind()
    {
        return kind;
    }

    @Override
    public long asLong()
    {
        if (kind == Kind.DOUBLE)
        {
            throw typeError(Kind.LONG);
        }
        return integer;
    }

    @Override
    public int asInt()
    {
        final long value = asLong();
        if (value < Integer.MIN_VALUE || value > Integer.MAX_VALUE)
        {
            throw error(ERROR_INT_RANGE);
        }
        return (int) value;
    }

    @Override
    public double asDouble()
    {
        return real;
    }
}
