/*
 * Copyright (C) 2013-2019 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */
package com.b3dgs.lionengine.game.feature.tile.map.group;

import static com.b3dgs.lionengine.UtilAssert.assertEquals;
import static com.b3dgs.lionengine.UtilAssert.assertFalse;
import static com.b3dgs.lionengine.UtilAssert.assertNull;
import static com.b3dgs.lionengine.UtilAssert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.b3dgs.lionengine.Constant;
import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.Medias;
import com.b3dgs.lionengine.game.feature.Featurable;
import com.b3dgs.lionengine.game.feature.FeaturableModel;
import com.b3dgs.lionengine.game.feature.tile.Tile;
import com.b3dgs.lionengine.game.feature.tile.TileGame;
import com.b3dgs.lionengine.game.feature.tile.TileGroup;
import com.b3dgs.lionengine.game.feature.tile.TileGroupType;
import com.b3dgs.lionengine.game.feature.tile.TileGroupsConfig;
import com.b3dgs.lionengine.game.feature.tile.TileRef;
import com.b3dgs.lionengine.game.feature.tile.map.MapTileGroup;
import com.b3dgs.lionengine.game.feature.tile.map.MapTileGroupModel;

/**
 * Test {@link MapTileGroupModel}.
 */
public final class MapTileGroupModelTest
{
    /**
     * Prepare test.
     */
    @BeforeAll
    public static void beforeTests()
    {
        Medias.setResourcesDirectory(System.getProperty("java.io.tmpdir"));
    }

    /**
     * Clean up test.
     */
    @AfterAll
    public static void afterTests()
    {
        Medias.setResourcesDirectory(null);
    }

    private final MapTileGroup mapGroup = new MapTileGroupModel();
    private final Featurable featurable = new FeaturableModel();
    private final Tile tile = new TileGame(Integer.valueOf(0), 0, 0, 0, 1, 1);

    /**
     * Prepare test.
     */
    @BeforeEach
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
        assertTrue(mapGroup.getGroup(Constant.EMPTY_STRING).isEmpty());
        assertEquals(MapTileGroupModel.NO_GROUP_NAME, mapGroup.getGroup(tile));
        assertEquals(MapTileGroupModel.NO_GROUP_NAME, mapGroup.getGroup(new TileRef(0, 0)));
        assertEquals(MapTileGroupModel.NO_GROUP_NAME, mapGroup.getGroup(Integer.valueOf(0), 0));
        assertTrue(mapGroup.getGroups().containsAll(Arrays.asList(MapTileGroupModel.NO_GROUP_NAME)));
        assertNull(mapGroup.getGroupsConfig());

        assertEquals(TileGroupType.NONE, mapGroup.getType(Constant.EMPTY_STRING));
        assertEquals(TileGroupType.NONE, mapGroup.getType(tile));
    }

    /**
     * Test the map tile with custom group.
     */
    @Test
    public void testCustom()
    {
        mapGroup.changeGroup(tile, "water");

        assertEquals(new TileRef(tile), mapGroup.getGroup("water").iterator().next());
        assertEquals("water", mapGroup.getGroup(tile));
        assertEquals("water", mapGroup.getGroup(new TileRef(0, 0)));
        assertEquals("water", mapGroup.getGroup(Integer.valueOf(0), 0));
        assertTrue(mapGroup.getGroups().containsAll(Arrays.asList("water")));

        mapGroup.changeGroup(tile, "tree");

        assertEquals(new TileRef(tile), mapGroup.getGroup("tree").iterator().next());
        assertEquals("tree", mapGroup.getGroup(tile));
        assertEquals("tree", mapGroup.getGroup(new TileRef(0, 0)));
        assertEquals("tree", mapGroup.getGroup(Integer.valueOf(0), 0));
        assertTrue(mapGroup.getGroups().containsAll(Arrays.asList("water", "tree")));

        mapGroup.changeGroup(tile, null);

        assertFalse(mapGroup.getGroup(MapTileGroupModel.NO_GROUP_NAME).iterator().hasNext());
        assertEquals(MapTileGroupModel.NO_GROUP_NAME, mapGroup.getGroup(tile));
        assertEquals(MapTileGroupModel.NO_GROUP_NAME, mapGroup.getGroup(new TileRef(0, 0)));
        assertEquals(MapTileGroupModel.NO_GROUP_NAME, mapGroup.getGroup(Integer.valueOf(0), 0));
    }

    /**
     * Test the map tile with loaded media.
     */
    @Test
    public void testLoadMedia()
    {
        final Media configGroups = Medias.create("groups.xml");
        final Collection<TileGroup> groups = new ArrayList<>();
        groups.add(new TileGroup("water", TileGroupType.PLAIN, Arrays.asList(new TileRef(0, 0))));
        TileGroupsConfig.exports(configGroups, groups);

        mapGroup.loadGroups(configGroups);

        assertEquals(new TileRef(tile), mapGroup.getGroup("water").iterator().next());
        assertEquals("water", mapGroup.getGroup(tile));
        assertEquals("water", mapGroup.getGroup(new TileRef(0, 0)));
        assertEquals("water", mapGroup.getGroup(Integer.valueOf(0), 0));
        assertTrue(mapGroup.getGroups().containsAll(Arrays.asList("water")));
        assertEquals(TileGroupType.PLAIN, mapGroup.getType("water"));
        assertEquals(TileGroupType.PLAIN, mapGroup.getType(tile));
    }
}
