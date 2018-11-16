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
                v = UtilMath.clamp(Math.floor(x - tile.getX()), 0, tile.getWidth());
                break;
            case Y:
                v = UtilMath.clamp(Math.floor(y - tile.getY()), 0, tile.getHeight());
                break;
            default:
                throw new LionEngineException(input);
        }
        return v;
    }

    /**
     * Get the horizontal collision location between the tile and the current location.
     * 
     * @param formula The collision formula.
     * @param x The current horizontal location.
     * @param y The current vertical location.
     * @param offsetX The horizontal offset.
     * @return The horizontal collision (<code>null</code> if none).
     */
    private Double getCollisionX(CollisionFormula formula, double x, double y, int offsetX)
    {
        final CollisionRange range = formula.getRange();
        if (range.getOutput() == Axis.X)
        {
            final double yOnTile = getInputValue(Axis.Y, x, y);

            if (Double.compare(yOnTile, range.getMinY()) >= 0 && Double.compare(yOnTile, range.getMaxY()) <= 0)
            {
                final double xOnTile = getInputValue(Axis.X, x, y);
                final double result = formula.getFunction().compute(yOnTile);

                // CHECKSTYLE IGNORE LINE: NestedIfDepth
                if (Double.compare(xOnTile, result + range.getMinX()) >= 0
                    && Double.compare(xOnTile, result + range.getMaxX()) <= 0)
                {
                    final double coll = tile.getX() + result - offsetX;
                    return Double.valueOf(coll);
                }
            }
        }
        return null;
    }

    /**
     * Get the vertical collision location between the tile and the current location.
     * 
     * @param formula The collision formula.
     * @param x The current horizontal location.
     * @param y The current vertical location.
     * @param offsetY The vertical offset.
     * @return The vertical collision (<code>null</code> if none).
     */
    private Double getCollisionY(CollisionFormula formula, double x, double y, int offsetY)
    {
        final CollisionRange range = formula.getRange();
        if (range.getOutput() == Axis.Y)
        {
            final double xOnTile = getInputValue(Axis.X, x, y);

            if (Double.compare(xOnTile, range.getMinX()) >= 0 && Double.compare(xOnTile, range.getMaxX()) <= 0)
            {
                final double yOnTile = getInputValue(Axis.Y, x, y);
                final double result = formula.getFunction().compute(xOnTile);

                // CHECKSTYLE IGNORE LINE: NestedIfDepth
                if (Double.compare(yOnTile, result + range.getMinY() - 1) >= 0
                    && Double.compare(yOnTile, result + range.getMaxY()) <= 0)
                {
                    final double coll = tile.getY() + result - offsetY;
                    return Double.valueOf(coll);
                }
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
            if (formulas.contains(formula) && category.getAxis() == formula.getRange().getOutput())
            {
                final Double collisionX = getCollisionX(formula, x, y, category.getOffsetX());
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
            if (formulas.contains(formula) && category.getAxis() == formula.getRange().getOutput())
            {
                final Double collisionY = getCollisionY(formula, x, y, category.getOffsetY());
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
