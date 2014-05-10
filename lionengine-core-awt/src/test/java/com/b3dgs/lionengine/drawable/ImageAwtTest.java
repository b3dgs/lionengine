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
package com.b3dgs.lionengine.drawable;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Transparency;
import com.b3dgs.lionengine.UtilFile;
import com.b3dgs.lionengine.Version;
import com.b3dgs.lionengine.core.Core;
import com.b3dgs.lionengine.core.Engine;
import com.b3dgs.lionengine.core.Verbose;

/**
 * Test the image class.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public class ImageAwtTest
        extends ImageTest
{
    /**
     * Prepare test.
     */
    @BeforeClass
    public static void setUp()
    {
        Engine.start("ImageAwtTest", Version.create(1, 0, 0), Verbose.CRITICAL,
                UtilFile.getPath("src", "test", "resources", "drawable"));
        ImageTest.media = Core.MEDIA.create("image.png");
        ImageTest.g = Core.GRAPHIC.createImageBuffer(100, 100, Transparency.OPAQUE).createGraphic();
    }

    /**
     * Clean up test.
     */
    @AfterClass
    public static void cleanUp()
    {
        Engine.terminate();
    }

    @Override
    @Test
    public void testImageFailure()
    {
        try
        {
            Core.GRAPHIC.createImageBuffer(-16, 16, Transparency.OPAQUE);
            Assert.fail();
        }
        catch (final IllegalArgumentException exception)
        {
            // Success
        }
        try
        {
            Core.GRAPHIC.createImageBuffer(16, -16, Transparency.OPAQUE);
            Assert.fail();
        }
        catch (final IllegalArgumentException exception)
        {
            // Success
        }
        // Load unexisting image
        try
        {
            Drawable.loadImage(Core.MEDIA.create("void"));
            Assert.fail();
        }
        catch (final LionEngineException exception)
        {
            // Success
        }
    }
}
