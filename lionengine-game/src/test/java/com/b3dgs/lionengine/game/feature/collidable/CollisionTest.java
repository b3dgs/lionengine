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
package com.b3dgs.lionengine.game.feature.collidable;

import org.junit.Assert;
import org.junit.Test;

import com.b3dgs.lionengine.LionEngineException;

/**
 * Test collision data class.
 */
public class CollisionTest
{
    /**
     * Test collision data functions.
     */
    @Test
    public void testCollision()
    {
        final Collision collision = new Collision("void", 1, 2, 3, 4, true);

        Assert.assertEquals("void", collision.getName());
        Assert.assertTrue(collision.getOffsetX() == 1);
        Assert.assertTrue(collision.getOffsetY() == 2);
        Assert.assertTrue(collision.getWidth() == 3);
        Assert.assertTrue(collision.getHeight() == 4);
        Assert.assertTrue(collision.hasMirror());
    }

    /**
     * Test collision with <code>null</code> name.
     */
    @Test(expected = LionEngineException.class)
    public void testCollisionNullName()
    {
        Assert.assertNull(new Collision(null, 1, 2, 3, 4, true));
    }

    /**
     * Test the collision hash code.
     */
    @Test
    public void testHashCode()
    {
        final int collision = new Collision("void", 1, 2, 3, 4, true).hashCode();

        Assert.assertEquals(collision, new Collision("void", 1, 2, 3, 4, true).hashCode());

        Assert.assertNotEquals(collision, new Object().hashCode());
        Assert.assertNotEquals(collision, new Collision("", 1, 2, 3, 4, true).hashCode());
        Assert.assertNotEquals(collision, new Collision("void", 0, 2, 3, 4, true).hashCode());
        Assert.assertNotEquals(collision, new Collision("void", 1, 0, 3, 4, true).hashCode());
        Assert.assertNotEquals(collision, new Collision("void", 1, 2, 0, 4, true).hashCode());
        Assert.assertNotEquals(collision, new Collision("void", 1, 2, 3, 0, true).hashCode());
        Assert.assertNotEquals(collision, new Collision("void", 1, 2, 3, 4, false).hashCode());
    }

    /**
     * Test the collision equality.
     */
    @Test
    public void testEquals()
    {
        final Collision collision = new Collision("void", 1, 2, 3, 4, true);

        Assert.assertEquals(collision, collision);
        Assert.assertEquals(collision, new Collision("void", 1, 2, 3, 4, true));

        Assert.assertNotEquals(collision, null);
        Assert.assertNotEquals(collision, new Object());
        Assert.assertNotEquals(collision, new Collision("", 1, 2, 3, 4, true));
        Assert.assertNotEquals(collision, new Collision("void", 0, 2, 3, 4, true));
        Assert.assertNotEquals(collision, new Collision("void", 1, 0, 3, 4, true));
        Assert.assertNotEquals(collision, new Collision("void", 1, 2, 0, 4, true));
        Assert.assertNotEquals(collision, new Collision("void", 1, 2, 3, 0, true));
        Assert.assertNotEquals(collision, new Collision("void", 1, 2, 3, 4, false));
    }
}
