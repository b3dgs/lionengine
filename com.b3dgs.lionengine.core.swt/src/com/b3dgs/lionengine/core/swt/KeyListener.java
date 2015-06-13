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

import org.eclipse.swt.events.KeyEvent;

import com.b3dgs.lionengine.core.InputDeviceKeyListener;

/**
 * Keyboard listener implementation.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
final class KeyListener
        implements org.eclipse.swt.events.KeyListener
{
    /** The original listener. */
    private final InputDeviceKeyListener listener;

    /**
     * Internal constructor.
     * 
     * @param listener The original listener.
     */
    KeyListener(InputDeviceKeyListener listener)
    {
        this.listener = listener;
    }

    /*
     * KeyListener
     */

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
