/*
 * Copyright (C) 2013-2021 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.awt;

import java.awt.HeadlessException;
import java.awt.MouseInfo;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.b3dgs.lionengine.Verbose;

/**
 * Mouse implementation.
 */
final class MouseClickAwt implements MouseListener
{
    /** Default button number. */
    private static final int DEFAULT_BUTTONS = 3;

    /**
     * Get the buttons number.
     * 
     * @return The buttons number.
     */
    private static int getButtonsNumber()
    {
        try
        {
            return Math.max(0, MouseInfo.getNumberOfButtons()) + 1;
        }
        catch (final HeadlessException exception)
        {
            Verbose.exception(exception);
            return DEFAULT_BUTTONS;
        }
    }

    /** Actions pressed listeners. */
    private final Map<Integer, List<EventAction>> actionsPressed = new HashMap<>();
    /** Actions released listeners. */
    private final Map<Integer, List<EventAction>> actionsReleased = new HashMap<>();
    /** Clicks flags. */
    private final boolean[] clicks;
    /** Clicked flags. */
    private final boolean[] clicked;
    /** Last click number. */
    private int lastClick;

    /**
     * Internal constructor.
     */
    MouseClickAwt()
    {
        super();

        final int mouseButtons = getButtonsNumber();
        clicks = new boolean[mouseButtons];
        clicked = new boolean[mouseButtons];
    }

    /**
     * Robot click mouse.
     * 
     * @param click The click number.
     */
    void robotPress(int click)
    {
        lastClick = click;
        if (lastClick < clicks.length)
        {
            clicks[lastClick] = true;
        }
    }

    /**
     * Robot release mouse.
     * 
     * @param click The click number.
     */
    void robotRelease(int click)
    {
        lastClick = 0;

        final int button = click;
        if (button < clicks.length)
        {
            clicks[button] = false;
            clicked[button] = false;
        }
    }

    /**
     * Add a pressed action.
     * 
     * @param click The action click.
     * @param action The associated action.
     */
    void addActionPressed(int click, EventAction action)
    {
        final List<EventAction> list;
        final Integer key = Integer.valueOf(click);
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

    /**
     * Add a released action.
     * 
     * @param click The action click.
     * @param action The associated action.
     */
    void addActionReleased(int click, EventAction action)
    {
        final Integer key = Integer.valueOf(click);
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

    /**
     * Get the last click.
     * 
     * @return The last click.
     */
    int getClick()
    {
        return lastClick;
    }

    /**
     * Check if click is clicked.
     * 
     * @param click The click to check.
     * @return <code>true</code> if clicked, <code>false</code> else.
     */
    boolean hasClicked(int click)
    {
        if (click < clicks.length)
        {
            return clicks[click];
        }
        return false;
    }

    /**
     * Check if click is clicked once.
     * 
     * @param click The click to check.
     * @return <code>true</code> if clicked once, <code>false</code> else.
     */
    boolean hasClickedOnce(int click)
    {
        if (click < clicks.length && clicks[click] && !clicked[click])
        {
            clicked[click] = true;
            return true;
        }
        return false;
    }

    /*
     * MouseListener
     */

    @Override
    public void mousePressed(MouseEvent event)
    {
        lastClick = event.getButton();
        clicks[lastClick] = true;

        final Integer key = Integer.valueOf(lastClick);
        if (actionsPressed.containsKey(key))
        {
            final List<EventAction> actions = actionsPressed.get(key);
            for (final EventAction current : actions)
            {
                current.action();
            }
        }
    }

    @Override
    public void mouseReleased(MouseEvent event)
    {
        final Integer key = Integer.valueOf(lastClick);
        lastClick = 0;

        final int button = event.getButton();
        clicks[button] = false;
        clicked[button] = false;

        if (actionsReleased.containsKey(key))
        {
            final List<EventAction> actions = actionsReleased.get(key);
            for (final EventAction current : actions)
            {
                current.action();
            }
        }
    }

    @Override
    public void mouseClicked(MouseEvent event)
    {
        // Nothing to do
    }

    @Override
    public void mouseEntered(MouseEvent event)
    {
        // Nothing to do
    }

    @Override
    public void mouseExited(MouseEvent event)
    {
        // Nothing to do
    }
}
