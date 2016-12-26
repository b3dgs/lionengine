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

import com.b3dgs.lionengine.Context;
import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.Resolution;
import com.b3dgs.lionengine.Verbose;
import com.b3dgs.lionengine.core.Medias;
import com.b3dgs.lionengine.core.sequence.Sequence;
import com.b3dgs.lionengine.game.Services;
import com.b3dgs.lionengine.game.feature.tile.map.MapTile;
import com.b3dgs.lionengine.game.feature.tile.map.MapTileGame;
import com.b3dgs.lionengine.game.feature.tile.map.persister.MapTilePersister;
import com.b3dgs.lionengine.game.feature.tile.map.persister.MapTilePersisterModel;
import com.b3dgs.lionengine.graphic.Graphic;
import com.b3dgs.lionengine.io.FileWriting;
import com.b3dgs.lionengine.io.Keyboard;

/**
 * Game loop designed to handle our little world.
 */
class Scene extends Sequence
{
    /** Native resolution. */
    private static final Resolution NATIVE = new Resolution(320, 240, 60);
    /** Level file. */
    private static final Media LEVEL = Medias.create("map", "level.lvl");

    /**
     * Import and save the level.
     */
    private static void importAndSave()
    {
        final Services services = new Services();
        final MapTile map = services.create(MapTileGame.class);
        map.create(Medias.create("map", "level.png"));
        final MapTilePersister mapPersister = map.addFeatureAndGet(new MapTilePersisterModel());
        map.prepareFeatures(services);
        try (FileWriting output = new FileWriting(LEVEL))
        {
            mapPersister.save(output);
        }
        catch (final IOException exception)
        {
            Verbose.exception(exception, "Error on saving map !");
        }
    }

    /** World reference. */
    private final World world;

    /**
     * Constructor.
     * 
     * @param context The context reference.
     */
    public Scene(Context context)
    {
        super(context, NATIVE);
        world = new World(context);
        getInputDevice(Keyboard.class).addActionPressed(Keyboard.ESCAPE, () -> end());
    }

    @Override
    public void load()
    {
        if (!LEVEL.exists())
        {
            importAndSave();
        }
        world.loadFromFile(LEVEL);
    }

    @Override
    public void update(double extrp)
    {
        world.update(extrp);
    }

    @Override
    public void render(Graphic g)
    {
        world.render(g);
    }
}
