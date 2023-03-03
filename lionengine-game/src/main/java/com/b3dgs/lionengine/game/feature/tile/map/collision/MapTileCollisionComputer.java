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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.UtilMath;
import com.b3dgs.lionengine.game.feature.Transformable;
import com.b3dgs.lionengine.game.feature.tile.Tile;
import com.b3dgs.lionengine.game.feature.tile.map.MapTile;

/**
 * Compute map tile collision.
 */
final class MapTileCollisionComputer
{
    private static final int MAX_GLUED = 5;

    /**
     * Compute the collision from current location.
     * 
     * @param map The map surface reference.
     * @param loaderList The loader reference.
     * @param category The collision category.
     * @param ox The current horizontal location.
     * @param oy The current vertical location.
     * @param x The current horizontal location.
     * @param y The current vertical location.
     * @return The computed collision result, <code>null</code> if none.
     */
    private static CollisionResult computeCollision(MapTile map,
                                                    Function<Tile, List<CollisionFormula>> loaderList,
                                                    CollisionCategory category,
                                                    double ox,
                                                    double oy,
                                                    double x,
                                                    double y)
    {
        final Tile tile = map.getTileAt(getPositionToSide(ox, x), getPositionToSide(oy, y));
        if (tile != null)
        {
            double cx = Double.NaN;
            double cy = Double.NaN;
            CollisionFormula fx = null;
            CollisionFormula fy = null;
            final List<CollisionFormula> formulasList = loaderList.apply(tile);

            final int n = formulasList.size();
            for (int i = 0; i < n; i++)
            {
                final CollisionFormula formula = formulasList.get(i);

                if (Double.isNaN(cx))
                {
                    cx = getCollisionX(tile, category, formula, x, y);
                    fx = formula;
                }
                if (Double.isNaN(cy))
                {
                    cy = getCollisionY(tile, category, formula, x, y);
                    fy = formula;
                }
                if (!Double.isNaN(cx) && !Double.isNaN(cy))
                {
                    break;
                }
            }
            if (!Double.isNaN(cx) || !Double.isNaN(cy))
            {
                return CollisionResult.get(category, cx, cy, tile, fx, fy);
            }
        }
        return null;
    }

    /**
     * Get the horizontal collision location between the tile and the movement vector.
     * 
     * @param tile The tile reference.
     * @param category The collision category.
     * @param formula The formula to apply.
     * @param x The current horizontal location.
     * @param y The current vertical location.
     * @return The horizontal collision (<code>null</code> if none).
     */
    private static double getCollisionX(Tile tile,
                                        CollisionCategory category,
                                        CollisionFormula formula,
                                        double x,
                                        double y)
    {
        final CollisionRange range = formula.getRange();
        if (Axis.X == category.getAxis() && Axis.X == range.getOutput() && containsCollisionFormula(category, formula))
        {
            final double collisionX = getCollisionX(tile, range, formula.getFunction(), x, y, category.getOffsetX());
            if (!Double.isNaN(collisionX))
            {
                return collisionX;
            }
        }
        return Double.NaN;
    }

    /**
     * Get the vertical collision location between the tile and the movement vector.
     * 
     * @param tile The tile reference.
     * @param category The collision category.
     * @param formula The formula to apply.
     * @param x The current horizontal location.
     * @param y The current vertical location.
     * @return The vertical collision (<code>null</code> if none).
     */
    private static double getCollisionY(Tile tile,
                                        CollisionCategory category,
                                        CollisionFormula formula,
                                        double x,
                                        double y)
    {
        final CollisionRange range = formula.getRange();
        if (Axis.Y == category.getAxis() && Axis.Y == range.getOutput() && containsCollisionFormula(category, formula))
        {
            final double collisionY = getCollisionY(tile, range, formula.getFunction(), x, y, category.getOffsetY());
            if (!Double.isNaN(collisionY))
            {
                return collisionY;
            }
        }
        return Double.NaN;
    }

