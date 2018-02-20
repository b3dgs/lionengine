/*
 * Copyright (C) 2013-2017 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.graphic;

/**
 * No transform implementation.
 */
public final class TransformNone implements Transform
{
    private final double scaleX;
    private final double scaleY;

    /**
     * Create transform.
     * 
     * @param scaleX The horizontal scaling.
     * @param scaleY The vertical scaling.
     */
    public TransformNone(double scaleX, double scaleY)
    {
        super();

        this.scaleX = scaleX;
        this.scaleY = scaleY;
    }

    /*
     * Transform
     */

    @Override
    public void setInterpolation(boolean bilinear)
    {
        // Nothing to do
    }

    @Override
    public void scale(double sx, double sy)
    {
        // Nothing to do
    }

    @Override
    public double getScaleY()
    {
        return scaleX;
    }

    @Override
    public double getScaleX()
    {
        return scaleY;
    }

    @Override
    public int getInterpolation()
    {
        return 0;
    }
}
