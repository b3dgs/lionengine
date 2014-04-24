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
package com.b3dgs.lionengine.drawable;

import com.b3dgs.lionengine.Check;
import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.Transparency;
import com.b3dgs.lionengine.core.Core;
import com.b3dgs.lionengine.core.Graphic;
import com.b3dgs.lionengine.core.ImageBuffer;

/**
 * Tiled sprite implementation.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
final class SpriteTiledImpl
        extends SpriteImpl
        implements SpriteTiled
{
    /** Sprite size error. */
    private static final String ERROR_SPRITE_TILE_SIZE = "Sprite tile size must be strictly positive !";

    /** Number of horizontal tiles. */
    private final int horizontalTiles;
    /** Number of vertical tiles. */
    private final int verticalTiles;
    /** Original tile width. */
    private final int tileOriginalWidth;
    /** Original tile height. */
    private final int tileOriginalHeight;
    /** Total number of tiles. */
    private final int tilesNumber;

    /**
     * Constructor.
     * 
     * @param media The sprite media.
     * @param surface The surface reference.
     * @param tileWidth The tile width.
     * @param tileHeight The tile height.
     */
    SpriteTiledImpl(Media media, ImageBuffer surface, int tileWidth, int tileHeight)
    {
        super(media, surface);
        Check.argument(tileWidth > 0 && tileHeight > 0, SpriteTiledImpl.ERROR_SPRITE_TILE_SIZE);
        tileOriginalWidth = tileWidth;
        tileOriginalHeight = tileHeight;
        horizontalTiles = widthOriginal / tileWidth;
        verticalTiles = heightOriginal / tileHeight;
        tilesNumber = horizontalTiles * verticalTiles;
    }

    /*
     * SpriteTiled
     */

    @Override
    public void render(Graphic g, int tile, int x, int y)
    {
        final int cx = tile % horizontalTiles;
        final int cy = (int) Math.floor(tile / (double) horizontalTiles);
        final int w = getTileWidth();
        final int h = getTileHeight();

        g.drawImage(surface, x, y, x + w, y + h, cx * w, cy * h, cx * w + w, cy * h + h);
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
    public int getTileWidthOriginal()
    {
        return tileOriginalWidth;
    }

    @Override
    public int getTileHeightOriginal()
    {
        return tileOriginalHeight;
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
    public int getTilesNumber()
    {
        return tilesNumber;
    }

    @Override
    public ImageBuffer getTile(int tile)
    {
        final ImageBuffer buffer = Core.GRAPHIC
                .createImageBuffer(getTileWidth(), getTileHeight(), Transparency.BITMASK);
        final Graphic g = buffer.createGraphic();
        final int cx = tile % getTilesHorizontal();
        final int cy = (int) Math.floor(tile / (double) getTilesHorizontal());
        final int w = getTileWidth();
        final int h = getTileHeight();

        g.drawImage(surface, 0, 0, w, h, cx * w, cy * h, cx * w + w, cy * h + h);
        g.dispose();

        return buffer;
    }

    @Override
    protected void stretchSurface(int newWidth, int newHeight)
    {
        final int w = Math.round(newWidth / (float) getTilesHorizontal()) * getTilesHorizontal();
        final int h = Math.round(newHeight / (float) getTilesVertical()) * getTilesVertical();
        super.stretchSurface(w, h);
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
        if (object instanceof SpriteTiled)
        {
            final SpriteTiled sprite = (SpriteTiled) object;

            final boolean sameSurface = sprite.getSurface() == getSurface();
            final boolean sameTileWidth = sprite.getTileWidth() == getTileWidth();
            final boolean sameTileHeight = sprite.getTileHeight() == getTileHeight();
            final boolean sameHorizontalTiles = sprite.getTilesHorizontal() == getTilesHorizontal();
            final boolean sameVerticalTiles = sprite.getTilesVertical() == getTilesVertical();
            final boolean sameTilesNumber = sprite.getTilesNumber() == getTilesNumber();
            return sameTileWidth && sameTileHeight && sameHorizontalTiles && sameVerticalTiles && sameTilesNumber
                    && sameSurface;
        }
        return false;
    }

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + horizontalTiles;
        result = prime * result + tileOriginalHeight;
        result = prime * result + tileOriginalWidth;
        result = prime * result + tilesNumber;
        result = prime * result + verticalTiles;
        return result;
    }
}
