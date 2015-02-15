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
package com.b3dgs.lionengine.game.map;

import com.b3dgs.lionengine.game.Featurable;

/**
 * Tile representation with the following data:
 * <ul>
 * <li><code>sheet</code> : tile sheet number</li>
 * <li><code>number</code> : tile number inside tilesheet</li>
 * <li><code>x & y</code> : real location on map</li>
 * </ul>
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public interface Tile
        extends Featurable<TileFeature>
{
    /**
     * Set sheet number.
     * 
     * @param sheet The sheet number (must not be <code>null</code>).
     */
    void setSheet(Integer sheet);

    /**
     * Set tile index inside sheet.
     * 
     * @param number The tile index.
     */
    void setNumber(int number);

    /**
     * Set tile location x.
     * 
     * @param x The tile location x.
     */
    void setX(int x);

    /**
     * Set tile location y.
     * 
     * @param y The tile location y.
     */
    void setY(int y);

    /**
     * Get the left position of the tile.
     * 
     * @return The left position of the tile.
     */
    int getLeft();

    /**
     * Get the right position of the tile.
     * 
     * @return The right position of the tile.
     */
    int getRight();

    /**
     * Get the top position of the tile.
     * 
     * @return The top position of the tile.
     */
    int getTop();

    /**
     * Get the bottom position of the tile.
     * 
     * @return The bottom position of the tile.
     */
    int getBottom();

    /**
     * Get the width.
     * 
     * @return The tile width.
     */
    int getWidth();

    /**
     * Get the height.
     * 
     * @return The tile height.
     */
    int getHeight();

    /**
     * Get sheet number.
     * 
     * @return The sheet number.
     */
    Integer getSheet();

    /**
     * Get tile index number.
     * 
     * @return The tile index number.
     */
    int getNumber();

    /**
     * Get tile location x.
     * 
     * @return The tile location x.
     */
    int getX();

    /**
     * Get tile location y.
     * 
     * @return The tile location y.
     */
    int getY();
}
