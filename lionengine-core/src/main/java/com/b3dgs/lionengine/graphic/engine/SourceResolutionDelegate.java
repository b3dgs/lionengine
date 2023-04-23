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
package com.b3dgs.lionengine.graphic.engine;

import java.util.function.IntSupplier;

import com.b3dgs.lionengine.Resolution;

/**
 * Provide resolution from source.
 */
public final class SourceResolutionDelegate implements SourceResolutionProvider
{
    private final IntSupplier width;
    private final IntSupplier height;
    private final IntSupplier rate;

    /**
     * Create delegate.
     * 
     * @param resolution The resolution reference.
     */
    public SourceResolutionDelegate(Resolution resolution)
    {
        this(resolution::getWidth, resolution::getHeight, resolution::getRate);
    }

    /**
     * Create delegate.
     * 
     * @param width The width provider.
     * @param height The height provider.
     * @param rate The rate provider.
     */
    public SourceResolutionDelegate(IntSupplier width, IntSupplier height, IntSupplier rate)
    {
        this.width = width;
        this.height = height;
        this.rate = rate;
    }

    @Override
    public int getWidth()
    {
        return width.getAsInt();
    }

    @Override
    public int getHeight()
    {
        return height.getAsInt();
    }

    @Override
    public int getRate()
    {
        return rate.getAsInt();
    }
}
