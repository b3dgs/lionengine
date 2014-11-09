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

import com.b3dgs.lionengine.UtilMath;

/**
 * Represents a 2D vector force, using double precision. This can be used to describe a vectorial force, on 2 axis
 * (horizontal & vertical). Can be used as a speed.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public final class Force
        implements Direction
{
    /** Horizontal force vector. */
    private double fh;
    /** Vertical force vector. */
    private double fv;
    /** Last horizontal force. */
    private double lastFh;
    /** Last vertical force. */
    private double lastFv;
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
     * Reach specified force at the specified speed. Has to be called in an <code>update(extrp)</code> function.
     * 
     * @param extrp The extrapolation value.
     * @param direction The direction to reach.
     * @param speed The reach speed.
     * @param sensibility The sensibility value (will round to destination between -sensibility and +sensibility).
     */
    public void reachForce(double extrp, Direction direction, double speed, double sensibility)
    {
        updateLastForce(direction);

        // Not arrived
        if (!arrivedH)
        {
            updateNotArrivedH(extrp, direction, speed, sensibility);
        }
        else
        {
            fh = direction.getDirectionHorizontal();
        }
        if (!arrivedV)
        {
            updateNotArrivedV(extrp, direction, speed, sensibility);
        }
        else
        {
            fv = direction.getDirectionVertical();
        }
        fixForce();
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
        lastFh = fh;
        lastFv = fv;
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
        lastFh = fh;
        lastFv = fv;
        this.fh = fh;
        this.fv = fv;
        fixForce();
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
     * Update the last direction.
     * 
     * @param direction The direction to reach.
     */
    private void updateLastForce(Direction direction)
    {
        if (Double.compare(lastFh, direction.getDirectionHorizontal()) != 0)
        {
            lastFh = direction.getDirectionHorizontal();
            arrivedH = false;
        }
        if (Double.compare(lastFv, direction.getDirectionVertical()) != 0)
        {
            lastFv = direction.getDirectionVertical();
            arrivedV = false;
        }
    }

    /**
     * Update the force if still not reached on horizontal axis.
     * 
     * @param extrp The extrapolation value.
     * @param direction The direction to reach.
     * @param speed The reach speed.
     * @param sensibility The sensibility value.
     */
    private void updateNotArrivedH(double extrp, Direction direction, double speed, double sensibility)
    {
        if (fh < direction.getDirectionHorizontal())
        {
            fh += speed * extrp;
            if (fh > direction.getDirectionHorizontal() - sensibility)
            {
                fh = direction.getDirectionHorizontal();
                arrivedH = true;
            }
        }
        else if (fh > direction.getDirectionHorizontal())
        {
            fh -= speed * extrp;
            if (fh < direction.getDirectionHorizontal() + sensibility)
            {
                fh = direction.getDirectionHorizontal();
                arrivedH = true;
            }
        }
    }

    /**
     * Update the force if still not reached on vertical axis.
     * 
     * @param extrp The extrapolation value.
     * @param direction The direction to reach.
     * @param speed The reach speed.
     * @param sensibility The sensibility value.
     */
    private void updateNotArrivedV(double extrp, Direction direction, double speed, double sensibility)
    {
        if (fv < direction.getDirectionVertical())
        {
            fv += speed * extrp;
            if (fv > direction.getDirectionVertical() - sensibility)
            {
                fv = direction.getDirectionVertical();
                arrivedV = true;
            }
        }
        else if (fv > direction.getDirectionVertical())
        {
            fv -= speed * extrp;
            if (fv < direction.getDirectionVertical() + sensibility)
            {
                fv = direction.getDirectionVertical();
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
