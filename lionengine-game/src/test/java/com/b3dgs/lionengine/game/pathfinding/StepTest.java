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
package com.b3dgs.lionengine.game.pathfinding;

import org.junit.Assert;
import org.junit.Test;

/**
 * Test the step class.
 */
public class StepTest
{
    /**
     * Test the getters.
     */
    @Test
    public void testGetters()
    {
        final Step step = new Step(0, 1);

        Assert.assertEquals(0, step.getX());
        Assert.assertEquals(1, step.getY());
    }

    /**
     * Test the hash code.
     */
    @Test
    public void testHashcode()
    {
        final int hash = new Step(0, 1).hashCode();

        Assert.assertEquals(hash, new Step(0, 1).hashCode());
        Assert.assertEquals(hash, new Step(0, 0).hashCode());
        Assert.assertEquals(hash, new Step(1, 0).hashCode());

        Assert.assertNotEquals(hash, new Object().hashCode());
        Assert.assertNotEquals(hash, new Step(1, 1).hashCode());
    }

    /**
     * Test the equality.
     */
    @Test
    public void testEquals()
    {
        final Step step = new Step(0, 1);

        Assert.assertEquals(step, step);
        Assert.assertEquals(step, new Step(0, 1));

        Assert.assertNotEquals(step, null);
        Assert.assertNotEquals(step, new Object());
        Assert.assertNotEquals(step, new Step(0, 0));
        Assert.assertNotEquals(step, new Step(1, 1));
        Assert.assertNotEquals(step, new Step(1, 0));
    }
}
