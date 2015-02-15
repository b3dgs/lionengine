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
package com.b3dgs.lionengine.core.awt;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.b3dgs.lionengine.Config;
import com.b3dgs.lionengine.Resolution;
import com.b3dgs.lionengine.core.FactoryGraphicProvider;
import com.b3dgs.lionengine.core.Graphic;
import com.b3dgs.lionengine.core.Loader;
import com.b3dgs.lionengine.core.Sequence;

/**
 * Test the renderer.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public class RendererAwtTest
{
    /**
     * Prepare test.
     */
    @BeforeClass
    public static void setUp()
    {
        FactoryGraphicProvider.setFactoryGraphic(new FactoryGraphicAwt());
    }

    /**
     * Clean up test.
     */
    @AfterClass
    public static void cleanUp()
    {
        FactoryGraphicProvider.setFactoryGraphic(null);
    }

    /**
     * Test the renderer.
     * 
     * @throws InterruptedException If error.
     */
    @Test
    public void testRenderer() throws InterruptedException
    {
        final Resolution resolution = new Resolution(320, 240, 32);
        final Config config = new Config(resolution, 60, false);
        final Loader loader = new Loader(config);
        config.setSource(resolution);
        final RendererAwt renderer = new RendererAwt(config);
        renderer.asyncLoad(new Sequence(loader, resolution)
        {
            @Override
            public void load()
            {
                // Nothing to do
            }

            @Override
            public void update(double extrp)
            {
                // Nothing to do
            }

            @Override
            public void render(Graphic g)
            {
                end();
            }
        });
        renderer.join();
    }
}
