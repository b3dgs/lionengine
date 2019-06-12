/*
 * Copyright (C) 2013-2019 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.game.feature;

import java.util.Collection;
import java.util.HashSet;

import com.b3dgs.lionengine.LionEngineException;

/**
 * Default identifiable implementation. Handle a list of unique ID, provide the next free ID, and recycle destroyed ID.
 */
public class IdentifiableModel extends FeatureModel implements Identifiable, Recyclable
{
    /** Free ID error. */
    static final String ERROR_FREE_ID = "No more free id available !";
    /** ID used. */
    private static final Collection<Integer> IDS = new HashSet<>();
    /** Last ID used (last maximum id value). */
    private static int lastId;

    /**
     * Get the next unused ID.
     * 
     * @return The next unused ID.
     * @throws LionEngineException If there is more than {@link Integer#MAX_VALUE} at the same time.
     */
    private static Integer getFreeId()
    {
        if (IDS.size() == Integer.MAX_VALUE)
        {
            throw new LionEngineException(ERROR_FREE_ID);
        }
        Integer id;
        while (IDS.contains(id = Integer.valueOf(lastId))) // CHECKSTYLE IGNORE LINE: TrailingComment|InnerAssignment
        {
            lastId++;
        }
        IDS.add(id);
        return id;
    }

    /** Listeners. */
    private final Collection<IdentifiableListener> listeners = new HashSet<>(1);
    /** Unique ID. */
    private final Integer id = getFreeId();
    /** Destroy request flag. */
    private boolean destroy;
    /** Destroyed flag. */
    private boolean destroyed;

    /**
     * Create the identifiable with a unique ID.
     * 
     * @throws LionEngineException If no free ID available.
     */
    public IdentifiableModel()
    {
        super();
    }

    /*
     * Identifiable
     */

    @Override
    public void addListener(IdentifiableListener listener)
    {
        listeners.add(listener);
    }

    @Override
    public void removeListener(IdentifiableListener listener)
    {
        listeners.remove(listener);
    }

    @Override
    public Integer getId()
    {
        if (destroyed)
        {
            return null;
        }
        return id;
    }

    @Override
    public void destroy()
    {
        if (!destroy)
        {
            destroy = true;

            for (final IdentifiableListener listener : listeners)
            {
                listener.notifyDestroyed(id);
            }
        }
    }

    @Override
    public void notifyDestroyed()
    {
        destroyed = true;
    }

    @Override
    public void recycle()
    {
        destroy = false;
        destroyed = false;
    }
}
