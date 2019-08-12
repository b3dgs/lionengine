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
package com.b3dgs.lionengine.game.feature.tile.map.transition.circuit.generator;

import com.b3dgs.lionengine.Check;
import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.game.feature.tile.TileRef;
import com.b3dgs.lionengine.game.feature.tile.map.MapTile;

/**
 * Describe with which tile map must be filled.
 */
public class PrefMapFill extends PrefAbstract
{
    /** The sheet value. */
    private final Integer sheet;
    /** The number value. */
    private final int number;

    /**
     * Create preference.
     * 
     * @param tile The tile to use.
     * @throws LionEngineException If invalid arguments.
     */
    public PrefMapFill(TileRef tile)
    {
        super(1);

        Check.notNull(tile);

        sheet = tile.getSheet();
        number = tile.getNumber();
    }

    /*
     * Preference
     */

    @Override
    public void apply(MapTile map)
    {
        for (int tx = 0; tx < map.getInTileWidth(); tx++)
        {
            for (int ty = 0; ty < map.getInTileHeight(); ty++)
            {
                map.setTile(tx, ty, sheet, number);
            }
        }
    }
}
