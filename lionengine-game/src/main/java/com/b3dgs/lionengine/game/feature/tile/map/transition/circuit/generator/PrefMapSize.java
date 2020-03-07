/*
 * Copyright (C) 2013-2020 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
import com.b3dgs.lionengine.game.feature.tile.map.MapTile;

/**
 * Describe the map size preference for map generator.
 */
public class PrefMapSize extends PrefAbstract
{
    /** Tile width. */
    private final int tileWidth;
    /** Tile height. */
    private final int tileHeight;
    /** Width in tile. */
    private final int widthInTile;
    /** Height in tile. */
    private final int heightInTile;

    /**
     * Create preference.
     * 
     * @param tileWidth The tile width.
     * @param tileHeight The tile height.
     * @param widthInTile The map width in tile (must be strictly positive).
     * @param heightInTile The map height in tile (must be strictly positive).
     * @throws LionEngineException If size is invalid.
     */
    public PrefMapSize(int tileWidth, int tileHeight, int widthInTile, int heightInTile)
    {
        super(0);

        Check.superiorStrict(tileWidth, 0);
        Check.superiorStrict(tileHeight, 0);
        Check.superiorStrict(widthInTile, 0);
        Check.superiorStrict(heightInTile, 0);

        this.tileWidth = tileWidth;
        this.tileHeight = tileHeight;
        this.widthInTile = widthInTile;
        this.heightInTile = heightInTile;
    }

    /*
     * Preference
     */

    @Override
    public void apply(MapTile map)
    {
        map.create(tileWidth, tileHeight, widthInTile, heightInTile);
    }
}
