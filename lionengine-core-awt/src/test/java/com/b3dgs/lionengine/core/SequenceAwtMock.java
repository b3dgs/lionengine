/*
 * Copyright (C) 2013-2014 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.core;

import com.b3dgs.lionengine.ColorRgba;
import com.b3dgs.lionengine.Resolution;
import com.b3dgs.lionengine.UtilityRandom;

/**
 * Sequence mock.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public class SequenceAwtMock
        extends Sequence
{
    /** First. */
    private int count;

    /**
     * Constructor.
     * 
     * @param loader The loader reference.
     */
    public SequenceAwtMock(Loader loader)
    {
        super(loader, new Resolution(100, 100, 60));
        setExtrapolated(false);
    }

    @Override
    protected void load()
    {
        count = 0;
    }

    @Override
    protected void update(double extrp)
    {
        if (count > 30)
        {
            start(true, SequenceAwtMock2.class);
            end();
        }
        count++;
    }

    @Override
    protected void render(Graphic g)
    {
        if (count > 5)
        {
            for (int x = 0; x < getWidth(); x++)
            {
                for (int y = 0; y < getHeight(); y++)
                {
                    g.setColor(new ColorRgba(UtilityRandom.getRandomInteger(255), UtilityRandom.getRandomInteger(255),
                            UtilityRandom.getRandomInteger(255)));
                    g.drawRect(x, y, 1, y, false);
                }
            }
        }
    }
}
