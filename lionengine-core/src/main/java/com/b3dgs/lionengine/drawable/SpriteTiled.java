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

import com.b3dgs.lionengine.Graphic;
import com.b3dgs.lionengine.core.ImageBuffer;

/**
 * Tiled sprite are mainly used for tile based levels. It works by loading an image and render only a part of it
 * (virtually splited). The first tile is 0.
 * <p>
 * Example:
 * </p>
 * 
 * <pre>
 * // Load
 * final SpriteTiled tilesheet = Drawable.loadSpriteTiled(UtilityMedia.get(&quot;tilesheet.png&quot;), 16, 16);
 * tilesheet.load(false);
 * 
 * // Render
 * tilesheet.render(g, 1, 300, 300);
 * tilesheet.render(g, 350, 300);
 * </pre>
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public interface SpriteTiled
        extends Sprite
{
    /**
     * Render a tile to the specified coordinates.
     * 
     * @param g The graphic output.
     * @param tile The tile to render (>= 0).
     * @param x The horizontal location..
     * @param y The vertical location.
     */
    void render(Graphic g, int tile, int x, int y);

    /**
     * Get the number of horizontal tiles.
     * 
     * @return The number of horizontal tiles.
     */
    int getTilesHorizontal();

    /**
     * Get the number of vertical tiles.
     * 
     * @return The number of vertical tiles.
     */
    int getTilesVertical();

    /**
     * Get the number of tiles.
     * 
     * @return The number of tiles.
     */
    int getTilesNumber();

    /**
     * Get current tile width.
     * 
     * @return The tile width.
     */
    int getTileWidth();

    /**
     * Get current tile height.
     * 
     * @return The tile height.
     */
    int getTileHeight();

    /**
     * Get original tile width.
     * 
     * @return The tile width.
     */
    int getTileWidthOriginal();

    /**
     * Get original tile height.
     * 
     * @return The tile height.
     */
    int getTileHeightOriginal();

    /**
     * Get a tile (store it on a new buffered image, no reference, can be slow).
     * 
     * @param tile The desired tile.
     * @return The tile's surface.
     */
    ImageBuffer getTile(int tile);
}
