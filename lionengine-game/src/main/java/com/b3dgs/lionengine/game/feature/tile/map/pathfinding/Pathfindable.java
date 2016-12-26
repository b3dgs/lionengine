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
package com.b3dgs.lionengine.game.feature.tile.map.pathfinding;

import java.util.Collection;

import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Localizable;
import com.b3dgs.lionengine.Updatable;
import com.b3dgs.lionengine.game.Feature;
import com.b3dgs.lionengine.game.Tiled;
import com.b3dgs.lionengine.game.feature.tile.map.Orientable;
import com.b3dgs.lionengine.graphic.Renderable;

/**
 * Describe an object which can move on a {@link MapTilePath} by using A-Star algorithm.
 */
public interface Pathfindable extends Feature, Tiled, Orientable, Updatable, Renderable
{
    /**
     * Add a pathfindable listener.
     * 
     * @param listener The pathfindable listener to add.
     */
    void addListener(PathfindableListener listener);

    /**
     * Clear all ignored objects ID.
     */
    void clearIgnoredId();

    /**
     * Clear the list of objects ID that share the same path.
     */
    void clearSharedPathIds();

    /**
     * Move to specified destination only when calling this function.
     * 
     * @param extrp The extrapolation value.
     * @param x The destination horizontal location.
     * @param y The destination vertical location.
     */
    void moveTo(double extrp, double x, double y);

    /**
     * Stop any pathfinding movements.
     */
    void stopMoves();

    /**
     * Set movement speed.
     * 
     * @param speedX The horizontal speed.
     * @param speedY The vertical speed.
     */
    void setSpeed(double speedX, double speedY);

    /**
     * Ignore an object ID while searching pathfinding. It allows to not be blocked by this ID.
     * 
     * @param id The object ID to ignore.
     * @param state <code>true</code> to ignore, <code>false</code> else.
     */
    void setIgnoreId(Integer id, boolean state);

    /**
     * Set the object ID list that shares the same path (this can be used in grouped movement).
     * 
     * @param ids The object ID list to add.
     */
    void setSharedPathIds(Collection<Integer> ids);

    /**
     * Assign a specified location. Will move automatically until reach it after this call.
     * 
     * @param localizable The destination location.
     * @return <code>true</code> if destination reachable, <code>false</code> else.
     */
    boolean setDestination(Localizable localizable);

    /**
     * Assign a specified location. Will move automatically until reach it after this call.
     * 
     * @param tiled The destination location in tile.
     * @return <code>true</code> if destination reachable, <code>false</code> else.
     */
    boolean setDestination(Tiled tiled);

    /**
     * Assign a specified location. Will move automatically until reach it after this call.
     * 
     * @param tx The horizontal location in tile.
     * @param ty The vertical location in tile.
     * @return <code>true</code> if destination reachable, <code>false</code> else.
     */
    boolean setDestination(int tx, int ty);

    /**
     * Set specified location in tile.
     * 
     * @param tx The horizontal location in tile.
     * @param ty The vertical location in tile.
     */
    void setLocation(int tx, int ty);

    /**
     * Render additional information on path rendering (movement cost for each tile is displayed).
     * 
     * @param debug <code>true</code> to show debug information relative to path, <code>false</code> else.
     */
    void setRenderDebug(boolean debug);

    /**
     * Get horizontal movement speed.
     * 
     * @return The horizontal movement speed.
     */
    double getSpeedX();

    /**
     * Get vertical movement speed.
     * 
     * @return The vertical movement speed.
     */
    double getSpeedY();

    /**
     * Get horizontal current speed.
     * 
     * @return The horizontal current speed.
     */
    double getMoveX();

    /**
     * Get vertical current speed.
     * 
     * @return The vertical current speed.
     */
    double getMoveY();

    /**
     * Get the cost movement depending of the category.
     * 
     * @param category The category the check.
     * @return The category movement cost.
     * @throws LionEngineException If category has not been found.
     */
    double getCost(String category);

    /**
     * Check if movement is allowed for the specified tile.
     * 
     * @param category The category the check.
     * @param movement The movement to check.
     * @return <code>true</code> if movement allowed, <code>false</code> else.
     * @throws LionEngineException If category has not been found.
     */
    boolean isMovementAllowed(String category, MovementTile movement);

    /**
     * Check if a path exists between object and destination.
     * 
     * @param tx The horizontal location in tile.
     * @param ty The vertical location in tile.
     * @return <code>true</code> if path exists, <code>false</code> else.
     */
    boolean isPathAvailable(int tx, int ty);

    /**
     * Check if the category is considered as blocking.
     * 
     * @param category The category the check.
     * @return <code>true</code> if blocking, <code>false</code> else or category not found.
     */
    boolean isBlocking(String category);

    /**
     * Check if has reached destination.
     * 
     * @return <code>true</code> if destination has been reached, <code>false</code> else.
     */
    boolean isDestinationReached();

    /**
     * Check if object ID is ignored.
     * 
     * @param id The object ID to check.
     * @return <code>true</code> if ignored, <code>false</code> else.
     */
    boolean isIgnoredId(Integer id);

    /**
     * Check if currently moving.
     * 
     * @return <code>true</code> if moving, <code>false</code> else.
     */
    boolean isMoving();
}
