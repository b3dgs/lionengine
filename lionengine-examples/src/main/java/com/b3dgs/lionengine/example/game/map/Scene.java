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
package com.b3dgs.lionengine.example.game.map;

import com.b3dgs.lionengine.Resolution;
import com.b3dgs.lionengine.core.Core;
import com.b3dgs.lionengine.core.Graphic;
import com.b3dgs.lionengine.core.Loader;
import com.b3dgs.lionengine.core.Sequence;
import com.b3dgs.lionengine.core.awt.Engine;
import com.b3dgs.lionengine.core.awt.Keyboard;
import com.b3dgs.lionengine.game.Camera;
import com.b3dgs.lionengine.game.map.TileGame;
import com.b3dgs.lionengine.game.utility.LevelRipConverter;

/**
 * Game loop designed to handle our world.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 * @see com.b3dgs.lionengine.example.core._1_minimal
 */
class Scene
        extends Sequence
{
    /** Native resolution. */
    private static final Resolution NATIVE = new Resolution(320, 240, 60);

    /** Map. */
    private final Map map;
    /** Camera. */
    private final Camera camera;
    /** Keyboard reference. */
    private final Keyboard keyboard;
    /** Speed. */
    private double speed;
    /** Map size. */
    private int size;

    /**
     * Constructor.
     * 
     * @param loader The loader reference.
     */
    public Scene(Loader loader)
    {
        super(loader, Scene.NATIVE);
        map = new Map();
        camera = new Camera();
        keyboard = getInputDevice(Keyboard.class);
    }

    @Override
    public void load()
    {
        final LevelRipConverter<TileGame> rip = new LevelRipConverter<>(Core.MEDIA.create("level.png"),
                Core.MEDIA.create("tile"), map);
        rip.start();
        camera.setView(0, 0, getWidth(), getHeight());
        camera.setLimits(map);
        size = map.getWidthInTile() * map.getTileWidth() - camera.getWidth();
        speed = 3;
    }

    @Override
    public void update(double extrp)
    {
        if (keyboard.isPressed(Keyboard.ESCAPE))
        {
            end();
        }
        if (camera.getX() > size)
        {
            camera.setLocation(size, 0);
            speed *= -1;
        }
        if (camera.getX() < 0)
        {
            camera.setLocation(0, 0);
            speed *= -1;
        }
        camera.moveLocation(extrp, speed, 0.0);
    }

    @Override
    public void render(Graphic g)
    {
        g.clear(0, 0, getWidth(), getHeight());
        map.render(g, camera);
    }

    @Override
    protected void onTerminate(boolean hasNextSequence)
    {
        Engine.terminate();
    }
}
