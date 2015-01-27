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
package com.b3dgs.lionengine.example.game.collision_tile;

import com.b3dgs.lionengine.ColorRgba;
import com.b3dgs.lionengine.Resolution;
import com.b3dgs.lionengine.core.Core;
import com.b3dgs.lionengine.core.Graphic;
import com.b3dgs.lionengine.core.Loader;
import com.b3dgs.lionengine.core.Sequence;
import com.b3dgs.lionengine.core.awt.Engine;
import com.b3dgs.lionengine.core.awt.Keyboard;
import com.b3dgs.lionengine.core.awt.Mouse;
import com.b3dgs.lionengine.game.Camera;
import com.b3dgs.lionengine.game.Services;
import com.b3dgs.lionengine.game.map.MapTile;

/**
 * This is where the game loop is running.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 * @see com.b3dgs.lionengine.example.core.minimal
 */
class Scene
        extends Sequence
{
    /** Native resolution. */
    private static final Resolution NATIVE = new Resolution(320, 240, 60);

    /** Keyboard reference. */
    private final Keyboard keyboard;
    /** Mouse reference. */
    private final Mouse mouse;
    /** Camera. */
    private final Camera camera;
    /** Map. */
    private final Map map;
    /** Entity. */
    private Entity entityRef;
    /** Entity. */
    private Entity entity;

    /**
     * Constructor.
     * 
     * @param loader The loader reference.
     */
    public Scene(Loader loader)
    {
        super(loader, Scene.NATIVE);
        keyboard = getInputDevice(Keyboard.class);
        mouse = getInputDevice(Mouse.class);
        mouse.setConfig(getConfig());
        camera = new Camera();
        map = new Map();

        setExtrapolated(false);
    }

    /*
     * Sequence
     */

    @Override
    public void load()
    {
        map.create(20, 15);
        map.loadPatterns(Core.MEDIA.create("tile"));
        map.createBlock(5, 7);
        map.createBlock(5, 8);
        map.createBlock(5, 9);
        map.createBlock(6, 7);
        map.createBlock(7, 7);
        map.createBlock(6, 8);
        map.loadCollisions(Core.MEDIA.create("tile", MapTile.COLLISIONS_FILE_NAME));

        final Services services = new Services();
        services.add(map);
        services.add(camera);
        entityRef = new Entity(services);
        entity = new Entity(services);

        camera.setLimits(map);
        camera.setView(0, 0, getWidth(), getHeight());
        map.createCollisionDraw();
    }

    @Override
    public void update(double extrp)
    {
        mouse.update(extrp);
        entity.updateMouse(mouse);
        entity.teleport(entityRef.getX(), entityRef.getY());
        entity.update(extrp);
        if (mouse.hasClicked(Mouse.RIGHT))
        {
            entityRef.teleport(camera.getViewpointX(mouse.getX()), camera.getViewpointY(mouse.getY()));
        }
        if (keyboard.isPressed(Keyboard.ESCAPE))
        {
            end();
        }
    }

    @Override
    public void render(Graphic g)
    {
        g.clear(0, 0, getWidth(), getHeight());
        map.render(g, camera);
        entity.render(g);
        entityRef.render(g);

        g.setColor(ColorRgba.WHITE);
        g.drawLine(camera, entity.getX(), entity.getY(), entityRef.getX(), entityRef.getY());
    }

    @Override
    protected void onTerminate(boolean hasNextSequence)
    {
        Engine.terminate();
    }
}
