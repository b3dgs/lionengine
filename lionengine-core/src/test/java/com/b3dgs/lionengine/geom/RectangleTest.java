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
package com.b3dgs.lionengine.geom;

import static com.b3dgs.lionengine.UtilAssert.assertEquals;
import static com.b3dgs.lionengine.UtilAssert.assertFalse;
import static com.b3dgs.lionengine.UtilAssert.assertHashEquals;
import static com.b3dgs.lionengine.UtilAssert.assertHashNotEquals;
import static com.b3dgs.lionengine.UtilAssert.assertNotEquals;
import static com.b3dgs.lionengine.UtilAssert.assertTrue;

import org.junit.jupiter.api.Test;

/**
 * Test {@link Rectangle}.
 */
final class RectangleTest
{
    /**
     * Test default constructor.
     */
    @Test
    void testConstructorDefault()
    {
        final Rectangle rectangle = new Rectangle();

        assertEquals(0.0, rectangle.getX());
        assertEquals(0.0, rectangle.getY());
        assertEquals(0, rectangle.getWidth());
        assertEquals(0, rectangle.getHeight());
        assertEquals(0.0, rectangle.getWidthReal());
        assertEquals(0.0, rectangle.getHeightReal());
        assertEquals(0.0, rectangle.getMinX());
        assertEquals(0.0, rectangle.getMinY());
        assertEquals(0.0, rectangle.getMaxX());
        assertEquals(0.0, rectangle.getMaxY());
    }

    /**
     * Test constructor with parameters.
     */
    @Test
    void testConstructorParameters()
    {
        final Rectangle rectangle = new Rectangle(1.0, 2.0, 3.6, 4.6);

        assertEquals(1.0, rectangle.getX());
        assertEquals(2.0, rectangle.getY());
        assertEquals(3, rectangle.getWidth());
        assertEquals(4, rectangle.getHeight());
        assertEquals(3.6, rectangle.getWidthReal());
        assertEquals(4.6, rectangle.getHeightReal());
        assertEquals(1.0, rectangle.getMinX());
        assertEquals(2.0, rectangle.getMinY());
        assertEquals(4.6, rectangle.getMaxX());
        assertEquals(6.6, rectangle.getMaxY());
    }

    /**
     * Test set.
     */
    @Test
    void testSet()
    {
        final Rectangle rectangle = new Rectangle();
        rectangle.set(1.0, 2.0, 3.6, 4.6);

        assertEquals(1.0, rectangle.getX());
        assertEquals(2.0, rectangle.getY());
        assertEquals(3, rectangle.getWidth());
        assertEquals(4, rectangle.getHeight());
        assertEquals(3.6, rectangle.getWidthReal());
        assertEquals(4.6, rectangle.getHeightReal());
        assertEquals(1.0, rectangle.getMinX());
        assertEquals(2.0, rectangle.getMinY());
        assertEquals(4.6, rectangle.getMaxX());
        assertEquals(6.6, rectangle.getMaxY());
    }

    /**
     * Test intersects.
     */
    @Test
    void testIntersects()
    {
        final Rectangle rectangle1 = new Rectangle(0.0, 0.0, 10.0, 10.0);
        final Rectangle rectangle2 = new Rectangle(1.0, 1.0, 5.0, 5.0);
        final Rectangle rectangle3 = new Rectangle(4.0, 1.0, 5.0, 5.0);
        final Rectangle rectangle4 = new Rectangle(1.0, 4.0, 5.0, 5.0);
        final Rectangle rectangle5 = new Rectangle(6.0, 6.0, 5.0, 5.0);

        assertFalse(rectangle1.intersects(null));

        assertTrue(rectangle2.intersects(rectangle1));
        assertTrue(rectangle2.intersects(rectangle3));
        assertTrue(rectangle2.intersects(rectangle4));
        assertFalse(rectangle2.intersects(rectangle5));

        assertTrue(rectangle3.intersects(rectangle1));
        assertTrue(rectangle3.intersects(rectangle2));
        assertTrue(rectangle3.intersects(rectangle4));
        assertFalse(rectangle3.intersects(rectangle5));

        assertTrue(rectangle5.intersects(rectangle1));
        assertFalse(rectangle5.intersects(rectangle2));
        assertFalse(rectangle5.intersects(rectangle3));
        assertFalse(rectangle5.intersects(rectangle4));
    }

    /**
     * Test contains.
     */
    @Test
    void testContains()
    {
        final Rectangle rectangle1 = new Rectangle(0.0, 0.0, 10.0, 10.0);
        final Rectangle rectangle2 = new Rectangle(1.0, 1.0, 5.0, 5.0);
        final Rectangle rectangle3 = new Rectangle(4.0, 1.0, 5.0, 5.0);
        final Rectangle rectangle4 = new Rectangle(1.0, 4.0, 5.0, 5.0);
        final Rectangle rectangle5 = new Rectangle(6.0, 6.0, 5.0, 5.0);

        assertFalse(rectangle1.contains(null));

        assertTrue(rectangle1.contains(rectangle2));

        assertFalse(rectangle2.contains(rectangle1));
        assertFalse(rectangle2.contains(rectangle3));
        assertFalse(rectangle2.contains(rectangle4));
        assertFalse(rectangle2.contains(rectangle5));

        assertFalse(rectangle4.contains(rectangle3));
        assertFalse(rectangle4.contains(rectangle2));
        assertTrue(rectangle4.contains(rectangle4));
        assertFalse(rectangle4.contains(rectangle5));

        assertTrue(rectangle1.contains(2, 3));

        assertFalse(rectangle1.contains(-2, -3));
        assertFalse(rectangle1.contains(-1, 11));
        assertFalse(rectangle1.contains(0, 11));
        assertFalse(rectangle1.contains(-1, 10));
        assertFalse(rectangle1.contains(11, 12));
        assertFalse(rectangle1.contains(11, -3));
    }

