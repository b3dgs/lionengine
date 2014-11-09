/*
 * Copyright (C) 2013-2014 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.test.game;

import org.junit.Assert;
import org.junit.Test;

import com.b3dgs.lionengine.game.Direction;
import com.b3dgs.lionengine.game.Force;

/**
 * Test force class.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public class ForceTest
{
    /**
     * Test force functions.
     */
    @Test
    public void testForce()
    {
        final Force forceA = new Force();
        Assert.assertTrue(forceA.getDirectionHorizontal() == 0);
        Assert.assertTrue(forceA.getDirectionVertical() == 0);
        forceA.setDirection(Direction.ZERO);
        Assert.assertTrue(forceA.getDirectionHorizontal() == 0);
        Assert.assertTrue(forceA.getDirectionVertical() == 0);
        forceA.setDirection(2.0, 2.0);
        Assert.assertTrue(forceA.getDirectionHorizontal() == 2);
        Assert.assertTrue(forceA.getDirectionVertical() == 2);

        final Force forceB = new Force(1.0, 3.0);
        Assert.assertTrue(forceB.getDirectionHorizontal() == 1);
        Assert.assertTrue(forceB.getDirectionVertical() == 3);
        forceB.addDirection(1, 1);
        Assert.assertTrue(forceB.getDirectionHorizontal() == 2);
        Assert.assertTrue(forceB.getDirectionVertical() == 4);
        forceB.addDirection(forceA);
        Assert.assertTrue(forceB.getDirectionHorizontal() == 4);
        Assert.assertTrue(forceB.getDirectionVertical() == 6);

        forceB.setDirectionMaximum(Direction.ZERO);
        forceB.addDirection(Direction.ZERO);
        Assert.assertTrue(forceB.getDirectionHorizontal() == 0);
        Assert.assertTrue(forceB.getDirectionVertical() == 0);

        forceB.setDirectionMinimum(forceA);
        forceB.setDirectionMaximum(forceA);
        forceB.reachForce(0.0, forceA, 1.0, 1.0);
        forceB.reachForce(0.0, forceA, 1.0, 1.0);
        Assert.assertTrue(forceB.getDirectionHorizontal() == forceA.getDirectionHorizontal());
        Assert.assertTrue(forceB.getDirectionVertical() == forceB.getDirectionVertical());
        forceB.reachForce(0.0, forceA, 1.0, 1.0);

        final Force forceC = new Force(100, 100);
        forceB.setDirectionMaximum(forceC);
        forceB.reachForce(1.0, Direction.ZERO, 1.0, 500);
        forceB.reachForce(1.0, Direction.ZERO, 5.0, 0.1);
        forceB.reachForce(1.0, forceC, -30.0, 500.0);
        forceB.reachForce(1.0, forceC, 5.0, 0.1);

        Assert.assertTrue(forceB.getDirectionHorizontal() == forceC.getDirectionHorizontal());
        Assert.assertTrue(forceB.getDirectionVertical() == forceC.getDirectionVertical());
    }
}
