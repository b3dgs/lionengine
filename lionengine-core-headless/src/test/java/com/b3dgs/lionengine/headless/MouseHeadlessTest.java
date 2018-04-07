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
package com.b3dgs.lionengine.headless;

import org.junit.Assert;
import org.junit.Test;

/**
 * Test {@link MouseHeadless}.
 */
public final class MouseHeadlessTest
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
    public void testClicked()
    {
        final MouseHeadless mouse = new MouseHeadless();

        Assert.assertFalse(mouse.hasClicked(MouseHeadless.LEFT));
        mouse.mousePressed(createEvent(MouseHeadless.LEFT, 0, 0));
        Assert.assertTrue(mouse.hasClicked(MouseHeadless.LEFT));
        mouse.mouseReleased(createEvent(MouseHeadless.LEFT, 0, 0));
        Assert.assertFalse(mouse.hasClicked(MouseHeadless.LEFT));

        Assert.assertFalse(mouse.hasClicked(MouseHeadless.RIGHT));
        mouse.mousePressed(createEvent(MouseHeadless.RIGHT, 0, 0));
        Assert.assertTrue(mouse.hasClicked(MouseHeadless.RIGHT));
        mouse.mouseReleased(createEvent(MouseHeadless.RIGHT, 0, 0));
        Assert.assertFalse(mouse.hasClicked(MouseHeadless.RIGHT));

        Assert.assertFalse(mouse.hasClickedOnce(MouseHeadless.MIDDLE));
        mouse.mousePressed(createEvent(MouseHeadless.MIDDLE, 0, 0));
        Assert.assertTrue(mouse.hasClickedOnce(MouseHeadless.MIDDLE));
        mouse.mouseReleased(createEvent(MouseHeadless.MIDDLE, 0, 0));
        Assert.assertFalse(mouse.hasClickedOnce(MouseHeadless.MIDDLE));

        mouse.mousePressed(createEvent(MouseHeadless.MIDDLE, 0, 0));
        Assert.assertTrue(mouse.hasClickedOnce(MouseHeadless.MIDDLE));
        Assert.assertFalse(mouse.hasClickedOnce(MouseHeadless.MIDDLE));
    }

    /**
     * Test click.
     */
    @Test
    public void testClick()
    {
        final MouseHeadless mouse = new MouseHeadless();

        mouse.mousePressed(createEvent(MouseHeadless.MIDDLE, 0, 0));
        Assert.assertEquals(MouseHeadless.MIDDLE, mouse.getClick());
        mouse.mouseReleased(createEvent(MouseHeadless.MIDDLE, 0, 0));
        Assert.assertNotEquals(MouseHeadless.MIDDLE, mouse.getClick());
    }

    /**
     * Test location.
     */
    @Test
    public void testLocation()
    {
        final MouseHeadless mouse = new MouseHeadless();

        mouse.mouseMoved(createEvent(MouseHeadless.LEFT, 0, 0));
        Assert.assertEquals(0, mouse.getX());
        Assert.assertEquals(0, mouse.getY());

        mouse.mouseMoved(createEvent(MouseHeadless.LEFT, 10, 20));
        Assert.assertEquals(10, mouse.getX());
        Assert.assertEquals(20, mouse.getY());
    }

    /**
     * Test do click robot.
     */
    @Test
    public void testDoClick()
    {
        final MouseHeadless mouse = new MouseHeadless();

        Assert.assertFalse(mouse.hasClicked(MouseHeadless.RIGHT));
        mouse.mouseMoved(createEvent(MouseHeadless.LEFT, 0, 0));
        mouse.doClickAt(MouseHeadless.RIGHT, 500, 500);
        mouse.update(1.0);
        try
        {
            Assert.assertEquals(500, mouse.getOnScreenX());
            Assert.assertEquals(500, mouse.getOnScreenY());
            Assert.assertTrue(mouse.hasClicked(MouseHeadless.RIGHT));
        }
        finally
        {
            mouse.doClickAt(MouseHeadless.LEFT, 499, 499);
        }

        Assert.assertTrue(mouse.hasClicked(MouseHeadless.LEFT));
        mouse.doClick(MouseHeadless.LEFT);
        Assert.assertTrue(mouse.hasClicked(MouseHeadless.LEFT));
        mouse.update(1.0);
        mouse.doClick(MouseHeadless.MIDDLE);
        mouse.update(1.0);

        mouse.doSetMouse(1, 2);
        Assert.assertEquals(1, mouse.getOnScreenX());
        Assert.assertEquals(2, mouse.getOnScreenY());

        mouse.doMoveMouse(3, 4);
        Assert.assertEquals(3, mouse.getOnScreenX());
        Assert.assertEquals(4, mouse.getOnScreenY());
    }

    /**
     * Test do click robot with out range click.
     */
    @Test
    public void testDoClickOutRange()
    {
        final MouseHeadless mouse = new MouseHeadless();
        mouse.doClick(Integer.MAX_VALUE);
        mouse.update(1.0);
        mouse.update(1.0);

        Assert.assertFalse(mouse.hasClickedOnce(Integer.MAX_VALUE));
        Assert.assertFalse(mouse.hasClicked(Integer.MAX_VALUE));
    }

    /**
     * Test move.
     */
    @Test
    public void testMouse()
    {
        final MouseHeadless mouse = new MouseHeadless();

        mouse.mouseMoved(createEvent(MouseHeadless.LEFT, 0, 0));
        mouse.mouseDragged(createEvent(0, 0, 0));
        mouse.update(1.0);
        Assert.assertEquals(0, mouse.getMoveX());
        Assert.assertEquals(0, mouse.getMoveY());
        Assert.assertTrue(mouse.hasMoved());
        Assert.assertFalse(mouse.hasMoved());
    }
}
