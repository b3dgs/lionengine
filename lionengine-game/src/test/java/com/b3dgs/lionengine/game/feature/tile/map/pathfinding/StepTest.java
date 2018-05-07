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
import static com.b3dgs.lionengine.UtilAssert.assertHashEquals;
import static com.b3dgs.lionengine.UtilAssert.assertHashNotEquals;
import static com.b3dgs.lionengine.UtilAssert.assertNotEquals;

import org.junit.jupiter.api.Test;

/**
 * Test {@link Step}.
 */
public final class StepTest
{
    /**
     * Test the getters.
     */
    @Test
    public void testGetters()
    {
        final Step step = new Step(0, 1);

        assertEquals(0, step.getX());
        assertEquals(1, step.getY());
    }

    /**
     * Test the equality.
     */
    @Test
    public void testEquals()
    {
        final Step step = new Step(0, 1);

        assertEquals(step, step);
        assertEquals(step, new Step(0, 1));

        assertNotEquals(step, null);
        assertNotEquals(step, new Object());
        assertNotEquals(step, new Step(0, 0));
        assertNotEquals(step, new Step(1, 1));
        assertNotEquals(step, new Step(1, 0));
    }

    /**
     * Test the hash code.
     */
    @Test
    public void testHashCode()
    {
        final Step hash = new Step(0, 1);

        assertHashEquals(hash, new Step(0, 1));
        assertHashEquals(hash, new Step(0, 0));
        assertHashEquals(hash, new Step(1, 0));

        assertHashNotEquals(hash, new Object());
        assertHashNotEquals(hash, new Step(1, 1));
    }
}
