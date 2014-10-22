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
package com.b3dgs.lionengine.core;

import java.lang.Thread.UncaughtExceptionHandler;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import com.b3dgs.lionengine.Check;
import com.b3dgs.lionengine.Config;
import com.b3dgs.lionengine.LionEngineException;

/**
 * Engine starter, need to be called only one time with the first {@link Sequence} to start, by using
 * {@link #start(Class, Object[])}.
 * <p>
 * Example:
 * </p>
 * 
 * <pre>
 * Engine.start(&quot;First Code&quot;, Version.create(1, 0, 0), Verbose.CRITICAL, &quot;resources&quot;);
 * final Resolution output = new Resolution(640, 480, 60);
 * final Config config = new Config(output, 16, true);
 * final Loader loader = new Loader(config);
 * loader.start(Scene.class);
 * </pre>
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 * @see Config
 */
public final class Loader
{
    /** Error message already started. */
    private static final String ERROR_STARTED = "Loader has already been started !";

    /**
     * Create a sequence from its class.
     * 
     * @param nextSequence The next sequence class.
     * @param loader The loader reference.
     * @param arguments The arguments list.
     * @return The sequence instance.
     * @throws LionEngineException If not able to create the sequence for any reason.
     */
    static Sequence createSequence(Class<? extends Sequence> nextSequence, Loader loader, Object... arguments)
            throws LionEngineException
    {
        Check.notNull(nextSequence);
        Check.notNull(loader);

        try
        {
            final Constructor<? extends Sequence> constructor = nextSequence.getDeclaredConstructor(Loader
                    .getParamTypes(loader, arguments));
            final boolean accessible = constructor.isAccessible();
            if (!accessible)
            {
                constructor.setAccessible(true);
            }

            final Sequence sequence = constructor.newInstance(getParams(loader, arguments));
            if (constructor.isAccessible() != accessible)
            {
                constructor.setAccessible(accessible);
            }

            return sequence;
        }
        catch (final ReflectiveOperationException exception)
        {
            throw new LionEngineException(exception);
        }
    }

    /**
     * Get the parameter types as array.
     * 
     * @param loader The loader reference.
     * @param arguments The arguments list.
     * @return The arguments array.
     */
    private static Class<?>[] getParamTypes(Loader loader, Object... arguments)
    {
        final Collection<Object> types = new ArrayList<>(1);
        types.add(loader.getClass());

        for (final Object argument : arguments)
        {
            types.add(argument.getClass());
        }

        final Class<?>[] typesArray = new Class<?>[types.size()];
        return types.toArray(typesArray);
    }

    /**
     * Get the parameter as array.
     * 
     * @param loader The loader reference.
     * @param arguments The arguments list.
     * @return The arguments array.
     */
    private static Object[] getParams(Loader loader, Object... arguments)
    {
        final Collection<Object> params = new ArrayList<>(1);
        params.add(loader);
        params.addAll(Arrays.asList(arguments));

        final Object[] paramsArray = new Object[params.size()];
        return params.toArray(paramsArray);
    }

    /** Renderer instance. */
    private final Renderer renderer;
    /** Started state. */
    private boolean started;

    /**
     * Constructor.
     * 
     * @param config The configuration used (must not be <code>null</code>).
     * @throws LionEngineException If the configuration is <code>null</code>.
     */
    public Loader(Config config) throws LionEngineException
    {
        Check.notNull(config);

        final Renderer renderer = Core.GRAPHIC.createRenderer(config);
        renderer.setUncaughtExceptionHandler(new UncaughtExceptionHandler()
        {
            @Override
            public void uncaughtException(Thread t, Throwable throwable)
            {
                renderer.terminate();
                Verbose.exception(Renderer.class, "run", throwable);
            }
        });
        this.renderer = renderer;
    }

    /**
     * Start the loader with an initial sequence. Has to be called only one time.
     * 
     * @param sequenceClass The the next sequence to start (must not be <code>null</code>).
     * @param arguments The sequence arguments list if needed by its constructor.
     * @throws LionEngineException If the loader has already been started.
     */
    public void start(Class<? extends Sequence> sequenceClass, Object... arguments) throws LionEngineException
    {
        Check.notNull(sequenceClass);

        if (!started)
        {
            started = true;
            renderer.startFirstSequence(sequenceClass, this, arguments);
        }
        else
        {
            throw new LionEngineException(ERROR_STARTED);
        }
    }

    /**
     * Get the renderer used.
     * 
     * @return The renderer used.
     */
    Renderer getRenderer()
    {
        return renderer;
    }
}
