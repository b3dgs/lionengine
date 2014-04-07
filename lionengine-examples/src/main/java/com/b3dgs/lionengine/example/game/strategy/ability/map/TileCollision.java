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
package com.b3dgs.lionengine.example.game.strategy.ability.map;

/**
 * List of collision types.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
enum TileCollision
{
    /** Ground collision. */
    GROUND0(TileCollisionGroup.GROUND),
    /** Ground collision. */
    GROUND1(TileCollisionGroup.GROUND),
    /** Ground collision. */
    GROUND2(TileCollisionGroup.GROUND),
    /** Ground collision. */
    GROUND3(TileCollisionGroup.GROUND),
    /** Ground collision. */
    GROUND4(TileCollisionGroup.GROUND),
    /** Ground collision. */
    GROUND5(TileCollisionGroup.GROUND),
    /** Ground collision. */
    GROUND6(TileCollisionGroup.GROUND),
    /** Ground collision. */
    GROUND7(TileCollisionGroup.GROUND),
    /** Ground collision. */
    GROUND8(TileCollisionGroup.GROUND),
    /** Ground collision. */
    GROUND9(TileCollisionGroup.GROUND),
    /** Tree collision. */
    TREE_BORDER(TileCollisionGroup.TREE),
    /** Tree collision. */
    TREE(TileCollisionGroup.TREE),
    /** Water collision. */
    WATER(TileCollisionGroup.WATER),
    /** Border collision. */
    BORDER(TileCollisionGroup.BORDER),
    /** No collision. */
    NONE(TileCollisionGroup.NONE);

    /** Collision group. */
    private final TileCollisionGroup group;

    /**
     * Constructor.
     * 
     * @param group The collision group.
     */
    private TileCollision(TileCollisionGroup group)
    {
        this.group = group;
    }

    /**
     * Get the collision group.
     * 
     * @return The collision group.
     */
    public TileCollisionGroup getGroup()
    {
        return group;
    }
}
