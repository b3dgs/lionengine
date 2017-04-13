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
package com.b3dgs.lionengine.game.feature.tile.map.pathfinding;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import com.b3dgs.lionengine.Constant;
import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.core.Medias;
import com.b3dgs.lionengine.game.Configurer;
import com.b3dgs.lionengine.io.Xml;
import com.b3dgs.lionengine.util.UtilTests;

/**
 * Test the pathfindable configuration.
 */
public class PathfindableConfigTest
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
        UtilTests.testPrivateConstructor(PathfindableConfig.class);
    }

    /**
     * Test the configuration reader.
     */
    @Test
    public void testConfig()
    {
        final Media media = Medias.create("pathfindable.xml");
        try
        {
            final Xml root = new Xml("test");
            final PathData data = new PathData("category", 1.0, true, Arrays.asList(MovementTile.UP));

            final Map<String, PathData> map = new HashMap<String, PathData>();
            map.put(data.getName(), data);
            root.add(PathfindableConfig.exports(map));
            root.save(media);

            final Map<String, PathData> loaded = PathfindableConfig.imports(new Configurer(media));

            Assert.assertEquals(map, loaded);
        }
        finally
        {
            Assert.assertTrue(media.getFile().delete());
        }
    }

    /**
     * Test the configuration reader without node.
     */
    @Test
    public void testConfigNoNode()
    {
        final Media media = Medias.create("pathfindable.xml");
        try
        {
            final Xml root = new Xml("test");
            root.save(media);

            Assert.assertTrue(PathfindableConfig.imports(new Configurer(media)).isEmpty());
        }
        finally
        {
            Assert.assertTrue(media.getFile().delete());
        }
    }

    /**
     * Test the configuration reader without movements.
     */
    @Test
    public void testConfigNoMovements()
    {
        final Media media = Medias.create("pathfindable.xml");
        try
        {
            final Xml root = new Xml("test");
            final PathData data = new PathData("category", 1.0, true, EnumSet.noneOf(MovementTile.class));

            final Map<String, PathData> map = new HashMap<String, PathData>();
            map.put(data.getName(), data);
            root.add(PathfindableConfig.exports(map));
            root.save(media);

            final Map<String, PathData> loaded = PathfindableConfig.imports(new Configurer(media));

            Assert.assertTrue(loaded.get(data.getName()).getAllowedMovements().isEmpty());
            Assert.assertEquals(map, loaded);
        }
        finally
        {
            Assert.assertTrue(media.getFile().delete());
        }
    }

    /**
     * Test the configuration reader with wrong movement.
     */
    @Test(expected = LionEngineException.class)
    public void testConfigWrongMovement()
    {
        final Media media = Medias.create("pathfindable.xml");
        try
        {
            final Xml root = new Xml("test");
            final PathData data = new PathData("category", 1.0, true, EnumSet.noneOf(MovementTile.class));
            final Xml node = root.createChild(PathfindableConfig.PATHFINDABLE);
            final Xml path = PathfindableConfig.exportPathData(data);
            final Xml movement = path.createChild(PathfindableConfig.MOVEMENT);
            movement.setText("VOID");
            node.add(path);
            root.save(media);

            Assert.assertNull(PathfindableConfig.imports(new Configurer(media)));
        }
        finally
        {
            Assert.assertTrue(media.getFile().delete());
        }
    }
}
