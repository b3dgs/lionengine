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
import java.util.HashSet;
import java.util.Set;

/**
 * Representation of a pathfindable tile.
 */
public class TilePathModel implements TilePath
{
    /** Object id reference. */
    private final Set<Integer> objectsId;
    /** Category name. */
    private final String category;

    /**
     * Create a tile path.
     * 
     * @param category The category name.
     */
    public TilePathModel(String category)
    {
        this.category = category;
        objectsId = new HashSet<Integer>();
    }

    /*
     * TilePath
     */

    @Override
    public void addObjectId(Integer id)
    {
        objectsId.add(id);
    }

    @Override
    public void removeObjectId(Integer id)
    {
        objectsId.remove(id);
    }

    @Override
    public Collection<Integer> getObjectsId()
    {
        return objectsId;
    }

    @Override
    public String getCategory()
    {
        return category;
    }
}
