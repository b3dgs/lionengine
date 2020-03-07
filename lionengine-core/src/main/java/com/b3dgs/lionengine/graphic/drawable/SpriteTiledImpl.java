/*
 * Copyright (C) 2013-2020 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.graphic.drawable;

import com.b3dgs.lionengine.Check;
import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.graphic.Graphic;
import com.b3dgs.lionengine.graphic.ImageBuffer;

/**
 * Tiled sprite implementation.
 */
final class SpriteTiledImpl extends SpriteImpl implements SpriteTiled
{
    /** Media reference (<code>null</code> created with existing surface). */
    private final Media media;
    /** Number of horizontal tiles. */
    private final int tilesHorizontal;
    /** Number of vertical tiles. */
    private final int tilesVertical;
    /** Current tile. */
    private int tile;

    /**
     * Internal constructor.
     * 
     * @param media The sprite media (must not be <code>null</code>).
     * @param tileWidth The tile width (must be strictly positive).
     * @param tileHeight The tile height (must be strictly positive).
     * @throws LionEngineException If arguments are invalid or image cannot be read.
     */
    SpriteTiledImpl(Media media, int tileWidth, int tileHeight)
    {
        super(media);

        Check.superiorStrict(tileWidth, 0);
        Check.superiorStrict(tileHeight, 0);

        this.media = media;
        tilesHorizontal = getWidth() / tileWidth;
        tilesVertical = getHeight() / tileHeight;
    }

    /**
     * Internal constructor.
     * 
     * @param surface The surface reference (must not be <code>null</code>).
     * @param tileWidth The tile width (must be strictly positive).
     * @param tileHeight The tile height (must be strictly positive).
     * @throws LionEngineException If arguments are invalid.
     */
    SpriteTiledImpl(ImageBuffer surface, int tileWidth, int tileHeight)
    {
        super(surface);

        Check.superiorStrict(tileWidth, 0);
        Check.superiorStrict(tileHeight, 0);

        media = null;
        tilesHorizontal = getWidth() / tileWidth;
        tilesVertical = getHeight() / tileHeight;
    }

    /*
     * SpriteTiled
     */

    @Override
    public void render(Graphic g)
    {
        final int ox = tile % tilesHorizontal;
        final int oy = (int) Math.floor(tile / (double) tilesHorizontal);
        render(g, getRenderX(), getRenderY(), getTileWidth(), getTileHeight(), ox, oy);
    }

    @Override
    public void setTile(int tile)
    {
        Check.superiorOrEqual(tile, 0);

        this.tile = tile;
    }

    @Override
    public int getTileWidth()
    {
        return getWidth() / getTilesHorizontal();
    }

    @Override
    public int getTileHeight()
    {
        return getHeight() / getTilesVertical();
    }

    @Override
    public int getTilesHorizontal()
    {
        return tilesHorizontal;
    }

    @Override
    public int getTilesVertical()
    {
        return tilesVertical;
    }

    @Override
    protected void stretch(int newWidth, int newHeight)
    {
        final int w = Math.round(newWidth / (float) getTilesHorizontal()) * getTilesHorizontal();
        final int h = Math.round(newHeight / (float) getTilesVertical()) * getTilesVertical();
        super.stretch(w, h);
    }

    @Override
    protected void computeRenderingPoint(int width, int height)
    {
        super.computeRenderingPoint(width / tilesHorizontal, height / tilesVertical);
    }

    /*
     * Object
     */

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        if (getSurface() != null)
        {
            result = prime * result + getSurface().hashCode();
        }
        else
        {
            result = prime * result + media.hashCode();
        }
        result = prime * result + tilesHorizontal;
        result = prime * result + tilesVertical;
        return result;
    }

    @Override
    public boolean equals(Object object)
    {
        if (object == this)
        {
            return true;
        }
        if (object == null || object.getClass() != getClass())
        {
            return false;
        }
        final SpriteTiledImpl other = (SpriteTiledImpl) object;
        return getSurface() == other.getSurface()
               && tilesHorizontal == other.tilesHorizontal
               && tilesVertical == other.tilesVertical;
    }
}
