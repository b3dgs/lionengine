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

import java.util.Collection;

import com.b3dgs.lionengine.Check;
import com.b3dgs.lionengine.stream.FileReading;

/**
 * Default class tile; containing following data:
 * <ul>
 * <li><code>pattern</code> : tilesheet number</li>
 * <li><code>number</code> : tile number inside current tilesheet</li>
 * <li><code>x & y</code> : real location</li>
 * <li><code>collision</code> : collision type</li>
 * </ul>
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public class TileGame
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
    /** Tile collision. */
    private CollisionTile collision;

    /**
     * Create a tile.
     * 
     * @param width The tile width (must be strictly positive).
     * @param height The tile height (must be strictly positive).
     * @param pattern The tile pattern (must not be <code>null</code>).
     * @param number The tile number (must be positive).
     * @param collision The tile collision (must not be <code>null</code>).
     */
    public TileGame(int width, int height, Integer pattern, int number, CollisionTile collision)
    {
        Check.superiorStrict(width, 0);
        Check.superiorStrict(height, 0);
        Check.superiorOrEqual(number, 0);
        Check.notNull(pattern);
        Check.notNull(collision);

        this.width = width;
        this.height = height;
        this.pattern = pattern;
        this.number = number;
        this.collision = collision;
        x = 0;
        y = 0;
    }

    /**
     * Set pattern number.
     * 
     * @param pattern The pattern number (must not be <code>null</code>).
     */
    public void setPattern(Integer pattern)
    {
        Check.notNull(pattern);
        this.pattern = pattern;
    }

    /**
     * Set tile index inside pattern.
     * 
     * @param number The tile index.
     */
    public void setNumber(int number)
    {
        Check.superiorOrEqual(number, 0);
        this.number = number;
    }

    /**
     * Set collision.
     * 
     * @param collision The collision (must not be <code>null</code>).
     */
    public void setCollision(CollisionTile collision)
    {
        Check.notNull(collision);
        this.collision = collision;
    }

    /**
     * Set tile location x. Should be used only when overriding the
     * {@link MapTileGame#loadTile(Collection, FileReading, int)} function.
     * 
     * @param x The tile location x.
     */
    public void setX(int x)
    {
        this.x = x;
    }

    /**
     * Set tile location y. Should be used only when overriding the
     * {@link MapTileGame#loadTile(Collection, FileReading, int)} function.
     * 
     * @param y The tile location y.
     */
    public void setY(int y)
    {
        this.y = y;
    }

    /**
     * Get the horizontal collision location between the tile and the transformable.
     * 
     * @param x The horizontal location.
     * @param y The vertical location.
     * @return The collision x (<code>null</code> if none).
     */
    public Double getCollisionX(double x, double y)
    {
        for (final CollisionFunction function : getCollision().getCollisionFunctions())
        {
            if (function.getAxis() == CollisionRefential.X)
            {
                final int min = function.getRange().getMin();
                final int max = function.getRange().getMax();
                final int input = getInputValue(function, x, y);
                if (input >= min && input <= max)
                {
                    final double value = getX() + function.computeCollision(input);
                    return Double.valueOf(value);
                }
            }
        }
        return null;
    }

    /**
     * Get the vertical collision location between the tile and the transformable.
     * 
     * @param x The horizontal location.
     * @param y The vertical location.
     * @return The collision y (<code>null</code> if none).
     */
    public Double getCollisionY(double x, double y)
    {
        for (final CollisionFunction function : getCollision().getCollisionFunctions())
        {
            if (function.getAxis() == CollisionRefential.Y)
            {
                final int min = function.getRange().getMin();
                final int max = function.getRange().getMax();
                final int input = getInputValue(function, x, y);
                if (input >= min && input <= max)
                {
                    final double value = getY() + function.computeCollision(input);
                    return Double.valueOf(value);
                }
            }
        }
        return null;
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
     * Get tile collision.
     * 
     * @return The tile collision.
     */
    public CollisionTile getCollision()
    {
        return collision;
    }

    /**
     * Get the input value from the function.
     * 
     * @param function The function used.
     * @param x The horizontal location.
     * @param y The vertical location.
     * @return The input value.
     */
    private int getInputValue(CollisionFunction function, double x, double y)
    {
        final CollisionRefential input = function.getInput();
        switch (input)
        {
            case X:
                return (int) Math.floor(x - getX());
            case Y:
                return (int) Math.floor(y - getY());
            default:
                throw new RuntimeException("Unknow type: " + input);
        }
    }
}
