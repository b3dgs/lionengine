/*
 * Copyright (C) 2013-2022 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.game.feature.tile.map.collision;

import java.util.ArrayDeque;
import java.util.List;
import java.util.Queue;

import com.b3dgs.lionengine.Check;
import com.b3dgs.lionengine.game.feature.tile.Tile;

/**
 * Represents the map collision results.
 */
public class CollisionResult
{
    /** Min to string size. */
    private static final int MIN_LENGHT = 30;

    private static final Queue<CollisionResult> CACHE = new ArrayDeque<>();

    /**
     * Get cached result data to fill, or create new one.
     * 
     * @param category The category.
     * @param x The horizontal collision location ({@link Double#NaN} if none).
     * @param y The vertical collision location ({@link Double#NaN} if none).
     * @param tile The collided tile.
     * @param formulaX The formula used on horizontal.
     * @param formulaY The formula used on vertical.
     * @return The result data.
     */
    public static CollisionResult get(CollisionCategory category,
                                      double x,
                                      double y,
                                      Tile tile,
                                      CollisionFormula formulaX,
                                      CollisionFormula formulaY)
    {
        final CollisionResult result = CACHE.poll();
        if (result != null)
        {
            result.category = category;
            result.x = x;
            result.y = y;
            result.tile = tile;
            result.formulaX = formulaX;
            result.formulaY = formulaY;
            return result;
        }
        return new CollisionResult(category, x, y, tile, formulaX, formulaY);
    }

    /**
     * Cache results.
     * 
     * @param results The results to cache.
     */
    public static void cache(List<CollisionResult> results)
    {
        CACHE.addAll(results);
    }

    /**
     * Cache result.
     * 
     * @param result The result to cache.
     */
    public static void cache(CollisionResult result)
    {
        CACHE.add(result);
    }

    /** Category. */
    private CollisionCategory category;
    /** Horizontal collision location (<code>null</code> if none). */
    private double x;
    /** Vertical collision location (<code>null</code> if none). */
    private double y;
    /** Collided tile. */
    private Tile tile;
    /** Formula used on horizontal. */
    private CollisionFormula formulaX;
    /** Formula used on vertical. */
    private CollisionFormula formulaY;

    /**
     * Create a collision result.
     * 
     * @param category The category.
     * @param x The horizontal collision location ({@link Double#NaN} if none).
     * @param y The vertical collision location ({@link Double#NaN} if none).
     * @param tile The collided tile.
     * @param formulaX The formula used on horizontal.
     * @param formulaY The formula used on vertical.
     */
    public CollisionResult(CollisionCategory category,
                           double x,
                           double y,
                           Tile tile,
                           CollisionFormula formulaX,
                           CollisionFormula formulaY)
    {
        super();

        Check.notNull(tile);

        this.category = category;
        this.x = x;
        this.y = y;
        this.tile = tile;
        this.formulaX = formulaX;
        this.formulaY = formulaY;
    }

    /**
     * Get the category.
     * 
     * @return The category.
     */
    public CollisionCategory getCategory()
    {
        return category;
    }

    /**
     * Get the horizontal collision location.
     * 
     * @return The horizontal collision location (<code>null</code> if none).
     */
    public double getX()
    {
        return x;
    }

    /**
     * Get the vertical collision location.
     * 
     * @return The vertical collision location (<code>null</code> if none).
     */
    public double getY()
    {
        return y;
    }

    /**
     * Get the collided tile.
     * 
     * @return The collided tile.
     */
    public Tile getTile()
    {
        return tile;
    }

    /**
     * Check the collision formula on X.
     * 
     * @param name The formula collision name prefix.
     * @return <code>true</code> if collision starts with prefix, <code>false</code> else.
     */
    public boolean startWithX(String name)
    {
        return formulaX != null && formulaX.getName().startsWith(name);
    }

    /**
     * Check the collision formula on X.
     * 
     * @param name The formula collision name prefix.
     * @return <code>true</code> if collision starts with prefix, <code>false</code> else.
     */
    public boolean endWithX(String name)
    {
        return formulaX != null && formulaX.getName().endsWith(name);
    }

    /**
     * Check the collision formula on Y.
     * 
     * @param name The formula collision name suffix.
     * @return <code>true</code> if collision starts with prefix, <code>false</code> else.
     */
    public boolean startWithY(String name)
    {
        return formulaY != null && formulaY.getName().startsWith(name);
    }

    /**
     * Check the collision formula on Y.
     * 
     * @param name The formula collision name suffix.
     * @return <code>true</code> if collision starts with prefix, <code>false</code> else.
     */
    public boolean endWithY(String name)
    {
        return formulaY != null && formulaY.getName().endsWith(name);
    }

    /**
     * Check the collision formula.
     * 
     * @param name The formula collision name contains.
     * @return <code>true</code> if collision starts with prefix, <code>false</code> else.
     */
    public boolean contains(String name)
    {
        return formulaX != null && formulaX.getName().contains(name)
               || formulaY != null && formulaY.getName().contains(name);
    }

    /**
     * Check the collision formula.
     * 
     * @param name The formula collision name contains.
     * @return <code>true</code> if collision contains name, <code>false</code> else.
     */
    public boolean containsX(String name)
    {
        return formulaX != null && formulaX.getName().contains(name);
    }

    /**
     * Check the collision formula.
     * 
     * @param name The formula collision name contains.
     * @return <code>true</code> if collision contains name, <code>false</code> else.
     */
    public boolean containsY(String name)
    {
        return formulaY != null && formulaY.getName().contains(name);
    }

    /*
     * Object
     */

    @Override
    public String toString()
    {
        return new StringBuilder(MIN_LENGHT).append(getClass().getSimpleName())
                                            .append(" [x=")
                                            .append(x)
                                            .append(", y=")
                                            .append(y)
                                            .append(", fx=")
                                            .append(formulaX.getName())
                                            .append(", fy=")
                                            .append(formulaY.getName())
                                            .append("]")
                                            .toString();
    }
}
