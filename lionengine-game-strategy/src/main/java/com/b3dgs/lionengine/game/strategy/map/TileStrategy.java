/*
 * Copyright (C) 2013-2014 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.game.strategy.map;

import com.b3dgs.lionengine.game.pathfinding.TilePath;

/**
 * Representation of a default tile, used for pathfinding.
 * 
 * @param <R> The resource enum type used.
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public abstract class TileStrategy<R extends Enum<R>>
        extends TilePath
{
    /** Resource type (<code>null</code> if none). */
    private R resourceType;

    /**
     * Constructor base.
     * 
     * @param width The tile width.
     * @param height The tile height.
     */
    public TileStrategy(int width, int height)
    {
        super(width, height);
    }

    /**
     * Check resource type from collision.
     * 
     * @return The resources found.
     */
    public abstract R checkResourceType();

    /**
     * Check if tile has resources.
     * 
     * @return <code>true</code> if has resources, <code>false</code> else.
     */
    public abstract boolean hasResources();

    /**
     * Set the resource type.
     * 
     * @param type The resource type.
     */
    public void setResourceType(R type)
    {
        resourceType = type;
    }

    /**
     * Get the resource type.
     * 
     * @return The resource type (<code>null</code> if none).
     */
    public R getResourceType()
    {
        return resourceType;
    }
}
