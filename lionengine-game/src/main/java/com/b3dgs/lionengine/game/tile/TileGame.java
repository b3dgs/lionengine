/*
 * Copyright (C) 2013-2016 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.game.tile;

import com.b3dgs.lionengine.Check;
import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.game.Features;

/**
 * Tile base implementation.
 */
public class TileGame implements Tile
{
    /** Features list. */
    private final Features<TileFeature> features = new Features<TileFeature>(TileFeature.class);
    /** Tile sheet number where tile is contained. */
    private final Integer sheet;
    /** Position number in the tilesheet. */
    private final int number;
    /** Horizontal location on map. */
    private final double x;
    /** Vertical location on map. */
    private final double y;
    /** Tile width. */
    private final int width;
    /** Tile height. */
    private final int height;
    /** In tile x. */
    private final int inTileX;
    /** In tile y. */
    private final int inTileY;

    /**
     * Create a tile.
     * 
     * @param sheet The sheet number (must be positive or equal to 0).
     * @param number The tile number on sheet (must be positive or equal to 0).
     * @param x The horizontal location.
     * @param y The vertical location.
     * @param width The tile width (must be strictly positive).
     * @param height The tile height (must be strictly positive).
     * @throws LionEngineException If invalid arguments.
     */
    public TileGame(Integer sheet, int number, double x, double y, int width, int height)
    {
        Check.notNull(sheet);
        Check.superiorOrEqual(sheet.intValue(), 0);
        Check.superiorOrEqual(number, 0);
        Check.superiorStrict(width, 0);
        Check.superiorStrict(height, 0);

        this.sheet = sheet;
        this.number = number;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;

        inTileX = (int) Math.floor(x / width);
        inTileY = (int) Math.floor(y / height);
    }

    /*
     * Tile
     */

    @Override
    public void addFeature(TileFeature feature)
    {
        features.add(feature);
    }

    @Override
    public Integer getSheet()
    {
        return sheet;
    }

    @Override
    public int getNumber()
    {
        return number;
    }

    @Override
    public double getX()
    {
        return x;
    }

    @Override
    public double getY()
    {
        return y;
    }

    @Override
    public int getWidth()
    {
        return width;
    }

    @Override
    public int getHeight()
    {
        return height;
    }

    @Override
    public int getInTileX()
    {
        return inTileX;
    }

    @Override
    public int getInTileY()
    {
        return inTileY;
    }

    @Override
    public int getInTileWidth()
    {
        return 1;
    }

    @Override
    public int getInTileHeight()
    {
        return 1;
    }

    @Override
    public <C extends TileFeature> C getFeature(Class<C> feature)
    {
        return features.get(feature);
    }

    @Override
    public Iterable<? extends TileFeature> getFeatures()
    {
        return features.getAll();
    }

    @Override
    public <C extends TileFeature> boolean hasFeature(Class<C> feature)
    {
        return features.contains(feature);
    }

    /*
     * Object
     */

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + height;
        result = prime * result + inTileX;
        result = prime * result + inTileY;
        result = prime * result + number;
        result = prime * result + sheet.hashCode();
        result = prime * result + width;
        long temp;
        temp = Double.doubleToLongBits(x);
        result = prime * result + (int) (temp ^ temp >>> 32);
        temp = Double.doubleToLongBits(y);
        result = prime * result + (int) (temp ^ temp >>> 32);
        return result;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (this == obj)
        {
            return true;
        }
        if (obj == null || !(obj instanceof TileGame))
        {
            return false;
        }
        final TileGame other = (TileGame) obj;
        if (!sheet.equals(other.sheet)
            || number != other.number
            || width != other.width
            || height != other.height
            || inTileX != other.inTileX
            || inTileY != other.inTileY)
        {
            return false;
        }
        return true;
    }
}
