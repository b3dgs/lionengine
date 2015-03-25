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
package com.b3dgs.lionengine.core.awt;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Collection;
import java.util.HashSet;

/**
 * Keyboard implementation.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
final class KeyboardAwt
        implements Keyboard, KeyListener
{
    /** No key code. */
    private static final int NO_KEY_CODE = -1;
    /** No key code value. */
    private static final Integer NO_KEY_CODE_VALUE = Integer.valueOf(NO_KEY_CODE);
    /** Empty key name. */
    private static final char EMPTY_KEY_NAME = ' ';

    /** List of keys. */
    private final Collection<Integer> keys;
    /** Pressed states. */
    private final Collection<Integer> pressed;
    /** Last key code. */
    private Integer lastCode = NO_KEY_CODE_VALUE;
    /** Last key name. */
    private char lastKeyName = EMPTY_KEY_NAME;
    /** Left key. */
    private Integer left = LEFT;
    /** Right key. */
    private Integer right = RIGHT;
    /** Up key. */
    private Integer up = UP;
    /** Down key. */
    private Integer down = DOWN;

    /**
     * Internal constructor.
     */
    KeyboardAwt()
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
     * InputDeviceDirectional
     */

    @Override
    public void setHorizontalControlPositive(Integer code)
    {
        right = code;
    }

    @Override
    public void setHorizontalControlNegative(Integer code)
    {
        left = code;
    }

    @Override
    public void setVerticalControlPositive(Integer code)
    {
        up = code;
    }

    @Override
    public void setVerticalControlNegative(Integer code)
    {
        down = code;
    }

    @Override
    public double getHorizontalDirection()
    {
        if (isPressed(left))
        {
            return -1;
        }
        else if (isPressed(right))
        {
            return 1;
        }
        return 0;
    }

    @Override
    public double getVerticalDirection()
    {
        if (isPressed(down))
        {
            return -1;
        }
        else if (isPressed(up))
        {
            return 1;
        }
        return 0;
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
        lastCode = NO_KEY_CODE_VALUE;
        lastKeyName = EMPTY_KEY_NAME;
        keys.remove(Integer.valueOf(event.getKeyCode()));
        pressed.remove(Integer.valueOf(event.getKeyCode()));
    }

    @Override
    public void keyTyped(KeyEvent event)
    {
        // Nothing to do
    }
}
