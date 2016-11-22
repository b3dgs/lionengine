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
package com.b3dgs.lionengine.game.collision.object;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import com.b3dgs.lionengine.Constant;
import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.core.Medias;
import com.b3dgs.lionengine.game.feature.Configurer;
import com.b3dgs.lionengine.game.feature.Setup;
import com.b3dgs.lionengine.stream.Xml;
import com.b3dgs.lionengine.stream.XmlNode;
import com.b3dgs.lionengine.test.UtilTests;

/**
 * Test the collidable configuration.
 */
public class CollidableConfigTest
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
     * Test the constructor.
     * 
     * @throws Exception If error.
     */
    @Test(expected = LionEngineException.class)
    public void testConstructor() throws Exception
    {
        UtilTests.testPrivateConstructor(CollidableConfig.class);
    }

    /**
     * Test the configuration import.
     */
    @Test
    public void testConfig()
    {
        final String group = "1";

        final Media media = Medias.create("object.xml");
        try
        {
            final XmlNode root = Xml.create("test");
            final XmlNode node = root.createChild("group");
            node.setText(group);
            Xml.save(root, media);

            final Integer id = CollidableConfig.imports(new Configurer(media));

            Assert.assertEquals(Integer.valueOf(group), id);
        }
        finally
        {
            Assert.assertTrue(media.getFile().delete());
        }
    }

    /**
     * Test the configuration with invalid group.
     */
    @Test(expected = LionEngineException.class)
    public void testConfigInvalidGroup()
    {
        final String group = "a";

        final Media media = Medias.create("object.xml");
        try
        {
            final XmlNode root = Xml.create("test");
            final XmlNode node = root.createChild("group");
            node.setText(group);
            Xml.save(root, media);

            Assert.assertNotNull(CollidableConfig.imports(new Configurer(media)));
        }
        finally
        {
            Assert.assertTrue(media.getFile().delete());
        }
    }

    /**
     * Test the configuration reader with default group.
     */
    @Test
    public void testConfigDefaultGroup()
    {
        final Media media = Medias.create("object.xml");
        try
        {
            final XmlNode root = Xml.create("test");
            Xml.save(root, media);

            final Integer id = CollidableConfig.imports(new Configurer(media));

            Assert.assertEquals(CollidableConfig.DEFAULT_GROUP, id);
        }
        finally
        {
            Assert.assertTrue(media.getFile().delete());
        }
    }

    /**
     * Test the configuration export.
     */
    @Test
    public void testExport()
    {
        final Media media = Medias.create("object.xml");
        try
        {
            final XmlNode root = Xml.create("test");
            Xml.save(root, media);

            final Collidable collidable = new CollidableModel(new Setup(media));
            collidable.setGroup(1);
            CollidableConfig.exports(root, collidable);

            Xml.save(root, media);

            final Integer id = CollidableConfig.imports(new Configurer(media));

            Assert.assertEquals(Integer.valueOf(1), id);
        }
        finally
        {
            Assert.assertTrue(media.getFile().delete());
        }
    }
}
