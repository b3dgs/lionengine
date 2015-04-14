/*
 * Copyright (C) 2013-2015 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.game.object;

import java.util.ArrayList;
import java.util.Collection;

import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.game.Featurable;

/**
 * Represents something designed to keep references on main game types, such as {@link Factory}, {@link Handler} ... in
 * order to access to them from the object instance (created by a {@link Factory} with a {@link Setup}).
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 * @see Factory
 * @see ObjectGame
 */
public class Services
{
    /** Service create error. */
    private static final String ERROR_SERVICE_CREATE = "Unable to create service: ";
    /** Service get error. */
    private static final String ERROR_SERVICE_GET = "Service not found: ";

    /** Services list. */
    private final Collection<Object> services;

    /**
     * Create a services.
     */
    public Services()
    {
        services = new ArrayList<>();
    }

    /**
     * Create a service from its type, and automatically {@link #add(Object)} it.
     * The service instance must provide a public constructor with {@link Services} as single argument, or the public
     * default constructor. Else, create manually the instance and use {@link #add(Object)} on it.
     * 
     * @param service The service class.
     * @return The service instance already added.
     */
    public <S> S create(Class<S> service)
    {
        try
        {
            final S instance = Factory.create(service, new Class<?>[]
            {
                Services.class
            }, this);
            return add(instance);
        }
        catch (final LionEngineException exception)
        {
            try
            {
                return add(service.newInstance());
            }
            catch (InstantiationException
                   | IllegalAccessException exception2)
            {
                throw new LionEngineException(exception2, ERROR_SERVICE_CREATE + service);
            }
        }
    }

    /**
     * Add a service.
     * 
     * @param service The service to add.
     * @return The added service (same as source).
     */
    public <S> S add(S service)
    {
        services.add(service);
        if (service instanceof Featurable)
        {
            for (final Object feature : ((Featurable<?>) service).getFeatures())
            {
                add(feature);
            }
        }
        return service;
    }

    /**
     * Get a service from its class.
     * 
     * @param service The service type.
     * @return The service found.
     * @throws LionEngineException If service not found.
     */
    public <C> C get(Class<C> service) throws LionEngineException
    {
        for (final Object object : services)
        {
            if (service.isAssignableFrom(object.getClass()))
            {
                return service.cast(object);
            }
        }
        if (service == getClass())
        {
            return service.cast(this);
        }
        throw new LionEngineException(ERROR_SERVICE_GET, service.getName());
    }
}
