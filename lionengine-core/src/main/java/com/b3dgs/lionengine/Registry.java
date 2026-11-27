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

import java.util.HashMap;
import java.util.Map;

/**
 * Explicit registry of data-driven types: registry ids mapped to factories (ADR-0020, ADR-0032, REQ-RT-007,
 * REQ-DEF-003).
 *
 * <p>
 * A registry id has the form {@code namespace:name}, each part matching {@code [a-z][a-z0-9_-]*}; the engine uses
 * the namespace {@link #ENGINE_NAMESPACE}, a game its own. Definition files name ids, never Java classes: there is no
 * reflection, {@code ServiceLoader} or class scanning.
 * </p>
 *
 * <p>
 * An instance, passed explicitly; several registries may coexist. The game fills it at startup, before any loading,
 * with {@link #register(String, RegistryFactory)}. Not thread-safe while being filled; once filled, it may be used by
 * the threads started after the registration, such as loading workers (ADR-0019).
 * </p>
 *
 * @param <T> The common type of the created instances.
 * @since 11.0.0
 */
public final class Registry<T>
{
    /** Namespace of the engine's own ids. */
    public static final String ENGINE_NAMESPACE = "lionengine";

    /** Error: an id that does not have the {@code namespace:name} form. */
    static final String ERROR_INVALID_ID = "Invalid registry id";
    /** Error: an id registered twice. */
    static final String ERROR_DUPLICATE_ID = "Duplicate registry id";
    /** Error: an id without factory. */
    static final String ERROR_UNKNOWN_ID = "Unknown registry id";

    /** Separator between the namespace and the name. */
    private static final char SEPARATOR = ':';

    /**
     * Tells whether an id has the {@code namespace:name} form, each part matching {@code [a-z][a-z0-9_-]*}.
     *
     * @param id The id to check, may be {@code null}.
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
     * Tells whether a part of an id matches {@code [a-z][a-z0-9_-]*}.
     *
     * @param id The id.
     * @param start The index of the first character of the part.
     * @param end The index after the last character of the part.
     * @return {@code true} if the part is valid.
     */
    private static boolean isValidPart(String id, int start, int end)
    {
        boolean valid = end > start && isLowerCase(id.charAt(start));
        for (int i = start + 1; valid && i < end; i++)
        {
            final char c = id.charAt(i);
            valid = isLowerCase(c) || JsonPath.isDigit(c) || c == '_' || c == '-';
        }
        return valid;
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

    /** Factories by id. */
    private final Map<String, RegistryFactory<? extends T>> factories = new HashMap<>();

    /**
     * Creates an empty registry.
     */
    public Registry()
    {
        super();
    }

    /**
     * Registers the factory of an id.
     *
     * @param id The registry id, {@code namespace:name}.
     * @param factory The factory creating the instances of this id (never {@code null}).
     * @throws IllegalArgumentException If {@code factory} is {@code null}.
     * @throws DefinitionException If the id is invalid or already registered.
     */
    public void register(String id, RegistryFactory<? extends T> factory)
    {
        if (factory == null)
        {
            throw new IllegalArgumentException("Factory must not be null");
        }
        if (!isValidId(id))
        {
            throw new DefinitionException(ERROR_INVALID_ID, id, null);
        }
        if (factories.containsKey(id))
        {
            throw new DefinitionException(ERROR_DUPLICATE_ID, id, null);
        }
        factories.put(id, factory);
    }

    /**
     * Creates an instance: calls the factory of the id, then checks that it read every member of the configuration
     * except {@link Configuration#COMMENT}.
     *
     * @param id The registry id, usually read from a definition file.
     * @param configuration The parameters of the instance (never {@code null}); it serves this creation only.
     * @return The new instance (never {@code null}).
     * @throws IllegalArgumentException If {@code configuration} is {@code null}.
     * @throws IllegalStateException If the configuration served another creation, or the factory returned
     *             {@code null}.
     * @throws DefinitionException If the id is invalid or not registered, or a parameter is missing, of the wrong
     *             type, invalid or not read; the error names the id.
     */
    public T create(String id, Configuration configuration)
    {
        if (configuration == null)
        {
            throw new IllegalArgumentException("Configuration must not be null");
        }
        if (!isValidId(id))
        {
            throw new DefinitionException(ERROR_INVALID_ID, configuration.getJson(), id, null);
        }
        final RegistryFactory<? extends T> factory = factories.get(id);
        if (factory == null)
        {
            throw new DefinitionException(ERROR_UNKNOWN_ID, configuration.getJson(), id, null);
        }
        configuration.bind(id);
        final T instance = factory.create(configuration);
        if (instance == null)
        {
            throw new IllegalStateException("Factory returned null for " + id);
        }
        configuration.checkAllRead();
        return instance;
    }
}
