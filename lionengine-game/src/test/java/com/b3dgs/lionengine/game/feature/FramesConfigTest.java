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
package com.b3dgs.lionengine.game.feature;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import com.b3dgs.lionengine.Constant;
import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.core.Medias;
import com.b3dgs.lionengine.stream.Xml;
import com.b3dgs.lionengine.stream.XmlNode;

/**
 * Test the frames configuration.
 */
public class FramesConfigTest
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
        Medias.setResourcesDirectory(Constant.EMPTY_STRING);
    }

    /**
     * Test the configuration import.
     */
    @Test
    public void testConfig()
    {
        final int horizontal = 1;
        final int vertical = 2;
        final FramesConfig config = new FramesConfig(horizontal, vertical);

        final Media media = Medias.create("object.xml");
        try
        {
            final XmlNode root = Xml.create("test");
            root.add(FramesConfig.exports(config));
            Xml.save(root, media);

            final FramesConfig loaded = FramesConfig.imports(Xml.load(media));
            Assert.assertEquals(config, loaded);
            Assert.assertEquals(config, FramesConfig.imports(new Setup(media)));
            Assert.assertEquals(config, FramesConfig.imports(new Configurer(media)));
        }
        finally
        {
            Assert.assertTrue(media.getFile().delete());
        }
    }

    /**
     * Test the hash code.
     */
    @Test
    public void testHashCode()
    {
        final int hash = new FramesConfig(1, 2).hashCode();

        Assert.assertEquals(hash, new FramesConfig(1, 2).hashCode());
        Assert.assertNotEquals(hash, new FramesConfig(0, 2).hashCode());
        Assert.assertNotEquals(hash, new FramesConfig(1, 0).hashCode());
    }

    /**
     * Test the equality.
     */
    @Test
    public void testEquals()
    {
        final FramesConfig config = new FramesConfig(1, 2);

        Assert.assertEquals(config, config);
        Assert.assertNotEquals(config, null);
        Assert.assertNotEquals(config, new Object());
        Assert.assertEquals(config, new FramesConfig(1, 2));
        Assert.assertNotEquals(config, new FramesConfig(0, 2));
        Assert.assertNotEquals(config, new FramesConfig(1, 0));
    }

    /**
     * Test the to string.
     */
    @Test
    public void testToString()
    {
        final FramesConfig config = new FramesConfig(1, 2);

        Assert.assertEquals("FramesConfig [horizontalFrames=1, verticalFrames=2]", config.toString());
    }
}
