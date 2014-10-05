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

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.b3dgs.lionengine.Transparency;
import com.b3dgs.lionengine.Version;

/**
 * Test the factory graphic.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public class FactoryGraphicAwtTest
        extends FactoryGraphicProviderTest
{
    /**
     * Prepare test.
     */
    @BeforeClass
    public static void setUp()
    {
        Engine.start("FactoryGraphicAwtTest", Version.create(1, 0, 0), Verbose.CRITICAL,
                FactoryGraphicProviderTest.PATH);

        FactoryGraphicProviderTest.MEDIA_IMAGE = Core.MEDIA.create("image.png");
        FactoryGraphicProviderTest.MEDIA_SAVE = Core.MEDIA.create("save.png");
        FactoryGraphicProviderTest.MEDIA_RASTER = Core.MEDIA.create("raster.xml");
        FactoryGraphicProviderTest.RASTER_ERROR = Core.MEDIA.create("raster_error.xml");

        FactoryGraphicProviderTest.IMAGE = Core.GRAPHIC.getImageBuffer(FactoryGraphicProviderTest.MEDIA_IMAGE, true);
    }

    /**
     * Clean up test.
     */
    @AfterClass
    public static void cleanUp()
    {
        Engine.terminate();
    }

    @Test(expected = IllegalArgumentException.class)
    @Override
    public void testNegativeImageWidth()
    {
        Core.GRAPHIC.createImageBuffer(-1, 1, Transparency.OPAQUE);
    }

    @Test(expected = IllegalArgumentException.class)
    @Override
    public void testNegativeImageHeight()
    {
        Core.GRAPHIC.createImageBuffer(1, -1, Transparency.OPAQUE);
    }
}
