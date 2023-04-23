/*
 * Copyright (C) 2013-2023 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
import static com.b3dgs.lionengine.UtilAssert.assertHashEquals;
import static com.b3dgs.lionengine.UtilAssert.assertHashNotEquals;
import static com.b3dgs.lionengine.UtilAssert.assertNotEquals;

import org.junit.jupiter.api.Test;

/**
 * Test {@link Node}.
 */
final class NodeTest
{
    /**
     * Test the getters.
     */
    @Test
    void testGetters()
    {
        final Node node = new Node(0, 1);
        node.setCost(1.2);
        node.setDepth(1);
        node.setHeuristic(2.1);
        node.setParent(node);

        assertEquals(0, node.getX());
        assertEquals(1, node.getY());
        assertEquals(1.2, node.getCost());
        assertEquals(2, node.getDepth());
        assertEquals(2.1, node.getHeuristic());
        assertEquals(node, node.getParent());

        assertEquals(0, new Node(0, 0).setParent(null));
    }

    /**
     * Test the equality.
     */
    @Test
    void testEquals()
    {
        final Node node = new Node(0, 1);
        node.setDepth(1);

        assertEquals(node, node);

        assertNotEquals(node, null);
        assertNotEquals(node, new Object());
        assertEquals(node, new Node(0, 1));
        assertNotEquals(node, new Node(0, 0));
        assertNotEquals(node, new Node(1, 1));
        assertNotEquals(node, new Node(1, 0));
    }

    /**
     * Test the hash code.
     */
    @Test
    void testHashCode()
    {
        final Node hash = new Node(0, 1);

        assertHashEquals(hash, new Node(0, 1));

        assertHashNotEquals(hash, new Object());
        assertHashNotEquals(hash, new Node(0, 0));
        assertHashNotEquals(hash, new Node(1, 0));
        assertHashNotEquals(hash, new Node(1, 1));
    }

    /**
     * Test to string.
     */
    @Test
    void testToString()
    {
        final Node node = new Node(1, 2);

        assertEquals("Node [x=1, y=2, depth=0, cost=0.0, heuristic=0.0, parent=null]", node.toString());

        node.setParent(node);

        assertEquals("Node [x=1, y=2, depth=1, cost=0.0, heuristic=0.0, parent={x=1, y=2}]", node.toString());
    }
}
