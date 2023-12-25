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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;

import com.b3dgs.lionengine.InputDeviceListener;
import com.b3dgs.lionengine.ListenableModel;
import com.b3dgs.lionengine.swt.EventAction;
import com.b3dgs.lionengine.swt.Keyboard;

/**
 * Keyboard input implementation.
 */
public final class KeyboardSwt implements Keyboard, KeyListener
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
    /** Empty key name. */
    private static final char EMPTY_KEY_NAME = ' ';
    /** Index value. */
    private static final Integer INDEX = Integer.valueOf(0);

    /** Push listener. */
    private final ListenableModel<InputDeviceListener> listeners = new ListenableModel<>();
    /** Actions pressed listeners. */
    private final Map<Integer, List<EventAction>> actionsPressed = new HashMap<>();
    /** Actions released listeners. */
    private final Map<Integer, List<EventAction>> actionsReleased = new HashMap<>();
    /** List of keys. */
    private final Collection<Integer> keys = new HashSet<>();
    /** Pressed states. */
    private final Collection<Integer> pressed = new HashSet<>();
    /** Last key code. */
    private Integer lastCode;
    /** Last key name. */
    private char lastKeyName = EMPTY_KEY_NAME;

    /**
     * Constructor.
     */
    public KeyboardSwt()
    {
        lastKeyName = ' ';
        lastCode = Integer.valueOf(-1);
    }

    @Override
    public void addListener(InputDeviceListener listener)
    {
        listeners.addListener(listener);
    }

    @Override
    public void removeListener(InputDeviceListener listener)
    {
        listeners.removeListener(listener);
    }

    @Override
    public void addActionPressed(Integer key, EventAction action)
    {
        final List<EventAction> list;
        if (actionsPressed.get(key) == null)
        {
            list = new ArrayList<>();
            actionsPressed.put(key, list);
        }
        else
        {
            list = actionsPressed.get(key);
        }
        list.add(action);
    }

    @Override
    public void addActionReleased(Integer key, EventAction action)
    {
        final List<EventAction> list;
        if (actionsReleased.get(key) == null)
        {
            list = new ArrayList<>();
            actionsReleased.put(key, list);
        }
        else
        {
            list = actionsReleased.get(key);
        }
        list.add(action);
    }

    @Override
    public void removeActionsPressed()
    {
        actionsPressed.clear();
    }

    @Override
    public void removeActionsReleased()
    {
        actionsReleased.clear();
    }

    @Override
    public boolean isPushed(Integer key)
    {
        return keys.contains(key);
    }

    @Override
    public boolean isPushedOnce(Integer key)
    {
        if (keys.contains(key) && !pressed.contains(key))
        {
            pressed.add(key);
            return true;
        }
        return false;
    }

    @Override
    public boolean isPushed()
    {
        return lastCode != null;
    }

    @Override
    public Integer getPushed()
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

    @Override
    public String getName()
    {
        return Keyboard.class.getSimpleName();
    }

    @Override
    public void keyPressed(KeyEvent event)
    {
        final Integer key = Integer.valueOf(event.keyCode);
        lastKeyName = event.character;
        keys.add(key);

        if (actionsPressed.containsKey(key))
        {
            final List<EventAction> actions = actionsPressed.get(key);
            for (final EventAction current : actions)
            {
                current.action();
            }
        }

        if (!key.equals(lastCode))
        {
            final int n = listeners.size();
            for (int i = 0; i < n; i++)
            {
                listeners.get(i).onDeviceChanged(INDEX, key, lastKeyName, true);
            }
            lastCode = key;
        }
    }

    @Override
    public void keyReleased(KeyEvent event)
    {
        final Integer key = Integer.valueOf(event.keyCode);
        keys.remove(key);
        pressed.remove(key);

        if (actionsReleased.containsKey(key))
        {
            final List<EventAction> actions = actionsReleased.get(key);
            for (final EventAction current : actions)
            {
                current.action();
            }
        }
        final int n = listeners.size();
        for (int i = 0; i < n; i++)
        {
            listeners.get(i).onDeviceChanged(INDEX, key, lastKeyName, false);
        }

        lastKeyName = EMPTY_KEY_NAME;
        lastCode = null;
    }
}
