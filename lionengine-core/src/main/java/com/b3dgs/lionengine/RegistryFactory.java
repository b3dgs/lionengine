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
 * Creates an instance of a data-driven type from its parameters (ADR-0020, ADR-0032, REQ-RT-007).
 *
 * <p>
 * Registered in a {@link Registry} under a registry id, for example a constructor reference
 * {@code registry.register("lionengine:movable", Movable::new)} with a constructor taking a {@link Configuration}.
 * The factory reads its parameters by name and type; every member it does not read, except
 * {@link Configuration#COMMENT}, makes the creation fail.
 * </p>
 *
 * <p>
 * Thread: the thread that calls {@link Registry#create(String, Configuration)}, a loading worker included.
 * </p>
 *
 * @param <T> The type of the created instances.
 * @since 11.0.0
 */
@FunctionalInterface
public interface RegistryFactory<T>
{
    /**
     * Creates an instance.
     *
     * @param configuration The parameters (never {@code null}); valid during the call only.
     * @return The new instance (never {@code null}).
     * @throws DefinitionException If a parameter is missing, of the wrong type or invalid.
     */
    T create(Configuration configuration);
}
