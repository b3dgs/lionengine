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
 * Value of a JSON document read by {@link JsonReader} (ADR-0028, ADR-0032, REQ-DEF-001).
 *
 * <p>
 * A value knows its kind and its location: the source of its document, its line and column (both from 1, where the
 * value starts) and its JSON path. The typed accessors ({@link #asString()}, {@link #asInt()}, ...) return the value
 * when its kind matches and otherwise throw a {@link DefinitionException} located at the value, such as
 * {@code Expected a string, found an integer (maps/level.json:4:12, $.name)}.
 * </p>
 *
 * <p>
 * JSON path syntax: {@code $} for the root, {@code .name} for a member whose name is an ASCII identifier,
 * {@code ['name']} otherwise (with {@code \} before a quote or a backslash), {@code [index]} for an array element,
 * for example {@code $.animations.walk.frames[3]} or {@code $.traits['lionengine:movable']}.
 * </p>
 *
 * <p>
 * Immutable once read: any thread may read a value after its publication, for example through an asynchronous
 * handle (ADR-0019). Only the reader of this package creates values.
 * </p>
 *
 * @since 11.0.0
 */
public abstract class JsonValue
{
    /** Start of a type error message. */
    private static final String EXPECTED = "Expected ";
    /** Separator of a type error message. */
    private static final String FOUND = ", found ";
    /** Description of a value accepted as a real number. */
    static final String NUMBER = "a number";

    /** Source of the document: resource key or caller-given name. */
    private final String source;
    /** Line where the value starts, from 1. */
    private final int line;
    /** Column where the value starts, from 1. */
    private final int column;
    /** Enclosing object or array, {@code null} for the root. */
    private final JsonValue parent;

    /**
     * Creates a value.
     *
     * @param source The source of the document.
     * @param line The line where the value starts, from 1.
     * @param column The column where the value starts, from 1.
     * @param parent The enclosing object or array, {@code null} for the root.
     */
    JsonValue(String source, int line, int column, JsonValue parent)
    {
        this.source = source;
        this.line = line;
        this.column = column;
        this.parent = parent;
    }

    /**
     * Returns the kind of the value.
     *
     * @return The kind (never {@code null}).
     */
    public abstract Kind getKind();

    /**
     * Returns the source of the document.
     *
     * @return The resource key or caller-given name given to the reader (never {@code null}).
     */
    public final String getSource()
    {
        return source;
    }

    /**
     * Returns the line where the value starts.
     *
     * @return The line, from 1.
     */
    public final int getLine()
    {
        return line;
    }

    /**
     * Returns the column where the value starts.
     *
     * @return The column, from 1, counted in UTF-16 characters (a tab counts as one).
     */
    public final int getColumn()
    {
        return column;
    }

    /**
     * Returns the JSON path of the value, computed on each call (meant for error messages).
     *
     * @return The JSON path, {@code $} for the root (never {@code null}).
     */
    public final String getPath()
    {
        int count = 0;
        for (JsonValue current = this; current.parent != null; current = current.parent)
        {
            count++;
        }
        final JsonValue[] chain = new JsonValue[count];
        JsonValue current = this;
        for (int i = count - 1; i >= 0; i--)
        {
            chain[i] = current;
            current = current.parent;
        }
        final StringBuilder path = new StringBuilder(JsonPath.ROOT);
        for (int i = 0; i < count; i++)
        {
            chain[i].parent.appendSegment(path, chain[i]);
        }
        return path.toString();
    }

    /**
     * Tells whether the value is the JSON literal {@code null}.
     *
     * @return {@code true} for {@code null}, {@code false} otherwise.
     */
    public final boolean isNull()
    {
        return getKind() == Kind.NULL;
    }

    /**
     * Returns the value as a string.
     *
     * @return The decoded string (never {@code null}).
     * @throws DefinitionException If the value is not a string.
     */
    public String asString()
    {
        throw typeError(Kind.STRING);
    }

    /**
     * Returns the value as a {@code long}.
     *
     * @return The integer.
     * @throws DefinitionException If the value is not an integer literal (a number with a fraction or an exponent is
     *             not one).
     */
    public long asLong()
    {
        throw typeError(Kind.LONG);
    }

    /**
     * Returns the value as an {@code int}.
     *
     * @return The integer.
     * @throws DefinitionException If the value is not an integer literal, or is outside the {@code int} range.
     */
    public int asInt()
    {
        throw typeError(Kind.LONG);
    }

    /**
     * Returns the value as a {@code double}; an integer is converted.
     *
     * @return The number.
     * @throws DefinitionException If the value is not a number.
     */
    public double asDouble()
    {
        throw typeError(NUMBER);
    }

    /**
     * Returns the value as a {@code boolean}.
     *
     * @return The boolean.
     * @throws DefinitionException If the value is not {@code true} or {@code false}.
     */
    public boolean asBoolean()
    {
        throw typeError(Kind.BOOLEAN);
    }

    /**
     * Returns the value as an object.
     *
     * @return The object (never {@code null}).
     * @throws DefinitionException If the value is not an object.
     */
    public JsonObject asObject()
    {
        throw typeError(Kind.OBJECT);
    }

    /**
     * Returns the value as an array.
     *
     * @return The array (never {@code null}).
     * @throws DefinitionException If the value is not an array.
     */
    public JsonArray asArray()
    {
        throw typeError(Kind.ARRAY);
    }

    /**
     * Creates an error located at this value.
     *
     * @param message The error description.
     * @return The error, to throw.
     */
    final DefinitionException error(String message)
    {
        return new DefinitionException(message, source, line, column, getPath());
    }

    /**
     * Creates the error of a value of the wrong kind.
     *
     * @param expected The description of the expected kind, such as {@code a string}.
     * @return The error, to throw.
     */
    final DefinitionException typeError(String expected)
    {
        return error(typeMessage(expected));
    }

    /**
     * Creates the error of a value of the wrong kind.
     *
     * @param expected The expected kind.
     * @return The error, to throw.
     */
    final DefinitionException typeError(Kind expected)
    {
        return typeError(expected.description);
    }

    /**
     * Returns the message of a value of the wrong kind, such as {@code Expected a string, found an integer}.
     *
     * @param expected The description of the expected kind, such as {@code a string}.
     * @return The message.
     */
    final String typeMessage(String expected)
    {
        return EXPECTED + expected + FOUND + getKind().description;
    }

    /**
     * Returns the message of a value of the wrong kind.
     *
     * @param expected The expected kind.
     * @return The message.
     */
    final String typeMessage(Kind expected)
    {
        return typeMessage(expected.description);
    }

    /**
     * Appends the path segment that names a child of this value. Only objects and arrays have children.
     *
     * @param path The path to complete.
     * @param child The child to name.
     */
    void appendSegment(StringBuilder path, JsonValue child)
    {
        // A value without children names none; objects and arrays override this method.
    }

    /**
     * Kind of a JSON value.
     *
     * @since 11.0.0
     */
    public enum Kind
    {
        /** Object: members in document order ({@link JsonObject}). */
        OBJECT("an object"),
        /** Array: elements in order ({@link JsonArray}). */
        ARRAY("an array"),
        /** String. */
        STRING("a string"),
        /** Number written without fraction or exponent that fits a {@code long}. */
        LONG("an integer"),
        /** Any other number. */
        DOUBLE("a real number"),
        /** {@code true} or {@code false}. */
        BOOLEAN("a boolean"),
        /** The literal {@code null}. */
        NULL("null");

        /** Description used in error messages. */
        private final String description;

        /**
         * Creates a kind.
         *
         * @param description The description used in error messages.
         */
        Kind(String description)
        {
            this.description = description;
        }
    }
}
