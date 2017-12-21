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
package com.b3dgs.lionengine.tutorials.mario.b;

import java.io.IOException;

import com.b3dgs.lionengine.Context;
import com.b3dgs.lionengine.game.feature.Services;
import com.b3dgs.lionengine.game.feature.WorldGame;
import com.b3dgs.lionengine.game.feature.tile.map.MapTile;
import com.b3dgs.lionengine.game.feature.tile.map.MapTileGame;
import com.b3dgs.lionengine.game.feature.tile.map.persister.MapTilePersister;
import com.b3dgs.lionengine.game.feature.tile.map.persister.MapTilePersisterModel;
import com.b3dgs.lionengine.game.feature.tile.map.viewer.MapTileViewerModel;
import com.b3dgs.lionengine.io.FileReading;
import com.b3dgs.lionengine.io.FileWriting;

/**
 * World implementation.
 */
class World extends WorldGame
{
    private final MapTile map = services.create(MapTileGame.class);
    private final MapTilePersister mapPersister = map.addFeatureAndGet(new MapTilePersisterModel(services));

    /**
     * Create world.
     * 
     * @param context The context reference.
     * @param services The services reference.
     */
    public World(Context context, Services services)
    {
        super(context, services);

        map.addFeature(new MapTileViewerModel(services));
        handler.add(map);
    }

    @Override
    protected void saving(FileWriting file) throws IOException
    {
        mapPersister.save(file);
    }

    @Override
    protected void loading(FileReading file) throws IOException
    {
        mapPersister.load(file);
        camera.setLimits(map);
    }
}
