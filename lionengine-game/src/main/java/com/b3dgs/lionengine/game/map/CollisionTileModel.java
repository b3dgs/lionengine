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

import java.util.HashSet;
import java.util.Set;

/**
 * Model implementation of the collision tile for fast inheritance.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public class CollisionTileModel
        implements CollisionTile
{
    /** Collision function X. */
    private final Set<CollisionFunction> collisionFunctions;

    /**
     * Constructor.
     */
    public CollisionTileModel()
    {
        collisionFunctions = new HashSet<>();
    }

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
    public Set<CollisionFunction> getCollisionFunctions()
    {
        return collisionFunctions;
    }
}
