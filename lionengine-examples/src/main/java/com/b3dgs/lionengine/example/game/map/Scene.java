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
import com.b3dgs.lionengine.core.Keyboard;
import com.b3dgs.lionengine.core.Loader;
import com.b3dgs.lionengine.core.Sequence;
import com.b3dgs.lionengine.game.CameraGame;
import com.b3dgs.lionengine.utility.LevelRipConverter;

/**
 * Game loop designed to handle our world.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 * @see com.b3dgs.lionengine.example.core.minimal
 */
final class Scene
        extends Sequence
{
    /** Native resolution. */
    private static final Resolution NATIVE = new Resolution(320, 240, 60);

    /** Keyboard reference. */
    private final Keyboard keyboard;
    /** Camera. */
    private final CameraGame camera;
    /** Map. */
    private final Map map;
    /** Offset x. */
    private double x;
    /** Side. */
    private double side;

    /**
     * Constructor.
     * 
     * @param loader The loader reference.
     */
    Scene(Loader loader)
    {
        super(loader, Scene.NATIVE);
        keyboard = getInputDevice(Keyboard.class);
        camera = new CameraGame();
        map = new Map();
    }

    /*
     * Sequence
     */

    @Override
    protected void load()
    {
        final LevelRipConverter<Tile> rip = new LevelRipConverter<>();
        rip.start(Core.MEDIA.create("level.png"), Core.MEDIA.create("tiles"), map);
        camera.setView(0, 0, getWidth(), getHeight());
        side = 3;
    }

    @Override
    protected void update(double extrp)
    {
        if (keyboard.isPressed(Keyboard.ESCAPE))
        {
            end();
        }

        x += side * extrp;
        final int size = map.getWidthInTile() * map.getTileWidth() - camera.getViewWidth();
        if (x > size)
        {
            x = size;
            side *= -1;
        }
        if (x < 0)
        {
            x = 0;
            side *= -1;
        }
        camera.setLocationX(x);
    }

    @Override
    protected void render(Graphic g)
    {
        g.clear(0, 0, getWidth(), getHeight());
        map.render(g, camera);
    }
}
