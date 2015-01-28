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
import java.util.HashSet;

import com.b3dgs.lionengine.Check;
import com.b3dgs.lionengine.UtilMath;
import com.b3dgs.lionengine.game.Axis;
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
    /** Tile collisions. */
    private final Collection<CollisionTile> collisions;

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
        pattern = Integer.valueOf(0);
        collisions = new HashSet<>();
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
    public void addCollision(CollisionTile collision)
    {
        Check.notNull(collision);
        collisions.add(collision);
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
        for (final CollisionTile collision : collisions)
        {
            final CollisionRange range = collision.getOutput();
            if (range.getAxis() == Axis.X)
            {
                final int min = range.getMin();
                final int max = range.getMax();
                final int input = getInputValue(collision, x, y);
                if (input >= min && input <= max)
                {
                    final double value = getX() + collision.getFormula().compute(input);
                    if (UtilMath.isBetween(value, min, max))
                    {
                        return Double.valueOf(value);
                    }
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
        for (final CollisionTile collision : collisions)
        {
            final CollisionRange range = collision.getOutput();
            if (range.getAxis() == Axis.Y)
            {
                final int min = range.getMin();
                final int max = range.getMax();
                final int input = getInputValue(collision, x, y);
                if (UtilMath.isBetween(input, min, max))
                {
                    final double value = getY() + collision.getFormula().compute(input);
                    if (UtilMath.isBetween(value, min, max))
                    {
                        return Double.valueOf(value);
                    }
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
     * Get tile collisions.
     * 
     * @return The tile collisions.
     */
    public Collection<CollisionTile> getCollisions()
    {
        return collisions;
    }

    /**
     * Get the input value from the function.
     * 
     * @param collision The collision used.
     * @param x The horizontal location.
     * @param y The vertical location.
     * @return The input value.
     */
    private int getInputValue(CollisionTile collision, double x, double y)
    {
        final Axis input = collision.getInput().getAxis();
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
