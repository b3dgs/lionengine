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
package com.b3dgs.lionengine.example.helloworld;

import com.b3dgs.lionengine.Align;
import com.b3dgs.lionengine.Resolution;
import com.b3dgs.lionengine.TextStyle;
import com.b3dgs.lionengine.core.Graphic;
import com.b3dgs.lionengine.core.Graphics;
import com.b3dgs.lionengine.core.Loader;
import com.b3dgs.lionengine.core.Sequence;
import com.b3dgs.lionengine.core.Text;
import com.b3dgs.lionengine.core.awt.Engine;
import com.b3dgs.lionengine.core.awt.Keyboard;
import com.b3dgs.lionengine.core.awt.EventAction;

/**
 * Scene implementation.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 * @see com.b3dgs.lionengine.example.core.minimal
 */
class Scene
        extends Sequence
{
    /** Native resolution. */
    private static final Resolution NATIVE = new Resolution(320, 240, 60);

    /** Text drawer. */
    private final Text text = Graphics.createText(Text.SANS_SERIF, 12, TextStyle.NORMAL);
    /** Keyboard. */
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
        text.setText("Hello");
        text.setLocation(getWidth() / 2, getHeight() / 2 - 8);
        text.setAlign(Align.CENTER);
    }

    @Override
    public void update(double extrp)
    {
        // Update
    }

    @Override
    public void render(Graphic g)
    {
        g.clear(0, 0, getWidth(), getHeight());
        // Simple rendering
        text.render(g);
        // Direct rendering
        text.draw(g, getWidth() / 2, getHeight() / 2 + 8, Align.CENTER, "World");
    }

    @Override
    protected void onTerminate(boolean hasNextSequence)
    {
        Engine.terminate();
    }
}
