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
import static org.junit.jupiter.api.Assertions.assertNotSame;

import org.junit.jupiter.api.Test;

/**
 * Tests of {@link Transformable} and {@link Transformable#TYPE}.
 */
final class TransformableTest
{
    /**
     * A new instance has every field at zero; the setters change only their fields.
     */
    @Test
    void testFields()
    {
        final Transformable transformable = Transformable.TYPE.create();
        assertState(transformable, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0);

        transformable.setPosition(1.5, -2.5);
        assertState(transformable, 1.5, -2.5, 0.0, 0.0, 0.0, 0.0);
        transformable.setSize(2.0, 3.0);
        assertState(transformable, 1.5, -2.5, 2.0, 3.0, 0.0, 0.0);
        transformable.setPreviousPosition(-1.0, 4.0);
        assertState(transformable, 1.5, -2.5, 2.0, 3.0, -1.0, 4.0);
    }

    /**
     * The capture copies the position into the previous position; the translation moves the position only.
     */
    @Test
    void testCaptureAndTranslate()
    {
        final Transformable transformable = new Transformable();
        transformable.setPosition(1.0, 2.0);
        transformable.capture();
        transformable.translate(0.5, -0.25);

        assertState(transformable, 1.5, 1.75, 0.0, 0.0, 1.0, 2.0);
    }

    /**
     * The reset gives back the state of a new instance; each creation is a new instance.
     */
    @Test
    void testReset()
    {
        final Transformable transformable = Transformable.TYPE.create();
        transformable.setPosition(1.0, 2.0);
        transformable.setSize(3.0, 4.0);
        transformable.setPreviousPosition(5.0, 6.0);
        Transformable.TYPE.reset(transformable);

        assertState(transformable, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0);
        assertNotSame(transformable, Transformable.TYPE.create());
    }

    /**
     * Checks every field.
     *
     * @param transformable The trait.
     * @param x The expected horizontal position.
     * @param y The expected vertical position.
     * @param width The expected width.
     * @param height The expected height.
     * @param previousX The expected previous horizontal position.
     * @param previousY The expected previous vertical position.
     */
    static void assertState(Transformable transformable,
                            double x,
                            double y,
                            double width,
                            double height,
                            double previousX,
                            double previousY)
    {
        assertEquals(x, transformable.getX());
        assertEquals(y, transformable.getY());
        assertEquals(width, transformable.getWidth());
        assertEquals(height, transformable.getHeight());
        assertEquals(previousX, transformable.getPreviousX());
        assertEquals(previousY, transformable.getPreviousY());
    }
}
