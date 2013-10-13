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
package com.b3dgs.lionengine.game;

/**
 * Represents a movement based on a current force and a force to reach.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public class Movement
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
     * Constructor.
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
        forceOldH = current.getForceHorizontal();
        current.reachForce(extrp, destination, velocity, sensibility);
    }

    /**
     * Reset the force to zero.
     */
    public void reset()
    {
        destination.setForce(Force.ZERO);
        current.setForce(Force.ZERO);
    }

    /**
     * Set the force to reach.
     * 
     * @param force The force to reach.
     */
    public void setForceToReach(Force force)
    {
        destination.setForce(force);
    }

    /**
     * Set the force to reach.
     * 
     * @param fh The horizontal force.
     * @param fv The vertical force.
     */
    public void setForceToReach(double fh, double fv)
    {
        destination.setForce(fh, fv);
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
     * Get the current force (active reference, not a copy).
     * 
     * @return The current force.
     */
    public Force getForce()
    {
        return current;
    }

    /**
     * Check if movement is horizontally decreasing.
     * 
     * @return <code>true</code> if horizontally decreasing, <code>false</code> else.
     */
    public boolean isDecreasingHorizontal()
    {
        return Math.abs(forceOldH) > Math.abs(current.getForceHorizontal());
    }

    /**
     * Check if movement is horizontally decreasing.
     * 
     * @return <code>true</code> if horizontally decreasing, <code>false</code> else.
     */
    public boolean isIncreasingHorizontal()
    {
        return Math.abs(forceOldH) < Math.abs(current.getForceHorizontal());
    }
}
