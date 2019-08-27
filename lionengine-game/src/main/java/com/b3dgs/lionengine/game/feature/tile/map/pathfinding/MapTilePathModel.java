/*
 * Copyright (C) 2013-2019 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.game.feature.tile.map.pathfinding;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.b3dgs.lionengine.Check;
import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.UtilMath;
import com.b3dgs.lionengine.game.Tiled;
import com.b3dgs.lionengine.game.feature.FeatureModel;
import com.b3dgs.lionengine.game.feature.Identifiable;
import com.b3dgs.lionengine.game.feature.Services;
import com.b3dgs.lionengine.game.feature.tile.Tile;
import com.b3dgs.lionengine.game.feature.tile.map.MapTile;
import com.b3dgs.lionengine.game.feature.tile.map.MapTileGroup;
import com.b3dgs.lionengine.game.feature.tile.map.MapTileGroupModel;
import com.b3dgs.lionengine.geom.Area;

/**
 * Map tile path model implementation.
 */
public class MapTilePathModel extends FeatureModel implements MapTilePath
{
    /** Categories list. */
    private final Map<String, PathCategory> categories = new HashMap<>();
    /** Map reference. */
    private final MapTile map;
    /** Map group reference. */
    private final MapTileGroup mapGroup;

    /**
     * Create feature.
     * <p>
     * The {@link Services} must provide the following services:
     * </p>
     * <ul>
     * <li>{@link MapTile}</li>
     * </ul>
     * <p>
     * The {@link MapTile} must provide the following features:
     * </p>
     * <ul>
     * <li>{@link MapTileGroup}</li>
     * </ul>
     * 
     * @param services The services reference (must not be <code>null</code>).
     * @throws LionEngineException If invalid argument.
     */
    public MapTilePathModel(Services services)
    {
        super();

        Check.notNull(services);

        map = services.get(MapTile.class);
        mapGroup = map.getFeature(MapTileGroupModel.class);
    }

    /**
     * Check if area if used.
     * 
     * @param mover The object moving on map.
     * @param ctx The horizontal tile index.
     * @param cty The vertical tile index.
     * @param ignoreObjectId The object ID to ignore.
     * @return <code>true</code> if area is used, <code>false</code> else.
     */
    private boolean isTileNotAvailable(Pathfindable mover, int ctx, int cty, Integer ignoreObjectId)
    {
        final Collection<Integer> ids = getObjectsId(ctx, cty);
        final Tile tile = map.getTile(ctx, cty);
        if (tile != null)
        {
            final TilePath tilePath = tile.getFeature(TilePath.class);
            if (mover.isBlocking(tilePath.getCategory())
                || !ids.isEmpty() && (ignoreObjectId == null || !ids.contains(ignoreObjectId)))
            {
                return true;
            }
        }
        return false;
    }

