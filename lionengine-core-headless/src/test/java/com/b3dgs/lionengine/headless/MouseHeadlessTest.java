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
package com.b3dgs.lionengine.headless;

import static com.b3dgs.lionengine.UtilAssert.assertEquals;
import static com.b3dgs.lionengine.UtilAssert.assertFalse;
import static com.b3dgs.lionengine.UtilAssert.assertNotEquals;
import static com.b3dgs.lionengine.UtilAssert.assertTrue;

import org.junit.jupiter.api.Test;

/**
 * Test {@link MouseHeadless}.
 */
final class MouseHeadlessTest
{
    /**
     * Create a mouse event.
     * 
     * @param click The click mouse.
     * @param x The horizontal location.
     * @param y The vertical location.
     * @return The event instance.
     */
    private static MouseEvent createEvent(int click, int x, int y)
    {
        return new MouseEvent(x, y, click);
    }

    /**
     * Test clicked state.
     */
    @Test
    void testClicked()
    {
        final MouseHeadless mouse = new MouseHeadless();

        assertFalse(mouse.hasClicked(MouseHeadless.LEFT));
        mouse.mousePressed(createEvent(MouseHeadless.LEFT, 0, 0));
        assertTrue(mouse.hasClicked(MouseHeadless.LEFT));
        mouse.mouseReleased(createEvent(MouseHeadless.LEFT, 0, 0));
        assertFalse(mouse.hasClicked(MouseHeadless.LEFT));

        assertFalse(mouse.hasClicked(MouseHeadless.RIGHT));
        mouse.mousePressed(createEvent(MouseHeadless.RIGHT, 0, 0));
        assertTrue(mouse.hasClicked(MouseHeadless.RIGHT));
        mouse.mouseReleased(createEvent(MouseHeadless.RIGHT, 0, 0));
        assertFalse(mouse.hasClicked(MouseHeadless.RIGHT));

        assertFalse(mouse.hasClickedOnce(MouseHeadless.MIDDLE));
        mouse.mousePressed(createEvent(MouseHeadless.MIDDLE, 0, 0));
        assertTrue(mouse.hasClickedOnce(MouseHeadless.MIDDLE));
        mouse.mouseReleased(createEvent(MouseHeadless.MIDDLE, 0, 0));
        assertFalse(mouse.hasClickedOnce(MouseHeadless.MIDDLE));

        mouse.mousePressed(createEvent(MouseHeadless.MIDDLE, 0, 0));
        assertTrue(mouse.hasClickedOnce(MouseHeadless.MIDDLE));
        assertFalse(mouse.hasClickedOnce(MouseHeadless.MIDDLE));
    }

    /**
     * Test click.
     */
    @Test
    void testClick()
    {
        final MouseHeadless mouse = new MouseHeadless();

        mouse.mousePressed(createEvent(MouseHeadless.MIDDLE, 0, 0));

        assertEquals(MouseHeadless.MIDDLE, mouse.getClick());

        mouse.mouseReleased(createEvent(MouseHeadless.MIDDLE, 0, 0));

        assertNotEquals(MouseHeadless.MIDDLE, mouse.getClick());
    }

    /**
     * Test location.
     */
    @Test
    void testLocation()
    {
        final MouseHeadless mouse = new MouseHeadless();
        mouse.mouseMoved(createEvent(MouseHeadless.LEFT, 0, 0));

        assertEquals(0, mouse.getX());
        assertEquals(0, mouse.getY());

        mouse.mouseMoved(createEvent(MouseHeadless.LEFT, 10, 20));

        assertEquals(10, mouse.getX());
        assertEquals(20, mouse.getY());
    }

    /**
     * Test do click robot.
     */
    @Test
    void testDoClick()
    {
        final MouseHeadless mouse = new MouseHeadless();

        assertFalse(mouse.hasClicked(MouseHeadless.RIGHT));

        mouse.mouseMoved(createEvent(MouseHeadless.LEFT, 0, 0));
        mouse.doClickAt(MouseHeadless.RIGHT, 500, 500);
        mouse.update(1.0);
        try
        {
            assertEquals(500, mouse.getOnScreenX());
            assertEquals(500, mouse.getOnScreenY());
            assertTrue(mouse.hasClicked(MouseHeadless.RIGHT));
        }
        finally
        {
            mouse.doClickAt(MouseHeadless.LEFT, 499, 499);
        }

        assertTrue(mouse.hasClicked(MouseHeadless.LEFT));

        mouse.doClick(MouseHeadless.LEFT);

        assertTrue(mouse.hasClicked(MouseHeadless.LEFT));

        mouse.update(1.0);
        mouse.doClick(MouseHeadless.MIDDLE);
        mouse.update(1.0);

        mouse.doSetMouse(1, 2);

        assertEquals(1, mouse.getOnScreenX());
        assertEquals(2, mouse.getOnScreenY());

        mouse.doMoveMouse(3, 4);

        assertEquals(3, mouse.getOnScreenX());
        assertEquals(4, mouse.getOnScreenY());
    }

    /**
     * Test do click robot with out range click.
     */
    @Test
    void testDoClickOutRange()
    {
        final MouseHeadless mouse = new MouseHeadless();
        mouse.doClick(Integer.MAX_VALUE);
        mouse.update(1.0);
        mouse.update(1.0);

        assertFalse(mouse.hasClickedOnce(Integer.MAX_VALUE));
        assertFalse(mouse.hasClicked(Integer.MAX_VALUE));
    }

    /**
     * Test move.
     */
    @Test
    void testMouse()
    {
        final MouseHeadless mouse = new MouseHeadless();
        mouse.mouseMoved(createEvent(MouseHeadless.LEFT, 0, 0));
        mouse.mouseDragged(createEvent(0, 0, 0));
        mouse.update(1.0);

        assertEquals(0, mouse.getMoveX());
        assertEquals(0, mouse.getMoveY());
        assertTrue(mouse.hasMoved());
        assertFalse(mouse.hasMoved());
    }
}
