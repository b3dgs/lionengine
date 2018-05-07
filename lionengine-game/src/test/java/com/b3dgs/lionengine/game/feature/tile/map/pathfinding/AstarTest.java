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

import static com.b3dgs.lionengine.UtilAssert.assertEquals;
import static com.b3dgs.lionengine.UtilAssert.assertNotNull;
import static com.b3dgs.lionengine.UtilAssert.assertPrivateConstructor;

import org.junit.jupiter.api.Test;

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
        final Services services = new Services();
        final MapTile map = services.add(new MapTileGame());
        map.addFeature(new MapTileGroupModel());
        map.addFeature(new MapTilePathModel(services));

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
