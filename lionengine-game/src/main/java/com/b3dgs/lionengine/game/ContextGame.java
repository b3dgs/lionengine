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

import com.b3dgs.lionengine.LionEngineException;

/**
 * Represents an object designed to keep references on main game types, such as {@link FactoryGame}, {@link HandlerGame}
 * ... in order to access to them from the object instance (created by a {@link FactoryGame} with a {@link SetupGame}).
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public class ContextGame
{
    /** Service error. */
    private static final String ERROR_SERVICE = "Service not found: ";

    /** Services list. */
    private final Collection<Object> services;

    /**
     * Constructor.
     */
    public ContextGame()
    {
        services = new ArrayList<>();
    }

    /**
     * Add a service.
     * 
     * @param service The service to add.
     */
    public void addService(Object service)
    {
        services.add(service);
    }

    /**
     * Get a service from its class.
     * 
     * @param service The service type.
     * @return The service found.
     * @throws LionEngineException If service not found.
     */
    public <C> C getService(Class<C> service) throws LionEngineException
    {
        for (final Object object : services)
        {
            if (service.isAssignableFrom(object.getClass()))
            {
                return service.cast(object);
            }
        }
        throw new LionEngineException(ContextGame.ERROR_SERVICE, service.getName());
    }
}
