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
package com.b3dgs.lionengine.swt.graphic;

import org.eclipse.swt.events.KeyEvent;

import com.b3dgs.lionengine.InputDeviceKeyListener;

/**
 * Keyboard listener implementation.
 */
public final class KeyboardSwtListener implements org.eclipse.swt.events.KeyListener
{
    /** The original listener. */
    private final InputDeviceKeyListener listener;

    /**
     * Internal constructor.
     * 
     * @param listener The original listener.
     */
    public KeyboardSwtListener(InputDeviceKeyListener listener)
    {
        this.listener = listener;
    }

    @Override
    public void keyPressed(KeyEvent event)
    {
        listener.keyPressed(event.keyCode, event.character);
    }

    @Override
    public void keyReleased(KeyEvent event)
    {
        listener.keyReleased(event.keyCode, event.character);
    }
}
