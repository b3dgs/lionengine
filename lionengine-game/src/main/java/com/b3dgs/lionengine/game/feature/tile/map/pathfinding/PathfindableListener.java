/*
 * Copyright (C) 2013-2024 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */
package com.b3dgs.lionengine.game.feature.tile.map.pathfinding;

/**
 * Pathfindable events listener.
 */
public interface PathfindableListener
{
    /**
     * Notify listener when mover starting to move.
     * 
     * @param pathfindable The pathfindable reference.
     */
    void notifyStartMove(Pathfindable pathfindable);

    /**
     * Notify listener while mover is moving.
     * 
     * @param pathfindable The pathfindable reference.
     * @param ox The old horizontal location.
     * @param oy The old vertical location.
     * @param nx The new horizontal location.
     * @param ny The new vertical location.
     */
    void notifyMoving(Pathfindable pathfindable, int ox, int oy, int nx, int ny);

    /**
     * Notify listener when mover has arrived.
     * 
     * @param pathfindable The pathfindable reference.
     */
    void notifyArrived(Pathfindable pathfindable);
}
