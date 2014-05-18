/*
 * Copyright (C) 2013-2014 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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

import com.b3dgs.lionengine.Resolution;
import com.b3dgs.lionengine.core.Core;
import com.b3dgs.lionengine.core.Graphic;
import com.b3dgs.lionengine.core.Keyboard;
import com.b3dgs.lionengine.core.Loader;
import com.b3dgs.lionengine.core.Sequence;
import com.b3dgs.lionengine.core.Verbose;
import com.b3dgs.lionengine.file.File;
import com.b3dgs.lionengine.file.FileReading;
import com.b3dgs.lionengine.file.FileWriting;
import com.b3dgs.lionengine.game.platform.CameraPlatform;
import com.b3dgs.lionengine.utility.LevelRipConverter;

/**
 * Game loop designed to handle our little world.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
final class Scene
        extends Sequence
{
    /** Native resolution. */
    private static final Resolution NATIVE = new Resolution(320, 240, 60);

    /** Keyboard reference. */
    private final Keyboard keyboard;
    /** Camera reference. */
    private final CameraPlatform camera;
    /** Map reference. */
    private final Map map;

    /**
     * Constructor.
     * 
     * @param loader The loader reference.
     */
    Scene(Loader loader)
    {
        super(loader, Scene.NATIVE);
        keyboard = getInputDevice(Keyboard.class);
        camera = new CameraPlatform(getWidth(), getHeight());
        map = new Map();
    }

    /**
     * Import and save the level.
     */
    private void importAndSave()
    {
        final LevelRipConverter<Tile> rip = new LevelRipConverter<>();
        rip.start(Core.MEDIA.create("level.png"), Core.MEDIA.create("tile"), map);
        try (FileWriting file = File.createFileWriting(Core.MEDIA.create("level.lvl"));)
        {
            map.save(file);
        }
        catch (final IOException exception)
        {
            Verbose.exception(Scene.class, "constructor", exception, "Error on saving map !");
        }
    }

    /*
     * Sequence
     */

    @Override
    protected void load()
    {
        importAndSave();
        try (FileReading reading = File.createFileReading(Core.MEDIA.create("level.lvl"));)
        {
            map.load(reading);
        }
        catch (final IOException exception)
        {
            Verbose.exception(Scene.class, "constructor", exception, "Error on loading map !");
        }
        camera.setLimits(map);
    }

    @Override
    protected void update(double extrp)
    {
        if (keyboard.isPressedOnce(Keyboard.ESCAPE))
        {
            end();
        }
    }

    @Override
    protected void render(Graphic g)
    {
        map.render(g, camera);
    }
}
