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
package com.b3dgs.lionengine.core.drawable;

import com.b3dgs.lionengine.Check;
import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.graphic.Graphic;
import com.b3dgs.lionengine.graphic.ImageBuffer;
import com.b3dgs.lionengine.graphic.SpriteTiled;

/**
 * Tiled sprite implementation.
 */
final class SpriteTiledImpl extends SpriteImpl implements SpriteTiled
{
    /** Media reference (<code>null</code> if none). */
    private final Media media;
    /** Number of horizontal tiles. */
    private final int horizontalTiles;
    /** Number of vertical tiles. */
    private final int verticalTiles;
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
        horizontalTiles = getWidth() / tileWidth;
        verticalTiles = getHeight() / tileHeight;
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
        horizontalTiles = getWidth() / tileWidth;
        verticalTiles = getHeight() / tileHeight;
    }

    /*
     * SpriteTiled
     */

    @Override
    public void render(Graphic g)
    {
        final int ox = tile % horizontalTiles;
        final int oy = (int) Math.floor(tile / (double) horizontalTiles);
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
        return horizontalTiles;
    }

    @Override
    public int getTilesVertical()
    {
        return verticalTiles;
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
        super.computeRenderingPoint(width / horizontalTiles, height / verticalTiles);
    }

    /*
     * Object
     */

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
        final SpriteTiled sprite = (SpriteTiled) object;

        final boolean sameSurface = sprite.getSurface() == getSurface();
        final boolean sameSize = sprite.getWidth() == getWidth() && sprite.getHeight() == getHeight();
        final boolean sameTiles = horizontalTiles == sprite.getTilesHorizontal()
                                  && verticalTiles == sprite.getTilesVertical();
        return sameSize && sameSurface && sameTiles;
    }

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        if (getSurface() != null)
        {
            result = prime * result + getSurface().hashCode();
        }
        if (media != null)
        {
            result = prime * result + media.hashCode();
        }
        result = prime * result + getWidth();
        result = prime * result + getHeight();
        result = prime * result + horizontalTiles;
        result = prime * result + verticalTiles;
        return result;
    }
}
