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
package com.b3dgs.lionengine.game.feature.collidable;

import static com.b3dgs.lionengine.UtilAssert.assertEquals;
import static com.b3dgs.lionengine.UtilAssert.assertHashEquals;
import static com.b3dgs.lionengine.UtilAssert.assertHashNotEquals;
import static com.b3dgs.lionengine.UtilAssert.assertNotEquals;

import org.junit.jupiter.api.Test;

/**
 * Test {@link CollisionCouple}.
 */
final class CollisionCoupleTest
{
    /**
     * Test collision couple data functions.
     */
    @Test
    void testCollision()
    {
        final Collision with = new Collision("with", 1, 2, 3, 4, true);
        final Collision by = new Collision("by", 1, 2, 3, 4, true);
        final CollisionCouple couple = new CollisionCouple(with, by);

        assertEquals(with, couple.getWith());
        assertEquals(by, couple.getBy());
    }

    /**
     * Test the equality.
     */
    @Test
    void testEquals()
    {
        final Collision with = new Collision("with", 1, 2, 3, 4, true);
        final Collision by = new Collision("by", 1, 2, 3, 4, true);
        final CollisionCouple couple = new CollisionCouple(with, by);

        assertEquals(couple, couple);
        assertEquals(couple, new CollisionCouple(with, by));

        assertNotEquals(couple, null);
        assertNotEquals(couple, new Object());
        assertNotEquals(couple, new CollisionCouple(with, with));
        assertNotEquals(couple, new CollisionCouple(by, with));
    }

    /**
     * Test the hash code.
     */
    @Test
    void testHashCode()
    {
        final Collision with = new Collision("with", 1, 2, 3, 4, true);
        final Collision by = new Collision("by", 1, 2, 3, 4, true);
        final CollisionCouple couple = new CollisionCouple(with, by);

        assertHashEquals(couple, new CollisionCouple(with, by));

        assertHashNotEquals(couple, new Object());
        assertHashNotEquals(couple, new CollisionCouple(with, with));
        assertHashNotEquals(couple, new CollisionCouple(by, with));
    }
}
