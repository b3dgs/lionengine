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
package com.b3dgs.lionengine.game.object;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import com.b3dgs.lionengine.Constant;
import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.core.Medias;
import com.b3dgs.lionengine.stream.Xml;
import com.b3dgs.lionengine.stream.XmlNode;

/**
 * Test the object configuration.
 */
public class ObjectConfigTest
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
        final String clazz = "class";
        final String setup = "setup";
        final ObjectConfig config = new ObjectConfig(clazz, setup);

        final Media media = Medias.create("object.xml");
        try
        {
            final XmlNode root = Xml.create("test");
            root.add(ObjectConfig.exportClass(clazz));
            root.add(ObjectConfig.exportSetup(setup));
            Xml.save(root, media);

            final ObjectConfig loaded = ObjectConfig.imports(Xml.load(media));
            Assert.assertEquals(config, loaded);
            Assert.assertEquals(config, ObjectConfig.imports(new Configurer(media)));
        }
        finally
        {
            Assert.assertTrue(media.getFile().delete());
        }
    }

    /**
     * Test the configuration with <code>null</code> class.
     */
    @Test(expected = LionEngineException.class)
    public void testConfigNullClass()
    {
        Assert.assertNotNull(new ObjectConfig(null, "setup"));
    }

    /**
     * Test the configuration with <code>null</code> setup.
     */
    @Test(expected = LionEngineException.class)
    public void testConfigNullSetup()
    {
        Assert.assertNotNull(new ObjectConfig("class", null));
    }

    /**
     * Test the hash code.
     */
    @Test
    public void testHashCode()
    {
        final int hash = new ObjectConfig("class", "setup").hashCode();

        Assert.assertEquals(hash, new ObjectConfig("class", "setup").hashCode());
        Assert.assertNotEquals(hash, new ObjectConfig("", "setup").hashCode());
        Assert.assertNotEquals(hash, new ObjectConfig("class", "").hashCode());
    }

    /**
     * Test the equality.
     */
    @Test
    public void testEquals()
    {
        final ObjectConfig config = new ObjectConfig("class", "setup");

        Assert.assertEquals(config, config);
        Assert.assertNotEquals(config, null);
        Assert.assertNotEquals(config, new Object());
        Assert.assertEquals(config, new ObjectConfig("class", "setup"));
        Assert.assertNotEquals(config, new ObjectConfig("", "setup"));
        Assert.assertNotEquals(config, new ObjectConfig("class", ""));
    }
}
