/*
 * Copyright (C) 2013-2021 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Aimed to load resources asynchronously from the main thread, in order to improve transition time between two parts.
 * <p>
 * Add resources to load with {@link #add(Enum, Resource)}, and call {@link #start()} to begin loading in a separate
 * process.
 * </p>
 * <p>
 * Call {@link #await()} where resources must be loaded, and access to them with {@link #get()}.
 * </p>
 * <p>
 * This class is Thread-Safe.
 * </p>
 * 
 * @param <T> The resource enum type.
 */
public final class ResourceLoader<T extends Enum<T>>
{
    /** Error started. */
    static final String ERROR_STARTED = "Resource loader already started !";
    /** Error not started. */
    static final String ERROR_NOT_STARTED = "Resource loader not started !";
    /** Error load not finished. */
    static final String ERROR_NOT_FINISHED = "Resource loader has not finished !";
    /** Error load skipped. */
    static final String ERROR_SKIPPED = "Resource loader interrupted !";

    /** Handled resources. */
    private final Map<T, Resource> resources = new HashMap<>();
    /** Done. */
    private final AtomicBoolean done = new AtomicBoolean(false);
    /** Started. */
    private final AtomicBoolean started = new AtomicBoolean(false);
    /** Thread used. */
    private final ResourceLoaderThread thread = new ResourceLoaderThread(resources);

    /**
     * Create the resource loader.
     */
    public ResourceLoader()
    {
        super();
    }

    /**
     * Add a resource to load. Must be called before {@link #start()}.
     * 
     * @param key The associated key (must not be <code>null</code>).
     * @param resource The resource to load (must not be <code>null</code>).
     * @throws LionEngineException If invalid arguments or loader has already been started.
     */
    public synchronized void add(T key, Resource resource)
    {
        Check.notNull(key);
        Check.notNull(resource);

        if (started.get())
        {
            throw new LionEngineException(ERROR_STARTED);
        }
        resources.put(key, resource);
    }

    /**
     * Start to load resources in a separate thread.
     * 
     * @throws LionEngineException If loader has already been started.
     */
    public synchronized void start()
    {
        if (started.get())
        {
            throw new LionEngineException(ERROR_STARTED);
        }
        started.set(true);
        thread.start();
    }

    /**
     * Wait for load to finish. Can be called only if {@link #start()} were performed somewhere before.
     * 
     * @throws LionEngineException If loading skipped or loader has not been started.
     */
    public synchronized void await()
    {
        if (!started.get())
        {
            throw new LionEngineException(ERROR_NOT_STARTED);
        }
        try
        {
            thread.join();
        }
        catch (final InterruptedException exception)
        {
            Thread.currentThread().interrupt();
            throw new LionEngineException(exception, ERROR_SKIPPED);
        }
        finally
        {
            done.set(true);
        }
    }

    /**
     * Get the loaded resources. Can be called safely after {@link #await()}. Else ensure all resources has been loaded.
     * 
     * @return The loaded resources as read only.
     * @throws LionEngineException If resources are not fully loaded.
     */
    public synchronized Map<T, Resource> get()
    {
        if (!done.get())
        {
            throw new LionEngineException(ERROR_NOT_FINISHED);
        }
        return Collections.unmodifiableMap(resources);
    }

    /**
     * Check if loading is finished.
     * 
     * @return <code>true</code> if finished, <code>false</code> else.
     */
    public boolean isFinished()
    {
        return done.get();
    }

    /**
     * Resource loader thread.
     */
    private static final class ResourceLoaderThread extends Thread
    {
        /** Handled resources. */
        private final Map<?, Resource> resources;

        /**
         * Create the resource loader.
         * 
         * @param resources The resources to load (must not be <code>null</code>).
         */
        ResourceLoaderThread(Map<?, Resource> resources)
        {
            super(ResourceLoaderThread.class.getName());

            this.resources = resources;
        }

        /*
         * Thread
         */

        @Override
        public void run()
        {
            resources.values().forEach(Resource::load);
        }
    }
}
