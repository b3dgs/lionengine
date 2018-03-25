/*
 * Copyright (C) 2013-2017 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.game;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.core.Medias;
import com.b3dgs.lionengine.io.Xml;

/**
 * Test the surface configuration.
 */
public class SurfaceConfigTest
{
    /**
     * Prepare test.
     */
    @BeforeClass
    public static void setUp()
    {
        Medias.setResourcesDirectory(System.getProperty("java.io.tmpdir"));
    }

    /**
     * Clean up test.
     */
    @AfterClass
    public static void cleanUp()
    {
        Medias.setResourcesDirectory(null);
    }

    /**
     * Test the configuration reader.
     */
    @Test
    public void testConfig()
    {
        final String image = "image";
        final String icon = "icon";
        final SurfaceConfig config = new SurfaceConfig(image, icon);

        final Media media = Medias.create("object.xml");
        try
        {
            final Xml root = new Xml("test");
            root.add(SurfaceConfig.exports(config));
            root.save(media);

            final SurfaceConfig loaded = SurfaceConfig.imports(new Xml(media));

            Assert.assertEquals(config, loaded);
            Assert.assertEquals(config, SurfaceConfig.imports(new Configurer(media)));
        }
        finally
        {
            Assert.assertTrue(media.getFile().delete());
        }
    }

    /**
     * Test the configuration without icon.
     */
    @Test
    public void testConfigNoIcon()
    {
        final String image = "image";
        final SurfaceConfig config = new SurfaceConfig(image, null);

        final Media media = Medias.create("object.xml");
        try
        {
            final Xml root = new Xml("test");
            root.add(SurfaceConfig.exports(config));
            root.save(media);

            final SurfaceConfig loaded = SurfaceConfig.imports(new Xml(media));

            Assert.assertEquals(config, loaded);
        }
        finally
        {
            Assert.assertTrue(media.getFile().delete());
        }
    }

    /**
     * Test the configuration with <code>null</code> image.
     */
    @Test(expected = LionEngineException.class)
    public void testConfigNullImage()
    {
        Assert.assertNotNull(new SurfaceConfig(null, "icon"));
    }

    /**
     * Test the configuration with <code>null</code> icon.
     */
    @Test
    public void testConfigNullIcon()
    {
        Assert.assertNotNull(new SurfaceConfig("image", null));
    }

    /**
     * Test the hash code.
     */
    @Test
    public void testHashCode()
    {
        final int hash = new SurfaceConfig("image", "icon").hashCode();

        Assert.assertEquals(hash, new SurfaceConfig("image", "icon").hashCode());
        Assert.assertNotEquals(hash, new SurfaceConfig("", "icon").hashCode());
        Assert.assertNotEquals(hash, new SurfaceConfig("image", "").hashCode());

        Assert.assertEquals(new SurfaceConfig("image", null).hashCode(), new SurfaceConfig("image", null).hashCode());
    }

    /**
     * Test the equality.
     */
    @Test
    public void testEquals()
    {
        final SurfaceConfig config = new SurfaceConfig("image", "icon");

        Assert.assertEquals(config, config);
        Assert.assertNotEquals(config, null);
        Assert.assertNotEquals(config, new Object());
        Assert.assertEquals(config, new SurfaceConfig("image", "icon"));
        Assert.assertNotEquals(config, new SurfaceConfig("", "icon"));
        Assert.assertNotEquals(config, new SurfaceConfig("image", ""));

        Assert.assertEquals(new SurfaceConfig("image", null), new SurfaceConfig("image", null));
        Assert.assertNotEquals(new SurfaceConfig("image", "icon"), new SurfaceConfig("image", null));
        Assert.assertNotEquals(new SurfaceConfig("image", null), new SurfaceConfig("image", "icon"));
    }

    /**
     * Test the to string.
     */
    @Test
    public void testToString()
    {
        final SurfaceConfig config = new SurfaceConfig("image", "icon");

        Assert.assertEquals("SurfaceConfig [image=image, icon=icon]", config.toString());
    }
}
