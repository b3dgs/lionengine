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
package com.b3dgs.lionengine.game.pathfinding;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Sorted list implementation.
 * 
 * @param <E> The comparable type.
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
final class SortedList<E extends Comparable<E>>
{
    /** List of object. */
    private final List<E> list;

    /**
     * Internal constructor.
     */
    SortedList()
    {
        list = new ArrayList<>();
    }

    /**
     * Get first element.
     * 
     * @return The first element.
     */
    public E first()
    {
        return list.get(0);
    }

    /**
     * Clear all elements.
     */
    public void clear()
    {
        list.clear();
    }

    /**
     * Add an element to the list.
     * 
     * @param element The element to add.
     */
    public void add(E element)
    {
        list.add(element);
        Collections.sort(list);
    }

    /**
     * Remove an element from the list.
     * 
     * @param element The element to remove.
     */
    public void remove(E element)
    {
        list.remove(element);
    }

    /**
     * Get the list size.
     * 
     * @return The list size.
     */
    public int size()
    {
        return list.size();
    }

    /**
     * Check if the list contain this element.
     * 
     * @param element The element to check.
     * @return <code>true</code> if element is contained, <code>false</code> else.
     */
    public boolean contains(E element)
    {
        return list.contains(element);
    }
}
