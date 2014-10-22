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
import java.util.Map;

import com.b3dgs.lionengine.core.Graphic;
import com.b3dgs.lionengine.game.purview.Handlable;

/**
 * Designed to handle objects. Maintain an objects list by updating and rendering them. Modifications on the list can be
 * done at any time because their are applied at the end of the update.
 * 
 * @param <E> The object type used.
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public abstract class HandlerGame<E extends Handlable>
{
    /** List of handlables. */
    private final Map<Integer, E> handlables;
    /** To delete list. */
    private final Collection<E> toDelete;
    /** To add list. */
    private final Collection<E> toAdd;
    /** Will delete flag. */
    private boolean willDelete;
    /** Will add flag. */
    private boolean willAdd;

    /**
     * Constructor.
     */
    public HandlerGame()
    {
        handlables = new HashMap<>(8);
        toDelete = new ArrayList<>(1);
        toAdd = new ArrayList<>(1);
        willDelete = false;
        willAdd = false;
    }

    /**
     * Update the handlable. Called by {@link #update(double)} for each handlable.
     * 
     * @param extrp The extrapolation value.
     * @param handlable The handlable to update.
     */
    protected abstract void update(double extrp, E handlable);

    /**
     * Render the object. Called by {@link #render(Graphic)} for each handlable.
     * 
     * @param g The graphics output.
     * @param handlable The handlable to render.
     */
    protected abstract void render(Graphic g, E handlable);

    /**
     * Update the handlable.
     * 
     * @param extrp The extrapolation value.
     */
    public void update(double extrp)
    {
        updateAdd();
        for (final E handlable : list())
        {
            update(extrp, handlable);
            if (handlable.isDestroyed())
            {
                remove(handlable);
            }
        }
        updateRemove();
    }

    /**
     * Render the handlables.
     * 
     * @param g The graphics output.
     */
    public void render(Graphic g)
    {
        for (final E handlable : list())
        {
            render(g, handlable);
        }
    }

    /**
     * Add a handlable to the list.
     * Do not forget to call {@link #updateAdd()} at the begin of the update to add them properly.
     * 
     * @param handlable The handlable to add.
     */
    public void add(E handlable)
    {
        toAdd.add(handlable);
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
        return handlables.get(key);
    }

    /**
     * Add a handlable to the remove list. Modifications are applied at the end of the update.
     * 
     * @param handlable The handlable to remove.
     */
    public void remove(E handlable)
    {
        toDelete.add(handlable);
        willDelete = true;
    }

    /**
     * Remove all handlables from the list. Modifications are applied at the end of the update.
     */
    public void removeAll()
    {
        toDelete.addAll(list());
        willDelete = true;
    }

    /**
     * Get the number of handlables.
     * 
     * @return The number of handlables.
     */
    public int size()
    {
        return handlables.size();
    }

    /**
     * Get the list reference of handlables.
     * 
     * @return The list reference of handlables.
     */
    public Collection<E> list()
    {
        return handlables.values();
    }

    /**
     * Check if handlable can be added.
     * 
     * @param handlable The handlable to check.
     * @return <code>true</code> if can be added, <code>false</code> else.
     */
    protected boolean canBeAdded(E handlable)
    {
        return true;
    }

    /**
     * Get the handlable key.
     * 
     * @param handlable The handlable reference.
     * @return The handlable key.
     */
    private Integer getKey(E handlable)
    {
        return handlable.getId();
    }

    /**
     * Update the add list.
     */
    private void updateAdd()
    {
        if (willAdd)
        {
            Collection<E> toKeep = null;
            for (final E handlable : toAdd)
            {
                if (canBeAdded(handlable))
                {
                    handlables.put(getKey(handlable), handlable);
                }
                else
                {
                    if (toKeep == null)
                    {
                        toKeep = new ArrayList<>(0);
                    }
                    toKeep.add(handlable);
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
            for (final E handlable : toDelete)
            {
                handlables.remove(getKey(handlable));
            }
            toDelete.clear();
            willDelete = false;
        }
    }
}
