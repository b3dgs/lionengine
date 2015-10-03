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
import com.b3dgs.lionengine.game.configurer.ConfigPathfinding;
import com.b3dgs.lionengine.game.object.Services;
import com.b3dgs.lionengine.game.trait.pathfindable.Pathfindable;
import com.b3dgs.lionengine.stream.Stream;
import com.b3dgs.lionengine.stream.XmlNode;

/**
 * Map tile path model implementation.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public class MapTilePathModel implements MapTilePath
{
    /** Map reference. */
    private final MapTile map;

    /**
     * Create a map tile path.
     * <p>
     * The {@link Services} must provide the following services:
     * </p>
     * <ul>
     * <li>{@link MapTile}</li>
     * </ul>
     * 
     * @param services The services reference.
     * @throws LionEngineException If services not found.
     */
    public MapTilePathModel(Services services) throws LionEngineException
    {
        map = services.get(MapTile.class);
    }

    /**
     * Get the closest unused location around the area. The returned tile is not blocking, nor used by an object.
     * 
     * @param mover The object moving on map.
     * @param stx The starting horizontal tile index.
     * @param sty The starting vertical tile index.
     * @param stw The source location width in tile.
     * @param sth The source location height in tile.
     * @param dtx The ending horizontal tile index.
     * @param dty The ending vertical tile index.
     * @param dtw The destination location width in tile.
     * @param dth The destination location height in tile.
     * @param radius The search radius.
     * @return The closest tile found.
     */
    private CoordTile getClosestAvailableTile(Pathfindable mover,
                                              int stx,
                                              int sty,
                                              int stw,
                                              int sth,
                                              int dtx,
                                              int dty,
                                              int dtw,
                                              int dth,
                                              int radius)
    {
        int closestX = 0;
        int closestY = 0;
        double dist = Double.MAX_VALUE;
        int size = 1;
        boolean found = false;
        while (!found)
        {
            for (int tx = stx - size; tx <= stx + size; tx++)
            {
                for (int ty = sty - size; ty <= sty + size; ty++)
                {
                    if (isAreaAvailable(mover, tx, ty, stw, sth, null))
                    {
                        final double d = UtilMath.getDistance(tx, ty, stw, sth, dtx, dty, dtw, dth);
                        if (d < dist)
                        {
                            dist = d;
                            closestX = tx;
                            closestY = ty;
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
        for (int ty = 0; ty < map.getInTileHeight(); ty++)
        {
            for (int tx = 0; tx < map.getInTileWidth(); tx++)
            {
                final Tile tile = map.getTile(tx, ty);
                if (tile != null)
                {
                    final TilePath tilePath = new TilePathModel(tile);
                    tile.addFeature(tilePath);

                    final String group = tile.getGroup();
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
    public void removeObjectId(int tx, int ty, Integer id)
    {
        final Tile tile = map.getTile(tx, ty);
        if (tile != null)
        {
            final TilePath tilePath = tile.getFeature(TilePath.class);
            tilePath.removeObjectId(id);
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
        return map.getTile(tiled.getInTileX(), tiled.getInTileY());
    }

    @Override
    public boolean isBlocked(Pathfindable mover, int tx, int ty, boolean ignoreObjectsId)
    {
        // Blocked if outside map range
        if (ty >= 0 && tx >= 0 && ty < map.getInTileHeight() && tx < map.getInTileWidth())
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
                if (ignoredCount < ids.size())
                {
                    return true;
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
    public double getCost(Pathfindable mover, int tx, int ty)
    {
        final Tile tile = map.getTile(tx, ty);
        if (tile != null)
        {
            final TilePath tilePath = tile.getFeature(TilePath.class);
            return mover.getCost(tilePath.getCategory());
        }
        return 0.0;
    }

    @Override
    public CoordTile getFreeTileAround(Pathfindable mover, Tiled tiled, int radius)
    {
        return getFreeTileAround(mover,
                                 tiled.getInTileX(),
                                 tiled.getInTileY(),
                                 tiled.getInTileWidth(),
                                 tiled.getInTileHeight(),
                                 radius);
    }

    @Override
    public CoordTile getFreeTileAround(Pathfindable mover, int tx, int ty, int tw, int th, int radius)
    {
        int size = 0;
        boolean search = true;
        while (search)
        {
            for (int ctx = tx - size; ctx <= tx + size; ctx++)
            {
                for (int cty = ty - size; cty <= ty + size; cty++)
                {
                    if (isAreaAvailable(mover, ctx, cty, tw, th, null))
                    {
                        return new CoordTile(ctx, cty);
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
    public CoordTile getClosestAvailableTile(Pathfindable mover, Tiled to, int radius)
    {
        return getClosestAvailableTile(mover,
                                       mover.getInTileX(),
                                       mover.getInTileY(),
                                       mover.getInTileWidth(),
                                       mover.getInTileHeight(),
                                       to.getInTileX(),
                                       to.getInTileY(),
                                       to.getInTileWidth(),
                                       to.getInTileHeight(),
                                       radius);
    }

    @Override
    public CoordTile getClosestAvailableTile(Pathfindable mover, int stx, int sty, int dtx, int dty, int radius)
    {
        return getClosestAvailableTile(mover, stx, sty, 1, 1, dtx, dty, 1, 1, radius);
    }

    @Override
    public boolean isAreaAvailable(Pathfindable mover, int tx, int ty, int tw, int th, Integer ignoreObjectId)
    {
        for (int cty = ty; cty < ty + th; cty++)
        {
            for (int ctx = tx; ctx < tx + tw; ctx++)
            {
                final Collection<Integer> ids = getObjectsId(ctx, cty);
                final Tile tile = map.getTile(ctx, cty);
                if (tile != null)
                {
                    final TilePath tilePath = tile.getFeature(TilePath.class);
                    try
                    {
                        if (mover.isBlocking(tilePath.getCategory())
                            || ignoreObjectId != null && ids.size() > 0 && !ids.contains(ignoreObjectId))
                        {
                            return false;
                        }
                    }
                    catch (final LionEngineException exception)
                    {
                        return true;
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
