/*
 * Copyright (C) 2013-2015 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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

import com.b3dgs.lionengine.Resolution;
import com.b3dgs.lionengine.core.Graphic;
import com.b3dgs.lionengine.core.Loader;
import com.b3dgs.lionengine.core.Media;
import com.b3dgs.lionengine.core.Medias;
import com.b3dgs.lionengine.core.Sequence;
import com.b3dgs.lionengine.core.Verbose;
import com.b3dgs.lionengine.core.awt.Keyboard;
import com.b3dgs.lionengine.game.map.MapTile;
import com.b3dgs.lionengine.game.map.MapTileGame;
import com.b3dgs.lionengine.stream.FileWriting;
import com.b3dgs.lionengine.stream.Stream;

/**
 * Game loop designed to handle our little world.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
class Scene
        extends Sequence
{
    /** Native resolution. */
    private static final Resolution NATIVE = new Resolution(320, 240, 60);
    /** Level file. */
    private static final Media LEVEL = Medias.create("level.lvl");

    /** Keyboard reference. */
    private final Keyboard keyboard;
    /** World reference. */
    private final World world;

    /**
     * Constructor.
     * 
     * @param loader The loader reference.
     */
    public Scene(Loader loader)
    {
        super(loader, Scene.NATIVE);
        keyboard = getInputDevice(Keyboard.class);
        world = new World(getConfig(), keyboard);
    }

    /**
     * Import and save the level.
     */
    private static void importAndSave()
    {
        final MapTile map = new MapTileGame(16, 16);
        map.create(Medias.create("level.png"), Medias.create("sheets.xml"), Medias.create("groups.xml"));
        try (FileWriting file = Stream.createFileWriting(LEVEL))
        {
            map.save(file);
        }
        catch (final IOException exception)
        {
            Verbose.exception(Scene.class, "importAndSave", exception, "Error on saving map !");
        }
    }

    @Override
    protected void load()
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
        if (keyboard.isPressedOnce(Keyboard.ESCAPE))
        {
            end();
        }
    }

    @Override
    public void render(Graphic g)
    {
        world.render(g);
    }
}
