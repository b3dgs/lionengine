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
package com.b3dgs.lionengine.game.feature.tile.map.collision;

import java.util.Collection;
import java.util.HashSet;

import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.game.feature.FeatureModel;
import com.b3dgs.lionengine.game.feature.tile.Tile;
import com.b3dgs.lionengine.util.UtilMath;

/**
 * Tile collision implementation.
 */
public class TileCollisionModel extends FeatureModel implements TileCollision
{
    /** The collision formulas used. */
    private final Collection<CollisionFormula> formulas = new HashSet<CollisionFormula>();
    /** Tile reference. */
    private final Tile tile;

    /**
     * Create a tile collision.
     * 
     * @param tile The tile reference.
     */
    public TileCollisionModel(Tile tile)
    {
        super();
        this.tile = tile;
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
                return (int) Math.floor(x - tile.getX());
            case Y:
                return (int) Math.floor(y - tile.getY());
            default:
                throw new LionEngineException(input);
        }
    }

    /**
     * Get the horizontal collision location between the tile and the movement vector.
     * 
     * @param formula The collision formula.
     * @param ox The old horizontal location.
     * @param oy The old vertical location.
     * @param x The current horizontal location.
     * @param y The current vertical location.
     * @param offsetX The horizontal offset.
     * @return The horizontal collision (<code>null</code> if none).
     */
    private Double getCollisionX(CollisionFormula formula, double ox, double oy, double x, double y, int offsetX)
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
                    return getCollisionX(ox, x, result, offsetX);
                }
            }
        }
        return null;
    }

    /**
     * Get the horizontal collision location between the tile and the movement vector.
     * 
     * @param ox The old horizontal location.
     * @param x The current horizontal location.
     * @param result The computed horizontal location.
     * @param offsetX The horizontal offset.
     * @return The horizontal collision (<code>null</code> if none).
     */
    private Double getCollisionX(double ox, double x, double result, int offsetX)
    {
        final double coll = tile.getX() + result - offsetX;
        final Double collisionX;
        if (x > ox)
        {
            collisionX = Double.valueOf(coll - 1);
        }
        else
        {
            collisionX = Double.valueOf(coll + 1);
        }
        return collisionX;
    }

    /**
     * Get the vertical collision location between the tile and the movement vector.
     * 
     * @param formula The collision formula.
     * @param ox The old horizontal location.
     * @param oy The old vertical location.
     * @param x The current horizontal location.
     * @param y The current vertical location.
     * @param offsetY The vertical offset.
     * @return The vertical collision (<code>null</code> if none).
     */
    private Double getCollisionY(CollisionFormula formula, double ox, double oy, double x, double y, int offsetY)
    {
        final CollisionRange range = formula.getRange();
        if (range.getOutput() == Axis.Y && ox >= tile.getX() + range.getMinX() && ox <= tile.getX() + range.getMaxX())
        {
            final int value = getInputValue(Axis.X, x, y);
            if (UtilMath.isBetween(value, range.getMinX(), range.getMaxX()))
            {
                final int current = getInputValue(Axis.Y, x, y);
                if (UtilMath.isBetween(current, range.getMinY(), range.getMaxY()))
                {
                    final double result = formula.getFunction().compute(value);
                    return getCollisionY(oy, y, result, offsetY);
                }
            }
        }
        return null;
    }

    /**
     * Get the vertical collision location between the tile and the movement vector.
     * 
     * @param oy The old vertical location.
     * @param y The current vertical location.
     * @param result The computed vertical location.
     * @param offsetY The vertical offset.
     * @return The vertical collision (<code>null</code> if none).
     */
    private Double getCollisionY(double oy, double y, double result, int offsetY)
    {
        final double coll = tile.getY() + result - offsetY;
        final Double collisionY;
        if (y > oy)
        {
            collisionY = Double.valueOf(coll - 1);
        }
        else
        {
            collisionY = Double.valueOf(coll + 1);
        }
        return collisionY;
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
                final Double collisionX = getCollisionX(formula, ox, oy, x, y, category.getOffsetX());
                if (collisionX != null)
                {
                    return collisionX;
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
                final Double collisionY = getCollisionY(formula, ox, oy, x, y, category.getOffsetY());
                if (collisionY != null)
                {
                    return collisionY;
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
}
