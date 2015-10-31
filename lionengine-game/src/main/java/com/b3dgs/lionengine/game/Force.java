/*
 * Copyright (C) 2013-2015 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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

import com.b3dgs.lionengine.Updatable;
import com.b3dgs.lionengine.UtilMath;

/**
 * Represents a 2D vector force, using double precision. This can be used to describe a vectorial force, on 2 axis
 * (horizontal and vertical). Can be used as a speed.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public class Force implements Direction, Updatable
{
    /**
     * Create a force from a vector movement.
     * 
     * <p>
     * The created force will describe the following values:
     * </p>
     * <ul>
     * <li>horizontal normalized speed ({@link #getDirectionHorizontal()}),</li>
     * <li>vertical normalized speed ({@link #getDirectionVertical()}),</li>
     * <li>normal value ({@link #getVelocity()}).</li>
     * </ul>
     * 
     * @param ox The old horizontal location.
     * @param oy The old vertical location.
     * @param x The current horizontal location.
     * @param y The current vertical location.
     * @return The tiles found.
     */
    public static Force fromVector(double ox, double oy, double x, double y)
    {
        // Distance calculation
        final double dh = x - ox;
        final double dv = y - oy;

        // Search vector and number of search steps
        final double norm;
        if (dh > dv)
        {
            norm = Math.abs(dh);
        }
        else
        {
            norm = Math.abs(dv);
        }
        final double sx = dh / norm;
        final double sy = dv / norm;

        final Force force = new Force(sx, sy);
        force.setVelocity(norm);
        return force;
    }

    /** Horizontal force vector. */
    private double fh;
    /** Vertical force vector. */
    private double fv;
    /** The reach speed. */
    private double velocity;
    /** The sensibility. */
    private double sensibility;
    /** Horizontal old force vector. */
    private double fhOld;
    /** Vertical old force vector. */
    private double fvOld;
    /** Horizontal destination force vector. */
    private double fhDest;
    /** Vertical destination force vector. */
    private double fvDest;
    /** Last horizontal force. */
    private double fhLast;
    /** Last vertical force. */
    private double fvLast;
    /** Reached horizontal force. */
    private boolean arrivedH;
    /** Reached vertical force. */
    private boolean arrivedV;
    /** Maximum direction. */
    private Direction directionMax;
    /** Minimum direction. */
    private Direction directionMin;

    /**
     * Create a zero force.
     */
    public Force()
    {
        this(0.0, 0.0);
    }

    /**
     * Create a force with specified values.
     * 
     * @param fh The horizontal force.
     * @param fv The vertical force.
     */
    public Force(double fh, double fv)
    {
        this.fh = fh;
        this.fv = fv;
        fixForce();
    }

    /**
     * Create a copy force.
     * 
     * @param force The force reference.
     */
    public Force(Force force)
    {
        velocity = force.velocity;
        sensibility = force.sensibility;
        directionMin = force.directionMin;
        directionMax = force.directionMax;
        fixForce();
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
     * @param sensibility The sensibility value (will round to destination between -sensibility and +sensibility).
     */
    public void setSensibility(double sensibility)
    {
        this.sensibility = sensibility;
    }

    /**
     * Increase direction with input value.
     * 
     * @param direction The direction to add.
     */
    public void addDirection(Direction direction)
    {
        addDirection(direction.getDirectionHorizontal(), direction.getDirectionVertical());
    }

    /**
     * Increase forces with input value.
     * 
     * @param fh The added horizontal force.
     * @param fv The added vertical force.
     */
    public void addDirection(double fh, double fv)
    {
        fhLast = fh;
        fvLast = fv;
        this.fh += fh;
        this.fv += fv;
        fixForce();
    }

    /**
     * Set direction.
     * 
     * @param direction The direction.
     */
    public void setDirection(Direction direction)
    {
        setDirection(direction.getDirectionHorizontal(), direction.getDirectionVertical());
    }

    /**
     * Set directions.
     * 
     * @param fh The horizontal direction.
     * @param fv The vertical direction.
     */
    public void setDirection(double fh, double fv)
    {
        fhLast = fh;
        fvLast = fv;
        this.fh = fh;
        this.fv = fv;
        fixForce();
    }

    /**
     * Set force destination to reach.
     * 
     * @param fh The horizontal destination.
     * @param fv The vertical destination.
     */
    public void setDestination(double fh, double fv)
    {
        fhDest = fh;
        fvDest = fv;
    }

    /**
     * Set the maximum reachable direction.
     * 
     * @param max The direction max.
     */
    public void setDirectionMaximum(Direction max)
    {
        directionMax = max;
    }

    /**
     * Set the minimum reachable direction.
     * 
     * @param min The direction min.
     */
    public void setDirectionMinimum(Direction min)
    {
        directionMin = min;
    }

    /**
     * Get the current velocity.
     * 
     * @return The current velocity.
     */
    public double getVelocity()
    {
        return velocity;
    }

    /**
     * Check if movement is horizontally decreasing.
     * 
     * @return <code>true</code> if horizontally decreasing, <code>false</code> else.
     */
    public boolean isDecreasingHorizontal()
    {
        return Math.abs(fhOld) > Math.abs(fh);
    }

    /**
     * Check if movement is horizontally decreasing.
     * 
     * @return <code>true</code> if horizontally decreasing, <code>false</code> else.
     */
    public boolean isIncreasingHorizontal()
    {
        return Math.abs(fvOld) < Math.abs(fv);
    }

    /**
     * Update the last direction.
     */
    private void updateLastForce()
    {
        if (Double.compare(fhLast, fhDest) != 0)
        {
            fhLast = fhDest;
            arrivedH = false;
        }
        if (Double.compare(fvLast, fvDest) != 0)
        {
            fvLast = fvDest;
            arrivedV = false;
        }
    }

    /**
     * Update the force if still not reached on horizontal axis.
     * 
     * @param extrp The extrapolation value.
     */
    private void updateNotArrivedH(double extrp)
    {
        if (fh < fhDest)
        {
            fh += velocity * extrp;
            if (fh > fhDest - sensibility)
            {
                fh = fhDest;
                arrivedH = true;
            }
        }
        else if (fh > fhDest)
        {
            fh -= velocity * extrp;
            if (fh < fhDest + sensibility)
            {
                fh = fhDest;
                arrivedH = true;
            }
        }
    }

    /**
     * Update the force if still not reached on vertical axis.
     * 
     * @param extrp The extrapolation value.
     */
    private void updateNotArrivedV(double extrp)
    {
        if (fv < fvDest)
        {
            fv += velocity * extrp;
            if (fv > fvDest - sensibility)
            {
                fv = fvDest;
                arrivedV = true;
            }
        }
        else if (fv > fvDest)
        {
            fv -= velocity * extrp;
            if (fv < fvDest + sensibility)
            {
                fv = fvDest;
                arrivedV = true;
            }
        }
    }

    /**
     * Fix the force to its limited range.
     */
    private void fixForce()
    {
        final double minH;
        final double minV;
        final double maxH;
        final double maxV;

        if (directionMin == null)
        {
            minH = fh;
            minV = fv;
        }
        else
        {
            minH = directionMin.getDirectionHorizontal();
            minV = directionMin.getDirectionVertical();
        }
        if (directionMax == null)
        {
            maxH = fh;
            maxV = fv;
        }
        else
        {
            maxH = directionMax.getDirectionHorizontal();
            maxV = directionMax.getDirectionVertical();
        }
        fh = UtilMath.fixBetween(fh, minH, maxH);
        fv = UtilMath.fixBetween(fv, minV, maxV);
    }

    /*
     * Updatable
     */

    @Override
    public void update(double extrp)
    {
        fhOld = fh;
        fvOld = fv;
        updateLastForce();

        // Not arrived
        if (!arrivedH)
        {
            updateNotArrivedH(extrp);
        }
        else
        {
            fh = fhDest;
        }
        if (!arrivedV)
        {
            updateNotArrivedV(extrp);
        }
        else
        {
            fv = fvDest;
        }
        fixForce();
    }

    /*
     * Direction
     */

    @Override
    public double getDirectionHorizontal()
    {
        return fh;
    }

    @Override
    public double getDirectionVertical()
    {
        return fv;
    }
}
