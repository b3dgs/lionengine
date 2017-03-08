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
package com.b3dgs.lionengine.game;

import org.junit.Assert;
import org.junit.Test;

/**
 * Test damages class.
 */
public class DamagesTest
{
    /**
     * Test coordinate tile functions.
     */
    @Test
    public void testDamages()
    {
        final Damages damages = new Damages();
        Assert.assertTrue(damages.getMin() == 0);
        Assert.assertTrue(damages.getMax() == 0);
        Assert.assertTrue(damages.getLast() == 0);
        Assert.assertTrue(damages.getRandom() == 0);

        final Damages damagesA = new Damages(1, 3);
        Assert.assertTrue(damagesA.getMin() == 1);
        Assert.assertTrue(damagesA.getMax() == 3);

        damagesA.setMin(0);
        damagesA.setMax(4);
        Assert.assertTrue(damagesA.getMin() == 0);
        Assert.assertTrue(damagesA.getMax() == 4);

        final int last = damagesA.getRandom();
        Assert.assertTrue(damagesA.getLast() == last);
    }
}
