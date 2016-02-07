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
package com.b3dgs.lionengine.mock;

import org.junit.Assert;

import com.b3dgs.lionengine.Filter;
import com.b3dgs.lionengine.Graphic;
import com.b3dgs.lionengine.core.Context;
import com.b3dgs.lionengine.core.InputDevice;
import com.b3dgs.lionengine.core.Sequence;
import com.b3dgs.lionengine.util.Constant;

/**
 * Filter sequence mock.
 */
public class SequenceFilterMock extends Sequence
{
    /**
     * Constructor.
     * 
     * @param context The context reference.
     * @param filter The filter used.
     */
    public SequenceFilterMock(Context context, Filter filter)
    {
        super(context, Constant.RESOLUTION_320_240);
        setFilter(filter);
    }

    /*
     * Sequence
     */

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
