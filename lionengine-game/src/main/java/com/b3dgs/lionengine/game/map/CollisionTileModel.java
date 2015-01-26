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
package com.b3dgs.lionengine.game.map;

import java.util.Collection;
import java.util.HashSet;

/**
 * Model implementation of the collision tile for fast inheritance.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public class CollisionTileModel
        implements CollisionTile
{
    /** Enum value. */
    private final Enum<?> value;
    /** Collision function X. */
    private final Collection<CollisionFunction> collisionFunctions;

    /**
     * Constructor.
     * 
     * @param value The collision enum value.
     */
    public CollisionTileModel(Enum<?> value)
    {
        this.value = value;
        collisionFunctions = new HashSet<>();
    }

    /*
     * CollisionTile
     */

    @Override
    public void addCollisionFunction(CollisionFunction function)
    {
        collisionFunctions.add(function);
    }

    @Override
    public void removeCollisionFunction(CollisionFunction function)
    {
        collisionFunctions.remove(function);
    }

    @Override
    public void removeCollisions()
    {
        collisionFunctions.clear();
    }

    @Override
    public Collection<CollisionFunction> getCollisionFunctions()
    {
        return collisionFunctions;
    }

    @Override
    public Enum<?> getValue()
    {
        return value;
    }
}
