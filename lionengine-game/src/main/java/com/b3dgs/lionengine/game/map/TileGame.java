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
import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.UtilMath;
import com.b3dgs.lionengine.game.Axis;
import com.b3dgs.lionengine.game.collision.CollisionCategory;
import com.b3dgs.lionengine.game.collision.CollisionFormula;
import com.b3dgs.lionengine.game.collision.CollisionRange;
import com.b3dgs.lionengine.game.collision.CollisionSource;
import com.b3dgs.lionengine.stream.FileReading;

/**
 * Default class tile; containing following data:
 * <ul>
 * <li><code>pattern</code> : tilesheet number</li>
 * <li><code>number</code> : tile number inside tilesheet</li>
 * <li><code>x & y</code> : real location on map</li>
 * <li><code>formulas</code> : collision formulas used</li>
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
    /** Tilesheet number where tile is contained. */
    private Integer pattern;
    /** Position number in the tilesheet. */
    private int number;
    /** Horizontal location on map. */
    private int x;
    /** Vertical location on map. */
    private int y;
    /** The collision formulas used. */
    private final Collection<CollisionFormula> formulas;
    /** Error type. */
    private static final String ERROR_TYPE = "Unknown type: ";

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
        formulas = new HashSet<>();
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
     * Add a formula.
     * 
     * @param formula The formula to add.
     */
    public void addCollisionFormula(CollisionFormula formula)
    {
        formulas.add(formula);
    }

    /**
     * Remove a collision formula.
     * 
     * @param formula The formula reference.
     */
    public void removeCollisionFormula(CollisionFormula formula)
    {
        formulas.remove(formula);
    }

    /**
     * Remove all supported collision formulas.
     */
    public void removeCollisionFormulas()
    {
        formulas.clear();
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
     * @param category The collision category.
     * @param ox The old horizontal location.
     * @param oy The old vertical location.
     * @param x The horizontal location.
     * @param y The vertical location.
     * @return The collision x (<code>null</code> if none).
     */
    public Double getCollisionX(CollisionCategory category, double ox, double oy, double x, double y)
    {
        final Collection<CollisionFormula> formulas = category.getCollisionFormulas();
        final Collection<CollisionFormula> tileFormulas = getCollisionFormulas();
        for (final CollisionFormula formula : formulas)
        {
            if (checkSide(formula, Axis.X, ox, oy, x, y) && tileFormulas.contains(formula))
            {
                final CollisionRange range = formula.getRange();
                if (range.getOutput() == Axis.X)
                {
                    final int value = getInputValue(Axis.Y, formula, x, y);
                    if (UtilMath.isBetween(value, range.getMinY(), range.getMaxY()))
                    {
                        final double result = formula.getFunction().compute(value);
                        if (UtilMath.isBetween(result, range.getMinX(), range.getMaxX()))
                        {
                            return Double.valueOf(getX() + result - category.getOffsetX());
                        }
                    }
                }
            }
        }
        return null;
    }

    /**
     * Get the vertical collision location between the tile and the transformable.
     * 
     * @param category The collision category.
     * @param ox The old horizontal location.
     * @param oy The old vertical location.
     * @param x The horizontal location.
     * @param y The vertical location.
     * @return The collision y (<code>null</code> if none).
     */
    public Double getCollisionY(CollisionCategory category, double ox, double oy, double x, double y)
    {
        final Collection<CollisionFormula> formulas = category.getCollisionFormulas();
        final Collection<CollisionFormula> tileFormulas = getCollisionFormulas();
        for (final CollisionFormula formula : formulas)
        {
            if (checkSide(formula, Axis.Y, ox, oy, x, y) && tileFormulas.contains(formula))
            {
                final CollisionRange range = formula.getRange();
                if (range.getOutput() == Axis.Y)
                {
                    final int value = getInputValue(Axis.X, formula, x, y);
                    if (UtilMath.isBetween(value, range.getMinX(), range.getMaxX()))
                    {
                        final double result = formula.getFunction().compute(value);
                        if (UtilMath.isBetween(result, range.getMinY(), range.getMaxY()))
                        {
                            return Double.valueOf(getY() + result - category.getOffsetY());
                        }
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
     * Get tile collision formulas.
     * 
     * @return The tile collision formulas.
     */
    public Collection<CollisionFormula> getCollisionFormulas()
    {
        return formulas;
    }

    /**
     * Check the collision source.
     * 
     * @param formula The formula reference.
     * @param axis The axis to check.
     * @param ox The old horizontal location.
     * @param oy The old vertical location.
     * @param x The horizontal location.
     * @param y The vertical location.
     * @return <code>true</code> if arriving from right side, <code>false</code> else.
     */
    private boolean checkSide(CollisionFormula formula, Axis axis, double ox, double oy, double x, double y)
    {
        final int old = getInputValue(axis, formula, ox, oy);
        final int value = getInputValue(axis, formula, x, y);
        final CollisionSource source = formula.getRange().getSource();
        switch (source)
        {
            case FROM_TOP:
                return old > value;
            case FROM_BOTTOM:
                return old < value;
            case FROM_LEFT:
                return old < value;
            case FROM_RIGHT:
                return old > value;
            default:
                throw new LionEngineException(ERROR_TYPE, source.name());
        }
    }

    /**
     * Get the input value from the function.
     * 
     * @param input The input used.
     * @param formula The collision formula used.
     * @param x The horizontal location.
     * @param y The vertical location.
     * @return The input value.
     */
    private int getInputValue(Axis input, CollisionFormula formula, double x, double y)
    {
        switch (input)
        {
            case X:
                return (int) Math.floor(x - getX());
            case Y:
                return (int) Math.floor(y - getY());
            default:
                throw new LionEngineException(ERROR_TYPE, input.name());
        }
    }
}
