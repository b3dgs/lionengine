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

import com.b3dgs.lionengine.Range;
import com.b3dgs.lionengine.util.UtilMath;

/**
 * Test {@link Damages}.
 */
public final class DamagesTest
{
    /**
     * Test constructor.
     */
    @Test
    public void testConstructor()
    {
        final Damages damages = new Damages();

        Assert.assertEquals(0, damages.getMin());
        Assert.assertEquals(0, damages.getMax());
        Assert.assertEquals(0, damages.getLast());

        final Range range = damages.getDamages();

        Assert.assertEquals(0, range.getMin());
        Assert.assertEquals(0, range.getMax());

        for (int i = 0; i < 100; i++)
        {
            Assert.assertEquals(0, damages.getRandom());
        }
    }

    /**
     * Test constructor with parameters.
     */
    @Test
    public void testConstructorParam()
    {
        final Damages damages = new Damages(1, 2);

        Assert.assertEquals(1, damages.getMin());
        Assert.assertEquals(2, damages.getMax());
        Assert.assertEquals(0, damages.getLast());

        final Range range = damages.getDamages();

        Assert.assertEquals(1, range.getMin());
        Assert.assertEquals(2, range.getMax());

        for (int i = 0; i < 100; i++)
        {
            final int damage = damages.getRandom();

            Assert.assertTrue(UtilMath.isBetween(damage, 1, 2));
        }
    }

    /**
     * Test constructor with negative values.
     */
    @Test
    public void testConstructorNegative()
    {
        final Damages damages = new Damages(-1, -2);

        Assert.assertEquals(0, damages.getMin());
        Assert.assertEquals(0, damages.getMax());
        Assert.assertEquals(0, damages.getLast());
    }

    /**
     * Test set min.
     */
    @Test
    public void testSetMin()
    {
        final Damages damages = new Damages(1, 5);

        Assert.assertEquals(1, damages.getMin());
        Assert.assertEquals(5, damages.getMax());

        damages.setMin(2);

        Assert.assertEquals(2, damages.getMin());
        Assert.assertEquals(5, damages.getMax());

        damages.setMin(6);

        Assert.assertEquals(6, damages.getMin());
        Assert.assertEquals(6, damages.getMax());
    }

    /**
     * Test set max.
     */
    @Test
    public void testSetMax()
    {
        final Damages damages = new Damages(1, 5);

        Assert.assertEquals(1, damages.getMin());
        Assert.assertEquals(5, damages.getMax());

        damages.setMax(6);

        Assert.assertEquals(1, damages.getMin());
        Assert.assertEquals(6, damages.getMax());

        damages.setMax(0);

        Assert.assertEquals(1, damages.getMin());
        Assert.assertEquals(1, damages.getMax());
    }

    /**
     * Test set damages.
     */
    @Test
    public void testSetDamages()
    {
        final Damages damages = new Damages();
        damages.setDamages(1, 5);

        Assert.assertEquals(1, damages.getMin());
        Assert.assertEquals(5, damages.getMax());
    }

    /**
     * Test set damages min over max.
     */
    @Test
    public void testSetDamagesMinOverMax()
    {
        final Damages damages = new Damages();
        damages.setDamages(5, 1);

        Assert.assertEquals(5, damages.getMin());
        Assert.assertEquals(5, damages.getMax());
    }

    /**
     * Test set damages with negative values.
     */
    @Test
    public void testSetDamagesNegative()
    {
        final Damages damages = new Damages();
        damages.setDamages(-1, -2);

        Assert.assertEquals(0, damages.getMin());
        Assert.assertEquals(0, damages.getMax());
    }

    /**
     * Test get last.
     */
    @Test
    public void testGetLast()
    {
        final Damages damages = new Damages(1, 1);

        Assert.assertEquals(1, damages.getRandom());
        Assert.assertEquals(1, damages.getLast());
    }
}
