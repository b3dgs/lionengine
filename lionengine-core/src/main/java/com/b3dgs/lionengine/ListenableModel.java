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
package com.b3dgs.lionengine;

import java.util.ArrayList;
import java.util.List;

/**
 * Listenable implementation based on an listeners list.
 * 
 * @param <T> The listener type.
 */
public class ListenableModel<T> implements Listenable<T>
{
    /** Listeners reference. */
    private final List<T> listeners = new ArrayList<>();
    /** Current size. */
    private int size;

    /**
     * Create model.
     */
    public ListenableModel()
    {
        super();
    }

    /**
     * Get the listeners count.
     * 
     * @return The listeners count.
     */
    public int size()
    {
        return size;
    }

    /**
     * Get listener at index.
     * 
     * @param i The listener index.
     * @return The listener reference.
     */
    public T get(int i)
    {
        return listeners.get(i);
    }

    /**
     * Get listeners reference.
     * 
     * @return The listeners reference.
     */
    public List<T> get()
    {
        return listeners;
    }

    /**
     * Remove all listeners.
     */
    public void clear()
    {
        listeners.clear();
        size = 0;
    }

    @Override
    public void addListener(T listener)
    {
        Check.notNull(listener);

        listeners.add(listener);
        size = listeners.size();
    }

    @Override
    public void removeListener(T listener)
    {
        Check.notNull(listener);

        listeners.remove(listener);
        size = listeners.size();
    }
}
