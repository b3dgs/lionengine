/*
 * Copyright (C) 2013-2020 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
 * Void {@link Updatable}.
 */
public final class UpdatableVoid
{
    /** Void instance. */
    private static final Updatable INSTANCE = extrp ->
    {
        // Nothing to do
    };

    /**
     * Get instance.
     * 
     * @return The instance.
     */
    public static Updatable getInstance()
    {
        return INSTANCE;
    }

    /**
     * Wrap updatable.
     * 
     * @param updatable The updatable to wrap.
     * @return The same updatable.
     */
    public static Updatable wrap(Updatable updatable)
    {
        return updatable;
    }

    /**
     * Private.
     */
    private UpdatableVoid()
    {
        throw new LionEngineException(LionEngineException.ERROR_PRIVATE_CONSTRUCTOR);
    }
}
