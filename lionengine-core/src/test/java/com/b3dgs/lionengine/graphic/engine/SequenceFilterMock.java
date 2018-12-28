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

import static com.b3dgs.lionengine.UtilAssert.assertEquals;
import static com.b3dgs.lionengine.UtilAssert.assertNull;

import java.util.concurrent.atomic.AtomicInteger;

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
    private final AtomicInteger rate = new AtomicInteger();

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
        setTime(0.1);
    }

    @Override
    protected void onRateChanged(int rate)
    {
        super.onRateChanged(rate);
        this.rate.set(rate);
    }

    @Override
    public void load()
    {
        setSystemCursorVisible(true);
        setSystemCursorVisible(false);

        assertNull(getInputDevice(InputDevice.class));
        assertEquals(320, getWidth());
        assertEquals(240, getHeight());
        assertEquals(60, getRate());
        assertEquals(6, rate.get());
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
