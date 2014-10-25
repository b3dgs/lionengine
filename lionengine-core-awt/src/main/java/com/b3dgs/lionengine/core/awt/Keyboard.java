/*
 * Copyright (C) 2013-2014 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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

import com.b3dgs.lionengine.core.InputDeviceDirectional;

/**
 * Represents the keyboard input. Gives informations such as pressed key and code.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public final class Keyboard
        implements InputDeviceDirectional, KeyListener
{
    /** Enter key. */
    public static final Integer ENTER = Integer.valueOf(KeyEvent.VK_ENTER);
    /** Tab key. */
    public static final Integer TAB = Integer.valueOf(KeyEvent.VK_TAB);
    /** Back Space key. */
    public static final Integer BACK_SPACE = Integer.valueOf(KeyEvent.VK_BACK_SPACE);
    /** Space key. */
    public static final Integer SPACE = Integer.valueOf(KeyEvent.VK_SPACE);
    /** Escape key. */
    public static final Integer ESCAPE = Integer.valueOf(KeyEvent.VK_ESCAPE);
    /** ALT key. */
    public static final Integer ALT = Integer.valueOf(KeyEvent.VK_ALT);
    /** CTRL key. */
    public static final Integer CONTROL = Integer.valueOf(KeyEvent.VK_CONTROL);
    /** Arrow left key. */
    public static final Integer LEFT = Integer.valueOf(KeyEvent.VK_LEFT);
    /** Arrow right key. */
    public static final Integer RIGHT = Integer.valueOf(KeyEvent.VK_RIGHT);
    /** Arrow down key. */
    public static final Integer DOWN = Integer.valueOf(KeyEvent.VK_DOWN);
    /** Arrow up key. */
    public static final Integer UP = Integer.valueOf(KeyEvent.VK_UP);

    /** List of keys. */
    private final Collection<Integer> keys;
    /** Pressed states. */
    private final Collection<Integer> pressed;
    /** Last key code. */
    private Integer lastCode;
    /** Last key name. */
    private char lastKeyName;
    /** Left key. */
    private Integer left;
    /** Right key. */
    private Integer right;
    /** Up key. */
    private Integer up;
    /** Down key. */
    private Integer down;

    /**
     * Internal constructor.
     */
    Keyboard()
    {
        keys = new HashSet<>();
        pressed = new HashSet<>();
        lastKeyName = ' ';
        lastCode = Integer.valueOf(-1);
    }

    /**
     * Check if the key is currently pressed.
     * 
     * @param key The key to check.
     * @return <code>true</code> if pressed, <code>false</code> else.
     */
    public boolean isPressed(Integer key)
    {
        return keys.contains(key);
    }

    /**
     * Check if the key is currently pressed (not continuously).
     * 
     * @param key The key to check.
     * @return <code>true</code> if pressed, <code>false</code> else.
     */
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

    /**
     * Get the current pressed key code.
     * 
     * @return The pressed key code.
     */
    public Integer getKeyCode()
    {
        return lastCode;
    }

    /**
     * Get the current pressed key name.
     * 
     * @return The pressed key name.
     */
    public char getKeyName()
    {
        return lastKeyName;
    }

    /**
     * Check if the keyboard is currently used (at least one pressed key).
     * 
     * @return <code>true</code> if has at least on pressed key, <code>false</code> else (no pressed key).
     */
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
    public int getHorizontalDirection()
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
    public int getVerticalDirection()
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
