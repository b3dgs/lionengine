/*
 * Copyright (C) 2013-2016 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.example.game.state;

import com.b3dgs.lionengine.core.Context;
import com.b3dgs.lionengine.core.Engine;
import com.b3dgs.lionengine.core.Resolution;
import com.b3dgs.lionengine.core.Sequence;
import com.b3dgs.lionengine.core.awt.Keyboard;
import com.b3dgs.lionengine.game.Services;
import com.b3dgs.lionengine.game.camera.Camera;
import com.b3dgs.lionengine.game.handler.ComponentRefresher;
import com.b3dgs.lionengine.game.handler.Handler;
import com.b3dgs.lionengine.game.layer.ComponentRendererLayer;
import com.b3dgs.lionengine.game.object.Factory;
import com.b3dgs.lionengine.graphic.Graphic;

/**
 * Game loop designed to handle our little world.
 * 
 * @see com.b3dgs.lionengine.example.core.minimal
 */
class Scene extends Sequence
{
    private static final Resolution NATIVE = new Resolution(320, 240, 60);

    private final Services services = new Services();
    private final Handler handler = services.create(Handler.class);
    private final Camera camera = services.create(Camera.class);

    /**
     * Constructor.
     * 
     * @param context The context reference.
     */
    public Scene(Context context)
    {
        super(context, NATIVE);
        handler.addUpdatable(new ComponentRefresher());
        handler.addRenderable(new ComponentRendererLayer());
    }

    /*
     * Sequence
     */

    @Override
    public void load()
    {
        final Keyboard keyboard = services.add(getInputDevice(Keyboard.class));
        keyboard.addActionPressed(Keyboard.ESCAPE, () -> end());

        camera.setView(0, 0, getWidth(), getHeight());
        services.add(Integer.valueOf(getConfig().getSource().getRate()));

        final Factory factory = services.create(Factory.class);
        final Mario mario = factory.create(Mario.MEDIA);
        handler.add(mario);
        mario.addInput(keyboard);
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
        g.drawLine(camera, 0, Mario.GROUND, getWidth(), Mario.GROUND);
        handler.render(g);
    }

    @Override
    public void onTerminated(boolean hasNextSequence)
    {
        Engine.terminate();
    }
}
