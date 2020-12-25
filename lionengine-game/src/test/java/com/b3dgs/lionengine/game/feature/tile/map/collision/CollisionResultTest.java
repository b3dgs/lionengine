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
package com.b3dgs.lionengine.game.feature.tile.map.collision;

import static com.b3dgs.lionengine.UtilAssert.assertEquals;
import static com.b3dgs.lionengine.UtilAssert.assertFalse;
import static com.b3dgs.lionengine.UtilAssert.assertTrue;

import org.junit.jupiter.api.Test;

import com.b3dgs.lionengine.game.feature.tile.Tile;
import com.b3dgs.lionengine.game.feature.tile.TileGame;

/**
 * Test {@link CollisionResult}.
 */
final class CollisionResultTest
{
    /** Range test. */
    private final CollisionRange range = new CollisionRange(Axis.X, 0, 1, 2, 3);
    /** Function test. */
    private final CollisionFunction function = new CollisionFunctionLinear(1.0, 2.0);
    /** Constraint test. */
    private final CollisionConstraint constaint = new CollisionConstraint();
    /** Formula test. */
    private final CollisionFormula formulaX = new CollisionFormula("formulaX", range, function, constaint);
    /** Formula test. */
    private final CollisionFormula formulaY = new CollisionFormula("formulaY", range, function, constaint);

    /**
     * Test the collision result.
     */
    @Test
    void testResult()
    {
        final Double x = Double.valueOf(1.0);
        final Double y = Double.valueOf(2.0);
        final Tile tile = new TileGame(1, 3, 4, 1, 1);
        final CollisionResult result = new CollisionResult(x, y, tile, formulaX, formulaY);

        assertEquals(x, result.getX());
        assertEquals(y, result.getY());
        assertEquals(tile, result.getTile());
        assertTrue(result.startWithX("formula"));
        assertFalse(result.startWithY("formulaZ"));
        assertTrue(result.endWithX("X"));
        assertFalse(result.endWithY("Z"));
        assertTrue(result.contains("formula"));
        assertFalse(result.contains("formulaZ"));
        assertTrue(result.containsX("formulaX"));
        assertFalse(result.containsY("formulaZ"));
    }

    /**
     * Test the collision no result.
     */
    @Test
    void testNoResult()
    {
        final Tile tile = new TileGame(1, 3, 4, 1, 1);
        CollisionResult result = new CollisionResult(null, null, tile, null, null);

        assertFalse(result.startWithX("formula"));
        assertFalse(result.startWithY("formulaZ"));

        assertFalse(result.contains("formula"));

        result = new CollisionResult(null, null, tile, formulaX, null);

        assertTrue(result.startWithX("formula"));
        assertFalse(result.startWithX("formulaZ"));
        assertFalse(result.startWithY("formula"));
        assertFalse(result.startWithY("formulaZ"));

        assertTrue(result.contains("formula"));
        assertFalse(result.contains("formulaZ"));

        result = new CollisionResult(null, null, tile, null, formulaY);

        assertFalse(result.startWithX("formula"));
        assertFalse(result.startWithX("formulaZ"));
        assertTrue(result.startWithY("formula"));
        assertFalse(result.startWithY("formulaZ"));

        assertTrue(result.contains("formula"));
        assertFalse(result.contains("formulaZ"));
    }

    /**
     * Test the to string.
     */
    @Test
    void testToString()
    {
        assertEquals("CollisionResult [x=1.0, y=2.0, fx=formulaX, fy=formulaY]",
                     new CollisionResult(Double.valueOf(1.0),
                                         Double.valueOf(2.0),
                                         new TileGame(1, 3, 4, 1, 1),
                                         formulaX,
                                         formulaY).toString());
    }
}
