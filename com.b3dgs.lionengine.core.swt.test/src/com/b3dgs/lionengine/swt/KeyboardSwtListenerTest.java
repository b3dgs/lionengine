/*
 * Copyright (C) 2013-2023 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.swt;

import static com.b3dgs.lionengine.swt.UtilAssert.assertTrue;

import java.util.concurrent.atomic.AtomicBoolean;

import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Widget;
import org.junit.jupiter.api.Test;

import com.b3dgs.lionengine.InputDeviceKeyListener;
import com.b3dgs.lionengine.swt.graphic.KeyboardSwt;
import com.b3dgs.lionengine.swt.graphic.KeyboardSwtListener;
import com.b3dgs.lionengine.swt.graphic.ScreenSwtTest;
import com.b3dgs.lionengine.swt.graphic.ToolsSwt;

/**
 * Test {@link KeyboardSwtListener}.
 */
final class KeyboardSwtListenerTest
{
    /**
     * Create a key event.
     * 
     * @param widget The widget parent.
     * @param key The event key.
     * @return The event instance.
     */
    private static KeyEvent createEvent(Widget widget, Integer key)
    {
        final Event event = new Event();
        event.widget = widget;
        event.keyCode = key.intValue();
        event.character = ' ';

        return new KeyEvent(event);
    }

    /**
     * Test key listener.
     */
    @Test
    void testListener()
    {
        ScreenSwtTest.checkMultipleDisplaySupport();
        final AtomicBoolean reachedPressed = new AtomicBoolean(false);
        final AtomicBoolean reachedReleased = new AtomicBoolean(false);
        final KeyboardSwtListener listener = new KeyboardSwtListener(new InputDeviceKeyListener()
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

        final Shell shell = new Shell(ToolsSwt.getDisplay());
        listener.keyPressed(createEvent(shell, KeyboardSwt.UP));
        listener.keyReleased(createEvent(shell, KeyboardSwt.UP));
        shell.dispose();

        assertTrue(reachedPressed.get());
        assertTrue(reachedReleased.get());
    }
}
