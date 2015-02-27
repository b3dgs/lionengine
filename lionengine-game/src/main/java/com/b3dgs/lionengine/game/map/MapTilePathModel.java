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
import java.util.Collections;

import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.UtilMath;
import com.b3dgs.lionengine.core.Media;
import com.b3dgs.lionengine.game.CoordTile;
import com.b3dgs.lionengine.game.Tiled;
import com.b3dgs.lionengine.game.collision.CollisionFormula;
import com.b3dgs.lionengine.game.configurer.ConfigPathfinding;
import com.b3dgs.lionengine.game.trait.Pathfindable;
import com.b3dgs.lionengine.stream.Stream;
import com.b3dgs.lionengine.stream.XmlNode;

/**
 * Map tile path model implementation.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public class MapTilePathModel
        implements MapTilePath
{
    /** Map reference. */
    private final MapTile map;

    /**
     * Create a map tile path.
     * 
     * @param map The map reference.
     */
    public MapTilePathModel(MapTile map)
    {
        this.map = map;
    }

    /**
     * Search a free area from this area.
     * 
     * @param entity The entity to search around.
     * @param radius The search size.
     * @return The free place found.
     */
    public CoordTile getFreeTileAround(Tiled entity, int radius)
    {
        return getFreeTileAround(entity.getLocationInTileX(), entity.getLocationInTileY(), radius);
    }

    /**
     * Get the closest tile location around the area. The returned tile is corresponding to the required collision.
     * 
     * @param from The tiled reference.
     * @param to The tiled reference.
     * @param collision The collision to search
     * @param radius The search size.
     * @return The closest location found.
     */
    public CoordTile getClosestTile(Tiled from, Tiled to, CollisionFormula collision, int radius)
    {
        final int sx = to.getLocationInTileX();
        final int sy = to.getLocationInTileY();

        final int fx = to.getLocationInTileX();
        final int fy = to.getLocationInTileY();
        final int fw = from.getWidthInTile();
        final int fh = from.getHeightInTile();
        int closestX = 0;
        int closestY = 0;
        int dist = Integer.MAX_VALUE;
        int size = 1;
        boolean found = false;
        while (!found)
        {
            for (int x = sx - size; x <= sx + size; x++)
            {
                for (int y = sy - size; y <= sy + size; y++)
                {
                    final Tile tile = map.getTile(x, y);
                    final TileCollision tileCollision = tile.getFeature(TileCollision.class);
                    if (tileCollision.getCollisionFormulas().contains(collision))
                    {
                        final int d = UtilMath.getDistance(fx, fy, fw, fh, x, y, 1, 1);
                        if (d < dist)
                        {
                            dist = d;
                            closestX = x;
                            closestY = y;
                            found = true;
                        }
                    }
                }
            }
            size++;
            if (size >= radius)
            {
                return null;
            }
        }
        return new CoordTile(closestX, closestY);
    }

    /**
     * Get the closest unused location around the area. The returned tile is not blocking, nor used by an entity.
     * 
     * @param sx The horizontal location.
     * @param sy The vertical location.
     * @param sw The source location width.
     * @param sh The source location height.
     * @param radius The search size.
     * @param dx The horizontal destination location.
     * @param dy The vertical destination location.
     * @param dw The destination location width.
     * @param dh The destination location height.
     * @return The closest location found.
     */
    private CoordTile getClosestAvailableTile(int sx, int sy, int sw, int sh, int radius, int dx, int dy, int dw, int dh)
    {
        int closestX = 0;
        int closestY = 0;
        int dist = Integer.MAX_VALUE;
        int size = 1;
        boolean found = false;
        while (!found)
        {
            for (int x = sx - size; x <= sx + size; x++)
            {
                for (int y = sy - size; y <= sy + size; y++)
                {
                    if (isAreaAvailable(x, y, sw, sh, null))
                    {
                        final int d = UtilMath.getDistance(x, y, sw, sh, dx, dy, dw, dh);
                        if (d < dist)
                        {
                            dist = d;
                            closestX = x;
                            closestY = y;
                            found = true;
                        }
                    }
                }
            }
            size++;
            if (size >= radius)
            {
                return null;
            }
        }
        return new CoordTile(closestX, closestY);
    }

    /*
     * MapTilePath
     */

    @Override
    public void loadPathfinding(Media pathfindingConfig) throws LionEngineException
    {
        final XmlNode nodePathfinding = Stream.loadXml(pathfindingConfig);
        final ConfigPathfinding config = ConfigPathfinding.create(nodePathfinding);
        for (int v = 0; v < map.getHeightInTile(); v++)
        {
            for (int h = 0; h < map.getWidthInTile(); h++)
            {
                final Tile tile = map.getTile(h, v);
                if (tile != null)
                {
                    final TilePath tilePath = new TilePathModel(tile);
                    tile.addFeature(tilePath);

                    final TileCollision tileCollision = tile.getFeature(TileCollision.class);
                    final String group = tileCollision.getGroup();
                    final String category = config.getCategory(group);
                    if (category != null)
                    {
                        tilePath.setCategory(category);
                    }
                }
            }
        }
    }

    @Override
    public void addObjectId(int tx, int ty, Integer id)
    {
        final Tile tile = map.getTile(tx, ty);
        if (tile != null)
        {
            final TilePath tilePath = tile.getFeature(TilePath.class);
            tilePath.addObjectId(id);
        }
    }

    @Override
    public Collection<Integer> getObjectsId(int tx, int ty)
    {
        final Tile tile = map.getTile(tx, ty);
        if (tile != null)
        {
            final TilePath tilePath = tile.getFeature(TilePath.class);
            return tilePath.getObjectsId();
        }
        return Collections.emptyList();
    }

    @Override
    public Tile getTile(Tiled tiled)
    {
        return map.getTile(tiled.getLocationInTileX(), tiled.getLocationInTileY());
    }

    @Override
    public boolean isBlocked(Pathfindable mover, int tx, int ty, boolean ignoreObjectsId)
    {
        // Blocked if outside map range
        if (ty >= 0 && tx >= 0 && ty < map.getHeightInTile() && tx < map.getWidthInTile())
        {
            // Check if all objects id are non blocking
            if (!ignoreObjectsId)
            {
                final Collection<Integer> ids = getObjectsId(tx, ty);
                int ignoredCount = 0;
                for (final Integer id : ids)
                {
                    if (mover.isIgnoredId(id))
                    {
                        ignoredCount++;
                    }
                }
                if (ignoredCount == ids.size())
                {
                    return false;
                }
            }
            // Check if tile is blocking
            final Tile tile = map.getTile(tx, ty);
            if (tile != null)
            {
                final TilePath tilePath = tile.getFeature(TilePath.class);
                return mover.isBlocking(tilePath.getCategory());
            }
        }
        return true;
    }

    @Override
    public double getCost(Pathfindable mover, int sx, int sy, int tx, int ty)
    {
        return 1;
    }

    @Override
    public CoordTile getFreeTileAround(int tx, int ty, int radius)
    {
        int size = 0;
        boolean search = true;
        while (search)
        {
            for (int x = tx - size; x <= tx + size; x++)
            {
                for (int y = ty - size; y <= ty + size; y++)
                {
                    if (isAreaAvailable(x, y, 1, 1, null))
                    {
                        return new CoordTile(x, y);
                    }
                }
            }
            size++;
            if (size > radius)
            {
                search = false;
            }
        }
        return null;
    }

    @Override
    public CoordTile getClosestAvailableTile(Tiled from, int radius, Tiled to)
    {
        return getClosestAvailableTile(from.getLocationInTileX(), from.getLocationInTileY(), from.getWidthInTile(),
                from.getHeightInTile(), radius, to.getLocationInTileX(), to.getLocationInTileY(), to.getWidthInTile(),
                to.getHeightInTile());
    }

    @Override
    public CoordTile getClosestAvailableTile(int sx, int sy, int radius, int dx, int dy)
    {
        return getClosestAvailableTile(sx, sy, 1, 1, radius, dx, dy, 1, 1);
    }

    @Override
    public boolean isAreaAvailable(int tx, int ty, int w, int h, Integer ignoreObjectId)
    {
        for (int y = ty; y < ty + h; y++)
        {
            for (int x = tx; x < tx + w; x++)
            {
                final Collection<Integer> ids = getObjectsId(x, y);
                final Tile tile = map.getTile(x, y);
                if (tile != null)
                {
                    if (ignoreObjectId != null && ids.size() > 0 && !ids.contains(ignoreObjectId))
                    {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    @Override
    public MapTile getMap()
    {
        return map;
    }
}
