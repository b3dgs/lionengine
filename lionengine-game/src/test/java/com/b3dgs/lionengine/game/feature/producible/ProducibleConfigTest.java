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
package com.b3dgs.lionengine.game.feature.producible;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import com.b3dgs.lionengine.Constant;
import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.core.Medias;
import com.b3dgs.lionengine.game.Configurer;
import com.b3dgs.lionengine.game.Setup;
import com.b3dgs.lionengine.game.SizeConfig;
import com.b3dgs.lionengine.io.Xml;

/**
 * Test the producible config class.
 */
public class ProducibleConfigTest
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
     * Test the producible configuration.
     */
    @Test
    public void testConfig()
    {
        final Media media = Medias.create("producible.xml");
        final ProducibleConfig producible = new ProducibleConfig(1, 2, 3);
        try
        {
            final Xml root = new Xml("test");
            root.add(SizeConfig.exports(new SizeConfig(producible.getWidth(), producible.getHeight())));
            root.add(ProducibleConfig.exports(producible));
            root.save(media);

            final ProducibleConfig loaded = ProducibleConfig.imports(new Xml(media));
            Assert.assertEquals(producible, loaded);
            Assert.assertEquals(producible, ProducibleConfig.imports(new Setup(media)));
            Assert.assertEquals(producible, ProducibleConfig.imports(new Configurer(media)));
        }
        finally
        {
            Assert.assertTrue(media.getFile().delete());
        }
    }

    /**
     * Test the producible hash code.
     */
    @Test
    public void testHashCode()
    {
        final int producible = new ProducibleConfig(1, 2, 3).hashCode();

        Assert.assertEquals(producible, producible);
        Assert.assertEquals(producible, new ProducibleConfig(1, 2, 3).hashCode());
        Assert.assertNotEquals(producible, new ProducibleConfig(0, 2, 3).hashCode());
        Assert.assertNotEquals(producible, new ProducibleConfig(1, 0, 3).hashCode());
        Assert.assertNotEquals(producible, new ProducibleConfig(1, 2, 0).hashCode());
    }

    /**
     * Test the producible equality.
     */
    @Test
    public void testEquals()
    {
        final ProducibleConfig producible = new ProducibleConfig(1, 2, 3);

        Assert.assertEquals(producible, producible);
        Assert.assertNotEquals(producible, null);
        Assert.assertNotEquals(producible, new Object());
        Assert.assertNotEquals(producible, new ProducibleConfig(0, 2, 3));
        Assert.assertNotEquals(producible, new ProducibleConfig(1, 0, 3));
        Assert.assertNotEquals(producible, new ProducibleConfig(1, 2, 0));
    }

    /**
     * Test the to string.
     */
    @Test
    public void testToString()
    {
        final ProducibleConfig producible = new ProducibleConfig(1, 2, 3);

        Assert.assertEquals("ProducibleConfig [steps=1, width=2, height=3]", producible.toString());
    }
}
