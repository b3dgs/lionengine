/*
 * Copyright (C) 2013-2020 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.game.feature.tile.map.pathfinding;

import static com.b3dgs.lionengine.UtilAssert.assertEquals;
import static com.b3dgs.lionengine.UtilAssert.assertNotNull;
import static com.b3dgs.lionengine.UtilAssert.assertPrivateConstructor;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.b3dgs.lionengine.Medias;
import com.b3dgs.lionengine.game.feature.Services;
import com.b3dgs.lionengine.game.feature.tile.map.MapTile;
import com.b3dgs.lionengine.game.feature.tile.map.MapTileGame;
import com.b3dgs.lionengine.game.feature.tile.map.MapTileGroupModel;

/**
 * Test {@link com.b3dgs.lionengine.game.feature.tile.map.pathfinding.Astar}.
 */
public final class AstarTest
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

    private final Services services = new Services();

    /**
     * Test the constructor.
     */
    @Test
    public void testConstructor()
    {
        assertPrivateConstructor(Astar.class);
    }

    /**
     * Test the create path finder.
     */
    @Test
    public void testCreatePathFinder()
    {
        final MapTile map = services.add(new MapTileGame());
        map.addFeature(new MapTileGroupModel());
        map.addFeature(new MapTilePathModel());

        assertNotNull(Astar.createPathFinder(map, 1, new HeuristicClosest()));
    }

    /**
     * Test the create heuristic closest.
     */
    @Test
    public void testCreateHeuristicClosest()
    {
        assertEquals(HeuristicClosest.class, Astar.createHeuristicClosest().getClass());
    }

    /**
     * Test the create heuristic closest squared.
     */
    @Test
    public void testCreateHeuristicClosestSquared()
    {
        assertEquals(HeuristicClosestSquared.class, Astar.createHeuristicClosestSquared().getClass());
    }

    /**
     * Test the create heuristic Manhattan.
     */
    @Test
    public void testCreateHeuristicManhattan()
    {
        assertEquals(HeuristicManhattan.class, Astar.createHeuristicManhattan(1).getClass());
    }
}
