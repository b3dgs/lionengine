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
 * Error in a definition file or in the parameters given to a registry factory (ADR-0032, REQ-DEF-001 to REQ-DEF-003).
 *
 * <p>
 * A document error carries its source (resource key or caller-given name), its line and column (both from 1,
 * {@link #UNKNOWN} when unknown) and its JSON path (for example {@code $.animations.walk.frames[3]}). A registry error
 * carries the registry id and the parameter name. An error about a parameter read by a registry factory carries both:
 * the location of the value and the id and parameter. The message returned by {@link #getMessage()} includes them,
 * for example {@code Missing value (objects/hero.json:4:28, $.traits['lionengine:movable'], lionengine:movable,
 * maxSpeed)}.
 * </p>
 *
 * <p>
 * Immutable; may be thrown on any thread (definition files can be read on a loading worker, ADR-0019). Not meant
 * to be serialized: no {@code serialVersionUID} is declared (Sonar S4926).
 * </p>
 *
 * @since 11.0.0
 */
@SuppressWarnings("serial")
public final class DefinitionException extends RuntimeException
{
    /** Line or column value when it is unknown. */
    public static final int UNKNOWN = 0;

    /** Separator between the location fields of the message. */
    private static final char LOCATION_SEPARATOR = ':';
    /** Opening of the message suffix. */
    private static final String SUFFIX_OPEN = " (";
    /** Separator between the parts of the message suffix. */
    private static final String PART_SEPARATOR = ", ";

    /**
     * Builds the message of a document error.
     *
     * @param message The error description.
     * @param source The source name, may be {@code null}.
     * @param line The line, {@link #UNKNOWN} when unknown.
     * @param column The column, {@link #UNKNOWN} when unknown.
     * @param path The JSON path, may be {@code null}.
     * @return The complete message.
     */
    private static String format(String message, String source, int line, int column, String path)
    {
        return location(message, source, line, column, path).append(')').toString();
    }

    /**
     * Builds the message of a registry error.
     *
     * @param message The error description.
     * @param id The registry id, may be {@code null}.
     * @param parameter The parameter name, may be {@code null}.
     * @return The complete message.
     */
    private static String format(String message, String id, String parameter)
    {
        final StringBuilder text = new StringBuilder(message).append(SUFFIX_OPEN).append(id);
        appendPart(text, parameter);
        return text.append(')').toString();
    }

    /**
     * Builds the message of an error about a value read by a registry factory.
     *
     * @param message The error description.
     * @param value The faulty value, or the object missing a member.
     * @param id The registry id, may be {@code null}.
     * @param parameter The parameter name, may be {@code null}.
     * @return The complete message.
     * @throws IllegalArgumentException If {@code value} is {@code null}.
     */
    private static String format(String message, JsonValue value, String id, String parameter)
    {
        if (value == null)
        {
            throw new IllegalArgumentException("Value must not be null");
        }
        final StringBuilder text = location(message, value.getSource(), value.getLine(), value.getColumn(),
                                            value.getPath());
        appendPart(text, id);
        appendPart(text, parameter);
        return text.append(')').toString();
    }

    /**
     * Starts a message with its location: {@code message (source:line:column, path}.
     *
     * @param message The error description.
     * @param source The source name, may be {@code null}.
     * @param line The line, {@link #UNKNOWN} when unknown.
     * @param column The column, {@link #UNKNOWN} when unknown.
     * @param path The JSON path, may be {@code null}.
     * @return The message being built, without its closing parenthesis.
     */
    private static StringBuilder location(String message, String source, int line, int column, String path)
    {
        final StringBuilder text = new StringBuilder(message).append(SUFFIX_OPEN);
        text.append(source).append(LOCATION_SEPARATOR).append(line).append(LOCATION_SEPARATOR).append(column);
        appendPart(text, path);
        return text;
    }

    /**
     * Appends a part of the message suffix, after a separator.
     *
     * @param text The message being built.
     * @param part The part, nothing is appended if {@code null}.
     */
    private static void appendPart(StringBuilder text, String part)
    {
        if (part != null)
        {
            text.append(PART_SEPARATOR).append(part);
        }
    }

    /** Source name (resource key or caller-given name), {@code null} for a registry error. */
    private final String source;
    /** Line from 1, {@link #UNKNOWN} when unknown. */
    private final int line;
    /** Column from 1, {@link #UNKNOWN} when unknown. */
    private final int column;
    /** JSON path, {@code null} when unknown. */
    private final String path;
    /** Registry id, {@code null} for a document error. */
    private final String id;
    /** Parameter name, {@code null} when not about one parameter. */
    private final String parameter;

    /**
     * Creates an error located in a definition document.
     *
     * @param message The error description (never {@code null}).
     * @param source The resource key or caller-given source name, {@code null} if unknown.
     * @param line The line from 1, {@link #UNKNOWN} when unknown.
     * @param column The column from 1, {@link #UNKNOWN} when unknown.
     * @param path The JSON path of the faulty value, {@code null} if unknown.
     */
    public DefinitionException(String message, String source, int line, int column, String path)
    {
        super(format(message, source, line, column, path));

        this.source = source;
        this.line = line;
        this.column = column;
        this.path = path;
        id = null;
        parameter = null;
    }

    /**
     * Creates an error about a registry id and, optionally, one of its parameters.
     *
     * @param message The error description (never {@code null}).
     * @param id The registry id in the {@code namespace:name} form, {@code null} if unknown.
     * @param parameter The parameter name, {@code null} if the error is not about one parameter.
     */
    public DefinitionException(String message, String id, String parameter)
    {
        super(format(message, id, parameter));

        source = null;
        line = UNKNOWN;
        column = UNKNOWN;
        path = null;
        this.id = id;
        this.parameter = parameter;
    }

    /**
     * Creates an error about a value read by a registry factory: located at the value, naming the id and parameter.
     *
     * @param message The error description (never {@code null}).
     * @param value The faulty value, or the object that misses the parameter (never {@code null}); its source, line,
     *            column and path are copied, the value itself is not kept.
     * @param id The registry id in the {@code namespace:name} form, {@code null} if unknown.
     * @param parameter The parameter name, {@code null} if the error is not about one parameter.
     * @throws IllegalArgumentException If {@code value} is {@code null}.
     */
    public DefinitionException(String message, JsonValue value, String id, String parameter)
    {
        super(format(message, value, id, parameter));

        source = value.getSource();
        line = value.getLine();
        column = value.getColumn();
        path = value.getPath();
        this.id = id;
        this.parameter = parameter;
    }

    /**
     * Returns the source name.
     *
     * @return The resource key or caller-given source name, {@code null} for a registry error or when unknown.
     */
    public String getSource()
    {
        return source;
    }

    /**
     * Returns the line.
     *
     * @return The line from 1, {@link #UNKNOWN} when unknown.
     */
    public int getLine()
    {
        return line;
    }

    /**
     * Returns the column.
     *
     * @return The column from 1, {@link #UNKNOWN} when unknown.
     */
    public int getColumn()
    {
        return column;
    }

    /**
     * Returns the JSON path of the faulty value.
     *
     * @return The JSON path, {@code null} when unknown.
     */
    public String getPath()
    {
        return path;
    }

    /**
     * Returns the registry id.
     *
     * @return The registry id, {@code null} for a document error or when unknown.
     */
    public String getId()
    {
        return id;
    }

    /**
     * Returns the parameter name.
     *
     * @return The parameter name, {@code null} when the error is not about one parameter.
     */
    public String getParameter()
    {
        return parameter;
    }
}
