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
package com.b3dgs.lionengine.game.map.transition;

import static com.b3dgs.lionengine.game.map.UtilMap.GROUND;
import static com.b3dgs.lionengine.game.map.UtilMap.SHEET;
import static com.b3dgs.lionengine.game.map.UtilMap.TILE_GROUND;
import static com.b3dgs.lionengine.game.map.UtilMap.TILE_TRANSITION;
import static com.b3dgs.lionengine.game.map.UtilMap.TILE_WATER;
import static com.b3dgs.lionengine.game.map.UtilMap.WATER;

import java.util.Collection;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

import com.b3dgs.lionengine.game.map.MapTile;
import com.b3dgs.lionengine.game.map.UtilMap;
import com.b3dgs.lionengine.game.tile.TileRef;

/**
 * Test the transitions extractor class.
 */
public class TransitionsExtractorTest
{
    /**
     * Test the transitions extraction.
     */
    @Test
    public void testExtraction()
    {
        final MapTile map = UtilMap.createMap(7);
        UtilMap.fill(map, TILE_WATER);
        UtilMap.fill(map, TILE_GROUND, TILE_TRANSITION, 3);

        final MapTile map2 = UtilMap.createMap(7);
        UtilMap.fill(map2, TILE_GROUND);
        UtilMap.fill(map2, TILE_WATER, TILE_TRANSITION, 3);

        final MapTile map3 = UtilMap.createMap(3);

        final TransitionsExtractor extractor = new TransitionsExtractorImpl();
        final Map<Transition, Collection<TileRef>> transitions = extractor.getTransitions(map, map2, map3);

        has(transitions, TransitionType.UP_LEFT, WATER, GROUND, TILE_TRANSITION);
        has(transitions, TransitionType.UP_LEFT, GROUND, WATER, TILE_TRANSITION);

        has(transitions, TransitionType.UP, WATER, GROUND, TILE_TRANSITION);
        has(transitions, TransitionType.UP, GROUND, WATER, TILE_TRANSITION);

        has(transitions, TransitionType.UP_RIGHT, WATER, GROUND, TILE_TRANSITION);
        has(transitions, TransitionType.UP_RIGHT, GROUND, WATER, TILE_TRANSITION);

        has(transitions, TransitionType.DOWN_LEFT, WATER, GROUND, TILE_TRANSITION);
        has(transitions, TransitionType.DOWN_LEFT, GROUND, WATER, TILE_TRANSITION);

        has(transitions, TransitionType.DOWN, WATER, GROUND, TILE_TRANSITION);
        has(transitions, TransitionType.DOWN, GROUND, WATER, TILE_TRANSITION);

        has(transitions, TransitionType.DOWN_RIGHT, WATER, GROUND, TILE_TRANSITION);
        has(transitions, TransitionType.DOWN_RIGHT, GROUND, WATER, TILE_TRANSITION);

        has(transitions, TransitionType.LEFT, WATER, GROUND, TILE_TRANSITION);
        has(transitions, TransitionType.LEFT, GROUND, WATER, TILE_TRANSITION);

        has(transitions, TransitionType.RIGHT, WATER, GROUND, TILE_TRANSITION);
        has(transitions, TransitionType.RIGHT, GROUND, WATER, TILE_TRANSITION);

        has(transitions, TransitionType.CORNER_UP_LEFT, WATER, GROUND, TILE_TRANSITION);
        has(transitions, TransitionType.CORNER_UP_LEFT, GROUND, WATER, TILE_TRANSITION);

        has(transitions, TransitionType.CORNER_UP_RIGHT, WATER, GROUND, TILE_TRANSITION);
        has(transitions, TransitionType.CORNER_UP_RIGHT, GROUND, WATER, TILE_TRANSITION);

        has(transitions, TransitionType.CORNER_DOWN_LEFT, WATER, GROUND, TILE_TRANSITION);
        has(transitions, TransitionType.CORNER_DOWN_LEFT, GROUND, WATER, TILE_TRANSITION);

        has(transitions, TransitionType.CORNER_DOWN_RIGHT, WATER, GROUND, TILE_TRANSITION);
        has(transitions, TransitionType.CORNER_DOWN_RIGHT, GROUND, WATER, TILE_TRANSITION);

        has(transitions, TransitionType.CENTER, WATER, WATER, TILE_WATER);
        has(transitions, TransitionType.CENTER, GROUND, GROUND, TILE_GROUND);
    }

    /**
     * Check the transition with tile.
     * 
     * @param transitions The transitions found.
     * @param type The transition type.
     * @param in The group in.
     * @param out The group out.
     * @param number The tile number.
     */
    private void has(Map<Transition, Collection<TileRef>> transitions,
                     TransitionType type,
                     String in,
                     String out,
                     int number)
    {
        final Transition transition = new Transition(type, in, out);
        final TileRef tile = new TileRef(SHEET, number);
        Assert.assertTrue(transitions.get(transition).contains(tile));
    }
}
