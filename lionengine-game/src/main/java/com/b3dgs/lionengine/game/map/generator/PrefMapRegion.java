/*
 * Copyright (C) 2013-2016 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.game.map.generator;

import com.b3dgs.lionengine.Check;
import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.game.map.MapTile;
import com.b3dgs.lionengine.game.map.transition.MapTileTransition;
import com.b3dgs.lionengine.game.tile.Tile;
import com.b3dgs.lionengine.game.tile.TileRef;
import com.b3dgs.lionengine.game.tile.Tiled;
import com.b3dgs.lionengine.util.UtilMath;
import com.b3dgs.lionengine.util.UtilRandom;

/**
 * Describe a region, filled by a tile in a particular area for a specified amount of random.
 */
public class PrefMapRegion extends PrefBase
{
    /** The sheet value. */
    private final Integer sheet;
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
     * @param tile The tile to use.
     * @param area The area in tile where random placement occurs.
     * @param maxSize The placement maximum size in tile (must be strictly positive).
     * @param count The number of random placement (must be strictly positive).
     * @throws LionEngineException If invalid arguments.
     */
    public PrefMapRegion(TileRef tile, Tiled area, int maxSize, int count)
    {
        super(2);

        Check.notNull(tile);
        Check.notNull(area);
        Check.superiorStrict(count, 0);
        Check.superiorStrict(maxSize, 0);

        sheet = tile.getSheet();
        number = tile.getNumber();
        this.area = area;
        this.maxSize = maxSize;
        this.count = count;
    }

    /*
     * Preference
     */

    @Override
    public void apply(MapTile map)
    {
        final MapTileTransition mapTransition = map.getFeature(MapTileTransition.class);
        final double tw = map.getTileWidth();
        final double th = map.getTileHeight();
        final int sx = area.getInTileX();
        final int sy = area.getInTileY();
        final int ex = UtilMath.clamp(area.getInTileWidth(), 0, map.getInTileWidth() - 1);
        final int ey = UtilMath.clamp(area.getInTileHeight(), 0, map.getInTileHeight() - 1);

        int remaining = count;
        while (remaining > 0)
        {
            final double tx = UtilRandom.getRandomInteger(sx, ex);
            final double ty = UtilRandom.getRandomInteger(sy, ey);

            final int size = UtilRandom.getRandomInteger(maxSize);
            final int halfBottom = (int) Math.floor(size / 2.0);
            final int halfTop = (int) Math.ceil(size / 2.0);
            for (int ox = -halfBottom; ox < halfTop; ox++)
            {
                for (int oy = -halfBottom; oy < halfTop; oy++)
                {
                    final double x = UtilMath.clamp(tx + ox, sx, ex) * tw;
                    final double y = UtilMath.clamp(ty + oy, sy, ey) * th;
                    final Tile tile = map.createTile(sheet, number, x, y);
                    map.setTile(tile);
                    mapTransition.resolve(tile);
                }
            }

            remaining--;
        }
    }
}
