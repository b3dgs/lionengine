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
package com.b3dgs.lionengine.game.rts.ability.mover;

import com.b3dgs.lionengine.game.Orientation;
import com.b3dgs.lionengine.game.Tiled;
import com.b3dgs.lionengine.game.pathfinding.Pathfindable;

/**
 * Represents the ability of moving on a PathBasedMap. It includes pathfinding handling.
 */
public interface MoverServices
        extends Pathfindable
{
    /**
     * Get the current orientation when moving.
     * 
     * @return The orientation movement.
     */
    Orientation getMovementOrientation();

    /**
     * Assign a specified location; will move automatically until reach it.
     * 
     * @param tiled The tiled to reach
     */
    void setDestination(Tiled tiled);

    /**
     * Adjust orientation to face to specified tile.
     * 
     * @param tx The horizontal tile to face.
     * @param ty The vertical tile to face.
     */
    void pointTo(int tx, int ty);

    /**
     * Adjust orientation to face to specified entity.
     * 
     * @param tiled The tiled to face to.
     */
    void pointTo(Tiled tiled);
}
