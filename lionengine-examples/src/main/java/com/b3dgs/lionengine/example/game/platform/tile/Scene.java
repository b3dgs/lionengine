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
package com.b3dgs.lionengine.example.game.platform.tile;

import com.b3dgs.lionengine.Graphic;
import com.b3dgs.lionengine.Resolution;
import com.b3dgs.lionengine.core.Keyboard;
import com.b3dgs.lionengine.core.Loader;
import com.b3dgs.lionengine.core.Mouse;
import com.b3dgs.lionengine.core.Sequence;
import com.b3dgs.lionengine.core.UtilityMedia;
import com.b3dgs.lionengine.game.platform.CameraPlatform;

/**
 * This is where the game loop is running.
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
    /** Mouse reference. */
    private final Mouse mouse;
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
        keyboard = getInputDevice(Keyboard.class);
        mouse = getInputDevice(Mouse.class);
        mouse.setConfig(getConfig());
        camera = new CameraPlatform(getWidth(), getHeight());
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
        map.loadPatterns(UtilityMedia.get("tiles"));
        map.createBlock(5, 7);
        map.createBlock(5, 8);
        map.createBlock(5, 9);
        map.createBlock(6, 7);
        map.createBlock(7, 7);
        map.createBlock(6, 8);
        map.loadCollisions(UtilityMedia.get("tiles", "collisions.xml"));

        entityRef.setLocation(192, 112);
        camera.setLimits(map);
        camera.setView(0, 0, getWidth(), getHeight());
        map.createCollisionDraw(TileCollision.class);
    }

    @Override
    protected void update(double extrp)
    {
        mouse.update();
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
        g.clear(0, 0, getWidth(), getHeight());
        map.render(g, camera);
        entity.render(g, camera);
        entityRef.render(g, camera);
    }
}
