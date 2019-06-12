/*
 * Copyright (C) 2013-2019 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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

import org.junit.jupiter.api.Test;

/**
 * Test {@link Attribute}.
 */
public final class AttributeTest
{
    /**
     * Test constructor.
     */
    @Test
    public void testConstructor()
    {
        final Attribute attribute = new Attribute();

        assertEquals(0, attribute.get());
    }

    /**
     * Test increase.
     */
    @Test
    public void testIncrease()
    {
        final Attribute attribute = new Attribute();
        attribute.increase(2);

        assertEquals(2, attribute.get());
    }

    /**
     * Test set.
     */
    @Test
    public void testSet()
    {
        final Attribute attribute = new Attribute();
        attribute.set(1);

        assertEquals(1, attribute.get());
    }
}
