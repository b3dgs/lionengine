/*
 * Copyright (C) 2013-2018 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
     * Check if tile contains at least one collision from the category.
     * 
     * @param tile The tile reference.
     * @param category The category reference.
     * @return The formula in common between tile and category, <code>null</code> if none.
     */
    private static boolean containsCollisionFormula(TileCollision tile, CollisionCategory category)
    {
        final Collection<CollisionFormula> formulas = tile.getCollisionFormulas();
        for (final CollisionFormula formula : category.getFormulas())
        {
            if (formulas.contains(formula))
            {
                return true;
            }
        }
        return false;
    }

    /**
     * Get the horizontal collision from current location.
     * 
     * @param category The collision category.
     * @param formula The formula to apply.
     * @param tileCollision The current tile collision.
     * @param x The current horizontal location.
     * @param y The current vertical location.
     * @return The computed horizontal collision.
     */
    private static Double getCollisionX(CollisionCategory category,
                                        CollisionFormula formula,
                                        TileCollision tileCollision,
                                        double x,
                                        double y)
    {
        if (Axis.X == category.getAxis() && containsCollisionFormula(tileCollision, category))
        {
            return tileCollision.getCollisionX(category, formula, x, y);
        }
        return null;
    }

    /**
     * Get the vertical collision from current location.
     * 
     * @param category The collision category.
     * @param formula The formula to apply.
     * @param tileCollision The current tile collision.
     * @param x The current horizontal location.
     * @param y The current vertical location.
     * @return The computed vertical collision.
     */
    private static Double getCollisionY(CollisionCategory category,
                                        CollisionFormula formula,
                                        TileCollision tileCollision,
                                        double x,
                                        double y)
    {
        if (Axis.Y == category.getAxis() && containsCollisionFormula(tileCollision, category))
        {
            return tileCollision.getCollisionY(category, formula, x, y);
        }
        return null;
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

    /** Map reference. */
    private final MapTile map;
    /** Last result. */
    private CollisionResult lastFound;

    /**
     * Create the map tile collision computer.
     * 
     * @param map The map tile owner.
     */
    MapTileCollisionComputer(MapTile map)
    {
        super();

        this.map = map;
    }

    /**
     * Search first tile hit by the transformable that contains collision, applying a ray tracing from its old location
     * to its current. This way, the transformable can not pass through a collidable tile.
     * 
     * @param transformable The transformable reference.
     * @param category The collisions category to search in.
     * @return The collision result, <code>null</code> if nothing found.
     */
    public CollisionResult computeCollision(Transformable transformable, CollisionCategory category)
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

        if (transformable.getY() > transformable.getOldY())
        {
            lastFound = null;
        }

        return computeCollision(category, sh, sv, sx, sy, max);
    }

    /**
     * Compute collision step by step moving first horizontal and then vertical.
     * 
     * @param category The collisions category to search in.
     * @param sh The starting horizontal location.
     * @param sv The starting vertical location.
     * @param sx The horizontal search vector.
     * @param sy The vertical search vector.
     * @param max The maximum search iterations.
     * @return The collision found, <code>null</code> if none.
     */
    // CHECKSTYLE IGNORE LINE: ExecutableStatementCount|CyclomaticComplexity|NPathComplexity
    private CollisionResult computeCollision(CollisionCategory category,
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
            CollisionResult current = computeCollision(category, ox, oy, x, y);
            if (current != null)
            {
                last = current;
                lastFound = last;
                if (current.getX() != null)
                {
                    x = current.getX().doubleValue();
                    collX = true;
                }
                else
                {
                    collX = false;
                }
                if (current.getY() != null)
                {
                    y = current.getY().doubleValue();
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

            current = computeCollision(category, ox, oy, x, y);
            if (current != null)
            {
                last = current;
                lastFound = last;
                if (current.getX() != null)
                {
                    x = current.getX().doubleValue();
                }
                if (current.getY() != null)
                {
                    y = current.getY().doubleValue();
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

        if (lastFound != null && category.isGlue() && last == null)
        {
            last = getGlued(category, ox, oy, x, y);
        }
        return last;
    }

    /**
     * Get glued collision by searching under if needed.
     * 
     * @param category The category reference.
     * @param ox The old horizontal collision.
     * @param oy The old vertical collision.
     * @param x The current horizontal collision.
     * @param y The current vertical collision.
     * @return The collision found, <code>null</code> if none.
     */
    private CollisionResult getGlued(CollisionCategory category, double ox, double oy, double x, double y)
    {
        for (int i = 1; i < MAX_GLUED; i++)
        {
            final CollisionResult found = computeCollision(category, ox, oy, x, y - i);
            if (found != null)
            {
                lastFound = found;
                return found;
            }
        }
        return null;
    }

    /**
     * Compute the collision from current location.
     * 
     * @param category The collision category.
     * @param ox The current horizontal location.
     * @param oy The current vertical location.
     * @param x The current horizontal location.
     * @param y The current vertical location.
     * @return The computed collision result, <code>null</code> if none.
     */
    private CollisionResult computeCollision(CollisionCategory category, double ox, double oy, double x, double y)
    {
        final Tile tile = map.getTileAt(getPositionToSide(ox, x), getPositionToSide(oy, y));
        if (tile != null)
        {
            final TileCollision tileCollision = tile.getFeature(TileCollision.class);
            Double cx = null;
            Double cy = null;
            CollisionFormula fx = null;
            CollisionFormula fy = null;
            for (final CollisionFormula formula : tileCollision.getCollisionFormulas())
            {
                if (cx == null)
                {
                    cx = getCollisionX(category, formula, tileCollision, x, y);
                    fx = formula;
                }
                if (cy == null)
                {
                    cy = getCollisionY(category, formula, tileCollision, x, y);
                    fy = formula;
                }
            }
            if (cx != null || cy != null)
            {
                return new CollisionResult(cx, cy, tile, fx, fy);
            }
        }
        return null;
    }
}
