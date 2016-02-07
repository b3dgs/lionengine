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
package com.b3dgs.lionengine.game;

import org.junit.Assert;
import org.junit.Test;

/**
 * Test alterable class.
 */
public class AlterableTest
{
    /**
     * Test alterable functions.
     */
    @Test
    public void testAlterable()
    {
        final Alterable alterableMax = new Alterable(-1);
        Assert.assertTrue(alterableMax.getMax() == 0);

        final int max = 50;
        final Alterable alterable = new Alterable(max);
        Assert.assertEquals(max, alterable.getMax());
        alterable.fill();
        Assert.assertEquals(100, alterable.getPercent());
        Assert.assertEquals(max, alterable.getCurrent());
        Assert.assertTrue(alterable.isFull());
        alterable.reset();
        Assert.assertTrue(alterable.isEmpty());
        alterable.set(max + 1);
        Assert.assertTrue(alterable.getMax() == max);
        alterable.set(max);

        final int step = 10;
        int increase = alterable.increase(step);
        Assert.assertEquals(0, increase);
        Assert.assertEquals(max, alterable.getCurrent());

        int decrease = alterable.decrease(step);
        Assert.assertEquals(step, decrease);
        Assert.assertEquals(max - step, alterable.getCurrent());

        increase = alterable.increase(step);
        Assert.assertEquals(step, increase);
        Assert.assertEquals(max, alterable.getCurrent());

        Assert.assertEquals(50, alterable.decrease(max + step));
        Assert.assertTrue(alterable.isEmpty());
        Assert.assertEquals(max, alterable.increase(max + step));

        Assert.assertTrue(alterable.isEnough(step));

        decrease = alterable.decrease(step);
        Assert.assertEquals(decrease, step);
        Assert.assertEquals(step, alterable.getNeeded(max));

        alterable.fill();
        Assert.assertTrue(alterable.isFull());
        alterable.setMax(max * 2);
        Assert.assertFalse(alterable.isFull());
        Assert.assertEquals(50, alterable.getPercent());
        alterable.fill();
        Assert.assertEquals(max * 2, alterable.getCurrent());

        alterable.setMax(-1);
        Assert.assertTrue(alterable.getMax() == 0);

        alterable.setMax(0);
        Assert.assertTrue(alterable.getPercent() == 0);
        Assert.assertTrue(alterable.getNeeded(0) == 0);
        alterable.reset();
        Assert.assertTrue(alterable.isEmpty());
        alterable.setMax(10);
        alterable.fill();
        Assert.assertTrue(!alterable.isEmpty());
    }
}
