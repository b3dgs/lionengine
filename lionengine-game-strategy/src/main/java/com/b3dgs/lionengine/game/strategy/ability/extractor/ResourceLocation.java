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
package com.b3dgs.lionengine.game.strategy.ability.extractor;

import com.b3dgs.lionengine.game.Tiled;

/**
 * Represents the resource location.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
final class ResourceLocation
        implements Tiled
{
    /** The horizontal location. */
    private int tx;
    /** The vertical location. */
    private int ty;
    /** The width in tile. */
    private int tw;
    /** The height in tile. */
    private int th;

    /**
     * Internal constructor.
     */
    ResourceLocation()
    {
        tx = 0;
        ty = 0;
        tw = 0;
        th = 0;
    }

    /**
     * Set the location.
     * 
     * @param tx The horizontal location.
     * @param ty The vertical location.
     */
    void setCoordinate(int tx, int ty)
    {
        this.tx = tx;
        this.ty = ty;
    }

    /**
     * Set the size.
     * 
     * @param tw The width in tile.
     * @param th The height in tile.
     */
    void setSize(int tw, int th)
    {
        this.tw = tw;
        this.th = th;
    }

    /*
     * Tiled
     */

    @Override
    public int getLocationInTileX()
    {
        return tx;
    }

    @Override
    public int getLocationInTileY()
    {
        return ty;
    }

    @Override
    public int getWidthInTile()
    {
        return tw;
    }

    @Override
    public int getHeightInTile()
    {
        return th;
    }
}
