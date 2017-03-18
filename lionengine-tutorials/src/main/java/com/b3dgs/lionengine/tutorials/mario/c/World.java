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
package com.b3dgs.lionengine.tutorials.mario.c;

import java.io.IOException;

import com.b3dgs.lionengine.Context;
import com.b3dgs.lionengine.core.Medias;
import com.b3dgs.lionengine.game.Services;
import com.b3dgs.lionengine.game.feature.CameraTracker;
import com.b3dgs.lionengine.game.feature.WorldGame;
import com.b3dgs.lionengine.game.feature.tile.map.MapTile;
import com.b3dgs.lionengine.game.feature.tile.map.MapTileGame;
import com.b3dgs.lionengine.game.feature.tile.map.MapTileGroup;
import com.b3dgs.lionengine.game.feature.tile.map.MapTileGroupModel;
import com.b3dgs.lionengine.game.feature.tile.map.collision.MapTileCollision;
import com.b3dgs.lionengine.game.feature.tile.map.collision.MapTileCollisionModel;
import com.b3dgs.lionengine.game.feature.tile.map.persister.MapTilePersister;
import com.b3dgs.lionengine.game.feature.tile.map.persister.MapTilePersisterModel;
import com.b3dgs.lionengine.game.feature.tile.map.viewer.MapTileViewerModel;
import com.b3dgs.lionengine.graphic.ColorRgba;
import com.b3dgs.lionengine.graphic.Graphic;
import com.b3dgs.lionengine.io.FileReading;
import com.b3dgs.lionengine.io.FileWriting;
import com.b3dgs.lionengine.io.awt.Keyboard;

/**
 * World implementation.
 */
class World extends WorldGame
{
    private static final ColorRgba BACKGROUND_COLOR = new ColorRgba(107, 136, 255);

    private final MapTile map = services.create(MapTileGame.class);

    /**
     * Constructor.
     * 
     * @param context The config reference.
     * @param services The services reference.
     */
    public World(Context context, Services services)
    {
        super(context, services);

        services.add(getInputDevice(Keyboard.class));

        camera.setIntervals(16, 0);

        map.addFeature(new MapTilePersisterModel(services));
        map.addFeature(new MapTileViewerModel(services));
        map.addFeature(new MapTileGroupModel());
        map.addFeature(new MapTileCollisionModel(services));
    }

    @Override
    public void render(Graphic g)
    {
        fill(g, BACKGROUND_COLOR);
        super.render(g);
    }

    @Override
    protected void saving(FileWriting file) throws IOException
    {
        map.getFeature(MapTilePersister.class).save(file);
    }

    @Override
    protected void loading(FileReading file) throws IOException
    {
        handler.add(map);

        map.getFeature(MapTilePersister.class).load(file);
        map.getFeature(MapTileGroup.class).loadGroups(Medias.create("map", "groups.xml"));
        map.getFeature(MapTileCollision.class).loadCollisions(Medias.create("map", "formulas.xml"),
                                                              Medias.create("map", "collisions.xml"));

        final Mario mario = factory.create(Mario.MEDIA);
        handler.add(mario);

        final CameraTracker tracker = new CameraTracker(services);
        camera.setLimits(map);
        tracker.track(mario);
        handler.add(tracker);
    }
}
