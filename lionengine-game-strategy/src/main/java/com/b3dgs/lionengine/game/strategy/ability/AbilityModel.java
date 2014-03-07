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
package com.b3dgs.lionengine.game.strategy.ability;

import java.util.HashSet;
import java.util.Set;

/**
 * Ability model implementation.
 * 
 * @param <L> Ability listener type.
 * @param <U> Services used type.
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public abstract class AbilityModel<L, U extends L>
{
    /** Listener list. */
    protected final Set<L> listeners;
    /** User reference. */
    protected final U user;

    /**
     * Constructor.
     * 
     * @param user The user services reference.
     */
    public AbilityModel(U user)
    {
        this.user = user;
        listeners = new HashSet<>(1);
        addListener(user);
    }

    /**
     * Add the listener.
     * 
     * @param listener The listener to add.
     */
    public final void addListener(L listener)
    {
        listeners.add(listener);
    }

    /**
     * Remove the listener.
     * 
     * @param listener The listener to remove.
     */
    public final void removeListener(L listener)
    {
        listeners.remove(listener);
    }
}
