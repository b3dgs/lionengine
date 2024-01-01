/*
 * Copyright (C) 2013-2024 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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

import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Optional;

import com.b3dgs.lionengine.Check;
import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.UtilReflection;
import com.b3dgs.lionengine.game.Feature;

/**
 * Represents something designed to keep references on main types, such as:
 * <ul>
 * <li>{@link com.b3dgs.lionengine.game.feature.Handler}</li>
 * <li>{@link com.b3dgs.lionengine.game.feature.Camera}</li>
 * <li>{@link com.b3dgs.lionengine.game.Cursor}</li>
 * <li>...</li>
 * </ul>
 * <p>
 * in order to access to them from the object instance, such as a {@link Featurable} or a {@link Feature}.
 * </p>
 * <p>
 * Ensure to add any required services before creating an object with the factory, else it will fail with a
 * {@link LionEngineException} when calling
 * {@link com.b3dgs.lionengine.game.feature.Factory#create(com.b3dgs.lionengine.Media)}.
 * </p>
 * <p>
 * Usage example:
 * </p>
 * 
 * <pre>
 * final Services services = new Services();
 * final Factory factory = services.create(Factory.class);
 * final Camera camera = services.create(Camera.class);
 * final MapTile map = services.create(MapTileGame.class);
 * </pre>
 */
public class Services
{
    /** Service get error. */
    static final String ERROR_SERVICE_GET = "Service not found: ";
    /** Service create error. */
    private static final String ERROR_SERVICE_CREATE = "Unable to create service: ";
    /** Service create error. */
    private static final String ERROR_SERVICE_NO_CONSTRUCTOR = "No recognized constructor found for: ";

    /** Services list. */
    private final Collection<Object> servicesSet = new HashSet<>();

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
     * final Services services = new Services();
     * final Factory factory = services.create(Factory.class); // Already added !
     * final Handler handler = services.create(Handler.class); // Already added !
     * final Camera camera = services.create(Camera.class); // Already added !
     * </pre>
     * 
     * <p>
     * An equivalent code could be:
     * </p>
     * 
     * <pre>
     * final Services services = new Services();
     * final Factory factory = new Factory(services);
     * final Handler handler = new Handler(services);
     * final Camera camera = new Camera();
     * ...
     * services.add(factory);
     * services.add(handler);
     * services.add(camera);
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
        catch (@SuppressWarnings("unused") final NoSuchMethodException exception)
        {
            try
            {
                final S instance = service.getConstructor().newInstance();
                return add(instance);
            }
            catch (final IllegalAccessException | NoSuchMethodException exception2)
            {
                throw new LionEngineException(exception2, ERROR_SERVICE_NO_CONSTRUCTOR + service);
            }
            catch (final InstantiationException | InvocationTargetException exception2)
            {
                throw new LionEngineException(exception2, ERROR_SERVICE_CREATE + service);
            }
        }
    }

    /**
     * Add a service.
     * <p>
     * The returned service will allow to add a service and keep its reference for an easy final initialization:
     * </p>
     * 
     * <pre>
     * final Services services = new Services();
     * final Text text = services.add(Graphics.createText(Text.SANS_SERIF, 9, TextStyle.NORMAL));
     * </pre>
     * 
     * <p>
     * An equivalent code could be:
     * </p>
     * 
     * <pre>
     * final Text text = Graphics.createText(Text.SANS_SERIF, 9, TextStyle.NORMAL);
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
        servicesSet.add(service);
        return service;
    }

    /**
     * Remove a service.
     * 
     * @param <S> The service type.
     * @param service The service to remove.
     */
    public <S> void remove(S service)
    {
        servicesSet.remove(service);
    }

    /**
     * Get a service from its class.
     * <p>
     * The first instance (previously added with {@link #add(Object)} or {@link #create(Class)}) which fit the required
     * type is returned.
     * </p>
     * 
     * <pre>
     * final Services services = new Services();
     * services.add(new Camera());
     * ...
     * final Viewer viewer = services.get(Viewer.class) // Get the camera as viewer
     * </pre>
     * 
     * @param <S> The service type.
     * @param service The service type.
     * @return The service implementation found.
     * @throws LionEngineException If service not found or <code>null</code>.
     */
    public <S> S get(Class<S> service)
    {
        final S instance = getService(service);
        if (instance == null)
        {
            throw new LionEngineException(ERROR_SERVICE_GET + service.getName());
        }
        return instance;
    }

    /**
     * Get a service from its class.
     * <p>
     * The first instance (previously added with {@link #add(Object)} or {@link #create(Class)}) which fit the required
     * type is returned.
     * </p>
     * 
     * @param <S> The service type.
     * @param service The service type.
     * @return The service implementation found.
     * @throws LionEngineException If <code>null</code> argument.
     */
    public <S> Optional<S> getOptional(Class<S> service)
    {
        return Optional.ofNullable(getService(service));
    }

    /**
     * Get a service from its class.
     * <p>
     * The first instance (previously added with {@link #add(Object)} or {@link #create(Class)}) which fit the required
     * type is returned.
     * </p>
     * 
     * <pre>
     * final Services services = new Services();
     * services.add(new Camera());
     * ...
     * final Viewer viewer = services.get(Viewer.class) // Get the camera as viewer
     * </pre>
     * 
     * @param <S> The service type.
     * @param service The service type.
     * @return The service implementation found.
     */
    private <S> S getService(Class<S> service)
    {
        Check.notNull(service);
        S instance = null;
        if (service == getClass())
        {
            instance = service.cast(this);
        }
        else
        {
            for (final Object object : servicesSet)
            {
                if (service.isAssignableFrom(object.getClass()))
                {
                    instance = service.cast(object);
                    break;
                }
            }
        }
        return instance;
    }
}
