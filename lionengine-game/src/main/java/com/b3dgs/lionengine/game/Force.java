/*
 * Copyright (C) 2013-2023 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.game;

import com.b3dgs.lionengine.Updatable;
import com.b3dgs.lionengine.UtilMath;

/**
 * Represents a 2D vector force, using double precision. This can be used to describe a vectorial force, on 2 axis
 * (horizontal and vertical). Can be used as a speed.
 */
public class Force implements Direction, Updatable
{
    /** Min to string length. */
    private static final int MIN_LENGTH = 45;

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
        final double sx;
        final double sy;
        if (Double.compare(norm, 0.0) == 0)
        {
            sx = 0;
            sy = 0;
        }
        else
        {
            sx = dh / norm;
            sy = dv / norm;
        }

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
        super();

        this.fh = fh;
        this.fv = fv;

        fixForce();
    }

    /**
     * Create a force with specified values.
     * 
     * @param fh The horizontal force.
     * @param fv The vertical force.
     * @param velocity The velocity.
     * @param sensibility The sensibility.
     */
    public Force(double fh, double fv, double velocity, double sensibility)
    {
        super();

        this.fh = fh;
        this.fv = fv;
        this.velocity = velocity;
        this.sensibility = sensibility;

        fixForce();
    }

    /**
     * Create a copy force.
     * 
     * @param force The force reference.
     */
    public Force(Force force)
    {
        this(force.getDirectionHorizontal(), force.getDirectionVertical(), force.getVelocity(), force.getSensibility());
    }

    /**
     * Set force destination and direction to zero.
     */
    public void zero()
    {
        zeroHorizontal();
        zeroVertical();
    }

    /**
     * Set horizontal force destination and direction to zero.
     */
    public void zeroHorizontal()
    {
        fhOld = 0.0;
        fhDest = 0.0;
        fh = 0.0;
    }

    /**
     * Set vertical force destination and direction to zero.
     */
    public void zeroVertical()
    {
        fvOld = 0.0;
        fvDest = 0.0;
        fv = 0.0;
    }

    /**
     * Check if force is zero (will never move).
     * 
     * @return <code>true</code> if zero, <code>false</code> else.
     */
    public boolean isZero()
    {
        return Double.compare(fhDest, 0.0) == 0
               && Double.compare(fvDest, 0.0) == 0
               && Double.compare(fh, 0.0) == 0
               && Double.compare(fv, 0.0) == 0;
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
     * @param extrp The extrapolation value.
     * @param direction The direction to add.
     */
    public void addDirection(double extrp, Direction direction)
    {
        addDirection(extrp, direction.getDirectionHorizontal(), direction.getDirectionVertical());
    }

    /**
     * Increase forces with input value.
     * 
     * @param extrp The extrapolation value.
     * @param fh The added horizontal force.
     * @param fv The added vertical force.
     */
    public void addDirection(double extrp, double fh, double fv)
    {
        this.fh += fh * extrp;
        this.fv += fv * extrp;
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
     * Get the current sensibility.
     * 
     * @return The current sensibility.
     */
    public double getSensibility()
    {
        return sensibility;
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
     * Update the force if still not reached on horizontal axis.
     * 
     * @param extrp The extrapolation value.
     */
    private void updateH(double extrp)
    {
        if (fh < fhDest)
        {
            fh += velocity * extrp;
            if (fh > fhDest - sensibility)
            {
                fh = fhDest;
            }
        }
        else if (fh > fhDest)
        {
            fh -= velocity * extrp;
            if (fh < fhDest + sensibility)
            {
                fh = fhDest;
            }
        }
    }

    /**
     * Update the force if still not reached on vertical axis.
     * 
     * @param extrp The extrapolation value.
     */
    private void updateV(double extrp)
    {
        if (fv < fvDest)
        {
            fv += velocity * extrp;
            if (fv > fvDest - sensibility)
            {
                fv = fvDest;
            }
        }
        else if (fv > fvDest)
        {
            fv -= velocity * extrp;
            if (fv < fvDest + sensibility)
            {
                fv = fvDest;
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
        fh = UtilMath.clamp(fh, minH, maxH) + 0.0;
        fv = UtilMath.clamp(fv, minV, maxV) + 0.0;
    }

    /*
     * Updatable
     */

    @Override
    public void update(double extrp)
    {
        fhOld = fh;
        fvOld = fv;

        updateH(extrp);
        updateV(extrp);

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

    /*
     * Object
     */

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        long temp;
        temp = Double.doubleToLongBits(fh);
        result = prime * result + (int) (temp ^ temp >>> 32);
        temp = Double.doubleToLongBits(fv);
        result = prime * result + (int) (temp ^ temp >>> 32);
        temp = Double.doubleToLongBits(sensibility);
        result = prime * result + (int) (temp ^ temp >>> 32);
        temp = Double.doubleToLongBits(velocity);
        result = prime * result + (int) (temp ^ temp >>> 32);
        return result;
    }

    @Override
    public boolean equals(Object object)
    {
        if (this == object)
        {
            return true;
        }
        if (object == null || object.getClass() != getClass())
        {
            return false;
        }
        final Force other = (Force) object;
        return Double.doubleToLongBits(fh) == Double.doubleToLongBits(other.fh)
               && Double.doubleToLongBits(fv) == Double.doubleToLongBits(other.fv)
               && Double.doubleToLongBits(sensibility) == Double.doubleToLongBits(other.sensibility)
               && Double.doubleToLongBits(velocity) == Double.doubleToLongBits(other.velocity);
    }

    @Override
    public String toString()
    {
        return new StringBuilder(MIN_LENGTH).append(getClass().getSimpleName())
                                            .append(" [fh=")
                                            .append(fh)
                                            .append(", fv=")
                                            .append(fv)
                                            .append(", velocity=")
                                            .append(velocity)
                                            .append(", sensibility=")
                                            .append(sensibility)
                                            .append("]")
                                            .toString();
    }
}
