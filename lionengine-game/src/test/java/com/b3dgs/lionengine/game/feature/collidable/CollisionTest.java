/*
 * Copyright (C) 2013-2018 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.game.feature.collidable;

import static com.b3dgs.lionengine.UtilAssert.assertEquals;
import static com.b3dgs.lionengine.UtilAssert.assertHashEquals;
import static com.b3dgs.lionengine.UtilAssert.assertHashNotEquals;
import static com.b3dgs.lionengine.UtilAssert.assertNotEquals;
import static com.b3dgs.lionengine.UtilAssert.assertThrows;
import static com.b3dgs.lionengine.UtilAssert.assertTrue;

import org.junit.jupiter.api.Test;

/**
 * Test {@link Collision}.
 */
public final class CollisionTest
{
    /**
     * Test collision data functions.
     */
    @Test
    public void testCollision()
    {
        final Collision collision = new Collision("void", 1, 2, 3, 4, true);

        assertEquals("void", collision.getName());
        assertTrue(collision.getOffsetX() == 1);
        assertTrue(collision.getOffsetY() == 2);
        assertTrue(collision.getWidth() == 3);
        assertTrue(collision.getHeight() == 4);
        assertTrue(collision.hasMirror());
    }

    /**
     * Test collision with <code>null</code> name.
     */
    @Test
    public void testCollisionNullName()
    {
        assertThrows(() -> new Collision(null, 1, 2, 3, 4, true), "Unexpected null argument !");
    }

    /**
     * Test the collision equality.
     */
    @Test
    public void testEquals()
    {
        final Collision collision = new Collision("void", 1, 2, 3, 4, true);

        assertEquals(collision, collision);
        assertEquals(collision, new Collision("void", 1, 2, 3, 4, true));

        assertNotEquals(collision, null);
        assertNotEquals(collision, new Object());
        assertNotEquals(collision, new Collision("", 1, 2, 3, 4, true));
        assertEquals(collision, new Collision("void", 0, 2, 3, 4, true));
        assertEquals(collision, new Collision("void", 1, 0, 3, 4, true));
        assertEquals(collision, new Collision("void", 1, 2, 0, 4, true));
        assertEquals(collision, new Collision("void", 1, 2, 3, 0, true));
        assertEquals(collision, new Collision("void", 1, 2, 3, 4, false));
    }

    /**
     * Test the collision hash code.
     */
    @Test
    public void testHashCode()
    {
        final Collision collision = new Collision("void", 1, 2, 3, 4, true);

        assertHashEquals(collision, new Collision("void", 1, 2, 3, 4, true));

        assertHashNotEquals(collision, new Object());
        assertHashNotEquals(collision, new Collision("", 1, 2, 3, 4, true));
        assertHashEquals(collision, new Collision("void", 0, 2, 3, 4, true));
        assertHashEquals(collision, new Collision("void", 1, 0, 3, 4, true));
        assertHashEquals(collision, new Collision("void", 1, 2, 0, 4, true));
        assertHashEquals(collision, new Collision("void", 1, 2, 3, 0, true));
        assertHashEquals(collision, new Collision("void", 1, 2, 3, 4, false));
    }
}
