/*
 * Copyright (C) 2013-2023 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */
package com.b3dgs.lionengine.graphic.engine;

import com.b3dgs.lionengine.Config;
import com.b3dgs.lionengine.Context;
import com.b3dgs.lionengine.UtilTests;
import com.b3dgs.lionengine.Verbose;
import com.b3dgs.lionengine.graphic.Graphic;

/**
 * Mock sequence.
 */
public final class SequenceArgumentsMock extends Sequence
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

    @Override
    public void load()
    {
        width = UtilTests.RESOLUTION_320_240.getWidth();
        height = UtilTests.RESOLUTION_320_240.getHeight();
        config = getConfig();
        setZoom(2.0);
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