    /**
     * Check if all objects id are non blocking.
     * 
     * @param mover The mover reference.
     * @param tx The horizontal tile to check.
     * @param ty The vertical tile to check.
     * @return <code>true</code> if blocked, <code>false</code> else.
     */
    private boolean isBlocked(Pathfindable mover, int tx, int ty)
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
        return ignoredCount < ids.size();
    }

    /**
     * Check if tile is blocking.
     * 
     * @param mover The mover reference.
     * @param tx The horizontal tile to check.
     * @param ty The vertical tile to check.
     * @return <code>true</code> if blocked, <code>false</code> else.
     */
    private boolean isTileBlocked(Pathfindable mover, int tx, int ty)
    {
        final Tile tile = map.getTile(tx, ty);
        if (tile != null)
        {
            final TilePath tilePath = tile.getFeature(TilePath.class);
            return mover.isBlocking(tilePath.getCategory());
        }
        return false;
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

    /**
     * Search a free area from this location.
     * 
     * @param mover The object moving on map.
     * @param tx The horizontal tile index.
     * @param ty The vertical tile index.
     * @param tw The width in tile.
     * @param th The height in tile.
     * @param radius The search radius.
     * @param id The mover id.
     * @return The free tile found (<code>null</code> if none).
     */
    // CHECKSTYLE IGNORE LINE: ReturnCount
    private CoordTile getFreeTileAround(Pathfindable mover, int tx, int ty, int tw, int th, int radius, Integer id)
    {
        final int w = mover.getInTileWidth();
        final int h = mover.getInTileHeight();

        for (int y = ty + th + radius; y > ty - h - radius - 1; y--)
        {
            if (isAreaAvailable(mover, tx - w - radius, y, w, h, id))
            {
                return new CoordTile(tx - w - radius, y);
            }
        }
        for (int x = tx - w - radius; x < tx + tw + radius + 1; x++)
        {
            if (isAreaAvailable(mover, x, ty - h - radius, w, h, id))
            {
                return new CoordTile(x, ty - h - radius);
            }
        }
        for (int y = ty - h - radius + 1; y < ty + th + radius + 1; y++)
        {
            if (isAreaAvailable(mover, tx + tw + radius, y, w, h, id))
            {
                return new CoordTile(tx + tw + radius, y);
            }
        }
        for (int x = tx + tw + radius - 1; x > tx - tw - radius + 2; x--)
        {
            if (isAreaAvailable(mover, x, ty + th + radius, w, h, id))
            {
                return new CoordTile(x, ty + th + radius);
            }
        }
        return null;
    }

    /**
     * Get the group category.
     * 
     * @param group The group name.
     * @return The category name (<code>null</code> if undefined).
     */
    private String getCategory(String group)
    {
        for (final PathCategory category : categories.values())
        {
            if (category.getGroups().contains(group))
            {
                return category.getName();
            }
        }
        return null;
    }

    /*
     * MapTilePath
     */

    @Override
    public void loadPathfinding(Media pathfindingConfig)
    {
        final Collection<PathCategory> config = PathfindingConfig.imports(pathfindingConfig);
        categories.clear();
        for (final PathCategory category : config)
        {
            categories.put(category.getName(), category);
        }
        for (int ty = 0; ty < map.getInTileHeight(); ty++)
        {
            for (int tx = 0; tx < map.getInTileWidth(); tx++)
            {
                final Tile tile = map.getTile(tx, ty);
                if (tile != null)
                {
                    loadTile(tile);
                }
            }
        }
    }

    @Override
    public void loadTile(Tile tile)
    {
        final String group = mapGroup.getGroup(tile);
        final String category = getCategory(group);
        final TilePath tilePath = new TilePathModel(category);
        tile.addFeature(tilePath);
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
        final boolean inside = ty >= 0 && tx >= 0 && ty < map.getInTileHeight() && tx < map.getInTileWidth();
        return !inside || !ignoreObjectsId && isBlocked(mover, tx, ty) || isTileBlocked(mover, tx, ty);
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
    public Collection<String> getCategories()
    {
        return categories.keySet();
    }

    @Override
    public CoordTile getFreeTileAround(Pathfindable mover, Tiled tiled)
    {
        return getFreeTileAround(mover,
                                 tiled.getInTileX(),
                                 tiled.getInTileY(),
                                 tiled.getInTileWidth(),
                                 tiled.getInTileHeight(),
                                 map.getInTileRadius());
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
        final Integer id = mover.getFeature(Identifiable.class).getId();
        int size = 0;
        while (size <= radius)
        {
            final CoordTile tile = getFreeTileAround(mover, tx, ty, tw, th, size, id);
            if (tile != null)
            {
                return tile;
            }
            size++;
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
    public boolean isAreaAvailable(Area area, Pathfindable mover)
    {
        final int tx = map.getInTileX(area);
        final int ty = map.getInTileY(area);
        final int tw = area.getWidth() / map.getTileWidth();
        final int th = area.getHeight() / map.getTileHeight();
        final Integer id = mover.getFeature(Identifiable.class).getId();

        for (int cty = ty; cty < ty + th; cty++)
        {
            for (int ctx = tx; ctx < tx + tw; ctx++)
            {
                if (isTileNotAvailable(mover, ctx, cty, id))
                {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public boolean isAreaAvailable(Pathfindable mover, int tx, int ty, int tw, int th, Integer ignoreObjectId)
    {
        for (int cty = ty; cty < ty + th; cty++)
        {
            for (int ctx = tx; ctx < tx + tw; ctx++)
            {
                if (isTileNotAvailable(mover, ctx, cty, ignoreObjectId))
                {
                    return false;
                }
            }
        }
        return true;
    }
}
