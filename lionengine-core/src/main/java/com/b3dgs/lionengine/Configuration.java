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
 * Typed, read-only view of one JSON object of a definition file (ADR-0020, ADR-0032, REQ-DEF-002, REQ-DEF-003).
 *
 * <p>
 * Getters read a member by name and type, each with and without a default. A missing required member, a value of
 * the wrong type or an integer outside the {@code int} range throws a {@link DefinitionException} located at the
 * value (or at the object for a missing member) and naming the parameter and, when the configuration is used by a
 * {@link Registry}, the registry id. Factory-specific checks, such as a range, throw the error built by
 * {@link #createError(String, String)}.
 * </p>
 *
 * <p>
 * Strict fields: the configuration remembers which members were read. {@link #checkAllRead()}, called by the
 * registry after the factory returns, or by a loader when it has read a file, fails on the first member that was not
 * read, except {@link #COMMENT}. Objects obtained with {@link #getObject(String)} are checked with their parent.
 * </p>
 *
 * <p>
 * Engine definition files start with {@link #FORMAT} and {@link #VERSION}, checked by
 * {@link #checkFormat(String, int)}.
 * </p>
 *
 * <p>
 * One configuration serves one creation or one loader. Not thread-safe: it records the members read.
 * </p>
 *
 * @since 11.0.0
 */
public final class Configuration
{
    /** Member that documents a definition and is never reported as unread. */
    public static final String COMMENT = "comment";
    /** Member naming the kind of an engine definition file, for example {@code lionengine.sheet}. */
    public static final String FORMAT = "format";
    /** Member giving the version of an engine definition file, an {@code int} from 1. */
    public static final String VERSION = "version";

    /** Error: a required member is missing. */
    static final String ERROR_MISSING = "Missing value";
    /** Error: a member was not read. */
    static final String ERROR_UNREAD = "Unknown field";
    /** Error start: the format is not the expected one. */
    static final String ERROR_FORMAT = "Unknown format ";
    /** Error start: the version is not supported. */
    static final String ERROR_VERSION = "Unsupported version ";

    /** End of the version error. */
    private static final String SUPPORTED = ", supported from 1 to ";
    /** End of the format error. */
    private static final String EXPECTED = ", expected ";

    /** Object read by this configuration. */
    private final JsonObject object;
    /** Whether each member, by index, was read. */
    private final boolean[] read;
    /** Configuration of each object member obtained, by index, {@code null} if none. */
    private final Configuration[] children;
    /** Registry id named in errors, {@code null} if none. */
    private String id;
    /** Whether a registry has used this configuration for a creation. */
    private boolean bound;

    /**
     * Creates a configuration of an object.
     *
     * @param object The object to read (never {@code null}).
     * @throws IllegalArgumentException If {@code object} is {@code null}.
     */
    public Configuration(JsonObject object)
    {
        this(object, null);
    }

    /**
     * Creates a configuration of an object for a registry id.
     *
     * @param object The object to read.
     * @param id The registry id named in errors, {@code null} if none.
     * @throws IllegalArgumentException If {@code object} is {@code null}.
     */
    private Configuration(JsonObject object, String id)
    {
        if (object == null)
        {
            throw new IllegalArgumentException("Object must not be null");
        }
        this.object = object;
        this.id = id;
        read = new boolean[object.size()];
        children = new Configuration[object.size()];
    }

    /**
     * Returns a required {@code int} member.
     *
     * @param name The member name.
     * @return The value.
     * @throws DefinitionException If the member is missing, not an integer or outside the {@code int} range.
     */
    public int getInt(String name)
    {
        return toInt(required(name), name);
    }

    /**
     * Returns an optional {@code int} member.
     *
     * @param name The member name.
     * @param defaultValue The value returned when the member is missing.
     * @return The value, or the default.
     * @throws DefinitionException If the member is not an integer or is outside the {@code int} range.
     */
    public int getInt(String name, int defaultValue)
    {
        final JsonValue value = optional(name);
        int result = defaultValue;
        if (value != null)
        {
            result = toInt(value, name);
        }
        return result;
    }

    /**
     * Returns a required {@code long} member.
     *
     * @param name The member name.
     * @return The value.
     * @throws DefinitionException If the member is missing or not an integer.
     */
    public long getLong(String name)
    {
        return toLong(required(name), name);
    }

    /**
     * Returns an optional {@code long} member.
     *
     * @param name The member name.
     * @param defaultValue The value returned when the member is missing.
     * @return The value, or the default.
     * @throws DefinitionException If the member is not an integer.
     */
    public long getLong(String name, long defaultValue)
    {
        final JsonValue value = optional(name);
        long result = defaultValue;
        if (value != null)
        {
            result = toLong(value, name);
        }
        return result;
    }

    /**
     * Returns a required {@code double} member; an integer is converted.
     *
     * @param name The member name.
     * @return The value.
     * @throws DefinitionException If the member is missing or not a number.
     */
    public double getDouble(String name)
    {
        return toDouble(required(name), name);
    }

    /**
     * Returns an optional {@code double} member; an integer is converted.
     *
     * @param name The member name.
     * @param defaultValue The value returned when the member is missing.
     * @return The value, or the default.
     * @throws DefinitionException If the member is not a number.
     */
    public double getDouble(String name, double defaultValue)
    {
        final JsonValue value = optional(name);
        double result = defaultValue;
        if (value != null)
        {
            result = toDouble(value, name);
        }
        return result;
    }

    /**
     * Returns a required {@code boolean} member.
     *
     * @param name The member name.
     * @return The value.
     * @throws DefinitionException If the member is missing or not a boolean.
     */
    public boolean getBoolean(String name)
    {
        return toBoolean(required(name), name);
    }

    /**
     * Returns an optional {@code boolean} member.
     *
     * @param name The member name.
     * @param defaultValue The value returned when the member is missing.
     * @return The value, or the default.
     * @throws DefinitionException If the member is not a boolean.
     */
    public boolean getBoolean(String name, boolean defaultValue)
    {
        final JsonValue value = optional(name);
        boolean result = defaultValue;
        if (value != null)
        {
            result = toBoolean(value, name);
        }
        return result;
    }

    /**
     * Returns a required string member.
     *
     * @param name The member name.
     * @return The value (never {@code null}).
     * @throws DefinitionException If the member is missing or not a string.
     */
    public String getString(String name)
    {
        return toText(required(name), name);
    }

    /**
     * Returns an optional string member.
     *
     * @param name The member name.
     * @param defaultValue The value returned when the member is missing, may be {@code null}.
     * @return The value, or the default.
     * @throws DefinitionException If the member is not a string.
     */
    public String getString(String name, String defaultValue)
    {
        final JsonValue value = optional(name);
        String result = defaultValue;
        if (value != null)
        {
            result = toText(value, name);
        }
        return result;
    }

    /**
     * Returns a required object member, as a configuration checked with this one by {@link #checkAllRead()}.
     *
     * @param name The member name.
     * @return The configuration of the object, the same instance on each call (never {@code null}).
     * @throws DefinitionException If the member is missing or not an object.
     */
    public Configuration getObject(String name)
    {
        required(name);
        return child(name);
    }

    /**
     * Returns an optional object member, as a configuration checked with this one by {@link #checkAllRead()}.
     *
     * @param name The member name.
     * @param defaultValue The value returned when the member is missing, may be {@code null}.
     * @return The configuration of the object, the same instance on each call, or the default.
     * @throws DefinitionException If the member is not an object.
     */
    public Configuration getObject(String name, Configuration defaultValue)
    {
        Configuration result = defaultValue;
        if (optional(name) != null)
        {
            result = child(name);
        }
        return result;
    }

    /**
     * Returns a required array member. Its elements are read with the {@link JsonValue} accessors; an object element
     * is checked by a configuration of its own.
     *
     * @param name The member name.
     * @return The array (never {@code null}).
     * @throws DefinitionException If the member is missing or not an array.
     */
    public JsonArray getArray(String name)
    {
        return toArray(required(name), name);
    }

    /**
     * Returns an optional array member.
     *
     * @param name The member name.
     * @param defaultValue The value returned when the member is missing, may be {@code null}.
     * @return The array, or the default.
     * @throws DefinitionException If the member is not an array.
     */
    public JsonArray getArray(String name, JsonArray defaultValue)
    {
        final JsonValue value = optional(name);
        JsonArray result = defaultValue;
        if (value != null)
        {
            result = toArray(value, name);
        }
        return result;
    }

    /**
     * Checks the header of an engine definition file: {@link #FORMAT} must be the expected kind and {@link #VERSION}
     * an integer from 1 to the newest version the caller supports.
     *
     * @param format The expected kind, for example {@code lionengine.sheet} (never {@code null}).
     * @param maxVersion The newest version the caller supports, from 1.
     * @return The version of the file, in {@code [1, maxVersion]}.
     * @throws IllegalArgumentException If {@code format} is {@code null} or {@code maxVersion} is below 1.
     * @throws DefinitionException If a member is missing, of the wrong type, of another kind, or of an unsupported
     *             version.
     */
    public int checkFormat(String format, int maxVersion)
    {
        if (format == null || maxVersion < 1)
        {
            throw new IllegalArgumentException("Invalid format check: " + format + ' ' + maxVersion);
        }
        final String actual = getString(FORMAT);
        if (!format.equals(actual))
        {
            throw createError(FORMAT, ERROR_FORMAT + actual + EXPECTED + format);
        }
        final int version = getInt(VERSION);
        if (version < 1 || version > maxVersion)
        {
            throw createError(VERSION, ERROR_VERSION + version + SUPPORTED + maxVersion);
        }
        return version;
    }

    /**
     * Checks that every member was read, except {@link #COMMENT}, here and in the objects obtained with
     * {@link #getObject(String)}.
     *
     * @throws DefinitionException On the first member not read, in document order, located at its value.
     */
    public void checkAllRead()
    {
        final int count = object.size();
        for (int i = 0; i < count; i++)
        {
            final String name = object.getKey(i);
            if (!read[i] && !COMMENT.equals(name))
            {
                throw new DefinitionException(ERROR_UNREAD, object.getValue(i), id, name);
            }
        }
        for (int i = 0; i < count; i++)
        {
            if (children[i] != null)
            {
                children[i].checkAllRead();
            }
        }
    }

    /**
     * Creates an error about a parameter, for a check of the factory or the loader itself.
     *
     * @param name The parameter name.
     * @param message The error description.
     * @return The error, located at the member value, or at the object if the member is missing, and naming the
     *         parameter and the registry id; to throw.
     */
    public DefinitionException createError(String name, String message)
    {
        JsonValue location = object.get(name);
        if (location == null)
        {
            location = object;
        }
        return new DefinitionException(message, location, id, name);
    }

    /**
     * Returns the object read by this configuration.
     *
     * @return The object.
     */
    JsonObject getJson()
    {
        return object;
    }

    /**
     * Marks this configuration as used by one registry creation, named in later errors.
     *
     * @param registryId The registry id.
     * @throws IllegalStateException If a registry already used this configuration.
     */
    void bind(String registryId)
    {
        if (bound)
        {
            throw new IllegalStateException("Configuration already used for " + id);
        }
        bound = true;
        id = registryId;
    }

    /**
     * Reads a required member and marks it as read.
     *
     * @param name The member name.
     * @return The value.
     * @throws DefinitionException If the member is missing.
     */
    private JsonValue required(String name)
    {
        final JsonValue value = optional(name);
        if (value == null)
        {
            throw new DefinitionException(ERROR_MISSING, object, id, name);
        }
        return value;
    }

    /**
     * Reads an optional member and marks it as read.
     *
     * @param name The member name.
     * @return The value, {@code null} if missing.
     */
    private JsonValue optional(String name)
    {
        final int index = object.indexOf(name);
        JsonValue value = null;
        if (index >= 0)
        {
            read[index] = true;
            value = object.getValue(index);
        }
        return value;
    }

    /**
     * Returns the configuration of an object member read before, created on the first call.
     *
     * @param name The member name, present.
     * @return The configuration of the object.
     * @throws DefinitionException If the member is not an object.
     */
    private Configuration child(String name)
    {
        final int index = object.indexOf(name);
        if (children[index] == null)
        {
            final JsonValue value = object.getValue(index);
            check(value, name, value.getKind() == JsonValue.Kind.OBJECT, JsonValue.Kind.OBJECT);
            children[index] = new Configuration(value.asObject(), id);
        }
        return children[index];
    }

    /**
     * Converts a value to an {@code int}.
     *
     * @param value The value.
     * @param name The member name.
     * @return The integer.
     * @throws DefinitionException If the value is not an integer or is outside the {@code int} range.
     */
    private int toInt(JsonValue value, String name)
    {
        final long integer = toLong(value, name);
        if (integer < Integer.MIN_VALUE || integer > Integer.MAX_VALUE)
        {
            throw new DefinitionException(JsonNumber.ERROR_INT_RANGE, value, id, name);
        }
        return (int) integer;
    }

    /**
     * Converts a value to a {@code long}.
     *
     * @param value The value.
     * @param name The member name.
     * @return The integer.
     * @throws DefinitionException If the value is not an integer.
     */
    private long toLong(JsonValue value, String name)
    {
        check(value, name, value.getKind() == JsonValue.Kind.LONG, JsonValue.Kind.LONG);
        return value.asLong();
    }

    /**
     * Converts a value to a {@code double}.
     *
     * @param value The value.
     * @param name The member name.
     * @return The number.
     * @throws DefinitionException If the value is not a number.
     */
    private double toDouble(JsonValue value, String name)
    {
        final JsonValue.Kind kind = value.getKind();
        if (kind != JsonValue.Kind.LONG && kind != JsonValue.Kind.DOUBLE)
        {
            throw new DefinitionException(value.typeMessage(JsonValue.NUMBER), value, id, name);
        }
        return value.asDouble();
    }

    /**
     * Converts a value to a {@code boolean}.
     *
     * @param value The value.
     * @param name The member name.
     * @return The boolean.
     * @throws DefinitionException If the value is not a boolean.
     */
    private boolean toBoolean(JsonValue value, String name)
    {
        check(value, name, value.getKind() == JsonValue.Kind.BOOLEAN, JsonValue.Kind.BOOLEAN);
        return value.asBoolean();
    }

    /**
     * Converts a value to a string.
     *
     * @param value The value.
     * @param name The member name.
     * @return The string.
     * @throws DefinitionException If the value is not a string.
     */
    private String toText(JsonValue value, String name)
    {
        check(value, name, value.getKind() == JsonValue.Kind.STRING, JsonValue.Kind.STRING);
        return value.asString();
    }

    /**
     * Converts a value to an array.
     *
     * @param value The value.
     * @param name The member name.
     * @return The array.
     * @throws DefinitionException If the value is not an array.
     */
    private JsonArray toArray(JsonValue value, String name)
    {
        check(value, name, value.getKind() == JsonValue.Kind.ARRAY, JsonValue.Kind.ARRAY);
        return value.asArray();
    }

    /**
     * Checks the kind of a value.
     *
     * @param value The value.
     * @param name The member name.
     * @param valid {@code true} if the value has the expected kind.
     * @param expected The expected kind, for the message.
     * @throws DefinitionException If the value does not have the expected kind.
     */
    private void check(JsonValue value, String name, boolean valid, JsonValue.Kind expected)
    {
        if (!valid)
        {
            throw new DefinitionException(value.typeMessage(expected), value, id, name);
        }
    }
}
