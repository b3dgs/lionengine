/*
 * Copyright (C) 2013-2020 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine;

import static com.b3dgs.lionengine.UtilAssert.assertEquals;
import static com.b3dgs.lionengine.UtilAssert.assertFalse;
import static com.b3dgs.lionengine.UtilAssert.assertHashEquals;
import static com.b3dgs.lionengine.UtilAssert.assertHashNotEquals;
import static com.b3dgs.lionengine.UtilAssert.assertNotEquals;
import static com.b3dgs.lionengine.UtilAssert.assertThrows;
import static com.b3dgs.lionengine.UtilAssert.assertTrue;

import org.junit.jupiter.api.Test;

/**
 * Test {@link Animation}.
 */
public final class AnimationTest
{
    /** Minimum frame value. */
    private static final int MIN = Animation.MINIMUM_FRAME;

    /**
     * Test <code>null</code> name argument.
     */
    @Test
    public void testNameNull()
    {
        assertThrows(() -> new Animation(null, 1, 1, 0.0, false, false), Check.ERROR_NULL);
    }

    /**
     * Test the failure minimum frame under minimum.
     */
    @Test
    public void testFailureMinimumFrame()
    {
        final int underMin = MIN - 1;
        assertThrows(() -> new Animation(Animation.DEFAULT_NAME, underMin, 0, 0.0, false, false),
                     Check.ERROR_ARGUMENT + underMin + Check.ERROR_SUPERIOR + MIN);
    }

    /**
     * Test the failure maximum frame under minimum frame.
     */
    @Test
    public void testFailureMaximumFrame()
    {
        final int underMin = MIN - 1;
        assertThrows(() -> new Animation(Animation.DEFAULT_NAME, MIN, underMin, 0.0, false, false),
                     Check.ERROR_ARGUMENT + underMin + Check.ERROR_SUPERIOR + MIN);
    }

    /**
     * Test the failure speed negative.
     */
    @Test
    public void testFailureSpeed()
    {
        final double speed = -1.0;
        assertThrows(() -> new Animation(Animation.DEFAULT_NAME, MIN, MIN + 1, -1.0, false, false),
                     Check.ERROR_ARGUMENT + speed + Check.ERROR_SUPERIOR + 0.0);
    }

    /**
     * Test the getters.
     */
    @Test
    public void testGetters()
    {
        final Animation animation = new Animation("name", 1, 2, 3.5, true, false);

        assertEquals("name", animation.getName());
        assertEquals(1, animation.getFirst());
        assertEquals(2, animation.getLast());
        assertEquals(3.5, animation.getSpeed());
        assertTrue(animation.hasReverse());
        assertFalse(animation.hasRepeat());
        assertEquals(2, animation.getFrames());
    }

    /**
     * Test the equals.
     */
    @Test
    public void testEquals()
    {
        final Animation animation = new Animation("test", 1, 2, 3, false, true);

        assertEquals(animation, animation);
        assertEquals(animation, new Animation("test", 1, 2, 3, false, true));
        assertEquals(animation, new Animation("test", 11, 12, 3, false, true));
        assertEquals(animation, new Animation("test", 1, 12, 2, false, true));
        assertEquals(animation, new Animation("test", 1, 2, 13, false, true));
        assertEquals(animation, new Animation("test", 1, 2, 3, true, true));
        assertEquals(animation, new Animation("test", 1, 2, 3, false, false));

        assertNotEquals(animation, null);
        assertNotEquals(animation, new Object());
        assertNotEquals(animation, new Animation("test1", 1, 2, 3, false, true));
    }

    /**
     * Test the hash code.
     */
    @Test
    public void testHashCode()
    {
        final Animation animation = new Animation("test", 1, 2, 3, false, true);

        assertHashEquals(animation, new Animation("test", 1, 2, 3, false, true));
        assertHashEquals(animation, new Animation("test", 11, 12, 3, false, true));
        assertHashEquals(animation, new Animation("test", 1, 12, 3, false, true));
        assertHashEquals(animation, new Animation("test", 1, 2, 13, false, true));
        assertHashEquals(animation, new Animation("test", 1, 2, 3, true, true));
        assertHashEquals(animation, new Animation("test", 1, 2, 3, false, false));

        assertHashNotEquals(animation, new Object());
        assertHashNotEquals(animation, new Animation("test1", 1, 2, 3, false, true));
    }
}
