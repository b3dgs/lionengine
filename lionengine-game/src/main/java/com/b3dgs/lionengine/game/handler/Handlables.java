/*
 * Copyright (C) 2013-2016 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
 */
package com.b3dgs.lionengine.game.handler;

import com.b3dgs.lionengine.LionEngineException;

/**
 * Represents the handlables handled by the handler, providing quick access to them from their type.
 * 
 * @see Handlable
 */
public interface Handlables
{
    /**
     * Get the handlable from its ID.
     * 
     * @param id The handlable ID.
     * @return The handlable instance.
     * @throws LionEngineException If no handlable found with this ID.
     */
    Handlable get(Integer id);

    /**
     * Get all handlables of this type.
     * 
     * @param <I> The handlable interface type.
     * @param type The expected type.
     * @return The handlables of this type.
     */
    <I> Iterable<I> get(Class<I> type);

    /**
     * Get all handlables.
     * 
     * @return The handlables.
     */
    Iterable<Handlable> values();
}
