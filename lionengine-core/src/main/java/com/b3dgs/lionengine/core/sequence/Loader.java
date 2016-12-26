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
package com.b3dgs.lionengine.core.sequence;

import java.lang.Thread.UncaughtExceptionHandler;
import java.util.concurrent.atomic.AtomicReference;

import com.b3dgs.lionengine.Check;
import com.b3dgs.lionengine.Config;
import com.b3dgs.lionengine.Constant;
import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Verbose;
import com.b3dgs.lionengine.graphic.Graphics;
import com.b3dgs.lionengine.graphic.Screen;

/**
 * Engine starter, need to be called only one time with the first {@link Sequence} to start, by using
 * {@link #start(Config, Class, Object...)}.
 * <p>
 * Example:
 * </p>
 * 
 * <pre>
 * Engine.start(&quot;First Code&quot;, Version.create(1, 0, 0), &quot;resources&quot;);
 * final Resolution output = new Resolution(640, 480, 60);
 * final Config config = new Config(output, 16, true);
 * final Loader loader = new Loader();
 * loader.start(config, Scene.class);
 * </pre>
 * 
 * <p>
 * This class is Thread-Safe.
 * </p>
 * 
 * @see Config
 */
public final class Loader
{
    /** Error message already started. */
    private static final String ERROR_STARTED = "Loader has already been started !";
    /** Error task stopped. */
    private static final String ERROR_TASK_STOPPED = "Task stopped before ended !";

    /** Started state. */
    private volatile boolean started;

    /**
     * Create a loader.
     */
    public Loader()
    {
        super();
    }

    /**
     * Start the loader with an initial sequence. Has to be called only one time.
     * 
     * @param config The configuration used (must not be <code>null</code>).
     * @param sequenceClass The the next sequence to start (must not be <code>null</code>).
     * @param arguments The sequence arguments list if needed by its constructor.
     * @return The asynchronous task executed.
     * @throws LionEngineException If the loader has already been started or sequence is invalid.
     */
    public synchronized TaskFuture start(final Config config,
                                         final Class<? extends Sequencable> sequenceClass,
                                         final Object... arguments)
    {
        Check.notNull(config);
        Check.notNull(sequenceClass);
        Check.notNull(arguments);

        if (started)
        {
            throw new LionEngineException(ERROR_STARTED);
        }

        final Runnable runnable = new Runnable()
        {
            @Override
            public void run()
            {
                handle(config, sequenceClass, arguments);
            }
        };
        final Thread thread = new Thread(runnable, Constant.ENGINE_NAME);
        final AtomicReference<Throwable> reference = new AtomicReference<Throwable>();
        thread.setUncaughtExceptionHandler(new UncaughtExceptionHandler()
        {
            @Override
            public void uncaughtException(Thread t, Throwable e)
            {
                reference.set(e);
                Verbose.exception(e);
            }
        });
        thread.start();

        started = true;

        return new TaskFuture()
        {
            @Override
            public void await()
            {
                check(thread, reference);
            }
        };
    }

    /**
     * Handle the sequence with its screen until no more sequence to run.
     * 
     * @param config The configuration used (must not be <code>null</code>).
     * @param sequenceClass The the next sequence to start (must not be <code>null</code>).
     * @param arguments The sequence arguments list if needed by its constructor.
     * @throws LionEngineException If an exception occurred.
     */
    void handle(Config config, Class<? extends Sequencable> sequenceClass, Object... arguments)
    {
        Screen screen = null;
        try
        {
            screen = Graphics.createScreen(config);
            screen.start();
            screen.awaitReady();

            Sequencable nextSequence = UtilSequence.create(sequenceClass, screen, arguments);
            while (nextSequence != null)
            {
                final Sequencable sequence = nextSequence;
                final String sequenceName = sequence.getClass().getName();

                Verbose.info("Starting sequence: ", sequenceName);
                sequence.start(screen);

                Verbose.info("Ending sequence: ", sequenceName);

                nextSequence = sequence.getNextSequence();
                sequence.onTerminated(nextSequence != null);
            }
        }
        finally
        {
            if (screen != null)
            {
                screen.dispose();
            }
            started = false;
        }
    }

    /**
     * Check thread execution by waiting its end, and re-throw exception if has.
     * 
     * @param thread The thread reference.
     * @param reference The thread exception referencer.
     */
    void check(Thread thread, AtomicReference<Throwable> reference)
    {
        try
        {
            thread.join();
        }
        catch (final InterruptedException exception)
        {
            Thread.currentThread().interrupt();
            throw new LionEngineException(exception, ERROR_TASK_STOPPED);
        }
        final Throwable throwable = reference.get();
        if (throwable != null)
        {
            if (throwable instanceof LionEngineException)
            {
                throw (LionEngineException) throwable;
            }
            throw new LionEngineException(throwable);
        }
    }
}
