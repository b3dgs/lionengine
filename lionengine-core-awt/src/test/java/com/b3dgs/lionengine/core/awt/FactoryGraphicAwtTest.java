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
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import com.b3dgs.lionengine.ColorRgba;
import com.b3dgs.lionengine.Config;
import com.b3dgs.lionengine.Filter;
import com.b3dgs.lionengine.Resolution;
import com.b3dgs.lionengine.TextStyle;
import com.b3dgs.lionengine.Transparency;
import com.b3dgs.lionengine.Version;
import com.b3dgs.lionengine.core.EngineCore;
import com.b3dgs.lionengine.core.ImageBuffer;
import com.b3dgs.lionengine.core.Renderer;
import com.b3dgs.lionengine.core.Text;

/**
 * Test the factory graphic provider class.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
@SuppressWarnings("static-method")
public class FactoryGraphicAwtTest
{
    /**
     * Prepare test.
     */
    @BeforeClass
    public static void setUp()
    {
        EngineCore.start("test", Version.create(0, 0, 0), new FactoryGraphicAwt(), new FactoryMediaAwt());
    }

    /**
     * Clean up test.
     */
    @AfterClass
    public static void cleanUp()
    {
        Engine.terminate();
    }

    /**
     * Test the graphic factory.
     */
    @Test
    public void testFactory()
    {
        final FactoryGraphicAwt factory = new FactoryGraphicAwt();
        Assert.assertNotNull(factory.createGraphic());
        Assert.assertNotNull(factory.createImageBuffer(1, 1, Transparency.BITMASK));
        final Renderer renderer = new Renderer(new Config(new Resolution(320, 240, 0), 16, false));
        Assert.assertNotNull(renderer);
        Assert.assertNotNull(factory.createScreen(renderer));
        Assert.assertNotNull(factory.createText(Text.SANS_SERIF, 10, TextStyle.NORMAL));
        Assert.assertNotNull(factory.createTransform());
        Assert.assertNotNull(factory.loadRaster(new MediaAwt(MediaAwt.class.getResource("raster.xml").getFile())));
        final ImageBuffer image = factory.getImageBuffer(
                new MediaAwt(MediaAwt.class.getResource("image.png").getFile()), false);
        Assert.assertNotNull(image);
        Assert.assertNotNull(factory.getImageBuffer(image));
        Assert.assertNotNull(factory.getRasterBuffer(image, 1, 1, 1, 1, 1, 1, 1));
        Assert.assertNotNull(factory.applyFilter(image, Filter.BILINEAR));
        Assert.assertNotNull(factory.applyFilter(image, Filter.HQ3X));
        Assert.assertNotNull(factory.applyMask(image, ColorRgba.BLACK));
        Assert.assertNotNull(factory.flipHorizontal(image));
        Assert.assertNotNull(factory.flipVertical(image));
        Assert.assertNotNull(factory.resize(image, 10, 10));
        Assert.assertNotNull(factory.rotate(image, 90));
        Assert.assertNotNull(factory.splitImage(image, 1, 1));
        final MediaAwt save = new MediaAwt("test");
        factory.saveImage(image, save);
        Assert.assertTrue(save.getFile().exists());
        Assert.assertTrue(save.getFile().delete());
        Assert.assertFalse(save.getFile().exists());
    }
}
