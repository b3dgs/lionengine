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
package com.b3dgs.lionengine.game.object;

import com.b3dgs.lionengine.LionEngineException;

/**
 * Represents the objects handled by the handler, providing quick access to them from their type.
 * 
 * @see ObjectGame
 */
public interface HandledObjects
{
    /**
     * Get the object from its id.
     * 
     * @param id The object id.
     * @return The handlable instance.
     * @throws LionEngineException If no object found with this id.
     * @see ObjectGame#getId()
     */
    ObjectGame get(Integer id);

    /**
     * Get all objects of this type.
     * 
     * @param <I> The object interface type.
     * @param type The expected type.
     * @return The objects list of this type.
     */
    <I> Iterable<I> get(Class<I> type);

    /**
     * Get all objects.
     * 
     * @return The objects list.
     */
    Iterable<ObjectGame> values();
}
