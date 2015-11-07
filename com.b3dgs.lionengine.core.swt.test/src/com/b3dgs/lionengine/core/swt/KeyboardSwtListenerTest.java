/*
 * Copyright (C) 2013-2015 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.core.swt;

import java.util.concurrent.atomic.AtomicBoolean;

import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Widget;
import org.junit.Assert;
import org.junit.Test;

import com.b3dgs.lionengine.core.InputDeviceKeyListener;
import com.b3dgs.lionengine.core.swt.Keyboard;
import com.b3dgs.lionengine.core.swt.KeyboardSwtListener;
import com.b3dgs.lionengine.core.swt.ToolsSwt;

/**
 * Test the key listener.
 */
public class KeyboardSwtListenerTest
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
    public void testListener()
    {
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
        listener.keyPressed(createEvent(shell, Keyboard.UP));
        listener.keyReleased(createEvent(shell, Keyboard.UP));
        shell.dispose();

        Assert.assertTrue(reachedPressed.get());
        Assert.assertTrue(reachedReleased.get());
    }
}
