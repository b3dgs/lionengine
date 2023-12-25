/*
 * Copyright (C) 2013-2023 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.game.feature.tile.map.transition.circuit.generator;

import com.b3dgs.lionengine.Check;
import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.UtilMath;
import com.b3dgs.lionengine.UtilRandom;
import com.b3dgs.lionengine.game.Tiled;
import com.b3dgs.lionengine.game.feature.tile.Tile;
import com.b3dgs.lionengine.game.feature.tile.TileGame;
import com.b3dgs.lionengine.game.feature.tile.map.MapTile;
import com.b3dgs.lionengine.game.feature.tile.map.transition.MapTileTransition;

/**
 * Describe a region, filled by a tile in a particular area for a specified amount of random.
 */
public class PrefMapRegion extends PrefAbstract
{
    /** The number value. */
    private final int number;
    /** The placement area. */
    private final Tiled area;
    /** Placement maximum size. */
    private final int maxSize;
    /** Placements count. */
    private final int count;

    /**
     * Create preference.
     * 
     * @param number The tile number to use.
     * @param area The area in tile where random placement occurs.
     * @param maxSize The placement maximum size in tile (must be strictly positive).
     * @param count The number of random placement (must be strictly positive).
     * @throws LionEngineException If invalid arguments.
     */
    public PrefMapRegion(int number, Tiled area, int maxSize, int count)
    {
        super(2);

        Check.superiorOrEqual(number, 0);
        Check.notNull(area);
        Check.superiorStrict(count, 0);
        Check.superiorStrict(maxSize, 0);

        this.number = number;
        this.area = area;
        this.maxSize = maxSize;
        this.count = count;
    }

    @Override
    public void apply(MapTile map)
    {
        final MapTileTransition mapTransition = map.getFeature(MapTileTransition.class);
        final int sx = area.getInTileX();
        final int sy = area.getInTileY();
        final int ex = UtilMath.clamp(area.getInTileWidth(), 0, map.getInTileWidth() - 1);
        final int ey = UtilMath.clamp(area.getInTileHeight(), 0, map.getInTileHeight() - 1);

        int remaining = count;
        while (remaining > 0)
        {
            final int tx = UtilRandom.getRandomInteger(sx, ex);
            final int ty = UtilRandom.getRandomInteger(sy, ey);

            final int size = UtilRandom.getRandomInteger(maxSize);
            final int halfBottom = (int) Math.floor(size / 2.0);
            final int halfTop = (int) Math.ceil(size / 2.0);

            for (int ox = -halfBottom; ox < halfTop; ox++)
            {
                for (int oy = -halfBottom; oy < halfTop; oy++)
                {
                    final int ntx = UtilMath.clamp(tx + ox, sx, ex);
                    final int nty = UtilMath.clamp(ty + oy, sy, ey);
                    final Tile tile = new TileGame(number, ntx, nty, map.getTileWidth(), map.getTileHeight());
                    map.setTile(tile.getInTileX(), tile.getInTileY(), number);
                    mapTransition.resolve(tile);
                }
            }

            remaining--;
        }
    }
}
