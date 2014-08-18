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
package com.b3dgs.lionengine.example.game.effect;

import com.b3dgs.lionengine.Resolution;
import com.b3dgs.lionengine.core.Graphic;
import com.b3dgs.lionengine.core.Keyboard;
import com.b3dgs.lionengine.core.Loader;
import com.b3dgs.lionengine.core.Mouse;
import com.b3dgs.lionengine.core.Sequence;
import com.b3dgs.lionengine.game.CameraGame;

/**
 * Scene implementation.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 * @see com.b3dgs.lionengine.example.core.minimal
 */
final class Scene
        extends Sequence
{
    /** Keyboard reference. */
    private final Keyboard keyboard;
    /** Mouse reference. */
    private final Mouse mouse;
    /** Camera. */
    private final CameraGame camera;
    /** Factory effect. */
    private final FactoryEffect factoryEffect;
    /** Handler effect. */
    private final HandlerEffect handlerEffect;

    /**
     * Constructor.
     * 
     * @param loader The loader reference.
     */
    Scene(Loader loader)
    {
        super(loader, new Resolution(320, 240, 60));
        keyboard = getInputDevice(Keyboard.class);
        mouse = getInputDevice(Mouse.class);
        camera = new CameraGame();
        factoryEffect = new FactoryEffect();
        handlerEffect = new HandlerEffect(camera);
        camera.setView(0, 0, getWidth(), getHeight());
    }

    /*
     * Sequence
     */

    @Override
    protected void load()
    {
        mouse.setConfig(getConfig());
    }

    @Override
    protected void update(double extrp)
    {
        mouse.update();
        if (keyboard.isPressed(Keyboard.ESCAPE))
        {
            end();
        }

        if (mouse.hasClicked(Mouse.LEFT))
        {
            final Effect effect = factoryEffect.create(Explode.MEDIA);
            effect.start(mouse.getX(), getHeight() - mouse.getY());
            handlerEffect.add(effect);
        }

        handlerEffect.update(extrp);
    }

    @Override
    protected void render(Graphic g)
    {
        g.clear(0, 0, getWidth(), getHeight());
        handlerEffect.render(g);
    }
}
