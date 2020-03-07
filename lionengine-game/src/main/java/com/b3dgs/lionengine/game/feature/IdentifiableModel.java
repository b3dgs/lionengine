/*
 * Copyright (C) 2013-2020 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
import com.b3dgs.lionengine.ListenableModel;

/**
 * Identifiable model implementation.
 * <p>
 * Handle a list of unique Id, provide the next free Id, and recycle destroyed Id.
 * </p>
 */
public class IdentifiableModel extends FeatureAbstract implements Identifiable, Recyclable
{
    /** Free Id error. */
    static final String ERROR_FREE_ID = "No more free id available !";
    /** Id used. */
    private static final Collection<Integer> IDS = new HashSet<>();
    /** Last Id used (last maximum id value). */
    private static int lastId;

    /**
     * Get the next unused Id.
     * 
     * @return The next unused Id.
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
    private final ListenableModel<IdentifiableListener> listenable = new ListenableModel<>();
    /** Unique Id. */
    private final Integer id = getFreeId();
    /** Destroy request flag. */
    private boolean destroy;
    /** Destroyed flag. */
    private boolean destroyed;

    /**
     * Create feature.
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
        listenable.addListener(listener);
    }

    @Override
    public void removeListener(IdentifiableListener listener)
    {
        listenable.removeListener(listener);
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

            for (int i = 0; i < listenable.size(); i++)
            {
                listenable.get(i).notifyDestroyed(id);
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

    /*
     * Object
     */

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + id.hashCode();
        return result;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (this == obj)
        {
            return true;
        }
        if (obj == null || getClass() != obj.getClass())
        {
            return false;
        }
        final IdentifiableModel other = (IdentifiableModel) obj;
        return other.id.equals(id);
    }
}
