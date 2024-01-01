/*
 * Copyright (C) 2013-2024 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.graphic.engine;

import com.b3dgs.lionengine.graphic.Transform;

/**
 * No transform implementation.
 * <p>
 * This class is Thread-Safe.
 * </p>
 * 
 * @param scaleX The horizontal scaling.
 * @param scaleY The vertical scaling.
 */
public record TransformNone(double scaleX, double scaleY) implements Transform
{
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
    public double getScaleX()
    {
        return scaleX;
    }

    @Override
    public double getScaleY()
    {
        return scaleY;
    }

    @Override
    public int getInterpolation()
    {
        return 0;
    }
}
