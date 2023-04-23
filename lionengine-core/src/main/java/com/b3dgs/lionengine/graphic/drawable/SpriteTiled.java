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
package com.b3dgs.lionengine.graphic.drawable;

import com.b3dgs.lionengine.SurfaceTile;

/**
 * Tiled sprite are mainly used for tile based levels. It works by loading an image and render only a part of it
 * (virtually splited). The first tile is 0.
 */
public interface SpriteTiled extends Sprite, SurfaceTile
{
    /**
     * Set the active tile.
     * 
     * @param tile The tile to render (superior or equal to 0 and inferior to {@link #getTilesHorizontal()} *
     *            {@link #getTilesVertical()}).
     */
    void setTile(int tile);

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
}
