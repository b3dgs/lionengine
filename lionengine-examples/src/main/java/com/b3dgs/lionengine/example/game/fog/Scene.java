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
package com.b3dgs.lionengine.example.game.fog;

import java.util.ArrayList;
import java.util.Collection;

import com.b3dgs.lionengine.Resolution;
import com.b3dgs.lionengine.core.Core;
import com.b3dgs.lionengine.core.Graphic;
import com.b3dgs.lionengine.core.Loader;
import com.b3dgs.lionengine.core.Sequence;
import com.b3dgs.lionengine.core.awt.Engine;
import com.b3dgs.lionengine.core.awt.Keyboard;
import com.b3dgs.lionengine.core.awt.Mouse;
import com.b3dgs.lionengine.drawable.Drawable;
import com.b3dgs.lionengine.drawable.SpriteTiled;
import com.b3dgs.lionengine.game.Camera;
import com.b3dgs.lionengine.game.map.FogOfWar;
import com.b3dgs.lionengine.game.map.MapTile;
import com.b3dgs.lionengine.game.map.MapTileGame;
import com.b3dgs.lionengine.game.object.Factory;
import com.b3dgs.lionengine.game.object.Services;
import com.b3dgs.lionengine.game.trait.fovable.Fovable;

/**
 * Game loop designed to handle our little world.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 * @see com.b3dgs.lionengine.example.core.minimal
 */
class Scene
        extends Sequence
{
    /** Native resolution. */
    private static final Resolution NATIVE = new Resolution(320, 240, 60);

    /** Camera reference. */
    private final Camera camera = new Camera();
    /** Map reference. */
    private final MapTile map = new MapTileGame(camera, 16, 16);
    /** Fog of war layer. */
    private final FogOfWar fogOfWar = new FogOfWar();
    /** Collection fog. */
    private final Collection<Fovable> fovables = new ArrayList<>();
    /** Keyboard reference. */
    private final Keyboard keyboard;
    /** Mouse reference. */
    private final Mouse mouse;
    /** Peon reference. */
    private Peon peon;

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
        setSystemCursorVisible(false);
    }

    @Override
    protected void load()
    {
        map.create(Core.MEDIA.create("level.png"), Core.MEDIA.create("sheets.xml"), Core.MEDIA.create("groups.xml"));
        map.setTileRenderer(fogOfWar);

        final SpriteTiled hide = Drawable.loadSpriteTiled(Core.MEDIA.create("hide.png"), 16, 16);
        final SpriteTiled fog = Drawable.loadSpriteTiled(Core.MEDIA.create("fog.png"), 16, 16);
        hide.load(false);
        fog.load(false);
        fogOfWar.setTilesheet(hide, fog);
        fogOfWar.setEnabled(true, true);
        fogOfWar.create(map, map);

        camera.setView(0, 0, getWidth(), getHeight());
        camera.setLimits(map);
        camera.setLocation(0, 0);

        final Services services = new Services();
        services.add(camera);
        services.add(map);

        final Factory factory = new Factory();
        factory.setServices(services);

        peon = factory.create(Peon.MEDIA);
        fovables.add(peon.getTrait(Fovable.class));
    }

    @Override
    public void update(double extrp)
    {
        mouse.update(extrp);
        peon.update(extrp);
        fogOfWar.update(fovables);
        if (keyboard.isPressedOnce(Keyboard.ESCAPE))
        {
            end();
        }
    }

    @Override
    public void render(Graphic g)
    {
        map.render(g);
        peon.render(g);
    }

    @Override
    protected void onTerminate(boolean hasNextSequence)
    {
        Engine.terminate();
    }
}
