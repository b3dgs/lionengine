/*
 * Copyright (C) 2013 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.example.c_platform.e_lionheart.entity.player;

import java.util.List;

import com.b3dgs.lionengine.example.c_platform.e_lionheart.map.TileCollision;
import com.b3dgs.lionengine.game.platform.CollisionTileCategory;

/**
 * List of valdyn collision categories on tile.
 */
public enum ValdynCollisionTileCategory implements CollisionTileCategory<TileCollision>
{
    /** Ground leg left. */
    LEG_LEFT(TileCollision.COLLISION_VERTICAL),
    /** Ground leg right. */
    LEG_RIGHT(TileCollision.COLLISION_VERTICAL),
    /** Horizontal knee left. */
    KNEE_LEFT(TileCollision.COLLISION_HORIZONTAL),
    /** Horizontal knee right. */
    KNEE_RIGHT(TileCollision.COLLISION_HORIZONTAL),
    /** Hand liana steep. */
    HAND_LIANA_STEEP(TileCollision.COLLISION_LIANA_STEEP),
    /** Hand liana leaning. */
    HAND_LIANA_LEANING(TileCollision.COLLISION_LIANA_LEANING);

    /** The collisions list. */
    private final List<TileCollision> collisions;

    /**
     * Constructor.
     * 
     * @param collisions The collisions list.
     */
    private ValdynCollisionTileCategory(List<TileCollision> collisions)
    {
        this.collisions = collisions;
    }

    @Override
    public List<TileCollision> getCollisions()
    {
        return collisions;
    }
}
