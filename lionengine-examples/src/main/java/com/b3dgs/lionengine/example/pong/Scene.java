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
package com.b3dgs.lionengine.example.pong;

import com.b3dgs.lionengine.Resolution;
import com.b3dgs.lionengine.core.Graphic;
import com.b3dgs.lionengine.core.Loader;
import com.b3dgs.lionengine.core.Sequence;
import com.b3dgs.lionengine.core.awt.Engine;
import com.b3dgs.lionengine.core.awt.Keyboard;
import com.b3dgs.lionengine.core.awt.EventAction;
import com.b3dgs.lionengine.game.Camera;
import com.b3dgs.lionengine.game.object.ComponentCollision;
import com.b3dgs.lionengine.game.object.ComponentRenderer;
import com.b3dgs.lionengine.game.object.ComponentUpdater;
import com.b3dgs.lionengine.game.object.Factory;
import com.b3dgs.lionengine.game.object.Handler;
import com.b3dgs.lionengine.game.object.Services;

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

    /** Services reference. */
    private final Services services = new Services();
    /** Game factory. */
    private final Factory factory = services.create(Factory.class);
    /** Handler. */
    private final Handler handler = services.create(Handler.class);
    /** Camera. */
    private final Camera camera = services.create(Camera.class);
    /** Keyboard reference. */
    private final Keyboard keyboard = getInputDevice(Keyboard.class);

    /**
     * Constructor.
     * 
     * @param loader The loader reference.
     */
    public Scene(Loader loader)
    {
        super(loader, NATIVE);
        keyboard.addActionPressed(Keyboard.ESCAPE, new EventAction()
        {
            @Override
            public void action()
            {
                end();
            }
        });
    }

    @Override
    protected void load()
    {
        camera.setView(0, 0, getWidth(), getHeight());
        setSystemCursorVisible(false);

        handler.addUpdatable(new ComponentUpdater());
        handler.addUpdatable(new ComponentCollision());
        handler.addRenderable(new ComponentRenderer());

        final Racket racket1 = factory.create(Racket.MEDIA);
        racket1.setSide(true);
        handler.add(racket1);

        final Racket racket2 = factory.create(Racket.MEDIA);
        racket2.setSide(false);
        handler.add(racket2);

        final Ball ball = factory.create(Ball.MEDIA);
        handler.add(ball);

        racket1.setBall(ball);
        racket2.setBall(ball);
    }

    @Override
    public void update(double extrp)
    {
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
