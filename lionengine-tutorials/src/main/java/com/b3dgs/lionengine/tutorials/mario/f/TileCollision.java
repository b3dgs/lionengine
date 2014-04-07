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
package com.b3dgs.lionengine.tutorials.mario.f;

import java.util.EnumSet;
import java.util.Set;

import com.b3dgs.lionengine.game.platform.CollisionFunction;
import com.b3dgs.lionengine.game.platform.CollisionTile;
import com.b3dgs.lionengine.game.platform.CollisionTileModel;

/**
 * List of tile collisions.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
enum TileCollision implements CollisionTile
{
    /** Ground collision. */
    GROUND,
    /** Block collision. */
    BLOCK,
    /** Wall collision. */
    WALL,
    /** Tube collision. */
    TUBE,
    /** No collision. */
    NONE;

    /** Vertical collisions list. */
    static final EnumSet<TileCollision> COLLISION_VERTICAL = EnumSet.noneOf(TileCollision.class);
    /** Horizontal collisions list. */
    static final EnumSet<TileCollision> COLLISION_HORIZONTAL = EnumSet.noneOf(TileCollision.class);

    /**
     * Static init.
     */
    static
    {
        TileCollision.COLLISION_VERTICAL.add(TileCollision.GROUND);
        TileCollision.COLLISION_VERTICAL.add(TileCollision.BLOCK);
        TileCollision.COLLISION_VERTICAL.add(TileCollision.TUBE);

        TileCollision.COLLISION_HORIZONTAL.add(TileCollision.GROUND);
        TileCollision.COLLISION_HORIZONTAL.add(TileCollision.BLOCK);
        TileCollision.COLLISION_HORIZONTAL.add(TileCollision.TUBE);
        TileCollision.COLLISION_HORIZONTAL.add(TileCollision.WALL);
    }

    /** Model. */
    private final CollisionTileModel model = new CollisionTileModel();

    /*
     * CollisionTile
     */

    @Override
    public void addCollisionFunction(CollisionFunction function)
    {
        model.addCollisionFunction(function);
    }

    @Override
    public void removeCollisionFunction(CollisionFunction function)
    {
        model.removeCollisionFunction(function);
    }

    @Override
    public Set<CollisionFunction> getCollisionFunctions()
    {
        return model.getCollisionFunctions();
    }
}
