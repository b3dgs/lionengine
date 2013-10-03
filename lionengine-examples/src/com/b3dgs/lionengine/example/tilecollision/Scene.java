/*
 * Copyright (C) 2013 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.example.tilecollision;

import com.b3dgs.lionengine.Resolution;
import com.b3dgs.lionengine.core.Graphic;
import com.b3dgs.lionengine.core.Loader;
import com.b3dgs.lionengine.core.Media;
import com.b3dgs.lionengine.core.Sequence;
import com.b3dgs.lionengine.game.platform.CameraPlatform;
import com.b3dgs.lionengine.input.Keyboard;

/**
 * This is where the game loop is running.
 */
final class Scene
        extends Sequence
{
    /** Native resolution. */
    private static final Resolution NATIVE = new Resolution(320, 240, 60);

    /** Camera. */
    private final CameraPlatform camera;
    /** Map. */
    private final Map map;
    /** Entity. */
    private final Entity entityRef;
    /** Entity. */
    private final Entity entity;

    /**
     * Constructor.
     * 
     * @param loader The loader reference.
     */
    Scene(Loader loader)
    {
        super(loader, Scene.NATIVE);
        camera = new CameraPlatform(width, height);
        map = new Map();
        entityRef = new Entity(map);
        entity = new Entity(map);
        setExtrapolated(false);
    }

    /*
     * Sequence
     */

    @Override
    protected void load()
    {
        map.create(20, 15);
        map.loadPatterns(Media.get("tiles"));
        map.createBlock(5, 7);
        map.createBlock(5, 8);
        map.createBlock(5, 9);
        map.createBlock(6, 7);
        map.createBlock(7, 7);
        map.createBlock(6, 8);

        entityRef.setLocation(192, 112);
        camera.setLimits(map);
        camera.setView(0, 0, width, height);
    }

    @Override
    protected void update(double extrp)
    {
        entity.updateMouse(mouse);
        entity.update(extrp);
        // Terminate
        if (keyboard.isPressed(Keyboard.ESCAPE))
        {
            end();
        }
    }

    @Override
    protected void render(Graphic g)
    {
        clearScreen(g);
        map.render(g, camera);
        entity.render(g, camera);
        entityRef.render(g, camera);
    }
}