    /**
     * Test rotation 0.
     */
    @Test
    void testRotate0()
    {
        final Rectangle rectangle = new Rectangle(1.0, 2.0, 5.0, 10.0);
        rectangle.rotate(0);

        assertEquals(new Rectangle(1.0, 2.0, 5.0, 10.0), rectangle);
    }

    /**
     * Test rotation 90.
     */
    @Test
    void testRotate90()
    {
        final Rectangle rectangle = new Rectangle(1.0, 2.0, 5.0, 10.0);
        rectangle.rotate(90);

        assertEquals(new Rectangle(-1.5, 4.5, 10.0, 5.0), rectangle);
    }

    /**
     * Test rotation 360.
     */
    @Test
    void testRotate360()
    {
        final Rectangle rectangle = new Rectangle(1.0, 2.0, 5.0, 10.0);
        rectangle.rotate(360);

        assertEquals(new Rectangle(1.0, 2.0, 5.0, 10.0), rectangle);
    }

    /**
     * Test translate.
     */
    @Test
    void testTranslate()
    {
        final Rectangle rectangle = new Rectangle(1.0, 1.0, 5.0, 5.0);
        rectangle.translate(1.0, 2.0);

        assertEquals(2.0, rectangle.getX());
        assertEquals(3.0, rectangle.getY());
        assertEquals(2.0, rectangle.getMinX());
        assertEquals(3.0, rectangle.getMinY());
        assertEquals(7.0, rectangle.getMaxX());
        assertEquals(8.0, rectangle.getMaxY());
        assertEquals(5.0, rectangle.getWidthReal());
        assertEquals(5.0, rectangle.getHeightReal());
    }

    /**
     * Test equals.
     */
    @Test
    void testEquals()
    {
        final Rectangle rectangle = new Rectangle(0, 1, 2, 3);

        assertEquals(rectangle, rectangle);
        assertEquals(rectangle, new Rectangle(0, 1, 2, 3));

        assertNotEquals(rectangle, null);
        assertNotEquals(rectangle, new Object());
        assertNotEquals(rectangle, new Rectangle(0, 1, 2, 0));
        assertNotEquals(rectangle, new Rectangle(0, 1, 0, 3));
        assertNotEquals(rectangle, new Rectangle(0, 1, 0, 0));
        assertNotEquals(rectangle, new Rectangle(0, 0, 2, 3));
        assertNotEquals(rectangle, new Rectangle(0, 0, 2, 0));
        assertNotEquals(rectangle, new Rectangle(0, 0, 0, 0));
        assertNotEquals(rectangle, new Rectangle(0, 0, 0, 0));
        assertNotEquals(rectangle, new Rectangle(1, 1, 2, 3));
        assertNotEquals(rectangle, new Rectangle(0, 1, 2, 0));
        assertNotEquals(rectangle, new Rectangle(0, 1, 0, 3));
        assertNotEquals(rectangle, new Rectangle(0, 1, 0, 0));
        assertNotEquals(rectangle, new Rectangle(0, 0, 2, 3));
        assertNotEquals(rectangle, new Rectangle(0, 0, 2, 0));
        assertNotEquals(rectangle, new Rectangle(0, 0, 0, 3));
        assertNotEquals(rectangle, new Rectangle(1, 0, 0, 0));
    }

    /**
     * Test hash code.
     */
    @Test
    void testHashCode()
    {
        final Rectangle rectangle = new Rectangle(0, 1, 2, 3);

        assertHashEquals(rectangle, new Rectangle(0, 1, 2, 3));

        assertHashNotEquals(rectangle, new Object());
        assertHashNotEquals(rectangle, new Rectangle(0, 1, 2, 0));
        assertHashNotEquals(rectangle, new Rectangle(0, 1, 0, 3));
        assertHashNotEquals(rectangle, new Rectangle(0, 1, 0, 0));
        assertHashNotEquals(rectangle, new Rectangle(0, 0, 2, 3));
        assertHashNotEquals(rectangle, new Rectangle(0, 0, 2, 0));
        assertHashNotEquals(rectangle, new Rectangle(0, 0, 0, 0));
        assertHashNotEquals(rectangle, new Rectangle(0, 0, 0, 0));
        assertHashNotEquals(rectangle, new Rectangle(1, 1, 2, 3));
        assertHashNotEquals(rectangle, new Rectangle(0, 1, 2, 0));
        assertHashNotEquals(rectangle, new Rectangle(0, 1, 0, 3));
        assertHashNotEquals(rectangle, new Rectangle(0, 1, 0, 0));
        assertHashNotEquals(rectangle, new Rectangle(0, 0, 2, 3));
        assertHashNotEquals(rectangle, new Rectangle(0, 0, 2, 0));
        assertHashNotEquals(rectangle, new Rectangle(0, 0, 0, 3));
        assertHashNotEquals(rectangle, new Rectangle(1, 0, 0, 0));
    }

    /**
     * Test to string.
     */
    @Test
    void testToString()
    {
        assertEquals("Rectangle [x=0.0, y=0.0, width=0.0, height=0.0]", new Rectangle().toString());
        assertEquals("Rectangle [x=0.0, y=1.0, width=2.0, height=3.0]", new Rectangle(0, 1, 2, 3).toString());
    }
}
