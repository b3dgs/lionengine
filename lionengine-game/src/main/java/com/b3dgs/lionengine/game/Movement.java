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
package com.b3dgs.lionengine.game;

/**
 * Represents a movement based on a current force and a force to reach.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public class Movement
        implements Direction
{
    /** The current force. */
    private final Force current;
    /** The force to reach. */
    private final Force destination;
    /** The reach speed. */
    private double velocity;
    /** The sensibility. */
    private double sensibility;
    /** Old horizontal force. */
    private double forceOldH;

    /**
     * Create a blank movement.
     */
    public Movement()
    {
        current = new Force();
        destination = new Force();
    }

    /**
     * Update the movement by updating the force calculation.
     * 
     * @param extrp The extrapolation value.
     */
    public void update(double extrp)
    {
        forceOldH = current.getDirectionHorizontal();
        current.reachForce(extrp, destination, velocity, sensibility);
    }

    /**
     * Reset the force to zero.
     */
    public void reset()
    {
        destination.setDirection(Direction.ZERO);
        current.setDirection(Direction.ZERO);
    }

    /**
     * Set the direction to reach.
     * 
     * @param direction The direction to reach.
     */
    public void setDirectionToReach(Direction direction)
    {
        destination.setDirection(direction);
    }

    /**
     * Set the direction to reach.
     * 
     * @param fh The horizontal direction.
     * @param fv The vertical direction.
     */
    public void setDirectionToReach(double fh, double fv)
    {
        destination.setDirection(fh, fv);
    }

    /**
     * Set the current force.
     * 
     * @param force The current force.
     */
    public void setForce(Force force)
    {
        current.setDirection(force);
    }

    /**
     * Set the current force.
     * 
     * @param fh The horizontal force.
     * @param fv The vertical force.
     */
    public void setForce(double fh, double fv)
    {
        current.setDirection(fh, fv);
    }

    /**
     * Set the movement velocity.
     * 
     * @param velocity The movement velocity.
     */
    public void setVelocity(double velocity)
    {
        this.velocity = velocity;
    }

    /**
     * Set the sensibility value.
     * 
     * @param sensibility The sensibility value.
     */
    public void setSensibility(double sensibility)
    {
        this.sensibility = sensibility;
    }

    /**
     * Check if movement is horizontally decreasing.
     * 
     * @return <code>true</code> if horizontally decreasing, <code>false</code> else.
     */
    public boolean isDecreasingHorizontal()
    {
        return Math.abs(forceOldH) > Math.abs(current.getDirectionHorizontal());
    }

    /**
     * Check if movement is horizontally decreasing.
     * 
     * @return <code>true</code> if horizontally decreasing, <code>false</code> else.
     */
    public boolean isIncreasingHorizontal()
    {
        return Math.abs(forceOldH) < Math.abs(current.getDirectionHorizontal());
    }

    /*
     * Direction
     */

    @Override
    public double getDirectionHorizontal()
    {
        return current.getDirectionHorizontal();
    }

    @Override
    public double getDirectionVertical()
    {
        return current.getDirectionVertical();
    }
}
