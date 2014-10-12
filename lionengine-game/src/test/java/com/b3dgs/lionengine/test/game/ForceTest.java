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
        Assert.assertTrue(forceA.getForceHorizontal() == 0);
        Assert.assertTrue(forceA.getForceVertical() == 0);
        forceA.setForce(Force.ZERO);
        Assert.assertTrue(forceA.getForceHorizontal() == 0);
        Assert.assertTrue(forceA.getForceVertical() == 0);
        forceA.setForce(2.0, 2.0);
        Assert.assertTrue(forceA.getForceHorizontal() == 2);
        Assert.assertTrue(forceA.getForceVertical() == 2);

        final Force forceB = new Force(1.0, 3.0);
        Assert.assertTrue(forceB.getForceHorizontal() == 1);
        Assert.assertTrue(forceB.getForceVertical() == 3);
        forceB.addForce(1, 1);
        Assert.assertTrue(forceB.getForceHorizontal() == 2);
        Assert.assertTrue(forceB.getForceVertical() == 4);
        forceB.addForce(forceA);
        Assert.assertTrue(forceB.getForceHorizontal() == 4);
        Assert.assertTrue(forceB.getForceVertical() == 6);

        forceB.setForceMaximum(Force.ZERO);
        forceB.addForce(Force.ZERO);
        Assert.assertTrue(forceB.getForceHorizontal() == 0);
        Assert.assertTrue(forceB.getForceVertical() == 0);

        forceB.setForceMinimum(forceA);
        forceB.setForceMaximum(forceA);
        forceB.reachForce(0.0, forceA, 1.0, 1.0);
        forceB.reachForce(0.0, forceA, 1.0, 1.0);
        Assert.assertTrue(forceB.getForceHorizontal() == forceA.getForceHorizontal());
        Assert.assertTrue(forceB.getForceVertical() == forceB.getForceVertical());
        forceB.reachForce(0.0, forceA, 1.0, 1.0);

        final Force forceC = new Force(100, 100);
        forceB.setForceMaximum(forceC);
        forceB.reachForce(1.0, Force.ZERO, 1.0, 500);
        forceB.reachForce(1.0, Force.ZERO, 5.0, 0.1);
        forceB.reachForce(1.0, forceC, -30.0, 500.0);
        forceB.reachForce(1.0, forceC, 5.0, 0.1);

        Assert.assertTrue(forceB.getForceHorizontal() == forceC.getForceHorizontal());
        Assert.assertTrue(forceB.getForceVertical() == forceC.getForceVertical());
    }
}
