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

import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.Medias;
import com.b3dgs.lionengine.Xml;

/**
 * Test {@link SizeConfig}.
 */
public final class SizeConfigTest
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
     * Test exports imports.
     */
    @Test
    public void testExportsImports()
    {
        final SizeConfig config = new SizeConfig(16, 32);

        final Xml root = new Xml("test");
        root.add(SizeConfig.exports(config));

        final Media media = Medias.create("object.xml");
        root.save(media);

        Assert.assertEquals(config, SizeConfig.imports(new Xml(media)));
        Assert.assertEquals(config, SizeConfig.imports(new Configurer(media)));

        Assert.assertTrue(media.getFile().delete());
    }

    /**
     * Test equals.
     */
    @Test
    public void testEquals()
    {
        final SizeConfig config = new SizeConfig(16, 32);

        Assert.assertEquals(config, config);
        Assert.assertEquals(config, new SizeConfig(16, 32));

        Assert.assertNotEquals(config, null);
        Assert.assertNotEquals(config, new Object());
        Assert.assertNotEquals(config, new SizeConfig(0, 32));
        Assert.assertNotEquals(config, new SizeConfig(16, 0));
    }

    /**
     * Test hash code.
     */
    @Test
    public void testHashCode()
    {
        final int hash = new SizeConfig(16, 32).hashCode();

        Assert.assertEquals(hash, new SizeConfig(16, 32).hashCode());

        Assert.assertNotEquals(hash, new SizeConfig(0, 32).hashCode());
        Assert.assertNotEquals(hash, new SizeConfig(16, 0).hashCode());
    }

    /**
     * Test to string.
     */
    @Test
    public void testToString()
    {
        final SizeConfig config = new SizeConfig(16, 32);

        Assert.assertEquals("SizeConfig [width=16, height=32]", config.toString());
    }
}