    /**
     * Check if tile contains at least one collision from the category.
     * 
     * @param category The category reference.
     * @param formula The tile formula.
     * @return The formula in common between tile and category, <code>null</code> if none.
     */
    private static boolean containsCollisionFormula(CollisionCategory category, CollisionFormula formula)
    {
        final List<CollisionGroup> groups = category.getGroups();
        final int n = groups.size();
        for (int i = 0; i < n; i++)
        {
            final CollisionGroup group = groups.get(i);
            if (group.getFormulas().contains(formula))
            {
                return true;
            }
        }
        return false;
    }

    /**
     * Get the horizontal collision location between the tile and the current location.
     * 
     * @param tile The tile reference.
     * @param range The collision range.
     * @param function The collision function.
     * @param x The current horizontal location.
     * @param y The current vertical location.
     * @param offsetX The horizontal offset.
     * @return The horizontal collision (<code>null</code> if none).
     */
    private static double getCollisionX(Tile tile,
                                        CollisionRange range,
                                        CollisionFunction function,
                                        double x,
                                        double y,
                                        int offsetX)
    {
        final double yOnTile = getInputValue(tile, Axis.Y, x, y);
        if (UtilMath.isBetween(yOnTile, range.getMinY(), range.getMaxY()))
        {
            final double xOnTile = getInputValue(tile, Axis.X, x, y);
            final double result = Math.floor(function.compute(yOnTile));

            if (UtilMath.isBetween(xOnTile, result + range.getMinX() - 1, result + range.getMaxX()))
            {
                return Math.floor(tile.getX() + result - offsetX);
            }
        }
        return Double.NaN;
    }

    /**
     * Get the vertical collision location between the tile and the current location.
     * 
     * @param tile The tile reference.
     * @param range The collision range.
     * @param function The collision function.
     * @param x The current horizontal location.
     * @param y The current vertical location.
     * @param offsetY The vertical offset.
     * @return The vertical collision (<code>null</code> if none).
     */
    private static double getCollisionY(Tile tile,
                                        CollisionRange range,
                                        CollisionFunction function,
                                        double x,
                                        double y,
                                        int offsetY)
    {
        final double xOnTile = getInputValue(tile, Axis.X, x, y);
        if (UtilMath.isBetween(xOnTile, range.getMinX(), range.getMaxX()))
        {
            final double yOnTile = getInputValue(tile, Axis.Y, x, y);
            final double result = Math.floor(function.compute(xOnTile));
            final double margin = Math.ceil(Math.abs(function.compute(1) - function.compute(0)));

            if (UtilMath.isBetween(yOnTile, result + range.getMinY() - margin, result + range.getMaxY()))
            {
                return Math.floor(tile.getY() + result - offsetY);
            }
        }
        return Double.NaN;
    }

    /**
     * Get the input value relative to tile.
     * 
     * @param tile The tile reference.
     * @param input The input used.
     * @param x The horizontal location.
     * @param y The vertical location.
     * @return The input value.
     */
    private static double getInputValue(Tile tile, Axis input, double x, double y)
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
     * Get position on tile depending on side.
     * 
     * @param old The old position.
     * @param cur The current position.
     * @return The position on side.
     */
    private static double getPositionToSide(double old, double cur)
    {
        // Moving right
        if (Double.compare(old, cur) > 0)
        {
            return cur;
        }
        // Moving left
        return old;
    }

    /** Last result. */
    private final Map<Transformable, CollisionResult> lastFound = new HashMap<>();

    /**
     * Create the map tile collision computer.
     */
    MapTileCollisionComputer()
    {
        super();
    }

    /**
     * Search first tile hit by the transformable that contains collision, applying a ray tracing from its old location
     * to its current. This way, the transformable can not pass through a collidable tile.
     * 
     * @param map The map surface reference.
     * @param loaderList The loader reference.
     * @param transformable The transformable reference.
     * @param category The collisions category to search in.
     * @return The collision result, <code>null</code> if nothing found.
     */
    public CollisionResult computeCollision(MapTile map,
                                            Function<Tile, List<CollisionFormula>> loaderList,
                                            Transformable transformable,
                                            CollisionCategory category)
    {
        // Distance calculation
        final double sh = transformable.getOldX() + category.getOffsetX();
        final double sv = transformable.getOldY() + category.getOffsetY();

        final double dh = transformable.getX() + category.getOffsetX() - sh;
        final double dv = transformable.getY() + category.getOffsetY() - sv;

        final double nh = Math.abs(dh);
        final double nv = Math.abs(dv);

        final int max = (int) Math.ceil(Math.max(nh, nv));
        final double sx;
        final double sy;

        if (Double.compare(nh, 1.0) >= 0 || Double.compare(nv, 1.0) >= 0)
        {
            sx = dh / max;
            sy = dv / max;
        }
        else
        {
            sx = dh;
            sy = dv;
        }

        if (category.isGlue() && transformable.getY() > transformable.getOldY())
        {
            lastFound.remove(transformable);
        }

        return computeCollision(map, loaderList, transformable, category, sh, sv, sx, sy, max);
    }

