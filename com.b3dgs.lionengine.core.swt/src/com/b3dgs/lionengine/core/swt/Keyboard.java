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
package com.b3dgs.lionengine.core.swt;

import java.util.Collection;
import java.util.HashSet;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;

import com.b3dgs.lionengine.core.InputDeviceDirectional;

/**
 * Keyboard input implementation.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public final class Keyboard
        implements InputDeviceDirectional, KeyListener
{
    /** Arrow up key. */
    public static final Integer UP = Integer.valueOf(SWT.ARROW_UP);
    /** Arrow down key. */
    public static final Integer DOWN = Integer.valueOf(SWT.ARROW_DOWN);
    /** Arrow right key. */
    public static final Integer RIGHT = Integer.valueOf(SWT.ARROW_RIGHT);
    /** Arrow left key. */
    public static final Integer LEFT = Integer.valueOf(SWT.ARROW_LEFT);
    /** CTRL key. */
    public static final Integer CONTROL = Integer.valueOf(SWT.CONTROL);
    /** ALT key. */
    public static final Integer ALT = Integer.valueOf(SWT.ALT);
    /** Escape key. */
    public static final Integer ESCAPE = Integer.valueOf(SWT.ESC);
    /** Enter key. */
    public static final Integer ENTER = Integer.valueOf(SWT.LF);
    /** Back Space key. */
    public static final Integer BACK_SPACE = Integer.valueOf(SWT.BS);
    /** Tab key. */
    public static final Integer TAB = Integer.valueOf(SWT.TAB);

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
     * Constructor.
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
        lastCode = Integer.valueOf(event.keyCode);
        lastKeyName = event.character;
        keys.add(Integer.valueOf(event.keyCode));
    }

    @Override
    public void keyReleased(KeyEvent event)
    {
        lastCode = Integer.valueOf(-1);
        lastKeyName = ' ';
        keys.remove(Integer.valueOf(event.keyCode));
        pressed.remove(Integer.valueOf(event.keyCode));
    }
}
