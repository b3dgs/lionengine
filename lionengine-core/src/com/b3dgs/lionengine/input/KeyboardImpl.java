/*
 * Copyright (C) 2013 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.input;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.HashSet;
import java.util.Set;

/**
 * Keyboard input implementation.
 */
final class KeyboardImpl
        implements Keyboard, KeyListener
{
    /** List of keys. */
    private final Set<Integer> keys;
    /** Pressed states. */
    private final Set<Integer> pressed;
    /** Last key code. */
    private Integer lastCode;
    /** Last key name. */
    private char lastKeyName;

    /**
     * Create a keyboard input.
     */
    KeyboardImpl()
    {
        keys = new HashSet<>();
        pressed = new HashSet<>();
        lastKeyName = ' ';
        lastCode = Integer.valueOf(-1);
    }

    /*
     * Keyboard
     */

    @Override
    public boolean isPressed(Integer key)
    {
        return keys.contains(key);
    }

    @Override
    public boolean isPressedOnce(Integer key)
    {
        if (keys.contains(key))
        {
            if (!pressed.contains(key))
            {
                pressed.add(key);
                return true;
            }
        }
        return false;
    }

    @Override
    public Integer getKeyCode()
    {
        return lastCode;
    }

    @Override
    public char getKeyName()
    {
        return lastKeyName;
    }

    @Override
    public boolean used()
    {
        return !keys.isEmpty();
    }

    /*
     * KeyListener
     */

    @Override
    public void keyPressed(KeyEvent event)
    {
        lastCode = Integer.valueOf(event.getKeyCode());
        lastKeyName = event.getKeyChar();
        keys.add(Integer.valueOf(event.getKeyCode()));
    }

    @Override
    public void keyReleased(KeyEvent event)
    {
        lastCode = Integer.valueOf(-1);
        lastKeyName = ' ';
        keys.remove(Integer.valueOf(event.getKeyCode()));
        pressed.remove(Integer.valueOf(event.getKeyCode()));
    }

    @Override
    public void keyTyped(KeyEvent event)
    {
        // Nothing to do
    }
}
