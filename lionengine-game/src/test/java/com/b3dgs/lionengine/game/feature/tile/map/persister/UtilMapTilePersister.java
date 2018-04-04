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
package com.b3dgs.lionengine.game.feature.tile.map.persister;

import java.io.IOException;
import java.util.ArrayList;

import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.game.feature.Services;
import com.b3dgs.lionengine.game.feature.tile.Tile;
import com.b3dgs.lionengine.game.feature.tile.map.MapTile;
import com.b3dgs.lionengine.game.feature.tile.map.MapTileGame;
import com.b3dgs.lionengine.graphic.drawable.SpriteTiled;
import com.b3dgs.lionengine.io.FileReading;
import com.b3dgs.lionengine.io.FileWriting;

/**
 * Utilities related to map tile persister test.
 */
public class UtilMapTilePersister
{
    /**
     * Create a test map.
     * 
     * @return The created test map.
     */
    public static MapTile createMap()
    {
        final Services services = new Services();
        final MapTile map = services.create(MapTileGame.class);
        map.addFeature(new MapTilePersisterModel(services));
        map.create(16, 32, 3, 3);
        map.loadSheets(new ArrayList<SpriteTiled>());

        final Integer sheet = Integer.valueOf(0);
        for (int x = 0; x < map.getInTileWidth(); x++)
        {
            for (int y = 1; y < map.getInTileHeight(); y++)
            {
                final Tile tile = map.createTile(sheet,
                                                 x * y,
                                                 x * (double) map.getTileWidth(),
                                                 y * (double) map.getTileHeight());
                map.setTile(tile);
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
    public static MapTile loadMap(Media level) throws IOException
    {
        final Services services = new Services();
        final MapTile map = services.create(MapTileGame.class);
        map.addFeature(new MapTilePersisterModel(services));
        try (FileReading input = new FileReading(level))
        {
            map.getFeature(MapTilePersister.class).load(input);
        }
        return map;
    }
}
