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
package com.b3dgs.lionengine.game.purview.model;

import com.b3dgs.lionengine.game.Direction;
import com.b3dgs.lionengine.game.Force;
import com.b3dgs.lionengine.game.Movement;
import com.b3dgs.lionengine.game.purview.Movable;

/**
 * Default movable implementation
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public class MovableModel
        implements Movable
{
    /** Movement force. */
    private final Movement movement;
    /** Movement jump force. */
    private final Force jumpForce;
    /** Jump max force. */
    private double jumpHeightMax;
    /** Movement max speed. */
    private double movementSpeedMax;

    /**
     * Create the model.
     */
    public MovableModel()
    {
        movement = new Movement();
        jumpForce = new Force();
    }

    /*
     * Movable
     */

    @Override
    public void updateMove(double extrp)
    {
        movement.update(extrp);
    }

    @Override
    public void resetMove()
    {
        movement.reset();
    }

    @Override
    public void setMoveToReach(double fh, double fv)
    {
        movement.setDirectionToReach(fh, fv);
    }

    @Override
    public void setJumpDirection(double fh, double fv)
    {
        jumpForce.setDirection(fh, fv);
    }

    @Override
    public void setMoveVelocity(double velocity)
    {
        movement.setVelocity(velocity);
    }

    @Override
    public void setMoveSensibility(double sensibility)
    {
        movement.setSensibility(sensibility);
    }

    @Override
    public void setMoveSpeedMax(double max)
    {
        movementSpeedMax = max;
    }

    @Override
    public void setJumpHeightMax(double max)
    {
        jumpHeightMax = max;
    }

    @Override
    public double getJumpHeightMax()
    {
        return jumpHeightMax;
    }

    @Override
    public Direction getJumpDirection()
    {
        return jumpForce;
    }

    @Override
    public double getMoveSpeedMax()
    {
        return movementSpeedMax;
    }

    @Override
    public boolean isMoveDecreasingX()
    {
        return movement.isDecreasingHorizontal();
    }

    @Override
    public boolean isMoveIncreasingX()
    {
        return movement.isIncreasingHorizontal();
    }

    @Override
    public double getDirectionHorizontal()
    {
        return movement.getDirectionHorizontal();
    }

    @Override
    public double getDirectionVertical()
    {
        return movement.getDirectionVertical();
    }
}
