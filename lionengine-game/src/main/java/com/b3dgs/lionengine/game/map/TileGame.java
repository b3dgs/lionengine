/*
 * Copyright (C) 2013-2014 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.game.map;

import com.b3dgs.lionengine.Check;
import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.game.Features;

/**
 * Tile base implementation.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public class TileGame
        implements Tile
{
    /** Error feature missing message. */
    private static final String ERROR_FEATURE_MISSING = "Feature missing: ";

    /** Tile width. */
    private final int width;
    /** Tile height. */
    private final int height;
    /** Features list. */
    private final Features<TileFeature> features;
    /** Tile sheet number where tile is contained. */
    private Integer sheet;
    /** Position number in the tilesheet. */
    private int number;
    /** Horizontal location on map. */
    private int x;
    /** Vertical location on map. */
    private int y;

    /**
     * Create a tile.
     * 
     * @param width The tile width (must be strictly positive).
     * @param height The tile height (must be strictly positive).
     */
    public TileGame(int width, int height)
    {
        Check.superiorStrict(width, 0);
        Check.superiorStrict(height, 0);

        this.width = width;
        this.height = height;
        features = new Features<>(TileFeature.class);
        sheet = Integer.valueOf(0);
        x = 0;
        y = 0;
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
    public void setSheet(Integer sheet)
    {
        Check.notNull(sheet);
        this.sheet = sheet;
    }

    @Override
    public void setNumber(int number)
    {
        Check.superiorOrEqual(number, 0);
        this.number = number;
    }

    @Override
    public void setX(int x)
    {
        this.x = x;
    }

    @Override
    public void setY(int y)
    {
        this.y = y;
    }

    @Override
    public int getLeft()
    {
        return getX();
    }

    @Override
    public int getRight()
    {
        return getX() + getWidth() - 1;
    }

    @Override
    public int getTop()
    {
        return getY() + getHeight() - 1;
    }

    @Override
    public int getBottom()
    {
        return getY();
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
    public int getX()
    {
        return x;
    }

    @Override
    public int getY()
    {
        return y;
    }

    @Override
    public <C extends TileFeature> C getFeature(Class<C> feature) throws LionEngineException
    {
        final C object = features.get(feature);
        if (object == null)
        {
            throw new LionEngineException(ERROR_FEATURE_MISSING, feature.getName());
        }
        return object;
    }

    @Override
    public Iterable<?> getFeatures()
    {
        return features.getAll();
    }
}
