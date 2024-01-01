/*
 * Copyright (C) 2013-2024 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.game;

import static com.b3dgs.lionengine.UtilAssert.assertEquals;
import static com.b3dgs.lionengine.UtilAssert.assertHashEquals;
import static com.b3dgs.lionengine.UtilAssert.assertHashNotEquals;
import static com.b3dgs.lionengine.UtilAssert.assertNotEquals;
import static com.b3dgs.lionengine.UtilAssert.assertTrue;

import org.junit.jupiter.api.Test;

import com.b3dgs.lionengine.Range;
import com.b3dgs.lionengine.UtilMath;

/**
 * Test {@link Damages}.
 */
final class DamagesTest
{
    /**
     * Test constructor.
     */
    @Test
    void testConstructor()
    {
        final Damages damages = new Damages();

        assertEquals(0, damages.getMin());
        assertEquals(0, damages.getMax());
        assertEquals(0, damages.getLast());

        final Range range = damages.getDamages();

        assertEquals(0, range.getMin());
        assertEquals(0, range.getMax());

        for (int i = 0; i < 100; i++)
        {
            assertEquals(0, damages.getRandom());
        }
    }

    /**
     * Test constructor with parameters.
     */
    @Test
    void testConstructorParam()
    {
        final Damages damages = new Damages(1, 2);

        assertEquals(1, damages.getMin());
        assertEquals(2, damages.getMax());
        assertEquals(0, damages.getLast());

        final Range range = damages.getDamages();

        assertEquals(1, range.getMin());
        assertEquals(2, range.getMax());

        for (int i = 0; i < 100; i++)
        {
            final int damage = damages.getRandom();

            assertTrue(UtilMath.isBetween(damage, 1, 2));
        }
    }

    /**
     * Test constructor with negative values.
     */
    @Test
    void testConstructorNegative()
    {
        final Damages damages = new Damages(-1, -2);

        assertEquals(0, damages.getMin());
        assertEquals(0, damages.getMax());
        assertEquals(0, damages.getLast());
    }

    /**
     * Test set min.
     */
    @Test
    void testSetMin()
    {
        final Damages damages = new Damages(1, 5);

        assertEquals(1, damages.getMin());
        assertEquals(5, damages.getMax());

        damages.setMin(2);

        assertEquals(2, damages.getMin());
        assertEquals(5, damages.getMax());

        damages.setMin(6);

        assertEquals(6, damages.getMin());
        assertEquals(6, damages.getMax());
    }

    /**
     * Test set max.
     */
    @Test
    void testSetMax()
    {
        final Damages damages = new Damages(1, 5);

        assertEquals(1, damages.getMin());
        assertEquals(5, damages.getMax());

        damages.setMax(6);

        assertEquals(1, damages.getMin());
        assertEquals(6, damages.getMax());

        damages.setMax(0);

        assertEquals(1, damages.getMin());
        assertEquals(1, damages.getMax());
    }

    /**
     * Test set damages.
     */
    @Test
    void testSetDamages()
    {
        final Damages damages = new Damages();
        damages.setDamages(1, 5);

        assertEquals(1, damages.getMin());
        assertEquals(5, damages.getMax());
    }

    /**
     * Test set damages with range.
     */
    @Test
    void testSetDamagesRange()
    {
        final Damages damages = new Damages();
        damages.setDamages(new Range(1, 5));

        assertEquals(1, damages.getMin());
        assertEquals(5, damages.getMax());
    }

    /**
     * Test set damages min over max.
     */
    @Test
    void testSetDamagesMinOverMax()
    {
        final Damages damages = new Damages();
        damages.setDamages(5, 1);

        assertEquals(5, damages.getMin());
        assertEquals(5, damages.getMax());
    }

    /**
     * Test set damages with negative values.
     */
    @Test
    void testSetDamagesNegative()
    {
        final Damages damages = new Damages();
        damages.setDamages(-1, -2);

        assertEquals(0, damages.getMin());
        assertEquals(0, damages.getMax());
    }

    /**
     * Test get last.
     */
    @Test
    void testGetLast()
    {
        final Damages damages = new Damages(1, 1);

        assertEquals(1, damages.getRandom());
        assertEquals(1, damages.getLast());
    }

    /**
     * Test equals.
     */
    @Test
    void testEquals()
    {
        final Damages damages = new Damages(1, 2);

        assertEquals(damages, damages);
        assertEquals(damages, new Damages(1, 2));

        assertNotEquals(damages, null);
        assertNotEquals(damages, new Object());
        assertNotEquals(damages, new Damages(2, 2));
        assertNotEquals(damages, new Damages(1, 1));
    }

    /**
     * Test equals.
     */
    @Test
    void testHashCode()
    {
        final Damages damages = new Damages(1, 2);

        assertHashEquals(damages, new Damages(1, 2));

        assertHashNotEquals(damages, new Object());
        assertHashNotEquals(damages, new Damages(2, 2));
        assertHashNotEquals(damages, new Damages(1, 1));
    }

    /**
     * Test to string.
     */
    @Test
    void testToString()
    {
        assertEquals("Damages [min=1, max=2]", new Damages(1, 2).toString());
    }
}
