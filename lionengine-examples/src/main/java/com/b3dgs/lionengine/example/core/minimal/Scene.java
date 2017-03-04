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
package com.b3dgs.lionengine.example.core.minimal;

import com.b3dgs.lionengine.Context;
import com.b3dgs.lionengine.Resolution;
import com.b3dgs.lionengine.core.Engine;
import com.b3dgs.lionengine.core.sequence.Loader;
import com.b3dgs.lionengine.core.sequence.Sequence;
import com.b3dgs.lionengine.graphic.Graphic;
import com.b3dgs.lionengine.io.awt.Keyboard;

/**
 * This is where the game loop is running. A sequence represents a thread handled by the {@link Loader}.
 * To link a sequence with another one, a simple call to {@link Sequence#end(Class, Object...)} is necessary.
 * This will terminate the current sequence, and start the linked one.
 */
class Scene extends Sequence
{
    /** Native resolution. */
    private static final Resolution NATIVE = new Resolution(320, 240, 60);

    /**
     * Constructor.
     * 
     * @param context The context reference.
     */
    public Scene(Context context)
    {
        super(context, NATIVE);
        getInputDevice(Keyboard.class).addActionPressed(Keyboard.ESCAPE, () -> end());
    }

    @Override
    public void load()
    {
        // Load
    }

    @Override
    public void update(double extrp)
    {
        // Update
    }

    @Override
    public void render(Graphic g)
    {
        // Render
    }

    @Override
    public void onTerminated(boolean hasNextSequence)
    {
        Engine.terminate();
    }
}
