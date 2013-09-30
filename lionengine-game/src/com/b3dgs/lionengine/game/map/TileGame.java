/*
 * Copyright (C) 2013 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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

import java.util.List;

/**
 * Default class tile; containing following data:
 * <ul>
 * <li><code>pattern</code> : tilesheet number</li>
 * <li><code>number</code> : tile number inside current tilesheet</li>
 * <li><code>x & y</code> : real location</li>
 * <li><code>collision</code> : collision name</li>
 * </ul>
 * <p>
 * </p>
 * 
 * @param <C> The collision type used.
 */
public abstract class TileGame<C extends Enum<C>>
{
    /** Tile width. */
    private final int width;
    /** Tile height. */
    private final int height;
    /** Tilesheet number where this tile is contained. */
    private Integer pattern;
    /** Location number in the tilesheet. */
    private int number;
    /** Tile x on map. */
    private int x;
    /** Tile y on map. */
    private int y;
    /** Tile collision name. */
    private C collision;

    /**
     * Create a new blank tile.
     * 
     * @param width The tile width.
     * @param height The tile height.
     * @param pattern The tile pattern.
     * @param number The tile number.
     * @param collision The tile collision.
     */
    public TileGame(int width, int height, Integer pattern, int number, C collision)
    {
        this.width = width;
        this.height = height;
        this.pattern = pattern;
        this.number = number;
        this.collision = collision;
        x = 0;
        y = 0;
    }

    /**
     * Check if tile fill condition.
     * 
     * @param collisions The collisions list.
     * @return <code>true</code> if collision is allowed, <code>false</code> else.
     */
    public boolean collisionTest(List<C> collisions)
    {
        if (collisions.isEmpty())
        {
            return true;
        }
        for (final C collision : collisions)
        {
            if (collision == getCollision())
            {
                return true;
            }
        }
        return false;
    }

    /**
     * Get the left position of the tile.
     * 
     * @return The left position of the tile.
     */
    public int getLeft()
    {
        return getX();
    }

    /**
     * Get the right position of the tile.
     * 
     * @return The right position of the tile.
     */
    public int getRight()
    {
        return getX() + getWidth() - 1;
    }

    /**
     * Get the top position of the tile.
     * 
     * @return The top position of the tile.
     */
    public int getTop()
    {
        return getY() + getHeight() - 1;
    }

    /**
     * Get the bottom position of the tile.
     * 
     * @return The bottom position of the tile.
     */
    public int getBottom()
    {
        return getY();
    }

    /**
     * Set pattern number.
     * 
     * @param pattern The pattern number.
     */
    public void setPattern(Integer pattern)
    {
        this.pattern = pattern;
    }

    /**
     * Set tile index inside pattern.
     * 
     * @param number The tile index.
     */
    public void setNumber(int number)
    {
        this.number = number;
    }

    /**
     * Set collision name.
     * 
     * @param collision The collision name.
     */
    public void setCollision(C collision)
    {
        this.collision = collision;
    }

    /**
     * Get the width.
     * 
     * @return The tile width.
     */
    public int getWidth()
    {
        return width;
    }

    /**
     * Get the height.
     * 
     * @return The tile height.
     */
    public int getHeight()
    {
        return height;
    }

    /**
     * Get pattern number.
     * 
     * @return The pattern number.
     */
    public Integer getPattern()
    {
        return pattern;
    }

    /**
     * Get tile index number.
     * 
     * @return The tile index number.
     */
    public int getNumber()
    {
        return number;
    }

    /**
     * Get tile location x.
     * 
     * @return The tile location x.
     */
    public int getX()
    {
        return x;
    }

    /**
     * Get tile location y.
     * 
     * @return The tile location y.
     */
    public int getY()
    {
        return y;
    }

    /**
     * Get tile collision name.
     * 
     * @return The tile collision name.
     */
    public C getCollision()
    {
        return collision;
    }

    /**
     * Set tile location x. Should be used only when overriding the
     * {@link MapTileGame#loadTile(List, com.b3dgs.lionengine.file.FileReading, int)} function.
     * 
     * @param x The tile location x.
     */
    public void setX(int x)
    {
        this.x = x;
    }

    /**
     * Set tile location y. Should be used only when overriding the
     * {@link MapTileGame#loadTile(List, com.b3dgs.lionengine.file.FileReading, int)} function.
     * 
     * @param y The tile location y.
     */
    public void setY(int y)
    {
        this.y = y;
    }
}
