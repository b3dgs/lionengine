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
package com.b3dgs.lionengine.example.game.cursor;

import com.b3dgs.lionengine.Resolution;
import com.b3dgs.lionengine.core.Core;
import com.b3dgs.lionengine.core.Graphic;
import com.b3dgs.lionengine.core.Loader;
import com.b3dgs.lionengine.core.Sequence;
import com.b3dgs.lionengine.core.awt.Engine;
import com.b3dgs.lionengine.core.awt.Keyboard;
import com.b3dgs.lionengine.core.awt.Mouse;
import com.b3dgs.lionengine.game.Cursor;

/**
 * Scene implementation.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 * @see com.b3dgs.lionengine.example.core.minimal
 */
class Scene
        extends Sequence
{
    /** Cursor. */
    private final Cursor cursor;
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
        keyboard = getInputDevice(Keyboard.class);
        mouse = getInputDevice(Mouse.class);
        cursor = new Cursor(mouse, Core.MEDIA.create("cursor", "cursor1.png"), Core.MEDIA.create("cursor",
                "cursor2.png"));
    }

    @Override
    public void load()
    {
        mouse.setConfig(getConfig());
        cursor.load(false);
        cursor.setArea(0, 0, getWidth(), getHeight());
        cursor.setSensibility(0.5, 0.5);
        setSystemCursorVisible(false);
    }

    @Override
    public void update(double extrp)
    {
        if (keyboard.isPressed(Keyboard.ESCAPE))
        {
            end();
        }

        if (mouse.hasClickedOnce(Mouse.LEFT))
        {
            cursor.setSurfaceId(0);
        }
        if (mouse.hasClickedOnce(Mouse.RIGHT))
        {
            cursor.setSurfaceId(1);
        }
        if (mouse.hasClickedOnce(Mouse.MIDDLE))
        {
            cursor.setSyncMode(!cursor.isSynchronized());
        }

        cursor.update(extrp);
        if (!cursor.isSynchronized())
        {
            mouse.lock();
        }
    }

    @Override
    public void render(Graphic g)
    {
        g.clear(0, 0, getWidth(), getHeight());
        cursor.render(g);
    }

    @Override
    protected void onTerminate(boolean hasNextSequence)
    {
        Engine.terminate();
    }
}
