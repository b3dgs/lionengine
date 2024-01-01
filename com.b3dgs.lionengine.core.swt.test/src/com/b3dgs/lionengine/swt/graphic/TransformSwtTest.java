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
package com.b3dgs.lionengine.swt.graphic;

import static com.b3dgs.lionengine.swt.UtilAssert.assertEquals;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.b3dgs.lionengine.graphic.Graphics;
import com.b3dgs.lionengine.graphic.Transform;

/**
 * Test {@link TransformSwt}.
 */
final class TransformSwtTest
{
    /**
     * Prepare tests.
     */
    @BeforeAll
    public static void beforeTests()
    {
        Graphics.setFactoryGraphic(new FactoryGraphicSwt());
    }

    /**
     * Clean up tests.
     */
    @AfterAll
    public static void afterTests()
    {
        Graphics.setFactoryGraphic(null);
    }

    /**
     * Test transformations.
     */
    @Test
    void testTransform()
    {
        final Transform transform = Graphics.createTransform();
        transform.scale(2.0, 3.0);
        transform.setInterpolation(true);

        assertEquals(2.0, transform.getScaleX());
        assertEquals(3.0, transform.getScaleY());
        assertEquals(getInterpolation(transform.getInterpolation()), transform.getInterpolation());

        transform.setInterpolation(false);
        assertEquals(getInterpolation(transform.getInterpolation()), transform.getInterpolation());
    }

    /**
     * Get the interpolation equivalence.
     * 
     * @param value The engine side value.
     * @return The target side value.
     */
    protected int getInterpolation(int value)
    {
        return value;
    }
}
