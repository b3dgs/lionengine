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
package com.b3dgs.lionengine.swt;

import static com.b3dgs.lionengine.swt.UtilAssert.assertEquals;
import static com.b3dgs.lionengine.swt.UtilAssert.assertFalse;
import static com.b3dgs.lionengine.swt.UtilAssert.assertTrue;

import java.util.concurrent.atomic.AtomicBoolean;

import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Widget;
import org.junit.jupiter.api.Test;

import com.b3dgs.lionengine.swt.graphic.KeyboardSwt;
import com.b3dgs.lionengine.swt.graphic.ScreenSwtTest;
import com.b3dgs.lionengine.swt.graphic.ToolsSwt;

/**
 * Test {@link KeyboardSwt}.
 */
final class KeyboardSwtTest
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
     * Test the keyboard not pressed state.
     */
    @Test
    void testNotPressed()
    {
        ScreenSwtTest.checkMultipleDisplaySupport();
        final KeyboardSwt keyboard = new KeyboardSwt();

        assertFalse(keyboard.isPushed(KeyboardSwt.ALT));
        assertFalse(keyboard.isPushed(KeyboardSwt.BACK_SPACE));
        assertFalse(keyboard.isPushed(KeyboardSwt.CONTROL));
        assertFalse(keyboard.isPushed(KeyboardSwt.DOWN));
        assertFalse(keyboard.isPushed(KeyboardSwt.ENTER));
        assertFalse(keyboard.isPushed(KeyboardSwt.ESCAPE));
        assertFalse(keyboard.isPushed(KeyboardSwt.LEFT));
        assertFalse(keyboard.isPushed(KeyboardSwt.RIGHT));
        assertFalse(keyboard.isPushed(KeyboardSwt.BACK_SPACE));
        assertFalse(keyboard.isPushed(KeyboardSwt.TAB));
        assertFalse(keyboard.isPushed(KeyboardSwt.UP));
        assertFalse(keyboard.isPushedOnce(KeyboardSwt.UP));
    }

    /**
     * Test the keyboard pressed.
     */
    @Test
    void testPressed()
    {
        ScreenSwtTest.checkMultipleDisplaySupport();
        final KeyboardSwt keyboard = new KeyboardSwt();
        final Shell shell = new Shell(ToolsSwt.getDisplay());

        keyboard.keyPressed(createEvent(shell, KeyboardSwt.ALT));

        assertTrue(keyboard.isPushed(KeyboardSwt.ALT));
        assertTrue(keyboard.isPushedOnce(KeyboardSwt.ALT));
        assertFalse(keyboard.isPushedOnce(KeyboardSwt.ALT));
        assertEquals(keyboard.getPushed(), KeyboardSwt.ALT);
        assertEquals(keyboard.getKeyName(), ' ');
        assertTrue(keyboard.used());

        keyboard.keyReleased(createEvent(shell, KeyboardSwt.ALT));

        assertFalse(keyboard.isPushed(KeyboardSwt.ALT));
        assertFalse(keyboard.used());
    }

    /**
     * Test the keyboard events.
     */
    @Test
    void testEvents()
    {
        ScreenSwtTest.checkMultipleDisplaySupport();
        final KeyboardSwt keyboard = new KeyboardSwt();
        final Shell shell = new Shell(ToolsSwt.getDisplay());
        final AtomicBoolean left = new AtomicBoolean(false);

        keyboard.addActionPressed(KeyboardSwt.LEFT, () -> left.set(true));
        keyboard.addActionPressed(KeyboardSwt.LEFT, () -> left.set(true));
        keyboard.addActionReleased(KeyboardSwt.LEFT, () -> left.set(false));
        keyboard.addActionReleased(KeyboardSwt.LEFT, () -> left.set(false));
        assertFalse(left.get());

        keyboard.keyPressed(createEvent(shell, KeyboardSwt.LEFT));
        assertTrue(left.get());

        keyboard.keyReleased(createEvent(shell, KeyboardSwt.LEFT));
        assertFalse(left.get());

        keyboard.removeActionsPressed();
        keyboard.removeActionsReleased();

        keyboard.keyPressed(createEvent(shell, KeyboardSwt.LEFT));
        assertFalse(left.get());

        keyboard.keyReleased(createEvent(shell, KeyboardSwt.LEFT));
        assertFalse(left.get());

        shell.dispose();
    }
}
