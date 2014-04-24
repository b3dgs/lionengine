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
package com.b3dgs.lionengine.game;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.b3dgs.lionengine.core.Graphic;

/**
 * Designed to handle objects. Maintain an objects list by updating and rendering them. Modifications on the list can be
 * done at any time because their are applied at the end of the update.
 * 
 * @param <E> The object type used.
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public abstract class HandlerObjectGame<E extends ObjectGame>
{
    /** List of entities handled. */
    private final Map<Integer, E> objects;
    /** List of entities to delete. */
    private final List<E> toDelete;
    /** List of entities to add. */
    private final List<E> toAdd;
    /** Will delete flag. */
    private boolean willDelete;
    /** Will add flag. */
    private boolean willAdd;

    /**
     * Constructor.
     */
    public HandlerObjectGame()
    {
        objects = new HashMap<>(8);
        toDelete = new ArrayList<>(1);
        toAdd = new ArrayList<>(1);
        willDelete = false;
        willAdd = false;
    }

    /**
     * Update the object; called by {@link #update(double)} for each object handled.
     * 
     * @param extrp The extrapolation value.
     * @param object The object to update.
     */
    protected abstract void update(double extrp, E object);

    /**
     * Render the object; called by {@link #render(Graphic)} for each object handled.
     * 
     * @param g The graphics output.
     * @param object The object to update.
     */
    protected abstract void render(Graphic g, E object);

    /**
     * Update the objects.
     * 
     * @param extrp The extrapolation value.
     */
    public void update(double extrp)
    {
        updateAdd();
        for (final E object : list())
        {
            update(extrp, object);
            if (object.isDestroyed())
            {
                remove(object);
            }
        }
        updateRemove();
    }

    /**
     * Render the objects.
     * 
     * @param g The graphics output.
     */
    public void render(Graphic g)
    {
        for (final E object : list())
        {
            render(g, object);
        }
    }

    /**
     * Add an object to the handler list. Don't forget to call {@link #updateAdd()} at the begin of the update to add
     * them properly.
     * 
     * @param object The object to add.
     */
    public void add(E object)
    {
        toAdd.add(object);
        willAdd = true;
    }

    /**
     * Get the object from its key.
     * 
     * @param key The object key.
     * @return The object reference.
     */
    public E get(Integer key)
    {
        return objects.get(key);
    }

    /**
     * Add an object to the remove list. Modifications are applied at the end of the update.
     * 
     * @param object The object to remove.
     */
    public void remove(E object)
    {
        toDelete.add(object);
        willDelete = true;
    }

    /**
     * Remove all objects from the list. Modifications are applied at the end of the update.
     */
    public void removeAll()
    {
        toDelete.addAll(list());
        willDelete = true;
    }

    /**
     * Get the number of objects handled.
     * 
     * @return The number of objects handled.
     */
    public int size()
    {
        return objects.size();
    }

    /**
     * Get the list reference of handled objects.
     * 
     * @return The list reference of handled objects.
     */
    public Collection<E> list()
    {
        return objects.values();
    }

    /**
     * Check if object can be added.
     * 
     * @param object The object to check.
     * @return <code>true</code> if can be added, <code>false</code> else.
     */
    protected boolean canBeAdded(E object)
    {
        return true;
    }

    /**
     * Get the object key.
     * 
     * @param object The object reference.
     * @return The object key.
     */
    private Integer getKey(E object)
    {
        return object.getId();
    }

    /**
     * Update the add list.
     */
    private void updateAdd()
    {
        if (willAdd)
        {
            List<E> toKeep = null;
            for (final E object : toAdd)
            {
                if (canBeAdded(object))
                {
                    objects.put(getKey(object), object);
                }
                else
                {
                    if (toKeep == null)
                    {
                        toKeep = new ArrayList<>(0);
                    }
                    toKeep.add(object);
                }
            }
            toAdd.clear();
            willAdd = false;
            if (toKeep != null)
            {
                toAdd.addAll(toKeep);
                toKeep.clear();
                willAdd = true;
            }
        }
    }

    /**
     * Update the remove list.
     */
    private void updateRemove()
    {
        if (willDelete)
        {
            for (final E object : toDelete)
            {
                objects.remove(getKey(object));
            }
            toDelete.clear();
            willDelete = false;
        }
    }
}
