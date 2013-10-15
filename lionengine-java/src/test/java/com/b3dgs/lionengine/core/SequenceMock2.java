/*
 * Copyright (C) 2013 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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

import org.junit.Assert;

import com.b3dgs.lionengine.Graphic;
import com.b3dgs.lionengine.Resolution;

/**
 * Sequence mock.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public class SequenceMock2
        extends Sequence
{
    /**
     * Constructor.
     * 
     * @param loader The loader reference.
     */
    public SequenceMock2(Loader loader)
    {
        super(loader, new Resolution(320, 240, 60));
        setResolution(new Resolution(100, 100, 60));
        setExtrapolated(true);
        setMouseVisible(false);
    }

    @Override
    protected void load()
    {
        Assert.assertTrue(getFps() >= 0);
        Assert.assertNotNull(getConfig());
        Assert.assertNotNull(getKeyboard());
        Assert.assertNotNull(getMouse());
    }

    @Override
    protected void update(double extrp)
    {
        start(new SequenceMock3(loader), true);
        end();
    }

    @Override
    protected void render(Graphic g)
    {
        clearScreen(g);
    }
}
