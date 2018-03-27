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

import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.core.Medias;
import com.b3dgs.lionengine.game.Configurer;
import com.b3dgs.lionengine.io.Xml;
import com.b3dgs.lionengine.util.UtilTests;

/**
 * Test {@link PathfindableConfig}.
 */
public final class PathfindableConfigTest
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
        UtilTests.testPrivateConstructor(PathfindableConfig.class);
    }

    /**
     * Test exports imports.
     */
    @Test
    public void testExportsImports()
    {
        final Map<String, PathData> map = new HashMap<>();
        final PathData data = new PathData("category", 1.0, true, Arrays.asList(MovementTile.UP));
        map.put(data.getName(), data);

        final Xml root = new Xml("test");
        root.add(PathfindableConfig.exports(map));

        final Media media = Medias.create("pathfindable.xml");
        root.save(media);

        Assert.assertEquals(map, PathfindableConfig.imports(new Configurer(media)));

        Assert.assertTrue(media.getFile().delete());
    }

    /**
     * Test without node.
     */
    @Test
    public void testNoNode()
    {
        final Xml root = new Xml("test");
        final Media media = Medias.create("pathfindable.xml");
        root.save(media);

        Assert.assertTrue(PathfindableConfig.imports(new Configurer(media)).isEmpty());

        Assert.assertTrue(media.getFile().delete());
    }

    /**
     * Test without movements.
     */
    @Test
    public void testNoMovements()
    {
        final Map<String, PathData> map = new HashMap<>();
        final PathData data = new PathData("category", 1.0, true, EnumSet.noneOf(MovementTile.class));
        map.put(data.getName(), data);

        final Xml root = new Xml("test");
        root.add(PathfindableConfig.exports(map));

        final Media media = Medias.create("pathfindable.xml");
        root.save(media);

        final Map<String, PathData> imported = PathfindableConfig.imports(new Configurer(media));

        Assert.assertTrue(imported.get(data.getName()).getAllowedMovements().isEmpty());
        Assert.assertEquals(map, imported);

        Assert.assertTrue(media.getFile().delete());
    }

    /**
     * Test with wrong movement.
     */
    @Test(expected = LionEngineException.class)
    public void testWrongMovement()
    {
        final PathData data = new PathData("category", 1.0, true, EnumSet.noneOf(MovementTile.class));
        final Xml path = PathfindableConfig.exportPathData(data);
        final Xml movement = path.createChild(PathfindableConfig.NODE_MOVEMENT);
        movement.setText("VOID");

        final Xml root = new Xml("test");
        final Xml node = root.createChild(PathfindableConfig.NODE_PATHFINDABLE);
        node.add(path);

        final Media media = Medias.create("pathfindable.xml");
        root.save(media);

        Assert.assertNull(PathfindableConfig.imports(new Configurer(media)));

        Assert.assertTrue(media.getFile().delete());
    }
}
