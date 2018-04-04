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
package com.b3dgs.lionengine.game.feature;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.Medias;
import com.b3dgs.lionengine.Xml;
import com.b3dgs.lionengine.game.Configurer;

/**
 * Test {@link FeaturableConfig}.
 */
public final class FeaturableConfigTest
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
        final String clazz = "class";
        final String setup = "setup";
        final FeaturableConfig config = new FeaturableConfig(clazz, setup);

        final Xml root = new Xml("test");
        root.add(FeaturableConfig.exportClass(clazz));
        root.add(FeaturableConfig.exportSetup(setup));

        final Media media = Medias.create("object.xml");
        root.save(media);

        Assert.assertEquals(config, FeaturableConfig.imports(new Xml(media)));
        Assert.assertEquals(config, FeaturableConfig.imports(new Configurer(media)));

        Assert.assertTrue(media.getFile().delete());
    }

    /**
     * Test with default class.
     */
    @Test
    public void testDefaultClass()
    {
        final Xml root = new Xml("test");
        final Media media = Medias.create("object.xml");
        root.save(media);

        Assert.assertEquals(new FeaturableConfig(FeaturableModel.class.getName(), ""),
                            FeaturableConfig.imports(new Xml(media)));

        Assert.assertTrue(media.getFile().delete());
    }

    /**
     * Test with default setup.
     */
    @Test
    public void testDefaultSetup()
    {
        final Xml root = new Xml("test");
        root.add(FeaturableConfig.exportClass("clazz"));

        final Media media = Medias.create("object.xml");
        root.save(media);

        Assert.assertEquals(new FeaturableConfig("clazz", ""), FeaturableConfig.imports(new Xml(media)));

        Assert.assertTrue(media.getFile().delete());
    }

    /**
     * Test with <code>null</code> class.
     */
    @Test(expected = LionEngineException.class)
    public void testNullClass()
    {
        Assert.assertNotNull(new FeaturableConfig(null, "setup"));
    }

    /**
     * Test with <code>null</code> setup.
     */
    @Test(expected = LionEngineException.class)
    public void testNullSetup()
    {
        Assert.assertNotNull(new FeaturableConfig("class", null));
    }

    /**
     * Test equals.
     */
    @Test
    public void testEquals()
    {
        final FeaturableConfig config = new FeaturableConfig("class", "setup");

        Assert.assertEquals(config, config);
        Assert.assertEquals(config, new FeaturableConfig("class", "setup"));

        Assert.assertNotEquals(config, null);
        Assert.assertNotEquals(config, new Object());
        Assert.assertNotEquals(config, new FeaturableConfig("", "setup"));
        Assert.assertNotEquals(config, new FeaturableConfig("class", ""));
    }

    /**
     * Test hash code.
     */
    @Test
    public void testHashCode()
    {
        final int hash = new FeaturableConfig("class", "setup").hashCode();

        Assert.assertEquals(hash, new FeaturableConfig("class", "setup").hashCode());

        Assert.assertNotEquals(hash, new FeaturableConfig("", "setup").hashCode());
        Assert.assertNotEquals(hash, new FeaturableConfig("class", "").hashCode());
    }

    /**
     * Test to string.
     */
    @Test
    public void testToString()
    {
        final FeaturableConfig config = new FeaturableConfig("clazz", "setup");

        Assert.assertEquals("FeaturableConfig [clazz=clazz, setup=setup]", config.toString());
    }
}
