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

import com.b3dgs.lionengine.Verbose;
import com.b3dgs.lionengine.core.Config;
import com.b3dgs.lionengine.core.Context;
import com.b3dgs.lionengine.core.Sequence;
import com.b3dgs.lionengine.graphic.Graphic;
import com.b3dgs.lionengine.test.UtilTests;

/**
 * Mock sequence.
 */
public class SequenceArgumentsMock extends Sequence
{
    /** Argument. */
    private final Object argument;
    /** Width. */
    private int width;
    /** Height. */
    private int height;
    /** Config. */
    private Config config;

    /**
     * Constructor.
     * 
     * @param context The context reference.
     * @param argument The argument reference.
     */
    public SequenceArgumentsMock(Context context, Object argument)
    {
        super(context, UtilTests.RESOLUTION_320_240);
        this.argument = argument;
        setSystemCursorVisible(true);
    }

    /*
     * Sequence
     */

    @Override
    public void load()
    {
        width = getWidth();
        height = getHeight();
        config = getConfig();
        setResolution(UtilTests.RESOLUTION_640_480);
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
        Verbose.info(argument.toString());
    }
}
