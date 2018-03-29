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

import com.b3dgs.lionengine.game.feature.Transformable;
import com.b3dgs.lionengine.game.feature.tile.Tile;
import com.b3dgs.lionengine.game.feature.tile.map.MapTile;
import com.b3dgs.lionengine.util.UtilMath;

/**
 * Compute map tile collision.
 */
final class MapTileCollisionComputer
{
    /**
     * Check if tile contains at least one collision from the category.
     * 
     * @param tile The tile reference.
     * @param category The category reference.
     * @return <code>true</code> if there is a formula in common between tile and category.
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
     * @param tileCollision The current tile collision.
     * @param ox The old horizontal location.
     * @param oy The old vertical location.
     * @param x The current horizontal location.
     * @param y The current vertical location.
     * @return The computed horizontal collision.
     */
    private static Double getCollisionX(CollisionCategory category,
                                        TileCollision tileCollision,
                                        double ox,
                                        double oy,
                                        double x,
                                        double y)
    {
        if (category.getAxis() == Axis.X)
        {
            return tileCollision.getCollisionX(category, ox, oy, x, y);
        }
        return null;
    }

    /**
     * Get the vertical collision from current location.
     * 
     * @param category The collision category.
     * @param tileCollision The current tile collision.
     * @param ox The old horizontal location.
     * @param oy The old vertical location.
     * @param x The current horizontal location.
     * @param y The current vertical location.
     * @return The computed vertical collision.
     */
    private static Double getCollisionY(CollisionCategory category,
                                        TileCollision tileCollision,
                                        double ox,
                                        double oy,
                                        double x,
                                        double y)
    {
        if (category.getAxis() == Axis.Y)
        {
            return tileCollision.getCollisionY(category, ox, oy, x, y);
        }
        return null;
    }

    /** Map reference. */
    private final MapTile map;

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

        // Search vector and number of search steps
        final double norm = Math.sqrt(dh * dh + dv * dv);
        final double sx;
        final double sy;
        if (Double.compare(norm, 0.0) == 0)
        {
            sx = 0;
            sy = 0;
        }
        else
        {
            sx = dh / norm;
            sy = dv / norm;
        }

        double h = sh;
        double v = sv;

        CollisionResult found = null;
        for (int count = 0; count < norm; count++)
        {
            final CollisionResult res = getResult(map, category, sx, sy, h, v);
            if (res != null)
            {
                found = res;
                if (res.getX() != null)
                {
                    h = res.getX().doubleValue();
                }
                if (res.getY() != null)
                {
                    v = res.getY().doubleValue();
                }
            }
            v += sy;
            h += sx;
        }
        return found;
    }

    /**
     * Compute the collision from current location.
     * 
     * @param map The map tile owner.
     * @param category The collision category.
     * @param ox The old horizontal location.
     * @param oy The old vertical location.
     * @param x The current horizontal location.
     * @param y The current vertical location.
     * @return The computed collision result, <code>null</code> if none.
     */
    private CollisionResult computeCollision(MapTile map,
                                             CollisionCategory category,
                                             double ox,
                                             double oy,
                                             double x,
                                             double y)
    {
        final Tile tile = map.getTile((int) Math.floor(x / map.getTileWidth()),
                                      (int) Math.floor(y / map.getTileHeight()));
        if (tile != null)
        {
            final TileCollision tileCollision = tile.getFeature(TileCollision.class);
            if (containsCollisionFormula(tileCollision, category))
            {
                final Double cx = getCollisionX(category, tileCollision, ox, oy, x, y);
                final Double cy = getCollisionY(category, tileCollision, ox, oy, x, y);
                return new CollisionResult(cx, cy, tile);
            }
        }
        return null;
    }

    /**
     * Get the collision result from current sub location.
     * 
     * @param map The map tile owner.
     * @param category The collision category.
     * @param sx The horizontal speed.
     * @param sy The vertical speed.
     * @param h The current horizontal location.
     * @param v The current vertical location.
     * @return The collision found, <code>null</code> if none.
     */
    private CollisionResult getResult(MapTile map, CollisionCategory category, double sx, double sy, double h, double v)
    {
        final double oh = UtilMath.getRound(sx, h);
        final double ov = UtilMath.getRound(sy, v);

        final CollisionResult result;
        result = computeCollision(map, category, oh, ov, UtilMath.getRound(sx, h + sx), UtilMath.getRound(sy, v));
        if (result == null)
        {
            return computeCollision(map,
                                    category,
                                    oh,
                                    ov,
                                    UtilMath.getRound(sx, h + sx),
                                    UtilMath.getRound(sy, v + sy));
        }
        return result;
    }
}
