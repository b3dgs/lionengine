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
package com.b3dgs.lionengine.core;

/**
 * Represents an identity transform.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public interface Transform
{
    /**
     * Scale the transform.
     * 
     * @param sx The horizontal scaling.
     * @param sy The vertical scaling.
     */
    void scale(double sx, double sy);

    /**
     * Set the interpolation usage.
     * 
     * @param bilinear <code>true</code> for bilinear, <code>false</code> to nearest neighbor.
     */
    void setInterpolation(boolean bilinear);

    /**
     * Get the horizontal scaling.
     * 
     * @return The horizontal scaling.
     */
    double getScaleX();

    /**
     * Get the vertical scaling.
     * 
     * @return The vertical scaling.
     */
    double getScaleY();

    /**
     * Get the interpolation.
     * 
     * @return The interpolation.
     */
    int getInterpolation();
}
