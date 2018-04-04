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
package com.b3dgs.lionengine.awt.graphic;

import java.awt.image.AffineTransformOp;

import com.b3dgs.lionengine.graphic.Transform;

/**
 * Transform implementation.
 */
final class TransformAwt implements Transform
{
    /** Scale x. */
    private double sx = 1.0;
    /** Scale y. */
    private double sy = 1.0;
    /** Interpolation. */
    private int interpolation = AffineTransformOp.TYPE_NEAREST_NEIGHBOR;

    /**
     * Internal constructor.
     */
    TransformAwt()
    {
        super();
    }

    /*
     * Transform
     */

    @Override
    public void scale(double sx, double sy)
    {
        this.sx = sx;
        this.sy = sy;
    }

    @Override
    public void setInterpolation(boolean bilinear)
    {
        if (bilinear)
        {
            interpolation = AffineTransformOp.TYPE_BILINEAR;
        }
        else
        {
            interpolation = AffineTransformOp.TYPE_NEAREST_NEIGHBOR;
        }
    }

    @Override
    public double getScaleX()
    {
        return sx;
    }

    @Override
    public double getScaleY()
    {
        return sy;
    }

    @Override
    public int getInterpolation()
    {
        return interpolation;
    }
}
