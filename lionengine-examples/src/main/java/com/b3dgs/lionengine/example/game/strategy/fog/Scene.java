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
package com.b3dgs.lionengine.example.game.strategy.fog;

import com.b3dgs.lionengine.Resolution;
import com.b3dgs.lionengine.Timing;
import com.b3dgs.lionengine.UtilRandom;
import com.b3dgs.lionengine.core.Core;
import com.b3dgs.lionengine.core.Graphic;
import com.b3dgs.lionengine.core.Loader;
import com.b3dgs.lionengine.core.Sequence;
import com.b3dgs.lionengine.core.awt.Engine;
import com.b3dgs.lionengine.core.awt.Keyboard;
import com.b3dgs.lionengine.game.ContextGame;
import com.b3dgs.lionengine.game.strategy.CameraStrategy;
import com.b3dgs.lionengine.game.utility.LevelRipConverter;

/**
 * Game loop designed to handle our little world.
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
    /** Map reference. */
    private final Map map;
    /** Fog of war reference. */
    private final FogOfWar fogOfWar;
    /** Camera reference. */
    private final CameraStrategy camera;
    /** Factory reference. */
    private final FactoryEntity factoryEntity;
    /** Handler reference. */
    private final HandlerEntity handlerEntity;
    /** Timer. */
    private final Timing timer;
    /** Peon. */
    private Peon peon;

    /**
     * Constructor.
     * 
     * @param loader The loader reference.
     */
    Scene(Loader loader)
    {
        super(loader, Scene.NATIVE);
        keyboard = getInputDevice(Keyboard.class);
        map = new Map();
        fogOfWar = new FogOfWar();
        camera = new CameraStrategy(map);
        factoryEntity = new FactoryEntity();
        handlerEntity = new HandlerEntity(camera);
        timer = new Timing();

        final ContextGame contextEntity = new ContextGame();
        contextEntity.addService(map);
        factoryEntity.setContext(contextEntity);
    }

    /*
     * Sequence
     */

    @Override
    protected void load()
    {
        final LevelRipConverter<Tile> rip = new LevelRipConverter<>();
        rip.start(Core.MEDIA.create("level.png"), Core.MEDIA.create("tile"), map);
        fogOfWar.create(map);

        camera.setView(0, 0, getWidth(), getHeight());
        camera.setSensibility(30, 30);
        camera.setBorders(map);

        peon = factoryEntity.create(Peon.MEDIA);
        handlerEntity.add(peon);
        timer.start();
    }

    @Override
    protected void update(double extrp)
    {
        if (keyboard.isPressed(Keyboard.ESCAPE))
        {
            end();
        }
        camera.update(keyboard);
        handlerEntity.update(extrp);
        if (timer.elapsed(500))
        {
            peon.teleport(UtilRandom.getRandomInteger(250), UtilRandom.getRandomInteger(200));
            fogOfWar.update(handlerEntity.list());
            timer.restart();
        }
    }

    @Override
    protected void render(Graphic g)
    {
        map.render(g, camera);
        handlerEntity.render(g);
        fogOfWar.render(g, camera);
    }

    @Override
    protected void onTerminate(boolean hasNextSequence)
    {
        Engine.terminate();
    }
}
