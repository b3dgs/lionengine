/*
 * Copyright (C) 2013-2018 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.headless;

import java.util.Collection;
import java.util.HashSet;

/**
 * Keyboard headless implementation.
 */
public final class KeyboardHeadless implements Keyboard
{
    /** Space key. */
    public static final Integer SPACE = Integer.valueOf(0);
    /** Arrow left key. */
    public static final Integer LEFT = Integer.valueOf(1);
    /** Arrow right key. */
    public static final Integer RIGHT = Integer.valueOf(2);
    /** Arrow down key. */
    public static final Integer DOWN = Integer.valueOf(3);
    /** Arrow up key. */
    public static final Integer UP = Integer.valueOf(4);
    /** No key code value. */
    public static final Integer NO_KEY_CODE = Integer.valueOf(-1);
    /** Empty key name. */
    private static final char EMPTY_KEY_NAME = ' ';

    /** List of keys. */
    private final Collection<Integer> keys = new HashSet<>();
    /** Pressed states. */
    private final Collection<Integer> pressed = new HashSet<>();
    /** Last key code. */
    private Integer lastCode = NO_KEY_CODE;
    /** Last key name. */
    private char lastKeyName = EMPTY_KEY_NAME;
    /** Left key. */
    private Integer leftKey = LEFT;
    /** Right key. */
    private Integer rightKey = RIGHT;
    /** Up key. */
    private Integer upKey = UP;
    /** Down key. */
    private Integer downKey = DOWN;

    /**
     * Constructor.
     */
    public KeyboardHeadless()
    {
        super();
    }

    /**
     * Notify key pressed.
     * 
     * @param event The associated event.
     */
    public void keyPressed(KeyEvent event)
    {
        lastKeyName = event.getName();
        lastCode = Integer.valueOf(event.getCode());
        if (!keys.contains(lastCode))
        {
            keys.add(lastCode);
        }
    }

    /**
     * Notify key released.
     * 
     * @param event The associated event.
     */
    public void keyReleased(KeyEvent event)
    {
        lastKeyName = EMPTY_KEY_NAME;
        lastCode = NO_KEY_CODE;

        final Integer key = Integer.valueOf(event.getCode());
        keys.remove(key);
        pressed.remove(key);
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
        if (keys.contains(key) && !pressed.contains(key))
        {
            pressed.add(key);
            return true;
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
        rightKey = code;
    }

    @Override
    public void setHorizontalControlNegative(Integer code)
    {
        leftKey = code;
    }

    @Override
    public void setVerticalControlPositive(Integer code)
    {
        upKey = code;
    }

    @Override
    public void setVerticalControlNegative(Integer code)
    {
        downKey = code;
    }

    @Override
    public Integer getHorizontalControlPositive()
    {
        return rightKey;
    }

    @Override
    public Integer getHorizontalControlNegative()
    {
        return leftKey;
    }

    @Override
    public Integer getVerticalControlPositive()
    {
        return upKey;
    }

    @Override
    public Integer getVerticalControlNegative()
    {
        return downKey;
    }

    @Override
    public double getHorizontalDirection()
    {
        final double direction;
        if (isPressed(leftKey))
        {
            direction = -1;
        }
        else if (isPressed(rightKey))
        {
            direction = 1;
        }
        else
        {
            direction = 0;
        }
        return direction;
    }

    @Override
    public double getVerticalDirection()
    {
        final int direction;
        if (isPressed(downKey))
        {
            direction = -1;
        }
        else if (isPressed(upKey))
        {
            direction = 1;
        }
        else
        {
            direction = 0;
        }
        return direction;
    }
}
