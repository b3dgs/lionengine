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
package com.b3dgs.lionengine.test.mock;

import org.junit.Assert;

import com.b3dgs.lionengine.Config;
import com.b3dgs.lionengine.core.Graphic;
import com.b3dgs.lionengine.core.InputDevice;
import com.b3dgs.lionengine.core.Loader;
import com.b3dgs.lionengine.core.Sequence;
import com.b3dgs.lionengine.core.Verbose;
import com.b3dgs.lionengine.test.util.Constant;

/**
 * Mock sequence.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public class SequenceArgumentsMock extends Sequence
{
    /** Width. */
    private int width;
    /** Height. */
    private int height;
    /** Config. */
    private Config config;

    /**
     * Constructor.
     * 
     * @param loader The loader reference.
     * @param argument The argument reference.
     */
    public SequenceArgumentsMock(Loader loader, Object argument)
    {
        super(loader, Constant.RESOLUTION_320_240);
        setExtrapolated(true);
        addKeyListener(null);
        setSystemCursorVisible(true);
        Assert.assertNull(getInputDevice(InputDevice.class));
    }

    /*
     * Sequence
     */

    @Override
    protected void load()
    {
        width = getWidth();
        height = getHeight();
        config = getConfig();
        setResolution(Constant.RESOLUTION_640_480);
    }

    @Override
    public void update(double extrp)
    {
        end();
    }

    @Override
    public void render(Graphic g)
    {
        Verbose.info("Sequence single mock info");
        Verbose.info(String.valueOf(width));
        Verbose.info(String.valueOf(height));
        Verbose.info(String.valueOf(config));
        Verbose.info(String.valueOf(getFps()));
    }
}
