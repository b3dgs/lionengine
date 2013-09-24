package com.b3dgs.lionengine.game.pathfinding;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Sorted list implementation.
 * 
 * @param <E> The comparable type.
 */
final class SortedList<E extends Comparable<E>>
{
    /** List of object. */
    private final List<E> list;

    /**
     * Constructor.
     */
    SortedList()
    {
        list = new ArrayList<>(8);
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
