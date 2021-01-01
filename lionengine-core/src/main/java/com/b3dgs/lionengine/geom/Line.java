/*
 * Copyright (C) 2013-2021 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.geom;

/**
 * Line interface.
 */
public final class Line
{
    /** Min to string size. */
    private static final int MIN_LENGHT = 38;

    /** The X coordinate of the start point of the line segment. */
    private double x1;
    /** The Y coordinate of the start point of the line segment. */
    private double y1;
    /** The X coordinate of the end point of the line segment. */
    private double x2;
    /** The Y coordinate of the end point of the line segment. */
    private double y2;

    /**
     * Create a blank line.
     */
    public Line()
    {
        this(0.0, 0.0, 0.0, 0.0);
    }

    /**
     * Create a line.
     * 
     * @param x1 The x coordinate of the start point.
     * @param y1 The y coordinate of the start point.
     * @param x2 The x coordinate of the end point.
     * @param y2 The y coordinate of the end point.
     */
    public Line(double x1, double y1, double x2, double y2)
    {
        super();

        this.x1 = x1;
        this.y1 = y1;
        this.x2 = x2;
        this.y2 = y2;
    }

    /**
     * Set the line coordinates.
     * 
     * @param x1 The x coordinate of the start point
     * @param y1 The y coordinate of the start point
     * @param x2 The x coordinate of the end point
     * @param y2 The y coordinate of the end point
     */
    public void set(double x1, double y1, double x2, double y2)
    {
        this.x1 = x1;
        this.y1 = y1;
        this.x2 = x2;
        this.y2 = y2;
    }

    /**
     * Get the x1 location.
     * 
     * @return The x1 location.
     */
    public double getX1()
    {
        return x1;
    }

    /**
     * Get the x2 location.
     * 
     * @return The x2 location.
     */
    public double getX2()
    {
        return x2;
    }

    /**
     * Get the y1 location.
     * 
     * @return The y1 location.
     */
    public double getY1()
    {
        return y1;
    }

    /**
     * Get the y2 location.
     * 
     * @return The y2 location.
     */
    public double getY2()
    {
        return y2;
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
        temp = Double.doubleToLongBits(x1);
        result = prime * result + (int) (temp ^ temp >>> 32);
        temp = Double.doubleToLongBits(y1);
        result = prime * result + (int) (temp ^ temp >>> 32);
        temp = Double.doubleToLongBits(x2);
        result = prime * result + (int) (temp ^ temp >>> 32);
        temp = Double.doubleToLongBits(y2);
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
        final Line other = (Line) object;
        return Double.compare(x1, other.x1) == 0
               && Double.compare(y1, other.y1) == 0
               && Double.compare(x2, other.x2) == 0
               && Double.compare(y2, other.y2) == 0;
    }

    @Override
    public String toString()
    {
        return new StringBuilder(MIN_LENGHT).append(getClass().getSimpleName())
                                            .append(" [x1=")
                                            .append(x1)
                                            .append(", y1=")
                                            .append(y1)
                                            .append(", x2=")
                                            .append(x2)
                                            .append(", y2=")
                                            .append(y2)
                                            .append("]")
                                            .toString();
    }
}
