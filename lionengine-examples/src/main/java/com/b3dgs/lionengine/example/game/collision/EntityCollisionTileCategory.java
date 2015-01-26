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
package com.b3dgs.lionengine.example.game.collision;

import java.util.Collection;

import com.b3dgs.lionengine.game.map.CollisionRefential;
import com.b3dgs.lionengine.game.map.CollisionTile;
import com.b3dgs.lionengine.game.map.CollisionTileCategory;

/**
 * List of entity collision categories on tile.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
enum EntityCollisionTileCategory implements CollisionTileCategory
{
    /** Default ground center collision. */
    GROUND_CENTER(TileCollision.COLLISION_VERTICAL, CollisionRefential.X, 0, 0),
    /** Ground leg left. */
    LEG_LEFT(TileCollision.COLLISION_VERTICAL, CollisionRefential.X, -4, 0),
    /** Ground leg right. */
    LEG_RIGHT(TileCollision.COLLISION_VERTICAL, CollisionRefential.X, 4, 0),
    /** Horizontal knee left. */
    KNEE_LEFT(TileCollision.COLLISION_HORIZONTAL, CollisionRefential.Y, -6, 1),
    /** Horizontal knee left. */
    KNEE_CENTER(TileCollision.COLLISION_HORIZONTAL, CollisionRefential.Y, 0, 1),
    /** Horizontal knee right. */
    KNEE_RIGHT(TileCollision.COLLISION_HORIZONTAL, CollisionRefential.Y, 6, 1);

    /** The collisions list. */
    private final Collection<CollisionTile> collisions;
    /** The slide axis. */
    private final CollisionRefential slide;
    /** Horizontal offset. */
    private final int x;
    /** Vertical offset. */
    private final int y;

    /**
     * Constructor.
     * 
     * @param collisions The collisions list.
     * @param slide The slide axis.
     * @param x The horizontal offset.
     * @param y The vertical offset.
     */
    private EntityCollisionTileCategory(Collection<CollisionTile> collisions, CollisionRefential slide, int x, int y)
    {
        this.collisions = collisions;
        this.slide = slide;
        this.x = x;
        this.y = y;
    }

    @Override
    public Collection<CollisionTile> getCollisions()
    {
        return collisions;
    }

    @Override
    public CollisionRefential getSlide()
    {
        return slide;
    }

    @Override
    public int getOffsetX()
    {
        return x;
    }

    @Override
    public int getOffsetY()
    {
        return y;
    }
}
