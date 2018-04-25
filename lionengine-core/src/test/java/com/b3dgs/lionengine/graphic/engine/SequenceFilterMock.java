/*
 * Copyright (C) 2013-2017 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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

import org.junit.Assert;

import com.b3dgs.lionengine.Context;
import com.b3dgs.lionengine.InputDevice;
import com.b3dgs.lionengine.UtilTests;
import com.b3dgs.lionengine.graphic.Filter;
import com.b3dgs.lionengine.graphic.Graphic;

/**
 * Filter sequence mock.
 */
final class SequenceFilterMock extends Sequence
{
    /**
     * Constructor.
     * 
     * @param context The context reference.
     * @param filter The filter used.
     */
    SequenceFilterMock(Context context, Filter filter)
    {
        super(context, UtilTests.RESOLUTION_320_240);

        setFilter(filter);
    }

    @Override
    public void load()
    {
        setSystemCursorVisible(true);
        setSystemCursorVisible(false);
        Assert.assertNull(getInputDevice(InputDevice.class));
    }

    @Override
    public void update(double extrp)
    {
        setFilter(null);
        getX();
        getY();
        end(SequenceArgumentsMock.class, new Object());
    }

    @Override
    public void render(Graphic g)
    {
        // Mock
    }
}
