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
package com.b3dgs.lionengine.example.game.effect;

import com.b3dgs.lionengine.Resolution;
import com.b3dgs.lionengine.core.Graphic;
import com.b3dgs.lionengine.core.Loader;
import com.b3dgs.lionengine.core.Sequence;
import com.b3dgs.lionengine.core.awt.Engine;
import com.b3dgs.lionengine.core.awt.Keyboard;
import com.b3dgs.lionengine.core.awt.Mouse;
import com.b3dgs.lionengine.game.Camera;
import com.b3dgs.lionengine.game.Services;
import com.b3dgs.lionengine.game.component.ComponentRenderer;
import com.b3dgs.lionengine.game.component.ComponentUpdater;
import com.b3dgs.lionengine.game.factory.Factory;
import com.b3dgs.lionengine.game.handler.Handler;

/**
 * Scene implementation.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 * @see com.b3dgs.lionengine.example.core.minimal
 */
class Scene
        extends Sequence
{
    /** Camera reference. */
    private final Camera camera;
    /** Factory effect. */
    private final Factory factory;
    /** Handler effect. */
    private final Handler handler;
    /** Keyboard reference. */
    private final Keyboard keyboard;
    /** Mouse reference. */
    private final Mouse mouse;

    /**
     * Constructor.
     * 
     * @param loader The loader reference.
     */
    public Scene(Loader loader)
    {
        super(loader, new Resolution(320, 240, 60));
        camera = new Camera();
        factory = new Factory();
        handler = new Handler();
        keyboard = getInputDevice(Keyboard.class);
        mouse = getInputDevice(Mouse.class);
    }

    @Override
    public void load()
    {
        final Services context = new Services();
        context.add(camera);
        factory.setServices(context);

        mouse.setConfig(getConfig());
        camera.setView(0, 0, getWidth(), getHeight());
        handler.addUpdatable(new ComponentUpdater());
        handler.addRenderable(new ComponentRenderer());
    }

    @Override
    public void update(double extrp)
    {
        if (keyboard.isPressed(Keyboard.ESCAPE))
        {
            end();
        }
        mouse.update(extrp);
        if (mouse.hasClicked(Mouse.LEFT))
        {
            final Effect effect = factory.create(Effect.EXPLODE);
            effect.start(camera.getViewpointX(mouse.getX()), camera.getViewpointY(mouse.getY()));
            handler.add(effect);
        }
        handler.update(extrp);
    }

    @Override
    public void render(Graphic g)
    {
        g.clear(0, 0, getWidth(), getHeight());
        handler.render(g);
    }

    @Override
    protected void onTerminate(boolean hasNextSequence)
    {
        Engine.terminate();
    }
}
