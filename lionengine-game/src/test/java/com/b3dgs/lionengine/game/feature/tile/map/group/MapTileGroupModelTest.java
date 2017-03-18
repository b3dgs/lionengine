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
package com.b3dgs.lionengine.game.feature.tile.map.group;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.b3dgs.lionengine.Constant;
import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.core.Medias;
import com.b3dgs.lionengine.game.Featurable;
import com.b3dgs.lionengine.game.FeaturableModel;
import com.b3dgs.lionengine.game.feature.tile.Tile;
import com.b3dgs.lionengine.game.feature.tile.TileGame;
import com.b3dgs.lionengine.game.feature.tile.TileGroup;
import com.b3dgs.lionengine.game.feature.tile.TileGroupType;
import com.b3dgs.lionengine.game.feature.tile.TileGroupsConfig;
import com.b3dgs.lionengine.game.feature.tile.TileRef;
import com.b3dgs.lionengine.game.feature.tile.map.MapTileGroup;
import com.b3dgs.lionengine.game.feature.tile.map.MapTileGroupModel;

/**
 * Test the map tile group class.
 */
public class MapTileGroupModelTest
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

    private final MapTileGroup mapGroup = new MapTileGroupModel();
    private final Featurable featurable = new FeaturableModel();
    private final Tile tile = new TileGame(Integer.valueOf(0), 0, 0, 0, 1, 1);

    /**
     * Prepare test.
     */
    @Before
    public void prepare()
    {
        mapGroup.prepare(featurable);
    }

    /**
     * Test the map tile with default group.
     */
    @Test
    public void testDefault()
    {
        Assert.assertTrue(mapGroup.getGroup(Constant.EMPTY_STRING).isEmpty());
        Assert.assertEquals(MapTileGroupModel.NO_GROUP_NAME, mapGroup.getGroup(tile));
        Assert.assertEquals(MapTileGroupModel.NO_GROUP_NAME, mapGroup.getGroup(new TileRef(0, 0)));
        Assert.assertEquals(MapTileGroupModel.NO_GROUP_NAME, mapGroup.getGroup(Integer.valueOf(0), 0));
        Assert.assertTrue(mapGroup.getGroups().containsAll(Arrays.asList(MapTileGroupModel.NO_GROUP_NAME)));
        Assert.assertNull(mapGroup.getGroupsConfig());

        Assert.assertEquals(TileGroupType.NONE, mapGroup.getType(Constant.EMPTY_STRING));
        Assert.assertEquals(TileGroupType.NONE, mapGroup.getType(tile));
    }

    /**
     * Test the map tile with custom group.
     */
    @Test
    public void testCustom()
    {
        mapGroup.changeGroup(tile, "water");

        Assert.assertEquals(new TileRef(tile), mapGroup.getGroup("water").iterator().next());
        Assert.assertEquals("water", mapGroup.getGroup(tile));
        Assert.assertEquals("water", mapGroup.getGroup(new TileRef(0, 0)));
        Assert.assertEquals("water", mapGroup.getGroup(Integer.valueOf(0), 0));
        Assert.assertTrue(mapGroup.getGroups().containsAll(Arrays.asList("water")));

        mapGroup.changeGroup(tile, "tree");

        Assert.assertEquals(new TileRef(tile), mapGroup.getGroup("tree").iterator().next());
        Assert.assertEquals("tree", mapGroup.getGroup(tile));
        Assert.assertEquals("tree", mapGroup.getGroup(new TileRef(0, 0)));
        Assert.assertEquals("tree", mapGroup.getGroup(Integer.valueOf(0), 0));
        Assert.assertTrue(mapGroup.getGroups().containsAll(Arrays.asList("water", "tree")));

        mapGroup.changeGroup(tile, null);

        Assert.assertFalse(mapGroup.getGroup(MapTileGroupModel.NO_GROUP_NAME).iterator().hasNext());
        Assert.assertEquals(MapTileGroupModel.NO_GROUP_NAME, mapGroup.getGroup(tile));
        Assert.assertEquals(MapTileGroupModel.NO_GROUP_NAME, mapGroup.getGroup(new TileRef(0, 0)));
        Assert.assertEquals(MapTileGroupModel.NO_GROUP_NAME, mapGroup.getGroup(Integer.valueOf(0), 0));
    }

    /**
     * Test the map tile with loaded media.
     */
    @Test
    public void testLoadMedia()
    {
        final Media configGroups = Medias.create("groups.xml");
        final Collection<TileGroup> groups = new ArrayList<TileGroup>();
        groups.add(new TileGroup("water", TileGroupType.PLAIN, Arrays.asList(new TileRef(0, 0))));
        TileGroupsConfig.exports(configGroups, groups);

        mapGroup.loadGroups(configGroups);

        Assert.assertEquals(new TileRef(tile), mapGroup.getGroup("water").iterator().next());
        Assert.assertEquals("water", mapGroup.getGroup(tile));
        Assert.assertEquals("water", mapGroup.getGroup(new TileRef(0, 0)));
        Assert.assertEquals("water", mapGroup.getGroup(Integer.valueOf(0), 0));
        Assert.assertTrue(mapGroup.getGroups().containsAll(Arrays.asList("water")));
        Assert.assertEquals(TileGroupType.PLAIN, mapGroup.getType("water"));
        Assert.assertEquals(TileGroupType.PLAIN, mapGroup.getType(tile));
    }
}