    /**
     * Compute collision step by step moving first horizontal and then vertical.
     * 
     * @param map The map surface reference.
     * @param loaderList The loader reference.
     * @param transformable The transformable reference.
     * @param category The collisions category to search in.
     * @param sh The starting horizontal location.
     * @param sv The starting vertical location.
     * @param sx The horizontal search vector.
     * @param sy The vertical search vector.
     * @param max The maximum search iterations.
     * @return The collision found, <code>null</code> if none.
     */
    // CHECKSTYLE IGNORE LINE: ExecutableStatementCount|CyclomaticComplexity|NPathComplexity|NCSS
    private CollisionResult computeCollision(MapTile map,
                                             Function<Tile, List<CollisionFormula>> loaderList,
                                             Transformable transformable,
                                             CollisionCategory category,
                                             double sh,
                                             double sv,
                                             double sx,
                                             double sy,
                                             int max)
    {
        double x = sh;
        double y = sv;
        double ox = x;
        double oy = y;

        boolean collX = false;
        boolean collY = false;

        CollisionResult last = null;
        for (int cur = 0; cur < max; cur++)
        {
            CollisionResult current = computeCollision(map, loaderList, category, ox, oy, x, y);
            if (current != null)
            {
                if (last != null)
                {
                    CollisionResult.cache(last);
                }
                last = current;
                if (!Double.isNaN(current.getX()))
                {
                    x = current.getX();
                    collX = true;
                }
                else
                {
                    collX = false;
                }
                if (!Double.isNaN(current.getY()))
                {
                    y = current.getY();
                    oy = y;
                }
            }
            else
            {
                collX = false;
            }

            if (!collX)
            {
                ox = x;
                x += sx;
            }

            current = computeCollision(map, loaderList, category, ox, oy, x, y);
            if (current != null)
            {
                if (last != null)
                {
                    CollisionResult.cache(last);
                }
                last = current;
                if (!Double.isNaN(current.getX()))
                {
                    x = current.getX();
                }
                if (!Double.isNaN(current.getY()))
                {
                    y = current.getY();
                    collY = true;
                }
                else
                {
                    collY = false;
                }
            }
            else
            {
                collY = false;
            }

            if (!collY)
            {
                oy = y;
                y += sy;
            }
        }

        if (category.isGlue())
        {
            if (last != null)
            {
                lastFound.put(transformable, last);
            }
            else if (lastFound.containsKey(transformable))
            {
                last = getGlued(map, loaderList, transformable, category, ox, oy, x, y);
            }
        }
        return last;
    }

    /**
     * Get glued collision by searching under if needed.
     * 
     * @param map The map surface reference.
     * @param loaderList The loader reference.
     * @param transformable The transformable reference.
     * @param category The category reference.
     * @param ox The old horizontal collision.
     * @param oy The old vertical collision.
     * @param x The current horizontal collision.
     * @param y The current vertical collision.
     * @return The collision found, <code>null</code> if none.
     */
    private CollisionResult getGlued(MapTile map,
                                     Function<Tile, List<CollisionFormula>> loaderList,
                                     Transformable transformable,
                                     CollisionCategory category,
                                     double ox,
                                     double oy,
                                     double x,
                                     double y)
    {
        for (int i = 1; i < MAX_GLUED; i++)
        {
            final CollisionResult found = computeCollision(map, loaderList, category, ox, oy, x, y - i);
            if (found != null)
            {
                lastFound.put(transformable, found);
                return found;
            }
        }
        return null;
    }
}
