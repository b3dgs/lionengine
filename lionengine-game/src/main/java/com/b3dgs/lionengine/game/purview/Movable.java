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
package com.b3dgs.lionengine.game.purview;

import com.b3dgs.lionengine.game.Direction;

/**
 * Represents something that can move and jump around.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public interface Movable
        extends Direction
{
    /**
     * Update the movement by updating the force calculation.
     * 
     * @param extrp The extrapolation value.
     */
    void updateMove(double extrp);

    /**
     * Reset the movement to zero.
     */
    void resetMove();

    /**
     * Set the movement to reach.
     * 
     * @param fh The horizontal direction.
     * @param fv The vertical direction.
     */
    void setMoveToReach(double fh, double fv);

    /**
     * Set the jump direction.
     * 
     * @param fh The horizontal force.
     * @param fv The vertical force.
     */
    void setJumpDirection(double fh, double fv);

    /**
     * Set the movement velocity.
     * 
     * @param velocity The movement velocity.
     */
    void setMoveVelocity(double velocity);

    /**
     * Set the sensibility value.
     * 
     * @param sensibility The sensibility value.
     */
    void setMoveSensibility(double sensibility);

    /**
     * Set the movement speed max.
     * 
     * @param max The movement speed max.
     */
    void setMoveSpeedMax(double max);

    /**
     * Set the jump height max.
     * 
     * @param max The jump height max.
     */
    void setJumpHeightMax(double max);

    /**
     * Get the jump height max.
     * 
     * @return The jump height max.
     */
    double getJumpHeightMax();

    /**
     * Get the jump direction.
     * 
     * @return The jump direction.
     */
    Direction getJumpDirection();

    /**
     * Get the movement speed max.
     * 
     * @return The movement speed max.
     */
    double getMoveSpeedMax();

    /**
     * Check if movement is horizontally decreasing.
     * 
     * @return <code>true</code> if horizontally decreasing, <code>false</code> else.
     */
    public boolean isMoveDecreasingX();

    /**
     * Check if movement is horizontally decreasing.
     * 
     * @return <code>true</code> if horizontally decreasing, <code>false</code> else.
     */
    public boolean isMoveIncreasingX();
}
