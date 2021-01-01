/*
 * Copyright (C) 2013-2021 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
import static com.b3dgs.lionengine.UtilAssert.assertFalse;
import static com.b3dgs.lionengine.UtilAssert.assertTrue;

import org.junit.jupiter.api.Test;

/**
 * Test {@link Alterable}.
 */
final class AlterableTest
{
    /**
     * Test constructor.
     */
    @Test
    void testConstructor()
    {
        final Alterable alterable = new Alterable(-1);

        assertEquals(0, alterable.getCurrent());
        assertEquals(0, alterable.getMax());
        assertEquals(0, alterable.getPercent());
    }

    /**
     * Test increase.
     */
    @Test
    void testIncrease()
    {
        final Alterable alterable = new Alterable(3);
        alterable.increase(1);

        assertEquals(1, alterable.getCurrent());

        alterable.increase(2.0, 1);

        assertEquals(3, alterable.getCurrent());

        alterable.increase(1);

        assertEquals(3, alterable.getCurrent());
    }

    /**
     * Test decrease.
     */
    @Test
    void testDecrease()
    {
        final Alterable alterable = new Alterable(3);
        alterable.fill();
        alterable.decrease(1);

        assertEquals(2, alterable.getCurrent());

        alterable.decrease(2.0, 1);

        assertEquals(0, alterable.getCurrent());

        alterable.decrease(1);

        assertEquals(0, alterable.getCurrent());
    }

    /**
     * Test fill.
     */
    @Test
    void testFill()
    {
        final Alterable alterable = new Alterable(4);

        assertEquals(0, alterable.getCurrent());

        alterable.fill();

        assertEquals(4, alterable.getCurrent());
    }

    /**
     * Test percent.
     */
    @Test
    void testPercent()
    {
        final Alterable alterable = new Alterable(1);
        alterable.fill();
        alterable.decrease(0.999, 1);

        assertEquals(1, alterable.getCurrent());
        assertEquals(1, alterable.getMax());
        assertEquals(1, alterable.getPercent());

        alterable.decrease(1);

        assertEquals(0, alterable.getCurrent());
        assertEquals(0, alterable.getPercent());
    }

    /**
     * Test reset.
     */
    @Test
    void testReset()
    {
        final Alterable alterable = new Alterable(4);
        alterable.set(2);

        assertEquals(2, alterable.getCurrent());

        alterable.reset();

        assertEquals(0, alterable.getCurrent());
    }

    /**
     * Test set max.
     */
    @Test
    void testSetMax()
    {
        final Alterable alterable = new Alterable(0);
        alterable.set(1);

        assertEquals(0, alterable.getCurrent());

        alterable.setMax(2);
        alterable.set(1);

        assertEquals(1, alterable.getCurrent());

        alterable.setMax(0);

        assertEquals(0, alterable.getCurrent());

        alterable.setMax(-1);

        assertEquals(0, alterable.getCurrent());
        assertEquals(0, alterable.getMax());
    }

    /**
     * Test set max over.
     */
    @Test
    void testSetMaxOver()
    {
        final Alterable alterable = new Alterable(0, true);
        alterable.set(1);

        assertEquals(1, alterable.getCurrent());
    }

    /**
     * Test getter.
     */
    @Test
    void testGetter()
    {
        final Alterable alterable = new Alterable(2);
        alterable.set(1);

        assertEquals(1, alterable.getCurrent());
        assertEquals(2, alterable.getMax());
        assertEquals(50, alterable.getPercent());
        assertEquals(0, alterable.getNeeded(0));
        assertEquals(0, alterable.getNeeded(1));
        assertEquals(1, alterable.getNeeded(2));
    }

    /**
     * Test is.
     */
    @Test
    void testIs()
    {
        final Alterable alterable = new Alterable(2);

        assertTrue(alterable.isEmpty());
        assertFalse(alterable.isFull());
        assertFalse(alterable.isEnough(1));

        alterable.set(2);

        assertFalse(alterable.isEmpty());
        assertTrue(alterable.isFull());
        assertTrue(alterable.isEnough(1));
    }
}
