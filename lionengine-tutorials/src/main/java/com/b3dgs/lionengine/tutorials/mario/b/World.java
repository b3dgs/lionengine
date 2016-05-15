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
package com.b3dgs.lionengine.tutorials.mario.b;

import java.io.IOException;

import com.b3dgs.lionengine.core.Config;
import com.b3dgs.lionengine.game.Services;
import com.b3dgs.lionengine.game.WorldGame;
import com.b3dgs.lionengine.game.camera.Camera;
import com.b3dgs.lionengine.game.map.MapTile;
import com.b3dgs.lionengine.game.map.MapTileGame;
import com.b3dgs.lionengine.game.map.feature.persister.MapTilePersister;
import com.b3dgs.lionengine.game.map.feature.persister.MapTilePersisterModel;
import com.b3dgs.lionengine.game.map.feature.viewer.MapTileViewer;
import com.b3dgs.lionengine.game.map.feature.viewer.MapTileViewerModel;
import com.b3dgs.lionengine.graphic.Graphic;
import com.b3dgs.lionengine.stream.FileReading;
import com.b3dgs.lionengine.stream.FileWriting;

/**
 * World implementation.
 */
class World extends WorldGame
{
    /** Services reference. */
    private final Services services = new Services();
    /** Camera reference. */
    private final Camera camera = services.create(Camera.class);
    /** Map reference. */
    private final MapTile map = services.create(MapTileGame.class);
    /** Map persister. */
    private final MapTilePersister mapPersister = map.createFeature(MapTilePersisterModel.class);
    /** Map viewer. */
    private final MapTileViewer mapViewer = map.createFeature(MapTileViewerModel.class);

    /**
     * Constructor.
     * 
     * @param config The config reference.
     */
    public World(Config config)
    {
        super(config);
    }

    @Override
    public void update(double extrp)
    {
        // Update
    }

    @Override
    public void render(Graphic g)
    {
        mapViewer.render(g);
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
        camera.setView(0, 0, width, height);
        camera.setLimits(map);
    }
}
