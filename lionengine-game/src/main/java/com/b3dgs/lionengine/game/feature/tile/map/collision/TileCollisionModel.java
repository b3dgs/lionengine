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
import com.b3dgs.lionengine.UtilMath;
import com.b3dgs.lionengine.game.feature.FeatureModel;
import com.b3dgs.lionengine.game.feature.tile.Tile;

/**
 * Tile collision implementation.
 */
public class TileCollisionModel extends FeatureModel implements TileCollision
{
    /** The collision formulas used. */
    private final Collection<CollisionFormula> formulas = new HashSet<>();
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
    public double getInputValue(Axis input, double x, double y)
    {
        final double v;
        switch (input)
        {
            case X:
                v = Math.floor(x - tile.getX());
                break;
            case Y:
                v = Math.floor(y - tile.getY());
                break;
            default:
                throw new LionEngineException(input);
        }
        return v;
    }

    /**
     * Get the horizontal collision location between the tile and the current location.
     * 
     * @param range The collision range.
     * @param function The collision function.
     * @param x The current horizontal location.
     * @param y The current vertical location.
     * @param offsetX The horizontal offset.
     * @return The horizontal collision (<code>null</code> if none).
     */
    private Double getCollisionX(CollisionRange range, CollisionFunction function, double x, double y, int offsetX)
    {
        final double yOnTile = getInputValue(Axis.Y, x, y);
        if (UtilMath.isBetween(yOnTile, range.getMinY(), range.getMaxY()))
        {
            final double xOnTile = getInputValue(Axis.X, x, y);
            final double result = function.compute(yOnTile);

            if (UtilMath.isBetween(xOnTile, result + range.getMinX() - 1, result + range.getMaxX()))
            {
                final double coll = Math.floor(tile.getX() + result - offsetX);
                return Double.valueOf(coll);
            }
        }
        return null;
    }

    /**
     * Get the vertical collision location between the tile and the current location.
     * 
     * @param range The collision range.
     * @param function The collision function.
     * @param x The current horizontal location.
     * @param y The current vertical location.
     * @param offsetY The vertical offset.
     * @return The vertical collision (<code>null</code> if none).
     */
    private Double getCollisionY(CollisionRange range, CollisionFunction function, double x, double y, int offsetY)
    {
        final double xOnTile = getInputValue(Axis.X, x, y);
        if (UtilMath.isBetween(xOnTile, range.getMinX(), range.getMaxX()))
        {
            final double yOnTile = getInputValue(Axis.Y, x, y);
            final double result = Math.floor(function.compute(xOnTile));

            if (UtilMath.isBetween(yOnTile, result + range.getMinY() - 1, result + range.getMaxY()))
            {
                final double coll = Math.floor(tile.getY() + result - offsetY);
                return Double.valueOf(coll);
            }
        }
        return null;
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
    public Double getCollisionX(CollisionCategory category, double x, double y)
    {
        for (final CollisionFormula formula : category.getFormulas())
        {
            final CollisionRange range = formula.getRange();
            if (category.getAxis() == range.getOutput() && formulas.contains(formula))
            {
                final Double collisionX = getCollisionX(range, formula.getFunction(), x, y, category.getOffsetX());
                if (collisionX != null)
                {
                    return collisionX;
                }
            }
        }
        return null;
    }

    @Override
    public Double getCollisionY(CollisionCategory category, double x, double y)
    {
        for (final CollisionFormula formula : category.getFormulas())
        {
            final CollisionRange range = formula.getRange();
            if (category.getAxis() == range.getOutput() && formulas.contains(formula))
            {
                final Double collisionY = getCollisionY(range, formula.getFunction(), x, y, category.getOffsetY());
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
