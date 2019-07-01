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
package com.b3dgs.lionengine.game.feature.tile.map;

import java.util.Collection;

import com.b3dgs.lionengine.Check;
import com.b3dgs.lionengine.Constant;
import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.UtilRandom;
import com.b3dgs.lionengine.game.feature.FeatureModel;
import com.b3dgs.lionengine.game.feature.Services;
import com.b3dgs.lionengine.game.feature.tile.Tile;

/**
 * Handle map tile append.
 */
public class MapTileAppenderModel extends FeatureModel implements MapTileAppender
{
    /** Invalid map tile size. */
    static final String ERROR_APPEND_MAP_TILE_SIZE = "Appended map does not have the same tile size: ";

    /** Map reference. */
    private final MapTileGame map;

    /**
     * Create feature.
     * <p>
     * The {@link Services} must provide the following services:
     * </p>
     * <ul>
     * <li>{@link MapTileGame}</li>
     * </ul>
     * 
     * @param services The services reference (must not be <code>null</code>).
     * @throws LionEngineException If invalid argument.
     */
    public MapTileAppenderModel(Services services)
    {
        super();

        Check.notNull(services);

        map = services.get(MapTileGame.class);
    }

    /**
     * Append the map at specified offset.
     * 
     * @param other The map to append.
     * @param offsetX The horizontal offset.
     * @param offsetY The vertical offset.
     */
    private void appendMap(MapTile other, int offsetX, int offsetY)
    {
        for (int v = 0; v < other.getInTileHeight(); v++)
        {
            final int ty = offsetY + v;
            for (int h = 0; h < other.getInTileWidth(); h++)
            {
                final int tx = offsetX + h;
                final Tile tile = other.getTile(h, v);
                if (tile != null)
                {
                    final double x = tx * (double) map.getTileWidth();
                    final double y = ty * (double) map.getTileHeight();
                    map.setTile(map.createTile(tile.getSheet(), tile.getNumber(), x, y));
                }
            }
        }
    }

    /*
     * MapTileAppender
     */

    @Override
    public void append(MapTile other, int offsetX, int offsetY)
    {
        Check.notNull(other);

        if (!map.isCreated())
        {
            map.create(other.getTileWidth(), other.getTileHeight(), 1, 1);
        }

        if (other.getTileWidth() != map.getTileWidth() || other.getTileHeight() != map.getTileHeight())
        {
            throw new LionEngineException(ERROR_APPEND_MAP_TILE_SIZE
                                          + other.getTileWidth()
                                          + Constant.SPACE
                                          + map.getTileHeight());
        }

        final int widthInTile = map.getInTileWidth();
        final int heightInTile = map.getInTileHeight();
        final int newWidth = Math.max(widthInTile, widthInTile - (widthInTile - offsetX) + other.getInTileWidth());
        final int newHeight = Math.max(heightInTile, heightInTile - (heightInTile - offsetY) + other.getInTileHeight());
        map.resize(newWidth, newHeight);

        appendMap(other, offsetX, offsetY);
    }

    @Override
    public void append(Collection<MapTile> maps, int offsetX, int offsetY, int randX, int randY)
    {
        int newWidth = map.getInTileWidth();
        int newHeight = map.getInTileHeight();
        final int[] randsX = new int[maps.size()];
        final int[] randsY = new int[randsX.length];
        int i = 0;
        int tw = 0;
        int th = 0;
        for (final MapTile current : maps)
        {
            randsX[i] = UtilRandom.getRandomInteger(randX);
            randsY[i] = UtilRandom.getRandomInteger(randY);

            newWidth += current.getInTileWidth() + randsX[i];
            newHeight += current.getInTileHeight() + randsY[i];

            if (tw == 0)
            {
                tw = current.getTileWidth();
                th = current.getTileHeight();
            }
            else if (tw != current.getTileWidth() || th != current.getTileHeight())
            {
                throw new LionEngineException(ERROR_APPEND_MAP_TILE_SIZE
                                              + current.getTileWidth()
                                              + Constant.SPACE
                                              + current.getTileHeight());
            }

            i++;
        }
        if (!map.isCreated())
        {
            map.create(tw, th, 1, 1);
        }
        map.resize(newWidth, newHeight);

        int ox = 0;
        int oy = 0;
        i = 0;
        for (final MapTile current : maps)
        {
            appendMap(current, ox, oy);
            ox += current.getInTileWidth() * offsetX + randsX[i];
            oy += current.getInTileHeight() * offsetY + randsY[i];
            i++;
        }
    }
}
