/*
 * Copyright (C) 2013-2016 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.game.feature;

import java.util.Collection;
import java.util.HashSet;

import com.b3dgs.lionengine.Check;
import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.util.UtilReflection;

/**
 * Represents something designed to keep references on main types, such as:
 * <ul>
 * <li>{@link com.b3dgs.lionengine.game.object.Factory}</li>
 * <li>{@link com.b3dgs.lionengine.game.handler.Handler}</li>
 * <li>{@link com.b3dgs.lionengine.game.camera.Camera}</li>
 * <li>{@link com.b3dgs.lionengine.game.Cursor}</li>
 * <li>...</li>
 * </ul>
 * <p>
 * in order to access to them from the object instance (created by a {@link com.b3dgs.lionengine.game.object.Factory} in
 * constructor {@link com.b3dgs.lionengine.game.object.ObjectGame#ObjectGame(com.b3dgs.lionengine.game.object.Setup)}).
 * </p>
 * <p>
 * Ensure to add any required services before creating an object with the factory, else it will fail with a
 * {@link LionEngineException} when calling
 * {@link com.b3dgs.lionengine.game.object.Factory#create(com.b3dgs.lionengine.Media)}.
 * </p>
 * <p>
 * Usage example:
 * </p>
 * 
 * <pre>
 * private final Services services = new Services();
 * private final Factory factory = services.create(Factory.class);
 * private final Camera camera = services.create(Camera.class);
 * private final MapTile map = services.create(MapTileGame.class);
 * </pre>
 */
public class Services
{
    /** Service create error. */
    private static final String ERROR_SERVICE_CREATE = "Unable to create service: ";
    /** Service create error. */
    private static final String ERROR_SERVICE_NO_CONSTRUCTOR = "No recognized constructor found for: ";
    /** Service get error. */
    private static final String ERROR_SERVICE_GET = "Service not found: ";

    /** Services list. */
    private final Collection<Object> services = new HashSet<Object>();

    /**
     * Create a services container.
     */
    public Services()
    {
        super();
    }

    /**
     * Create a service from its type, and automatically {@link #add(Object)} it.
     * <p>
     * The service instance must provide a public constructor with {@link Services} as single argument, or the public
     * default constructor. Else, create manually the instance and use {@link #add(Object)} on it.
     * </p>
     * <p>
     * The returned service will allow to keep its reference for an easy final initialization:
     * </p>
     * 
     * <pre>
     * private final Services services = new Services();
     * private final Factory factory = services.create(Factory.class); // Already added !
     * private final Camera camera = services.create(Camera.class); // Already added !
     * private final Handler handler = services.create(Handler.class); // Already added !
     * </pre>
     * 
     * <p>
     * An equivalent code could be:
     * </p>
     * 
     * <pre>
     * private final Services services = new Services();
     * private final Factory factory = new Factory(services);
     * private final Camera camera = new Camera();
     * private final Handler handler = new Handler(services);
     * ...
     * services.add(factory);
     * services.add(camera);
     * services.add(handler);
     * </pre>
     * 
     * @param <S> The service type.
     * @param service The service class.
     * @return The service instance already added.
     * @throws LionEngineException If unable to create service or if <code>null</code>.
     */
    public <S> S create(Class<S> service)
    {
        Check.notNull(service);
        try
        {
            final S instance = UtilReflection.create(service, new Class<?>[]
            {
                Services.class
            }, this);
            return add(instance);
        }
        catch (final NoSuchMethodException exception)
        {
            try
            {
                final S instance = service.newInstance();
                return add(instance);
            }
            catch (final IllegalAccessException exception2)
            {
                throw new LionEngineException(exception2, ERROR_SERVICE_NO_CONSTRUCTOR + service);
            }
            catch (final InstantiationException exception2)
            {
                throw new LionEngineException(exception2, ERROR_SERVICE_CREATE + service);
            }
        }
    }

    /**
     * Add a service. If the service is {@link Featurable}, all its features will be added
     * {@link Featurable#getFeatures()}) with {@link #add(Object)} (and so on).
     * <p>
     * The returned service will allow to add a service and keep its reference for an easy final initialization:
     * </p>
     * 
     * <pre>
     * private final Services services = new Services();
     * private final Text text = services.add(Graphics.createText(Text.SANS_SERIF, 9, TextStyle.NORMAL));
     * </pre>
     * 
     * <p>
     * An equivalent code could be:
     * </p>
     * 
     * <pre>
     * private final Text text = Graphics.createText(Text.SANS_SERIF, 9, TextStyle.NORMAL);
     * ...
     * services.add(text);
     * </pre>
     * 
     * @param <S> The service type.
     * @param service The service to add.
     * @return The added service (same as source).
     * @throws LionEngineException If service is <code>null</code>.
     */
    public <S> S add(S service)
    {
        Check.notNull(service);
        services.add(service);
        if (service instanceof Featurable)
        {
            for (final Feature feature : ((Featurable) service).getFeatures())
            {
                add(feature);
            }
        }
        return service;
    }

    /**
     * Get a service from its class.
     * <p>
     * The first instance (previously added with {@link #add(Object)} or {@link #create(Class)}) which fit the required
     * type is returned.
     * </p>
     * 
     * <pre>
     * Services services = new Services();
     * services.add(new Camera());
     * ...
     * Viewer viewer = services.get(Viewer.class) // Get the camera as viewer
     * </pre>
     * 
     * @param <S> The service type.
     * @param service The service type.
     * @return The service implementation found.
     * @throws LionEngineException If service not found or <code>null</code>.
     */
    public <S> S get(Class<S> service)
    {
        Check.notNull(service);
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
