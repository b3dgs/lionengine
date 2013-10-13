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

import com.b3dgs.lionengine.core.UtilityMath;

/**
 * Represents a 2D vector force, using double precision. This can be used to describe a vectorial force, on 2 axis
 * (horizontal & vertical). Can be used as a speed.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public final class Force
{
    /** Zero force. */
    public static final Force ZERO = new Force(0.0, 0.0);

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
    /** Maximum force. */
    private Force forceMax;
    /** Minimum force. */
    private Force forceMin;

    /**
     * Constructor.
     */
    public Force()
    {
        this(0.0, 0.0);
    }

    /**
     * Constructor.
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
     * @param force The force to reach.
     * @param speed The reach speed.
     * @param sensibility The sensibility value (will round to destination between -sensibility and +sensibility).
     */
    public void reachForce(double extrp, Force force, double speed, double sensibility)
    {
        // Last force
        if (Double.compare(lastFh, force.fh) != 0)
        {
            lastFh = force.fh;
            arrivedH = false;
        }
        if (Double.compare(lastFv, force.fv) != 0)
        {
            lastFv = force.fv;
            arrivedV = false;
        }
        // Not arrived
        if (!arrivedH)
        {
            if (fh < force.fh)
            {
                fh += speed * extrp;
                if (fh > force.fh - sensibility)
                {
                    fh = force.fh;
                    arrivedH = true;
                }
            }
            else if (fh > force.fh)
            {
                fh -= speed * extrp;
                if (fh < force.fh + sensibility)
                {
                    fh = force.fh;
                    arrivedH = true;
                }
            }
        }
        else
        {
            fh = force.fh;
        }
        if (!arrivedV)
        {
            if (fv < force.fv)
            {
                fv += speed * extrp;
                if (fv > force.fv - sensibility)
                {
                    fv = force.fv;
                    arrivedV = true;
                }
            }
            else if (fv > force.fv)
            {
                fv -= speed * extrp;
                if (fv < force.fv + sensibility)
                {
                    fv = force.fv;
                    arrivedV = true;
                }
            }
        }
        else
        {
            fv = force.fv;
        }
        fixForce();
    }

    /**
     * Increase force with input value.
     * 
     * @param force The force to add.
     */
    public void addForce(Force force)
    {
        addForce(force.fh, force.fv);
    }

    /**
     * Increase forces with input value.
     * 
     * @param fh The added horizontal force.
     * @param fv The added vertical force.
     */
    public void addForce(double fh, double fv)
    {
        lastFh = fh;
        lastFv = fv;
        this.fh += fh;
        this.fv += fv;
        fixForce();
    }

    /**
     * Set force.
     * 
     * @param force The force.
     */
    public void setForce(Force force)
    {
        setForce(force.getForceHorizontal(), force.getForceVertical());
    }

    /**
     * Set forces.
     * 
     * @param fh The horizontal force.
     * @param fv The vertical force.
     */
    public void setForce(double fh, double fv)
    {
        lastFh = fh;
        lastFv = fv;
        this.fh = fh;
        this.fv = fv;
        fixForce();
    }

    /**
     * Set the maximum reachable force.
     * 
     * @param max The force max.
     */
    public void setForceMaximum(Force max)
    {
        forceMax = max;
    }

    /**
     * Set the minimum reachable force.
     * 
     * @param min The force min.
     */
    public void setForceMinimum(Force min)
    {
        forceMin = min;
    }

    /**
     * Get horizontal force.
     * 
     * @return The horizontal force.
     */
    public double getForceHorizontal()
    {
        return fh;
    }

    /**
     * Get vertical force.
     * 
     * @return The vertical force.
     */
    public double getForceVertical()
    {
        return fv;
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

        if (forceMin == null)
        {
            minH = fh;
            minV = fv;
        }
        else
        {
            minH = forceMin.getForceHorizontal();
            minV = forceMin.getForceVertical();
        }
        if (forceMax == null)
        {
            maxH = fh;
            maxV = fv;
        }
        else
        {
            maxH = forceMax.getForceHorizontal();
            maxV = forceMax.getForceVertical();
        }
        fh = UtilityMath.fixBetween(fh, minH, maxH);
        fv = UtilityMath.fixBetween(fv, minV, maxV);
    }
}
