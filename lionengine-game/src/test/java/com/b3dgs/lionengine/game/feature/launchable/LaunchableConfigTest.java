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
package com.b3dgs.lionengine.game.feature.launchable;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import com.b3dgs.lionengine.Constant;
import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.core.Medias;
import com.b3dgs.lionengine.game.Force;
import com.b3dgs.lionengine.stream.Xml;
import com.b3dgs.lionengine.stream.XmlNode;

/**
 * Test the launchable config class.
 */
public class LaunchableConfigTest
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
     * Test the launchable configuration.
     */
    @Test
    public void testConfig()
    {
        final Media media = Medias.create("launchable.xml");
        final LaunchableConfig launchable = new LaunchableConfig("media", 10, 1, 2, new Force(1.0, 2.0));
        try
        {
            final XmlNode root = Xml.create("test");
            root.add(LaunchableConfig.exports(launchable));
            Xml.save(root, media);

            final LaunchableConfig loaded = LaunchableConfig.imports(Xml.load(media)
                                                                        .getChild(LaunchableConfig.NODE_LAUNCHABLE));

            Assert.assertEquals(launchable, loaded);
        }
        finally
        {
            Assert.assertTrue(media.getFile().delete());
        }
    }

    /**
     * Test the launchable <code>null</code> media.
     */
    @Test(expected = LionEngineException.class)
    public void testNullMedia()
    {
        Assert.assertNotNull(new LaunchableConfig(null, 0, 1, 2, new Force()));
    }

    /**
     * Test the launchable <code>null</code> force.
     */
    @Test(expected = LionEngineException.class)
    public void testNullForce()
    {
        Assert.assertNotNull(new LaunchableConfig("media", 0, 1, 2, null));
    }

    /**
     * Test the launchable hash code.
     */
    @Test
    public void testHashCode()
    {
        final int launchable = new LaunchableConfig("media", 10, 1, 2, new Force(1.0, 2.0)).hashCode();

        Assert.assertEquals(launchable, launchable);
        Assert.assertEquals(launchable, new LaunchableConfig("media", 10, 1, 2, new Force(1.0, 2.0)).hashCode());
        Assert.assertNotEquals(launchable, new LaunchableConfig("", 10, 1, 2, new Force(1.0, 2.0)).hashCode());
        Assert.assertNotEquals(launchable, new LaunchableConfig("media", 0, 1, 2, new Force(1.0, 2.0)).hashCode());
        Assert.assertNotEquals(launchable, new LaunchableConfig("media", 10, 1, 2, new Force(2.0, 1.0)).hashCode());
        Assert.assertNotEquals(launchable, new LaunchableConfig("media", 10, 0, 2, new Force(1.0, 2.0)).hashCode());
        Assert.assertNotEquals(launchable, new LaunchableConfig("media", 10, 1, 0, new Force(1.0, 2.0)).hashCode());
    }

    /**
     * Test the launchable equality.
     */
    @Test
    public void testEquals()
    {
        final LaunchableConfig launchable = new LaunchableConfig("media", 10, 1, 2, new Force(1.0, 2.0));

        Assert.assertEquals(launchable, launchable);
        Assert.assertNotEquals(launchable, null);
        Assert.assertNotEquals(launchable, new Object());
        Assert.assertNotEquals(launchable, new LaunchableConfig("", 10, 1, 2, new Force(1.0, 2.0)));
        Assert.assertNotEquals(launchable, new LaunchableConfig("media", 0, 1, 2, new Force(1.0, 2.0)));
        Assert.assertNotEquals(launchable, new LaunchableConfig("media", 10, 1, 2, new Force(2.0, 1.0)));
        Assert.assertNotEquals(launchable, new LaunchableConfig("media", 10, 0, 2, new Force(1.0, 2.0)));
        Assert.assertNotEquals(launchable, new LaunchableConfig("media", 10, 1, 0, new Force(1.0, 2.0)));
    }

    /**
     * Test the to string.
     */
    @Test
    public void testToString()
    {
        final LaunchableConfig launchable = new LaunchableConfig("media", 10, 1, 2, new Force(1.0, 2.0));

        Assert.assertEquals("LaunchableConfig [media=media, delay=10, ox=1, oy=2, vector="
                            + "Force [fh=1.0, fv=2.0, velocity=0.0, sensibility=0.0]]",
                            launchable.toString());
    }
}
