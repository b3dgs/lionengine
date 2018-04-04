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
package com.b3dgs.lionengine.game.feature.collidable;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.Medias;
import com.b3dgs.lionengine.UtilTests;
import com.b3dgs.lionengine.ViewerMock;
import com.b3dgs.lionengine.Xml;
import com.b3dgs.lionengine.game.Configurer;
import com.b3dgs.lionengine.game.feature.Services;
import com.b3dgs.lionengine.game.feature.Setup;

/**
 * Test {@link CollidableConfig}.
 */
public final class CollidableConfigTest
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
     * Test constructor.
     * 
     * @throws Exception If error.
     */
    @Test(expected = LionEngineException.class)
    public void testConstructor() throws Exception
    {
        UtilTests.testPrivateConstructor(CollidableConfig.class);
    }

    /**
     * Test exports imports.
     */
    @Test
    public void testExportsImports()
    {
        final String group = "1";
        final Media media = Medias.create("object.xml");
        final Xml root = new Xml("test");
        final Xml node = root.createChild("group");
        node.setText(group);
        root.save(media);

        Assert.assertEquals(Integer.valueOf(group), CollidableConfig.imports(new Configurer(media)));

        Assert.assertTrue(media.getFile().delete());
    }

    /**
     * Test with invalid group.
     */
    @Test(expected = LionEngineException.class)
    public void testInvalidGroup()
    {
        final Media media = Medias.create("object.xml");
        final Xml root = new Xml("test");
        final Xml node = root.createChild("group");
        node.setText("a");
        root.save(media);

        Assert.assertNotNull(CollidableConfig.imports(new Configurer(media)));

        Assert.assertTrue(media.getFile().delete());
    }

    /**
     * Test with default group.
     */
    @Test
    public void testDefaultGroup()
    {
        final Media media = Medias.create("object.xml");
        final Xml root = new Xml("test");
        root.save(media);

        Assert.assertEquals(CollidableConfig.DEFAULT_GROUP, CollidableConfig.imports(new Configurer(media)));

        Assert.assertTrue(media.getFile().delete());
    }

    /**
     * Test export.
     */
    @Test
    public void testExport()
    {
        final Media media = Medias.create("object.xml");
        final Xml root = new Xml("test");
        root.save(media);

        final Services services = new Services();
        services.add(new ViewerMock());

        final Collidable collidable = new CollidableModel(services, new Setup(media));
        collidable.setGroup(1);
        CollidableConfig.exports(root, collidable);

        root.save(media);

        Assert.assertEquals(Integer.valueOf(1), CollidableConfig.imports(new Configurer(media)));

        Assert.assertTrue(media.getFile().delete());
    }
}
