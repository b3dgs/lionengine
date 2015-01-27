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
package com.b3dgs.lionengine.example.game.projectile;

import com.b3dgs.lionengine.Resolution;
import com.b3dgs.lionengine.core.Graphic;
import com.b3dgs.lionengine.core.Loader;
import com.b3dgs.lionengine.core.Sequence;
import com.b3dgs.lionengine.core.awt.Engine;
import com.b3dgs.lionengine.core.awt.Keyboard;
import com.b3dgs.lionengine.game.Camera;
import com.b3dgs.lionengine.game.Services;
import com.b3dgs.lionengine.game.component.ComponentCollision;
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
    /** Handler. */
    private final Handler handler;
    /** Keyboard reference. */
    private final Keyboard keyboard;

    /**
     * Constructor.
     * 
     * @param loader The loader reference.
     */
    public Scene(Loader loader)
    {
        super(loader, new Resolution(320, 240, 60));
        handler = new Handler();
        keyboard = getInputDevice(Keyboard.class);
    }

    @Override
    public void load()
    {
        final Services context = new Services();
        final Factory factory = new Factory();
        final Camera camera = new Camera();
        context.add(factory);
        context.add(handler);
        context.add(camera);
        factory.setServices(context);

        final Ship ship1 = factory.create(Ship.MEDIA);
        final Ship ship2 = factory.create(Ship.MEDIA);
        handler.add(ship1);
        handler.add(ship2);

        ship1.mirror();
        ship1.setTarget(ship2);
        ship2.setTarget(ship1);

        handler.addUpdatable(new ComponentUpdater());
        handler.addUpdatable(new ComponentCollision());
        handler.addRenderable(new ComponentRenderer());

        camera.setView(0, 0, getWidth(), getHeight());
    }

    @Override
    public void update(double extrp)
    {
        if (keyboard.isPressed(Keyboard.ESCAPE))
        {
            end();
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
