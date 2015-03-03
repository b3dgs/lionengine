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

import java.util.Collection;
import java.util.HashSet;

import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.UtilMath;
import com.b3dgs.lionengine.game.Axis;
import com.b3dgs.lionengine.game.collision.CollisionCategory;
import com.b3dgs.lionengine.game.collision.CollisionFormula;
import com.b3dgs.lionengine.game.collision.CollisionRange;

/**
 * Tile collision implementation.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public class TileCollisionModel
        implements TileCollision
{
    /** Error type. */
    private static final String ERROR_TYPE = "Unknown type: ";

    /** Tile reference. */
    private final Tile tile;
    /** The collision formulas used. */
    private final Collection<CollisionFormula> formulas;

    /**
     * Create a tile collision.
     * 
     * @param tile The tile reference.
     */
    public TileCollisionModel(Tile tile)
    {
        this.tile = tile;
        formulas = new HashSet<>();
    }

    /**
     * Get the input value relative to tile.
     * 
     * @param input The input used.
     * @param x The horizontal location.
     * @param y The vertical location.
     * @return The input value.
     */
    private int getInputValue(Axis input, double x, double y)
    {
        switch (input)
        {
            case X:
                return (int) (x - tile.getX());
            case Y:
                return (int) (y - tile.getY());
            default:
                throw new LionEngineException(ERROR_TYPE, input.name());
        }
    }

    /*
     * TileCollision
     */

    @Override
    public void addCollisionFormula(CollisionFormula formula)
    {
        formulas.add(formula);
    }

    @Override
    public void removeCollisionFormula(CollisionFormula formula)
    {
        formulas.remove(formula);
    }

    @Override
    public void removeCollisionFormulas()
    {
        formulas.clear();
    }

    @Override
    public Double getCollisionX(CollisionCategory category, double ox, double oy, double x, double y)
    {
        for (final CollisionFormula formula : category.getFormulas())
        {
            if (formulas.contains(formula) && category.getAxis() == formula.getRange().getOutput())
            {
                final CollisionRange range = formula.getRange();
                if (range.getOutput() == Axis.X)
                {
                    final int value = getInputValue(Axis.Y, x, y);
                    if (UtilMath.isBetween(value, range.getMinY(), range.getMaxY()))
                    {
                        final int current = getInputValue(Axis.X, x, y);
                        final int previous = getInputValue(Axis.X, ox, oy);
                        final double result = formula.getFunction().compute(previous);
                        if (UtilMath.isBetween(current, range.getMinX(), range.getMaxX()))
                        {
                            final double coll = tile.getX() + result - category.getOffsetX();
                            if (x > ox)
                            {
                                return Double.valueOf(coll + range.getMinX() - 1);
                            }
                            return Double.valueOf(coll + range.getMaxX() + 1);
                        }
                    }
                }
            }
        }
        return null;
    }

    @Override
    public Double getCollisionY(CollisionCategory category, double ox, double oy, double x, double y)
    {
        for (final CollisionFormula formula : category.getFormulas())
        {
            if (formulas.contains(formula) && category.getAxis() == formula.getRange().getOutput())
            {
                final CollisionRange range = formula.getRange();
                if (range.getOutput() == Axis.Y && ox >= tile.getX() + range.getMinX()
                        && ox <= tile.getX() + range.getMaxX())
                {
                    final int value = getInputValue(Axis.X, x, y);
                    if (UtilMath.isBetween(value, range.getMinX(), range.getMaxX()))
                    {
                        final int current = getInputValue(Axis.Y, x, y);
                        final int previous = getInputValue(Axis.Y, ox, oy);
                        final double result = formula.getFunction().compute(previous);
                        if (UtilMath.isBetween(current, range.getMinY(), range.getMaxY()))
                        {
                            final double coll = tile.getY() + result - category.getOffsetY();
                            if (y > oy)
                            {
                                return Double.valueOf(coll + range.getMinY() - 1);
                            }
                            return Double.valueOf(coll + range.getMaxY() + 1);
                        }
                    }
                }
            }
        }
        return null;
    }

    @Override
    public Collection<CollisionFormula> getCollisionFormulas()
    {
        return formulas;
    }

    @Override
    public Tile getTile()
    {
        return tile;
    }
}
