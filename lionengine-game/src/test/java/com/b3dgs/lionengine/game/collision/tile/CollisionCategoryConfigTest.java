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
package com.b3dgs.lionengine.game.collision.tile;

import java.util.Arrays;
import java.util.Collection;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.b3dgs.lionengine.Constant;
import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.core.Medias;
import com.b3dgs.lionengine.game.Axis;
import com.b3dgs.lionengine.game.Configurer;
import com.b3dgs.lionengine.game.camera.Camera;
import com.b3dgs.lionengine.game.handler.Services;
import com.b3dgs.lionengine.game.map.MapTileGame;
import com.b3dgs.lionengine.game.map.feature.group.MapTileGroupModel;
import com.b3dgs.lionengine.stream.Xml;
import com.b3dgs.lionengine.stream.XmlNode;
import com.b3dgs.lionengine.test.UtilTests;

/**
 * Test the collision category configuration class.
 */
public class CollisionCategoryConfigTest
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

    /** Formula test. */
    private final CollisionFormula formula = new CollisionFormula("formula",
                                                                  new CollisionRange(Axis.X, 0, 1, 2, 3),
                                                                  new CollisionFunctionLinear(1.0, 2.0),
                                                                  new CollisionConstraint());
    /** Group test. */
    private final CollisionGroup group = new CollisionGroup("group", Arrays.asList(formula));
    /** Category test. */
    private final CollisionCategory category = new CollisionCategory("name", Axis.X, 1, 2, Arrays.asList(group));
    /** Map collision. */
    private MapTileCollision mapCollision;

    /**
     * Prepare test.
     */
    @Before
    public void prepare()
    {
        final Services services = new Services();
        services.add(new Camera());
        services.add(new MapTileGame());
        services.add(new MapTileGroupModel());
        mapCollision = new MapTileCollisionModel(services);
    }

    /**
     * Test the constructor.
     * 
     * @throws Exception If error.
     */
    @Test(expected = LionEngineException.class)
    public void testConstructor() throws Exception
    {
        UtilTests.testPrivateConstructor(CollisionCategoryConfig.class);
    }

    /**
     * Test the import export.
     */
    @Test
    public void testCategory()
    {
        final XmlNode root = Xml.create("categories");
        CollisionCategoryConfig.exports(root, category);
        CollisionCategoryConfig.exports(root, new CollisionCategory("name2", Axis.X, 1, 2, Arrays.asList(group)));

        final Collection<CollisionCategory> imported = CollisionCategoryConfig.imports(root);
        Assert.assertEquals(category, imported.iterator().next());
    }

    /**
     * Test the import from configurer.
     */
    @Test
    public void testConfigurer()
    {
        final Media formulasConfig = UtilConfig.createFormulaConfig(formula);
        final Media groupsConfig = UtilConfig.createGroupsConfig(group);
        mapCollision.loadCollisions(formulasConfig, groupsConfig);

        final XmlNode root = Xml.create("categories");
        CollisionCategoryConfig.exports(root, category);
        final Media media = Medias.create("object.xml");
        Xml.save(root, media);
        final Collection<CollisionCategory> imported = CollisionCategoryConfig.imports(new Configurer(media),
                                                                                       mapCollision);

        Assert.assertEquals(category, imported.iterator().next());
        Assert.assertTrue(formulasConfig.getFile().delete());
        Assert.assertTrue(groupsConfig.getFile().delete());
        Assert.assertTrue(media.getFile().delete());
    }

    /**
     * Test the import from map.
     */
    @Test
    public void testMap()
    {
        final Media formulasConfig = UtilConfig.createFormulaConfig(formula);
        final Media groupsConfig = UtilConfig.createGroupsConfig(group);
        mapCollision.loadCollisions(formulasConfig, groupsConfig);

        final XmlNode root = Xml.create("categories");
        CollisionCategoryConfig.exports(root, category);
        final CollisionCategory imported = CollisionCategoryConfig.imports(root.getChild(Configurer.PREFIX
                                                                                         + "category"),
                                                                           mapCollision);

        Assert.assertEquals(category, imported);
        Assert.assertTrue(formulasConfig.getFile().delete());
        Assert.assertTrue(groupsConfig.getFile().delete());
    }

    /**
     * Test the import from map with invalid axis.
     */
    @Test(expected = LionEngineException.class)
    public void testMapInvalidAxis()
    {
        final Media formulasConfig = UtilConfig.createFormulaConfig(formula);
        final Media groupsConfig = UtilConfig.createGroupsConfig(group);
        mapCollision.loadCollisions(formulasConfig, groupsConfig);

        final XmlNode root = Xml.create("categories");
        CollisionCategoryConfig.exports(root, category);
        root.getChild(Configurer.PREFIX + "category").writeString(CollisionCategoryConfig.AXIS, "void");
        final CollisionCategory imported = CollisionCategoryConfig.imports(root.getChild(Configurer.PREFIX
                                                                                         + "category"),
                                                                           mapCollision);

        Assert.assertTrue(formulasConfig.getFile().delete());
        Assert.assertTrue(groupsConfig.getFile().delete());
        Assert.assertNull(imported);
        Assert.fail();
    }
}
