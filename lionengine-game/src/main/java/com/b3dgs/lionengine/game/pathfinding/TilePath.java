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
package com.b3dgs.lionengine.game.pathfinding;

import java.util.Collection;

import com.b3dgs.lionengine.game.tile.TileFeature;

/**
 * Tile representation with the following data:
 * <ul>
 * <li><code>objects ID</code> : current objects ID located over the tile</li>
 * <li><code>blocking</code> : flag to know if tile can block path</li>
 * </ul>
 * <p>
 * This allows to know easily which objects are on tile.
 * </p>
 */
public interface TilePath extends TileFeature
{
    /**
     * Add an object ID over this tile.
     * 
     * @param id The object ID reference to add.
     */
    void addObjectId(Integer id);

    /**
     * Remove an object ID from this tile.
     * 
     * @param id The object ID reference to remove.
     */
    void removeObjectId(Integer id);

    /**
     * Get the objects ID over this tile.
     * 
     * @return The objects ID over this tile.
     */
    Collection<Integer> getObjectsId();

    /**
     * Get the category name.
     * 
     * @return The category name.
     */
    String getCategory();
}
