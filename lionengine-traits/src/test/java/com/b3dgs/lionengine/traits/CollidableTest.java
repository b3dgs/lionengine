/*
 * Copyright (C) 2013-2026 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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

package com.b3dgs.lionengine.traits;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import com.b3dgs.lionengine.physics.Box;

/**
 * Tests of {@link Collidable}: reset state, accessors, validation, the layer and mask rule and the world box
 * (REQ-STD-005, REQ-STD-010, ADR-0030, ADR-0034, ADR-0082).
 */
final class CollidableTest
{
    /**
     * A new or reset collidable has a box of size zero on the position, the default layer, every mask layer, and is
     * not a trigger.
     */
    @Test
    void testResetState()
    {
        final Collidable collidable = Collidable.TYPE.create();
        assertReset(collidable);
        collidable.setSize(1.0, 2.0);
        collidable.setOffset(0.5, -0.5);
        collidable.setLayer(4);
        collidable.setMask(2);
        collidable.setTrigger(true);

        assertEquals(1.0, collidable.getWidth());
        assertEquals(2.0, collidable.getHeight());
        assertEquals(0.5, collidable.getOffsetX());
        assertEquals(-0.5, collidable.getOffsetY());
        assertEquals(4, collidable.getLayer());
        assertEquals(2, collidable.getMask());
        assertTrue(collidable.isTrigger());

        Collidable.TYPE.reset(collidable);

        assertReset(collidable);
        assertReset(new Collidable());
    }

    /**
     * A size negative or not finite, or an offset not finite, is refused and leaves the value unchanged.
     *
     * @param first The first value.
     * @param second The second value.
     */
    @ParameterizedTest
    @CsvSource(
    {
        "-1, 1", "1, -0.5", "NaN, 1", "1, NaN", "Infinity, 1", "1, Infinity"
    })
    void testInvalidSize(double first, double second)
    {
        final Collidable collidable = new Collidable();
        collidable.setSize(3.0, 4.0);

        assertThrows(IllegalArgumentException.class, () -> collidable.setSize(first, second));
        assertEquals(3.0, collidable.getWidth());
        assertEquals(4.0, collidable.getHeight());
    }

    /**
     * An offset not finite is refused and leaves the offset unchanged.
     *
     * @param first The first value.
     * @param second The second value.
     */
    @ParameterizedTest
    @CsvSource(
    {
        "NaN, 0", "0, NaN", "Infinity, 0", "0, -Infinity"
    })
    void testInvalidOffset(double first, double second)
    {
        final Collidable collidable = new Collidable();
        collidable.setOffset(1.0, 2.0);

        assertThrows(IllegalArgumentException.class, () -> collidable.setOffset(first, second));
        assertEquals(1.0, collidable.getOffsetX());
        assertEquals(2.0, collidable.getOffsetY());
    }

    /**
     * Two collidables accept each other only if each mask includes the other's layer, the same in both directions.
     *
     * @param layerA The first layer.
     * @param maskA The first mask.
     * @param layerB The second layer.
     * @param maskB The second mask.
     * @param expected The expected result.
     */
    @ParameterizedTest
    @CsvSource(
    {
        "1, -1, 1, -1, true", "1, 2, 2, 1, true", "1, 2, 2, 2, false", "1, 1, 2, 1, false", "0, -1, 1, -1, false",
        "1, 0, 1, -1, false", "3, 4, 4, 2, true", "3, 4, 4, 8, false", "-2147483648, -2147483648, -2147483648, "
        + "-2147483648, true", "5, 3, 6, 1, true"
    })
    void testAccepts(int layerA, int maskA, int layerB, int maskB, boolean expected)
    {
        final Collidable a = new Collidable();
        a.setLayer(layerA);
        a.setMask(maskA);
        final Collidable b = new Collidable();
        b.setLayer(layerB);
        b.setMask(maskB);

        assertEquals(expected, a.accepts(b));
        assertEquals(expected, b.accepts(a));
    }

    /**
     * The world box is centered on the position plus the offset, with the collidable size.
     */
    @Test
    void testToBox()
    {
        final Transformable transformable = new Transformable();
        transformable.setPosition(1.0, 2.0);
        transformable.setSize(9.0, 9.0);
        final Collidable collidable = new Collidable();
        collidable.setSize(0.5, 1.5);
        collidable.setOffset(-0.25, 0.75);
        final Box box = new Box();

        collidable.toBox(transformable, box);

        assertEquals(0.75, box.getX());
        assertEquals(2.75, box.getY());
        assertEquals(0.5, box.getWidth());
        assertEquals(1.5, box.getHeight());

        transformable.setPosition(Double.NaN, 0.0);
        assertThrows(IllegalArgumentException.class, () -> collidable.toBox(transformable, box));
        assertFalse(Double.isNaN(box.getX()));
    }

    /**
     * Checks the reset state.
     *
     * @param collidable The collidable.
     */
    private static void assertReset(Collidable collidable)
    {
        assertEquals(0.0, collidable.getWidth());
        assertEquals(0.0, collidable.getHeight());
        assertEquals(0.0, collidable.getOffsetX());
        assertEquals(0.0, collidable.getOffsetY());
        assertEquals(Collidable.DEFAULT_LAYER, collidable.getLayer());
        assertEquals(1, collidable.getLayer());
        assertEquals(Collidable.ALL_LAYERS, collidable.getMask());
        assertFalse(collidable.isTrigger());
    }
}
