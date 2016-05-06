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

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.b3dgs.lionengine.Constant;
import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.core.Medias;
import com.b3dgs.lionengine.game.Axis;
import com.b3dgs.lionengine.game.Camera;
import com.b3dgs.lionengine.game.Services;
import com.b3dgs.lionengine.game.map.MapTileGame;
import com.b3dgs.lionengine.game.map.MapTileGroupModel;
import com.b3dgs.lionengine.stream.Xml;
import com.b3dgs.lionengine.stream.XmlNode;

/**
 * Test the collision group configuration class.
 */
public class CollisionGroupConfigTest
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
     * Test the group import export.
     */
    @Test
    public void testGroup()
    {
        final XmlNode root = Xml.create("groups");
        CollisionGroupConfig.exports(root, group);
        final Media config = Medias.create("groups.xml");
        Xml.save(root, config);
        final CollisionGroupConfig groups = CollisionGroupConfig.imports(config);

        Assert.assertEquals(group, groups.getGroups().values().iterator().next());
        Assert.assertTrue(config.getFile().delete());
    }

    /**
     * Test the group import export with map.
     */
    @Test
    public void testMap()
    {
        final XmlNode root = Xml.create("groups");
        CollisionGroupConfig.exports(root, group);
        final Media groupsConfig = Medias.create("groups.xml");
        Xml.save(root, groupsConfig);

        final Media formulasConfig = UtilConfig.createFormulaConfig(formula);
        mapCollision.loadCollisions(formulasConfig, groupsConfig);

        final CollisionGroupConfig groups = CollisionGroupConfig.imports(groupsConfig);

        Assert.assertEquals(group, groups.getGroups().values().iterator().next());
        Assert.assertTrue(groupsConfig.getFile().delete());
        Assert.assertTrue(formulasConfig.getFile().delete());
    }

    /**
     * Test the has and remove functions.
     */
    @Test
    public void testHasRemove()
    {
        final XmlNode root = Xml.create("groups");
        CollisionGroupConfig.exports(root, group);

        Assert.assertFalse(CollisionGroupConfig.has(root, "void"));
        Assert.assertTrue(CollisionGroupConfig.has(root, "group"));

        CollisionGroupConfig.remove(root, "void");
        CollisionGroupConfig.remove(root, "group");

        Assert.assertFalse(CollisionGroupConfig.has(root, "group"));
    }
}
