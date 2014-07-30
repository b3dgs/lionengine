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
package com.b3dgs.lionengine.game.purview.model;

import java.util.HashSet;
import java.util.Set;

import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.game.purview.Handlable;

/**
 * Handlable implementation.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public class HandlableModel
        implements Handlable
{
    /** Free id error. */
    private static final String ERROR_FREE_ID = "No more free id available !";
    /** Id used. */
    private static final Set<Integer> IDS = new HashSet<>(16);
    /** Last id used. */
    private static int lastId = 1;

    /**
     * Get the next unused id.
     * 
     * @return The next unused id.
     * @throws LionEngineException If there is more than {@link Integer#MAX_VALUE} at the same time.
     */
    private static Integer getFreeId() throws LionEngineException
    {
        if (HandlableModel.IDS.size() >= Integer.MAX_VALUE)
        {
            throw new LionEngineException(HandlableModel.ERROR_FREE_ID);
        }
        while (HandlableModel.IDS.contains(Integer.valueOf(HandlableModel.lastId)))
        {
            HandlableModel.lastId++;
        }
        return Integer.valueOf(HandlableModel.lastId);
    }

    /** Id. */
    private final Integer id;
    /** Destroyed flag (<code>true</code> will remove it from the handler). */
    private boolean destroy;

    /**
     * Constructor.
     * 
     * @throws LionEngineException If there is more than {@link Integer#MAX_VALUE} at the same time.
     */
    public HandlableModel() throws LionEngineException
    {
        destroy = false;
        id = HandlableModel.getFreeId();
        HandlableModel.IDS.add(id);
    }

    /*
     * Handlable
     */

    @Override
    public final Integer getId()
    {
        return id;
    }

    @Override
    public void destroy()
    {
        destroy = true;
        HandlableModel.IDS.remove(getId());
    }

    @Override
    public boolean isDestroyed()
    {
        return destroy;
    }
}
