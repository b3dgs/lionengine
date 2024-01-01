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
package com.b3dgs.lionengine.awt;

import static com.b3dgs.lionengine.UtilAssert.assertTrue;

import java.util.concurrent.atomic.AtomicBoolean;

import org.junit.jupiter.api.Test;

import com.b3dgs.lionengine.InputDeviceKeyListener;

/**
 * Test {@link KeyboardAwtListener}.
 */
final class KeyboardAwtListenerTest
{
    /**
     * Test key listener.
     */
    @Test
    void testKeyboardListener()
    {
        final AtomicBoolean reachedPressed = new AtomicBoolean(false);
        final AtomicBoolean reachedReleased = new AtomicBoolean(false);

        final KeyboardAwtListener listener = new KeyboardAwtListener(new InputDeviceKeyListener()
        {
            @Override
            public void keyPressed(int keyCode, char keyChar)
            {
                reachedPressed.set(true);
            }

            @Override
            public void keyReleased(int keyCode, char keyChar)
            {
                reachedReleased.set(true);
            }
        });

        listener.keyTyped(KeyboardAwtTest.createEvent(KeyboardAwt.UP));
        listener.keyPressed(KeyboardAwtTest.createEvent(KeyboardAwt.UP));
        listener.keyReleased(KeyboardAwtTest.createEvent(KeyboardAwt.UP));

        assertTrue(reachedPressed.get());
        assertTrue(reachedReleased.get());
    }
}
