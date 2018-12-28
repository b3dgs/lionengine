/*
 * Copyright (C) 2013-2018 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.graphic.engine;

import com.b3dgs.lionengine.Context;
import com.b3dgs.lionengine.UtilTests;
import com.b3dgs.lionengine.graphic.Graphic;

/**
 * Interrupt sequence mock.
 */
final class SequenceInterruptMock extends Sequence
{
    /** Pause time. */
    public static final long PAUSE_MILLI = 250L;

    /**
     * Constructor.
     * 
     * @param context The context reference.
     */
    SequenceInterruptMock(Context context)
    {
        super(context, UtilTests.RESOLUTION_320_240);
    }

    @Override
    public void load()
    {
        // Mock
    }

    @Override
    public void update(double extrp)
    {
        // Mock
    }

    @Override
    public void render(Graphic g)
    {
        UtilTests.pause(PAUSE_MILLI);
    }
}
