/*
 * Copyright (C) 2013-2021 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.game.feature.tile.map.persister;

import java.io.IOException;
import java.util.ArrayList;

import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.game.feature.tile.map.MapTile;
import com.b3dgs.lionengine.game.feature.tile.map.MapTileGame;
import com.b3dgs.lionengine.graphic.drawable.SpriteTiled;
import com.b3dgs.lionengine.io.FileReading;
import com.b3dgs.lionengine.io.FileWriting;

/**
 * Utilities related to map tile persister test.
 */
final class UtilMapTilePersister
{
    /**
     * Create a test map.
     * 
     * @return The created test map.
     */
    public static MapTile createMap()
    {
        final MapTileGame map = new MapTileGame();
        map.addFeature(new MapTilePersisterModel());
        map.create(16, 32, 3, 3);
        map.loadSheets(new ArrayList<SpriteTiled>());

        for (int tx = 0; tx < map.getInTileWidth(); tx++)
        {
            for (int ty = 1; ty < map.getInTileHeight(); ty++)
            {
                map.setTile(tx, ty, tx * ty);
            }
        }
        return map;
    }

    /**
     * Save map to file.
     * 
     * @param map The map to save.
     * @param level The level media.
     * @throws IOException If error.
     */
    public static void saveMap(MapTile map, Media level) throws IOException
    {
        try (FileWriting output = new FileWriting(level))
        {
            map.getFeature(MapTilePersister.class).save(output);
        }
    }

    /**
     * Load map from file.
     * 
     * @param level The level media.
     * @return The loaded map.
     * @throws IOException If error.
     */
    public static MapTileGame loadMap(Media level) throws IOException
    {
        final MapTileGame map = new MapTileGame();
        map.addFeature(new MapTilePersisterModel());
        try (FileReading input = new FileReading(level))
        {
            map.getFeature(MapTilePersister.class).load(input);
        }
        return map;
    }
}
