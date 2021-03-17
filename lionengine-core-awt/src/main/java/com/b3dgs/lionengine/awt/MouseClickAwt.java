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

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

/**
 * Mouse implementation.
 */
final class MouseClickAwt implements MouseListener
{
    /** No click value. */
    public static final Integer NO_CLICK_CODE = Integer.valueOf(0);

    /** Actions pressed listeners. */
    private final Map<Integer, List<EventAction>> actionsPressed = new HashMap<>();
    /** Actions released listeners. */
    private final Map<Integer, List<EventAction>> actionsReleased = new HashMap<>();
    /** Clicks flags. */
    private final Collection<Integer> clicks = new HashSet<>();
    /** Clicked flags. */
    private final Collection<Integer> clicked = new HashSet<>();
    /** Last click number. */
    private Integer lastClick = NO_CLICK_CODE;

    /**
     * Internal constructor.
     */
    MouseClickAwt()
    {
        super();
    }

    /**
     * Robot click mouse.
     * 
     * @param click The click number.
     */
    void robotPress(Integer click)
    {
        lastClick = click;
        clicks.add(click);
    }

    /**
     * Robot release mouse.
     * 
     * @param click The click number.
     */
    void robotRelease(Integer click)
    {
        lastClick = NO_CLICK_CODE;
        clicks.remove(click);
        clicked.remove(click);
    }

    /**
     * Add a pressed action.
     * 
     * @param click The action click.
     * @param action The associated action.
     */
    void addActionPressed(Integer click, EventAction action)
    {
        final List<EventAction> list;
        if (actionsPressed.get(click) == null)
        {
            list = new ArrayList<>();
            actionsPressed.put(click, list);
        }
        else
        {
            list = actionsPressed.get(click);
        }
        list.add(action);
    }

    /**
     * Add a released action.
     * 
     * @param click The action click.
     * @param action The associated action.
     */
    void addActionReleased(Integer click, EventAction action)
    {
        final List<EventAction> list;
        if (!actionsReleased.containsKey(click))
        {
            list = new ArrayList<>();
            actionsReleased.put(click, list);
        }
        else
        {
            list = actionsReleased.get(click);
        }
        list.add(action);
    }

    /**
     * Get the last click.
     * 
     * @return The last click.
     */
    Integer getClick()
    {
        return lastClick;
    }

    /**
     * Check if click is clicked.
     * 
     * @param click The click to check.
     * @return <code>true</code> if clicked, <code>false</code> else.
     */
    boolean hasClicked(Integer click)
    {
        return clicks.contains(click);
    }

    /**
     * Check if click is clicked once.
     * 
     * @param click The click to check.
     * @return <code>true</code> if clicked once, <code>false</code> else.
     */
    boolean hasClickedOnce(Integer click)
    {
        if (clicks.contains(click) && !clicked.contains(click))
        {
            clicked.add(click);
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
        lastClick = Integer.valueOf(event.getButton());
        clicks.add(lastClick);

        if (actionsPressed.containsKey(lastClick))
        {
            final List<EventAction> actions = actionsPressed.get(lastClick);
            for (final EventAction current : actions)
            {
                current.action();
            }
        }
    }

    @Override
    public void mouseReleased(MouseEvent event)
    {
        lastClick = NO_CLICK_CODE;

        final Integer click = Integer.valueOf(event.getButton());
        clicks.remove(click);
        clicked.remove(click);

        if (actionsReleased.containsKey(click))
        {
            final List<EventAction> actions = actionsReleased.get(click);
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
