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
package com.b3dgs.lionengine.graphic.engine;

import java.util.concurrent.atomic.AtomicReference;

import com.b3dgs.lionengine.Check;
import com.b3dgs.lionengine.Config;
import com.b3dgs.lionengine.Constant;
import com.b3dgs.lionengine.Context;
import com.b3dgs.lionengine.Engine;
import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Verbose;
import com.b3dgs.lionengine.graphic.Graphics;
import com.b3dgs.lionengine.graphic.Screen;

/**
 * Routine starter, need to be called only one time with the first {@link Sequence} to start, by using
 * {@link #start(Config, Class, Object...)}.
 * <p>
 * This class is Thread-Safe.
 * </p>
 */
public final class Loader
{
    /** Start sequence. */
    private static final String SEQUENCE_START = "Starting sequence: ";
    /** End sequence. */
    private static final String SEQUENCE_END = "Ending sequence: ";
    /** Error task stopped. */
    private static final String ERROR_TASK_STOPPED = "Task stopped before ended !";

    /**
     * Start the loader with an initial sequence.
     * 
     * @param config The configuration used (must not be <code>null</code>).
     * @param sequenceClass The the next sequence to start (must not be <code>null</code>).
     * @param arguments The sequence arguments list if needed by its constructor.
     * @return The asynchronous task executed.
     * @throws LionEngineException If sequence is invalid or wrong arguments.
     */
    public static TaskFuture start(Config config, Class<? extends Sequencable> sequenceClass, Object... arguments)
    {
        Check.notNull(config);
        Check.notNull(sequenceClass);
        Check.notNull(arguments);

        final Runnable runnable = () -> handle(config, sequenceClass, arguments);
        final Thread thread = new Thread(runnable, Constant.ENGINE_NAME);
        final AtomicReference<Throwable> reference = new AtomicReference<>();
        thread.setUncaughtExceptionHandler((t, e) ->
        {
            reference.set(e);
            Verbose.exception(e);
            Engine.terminate();
        });
        thread.start();

        return () -> check(thread, reference);
    }

    /**
     * Handle the sequence with its screen until no more sequence to run.
     * 
     * @param config The configuration used.
     * @param sequenceClass The the next sequence to start.
     * @param arguments The sequence arguments list if needed by its constructor.
     * @throws LionEngineException If an exception occurred.
     */
    private static void handle(Config config, Class<? extends Sequencable> sequenceClass, Object... arguments)
    {
        final Screen screen = Graphics.createScreen(config);
        try
        {
            screen.start();
            screen.awaitReady();

            final Context context = new ContextWrapper(screen);
            Sequencable nextSequence = UtilSequence.create(sequenceClass, context, arguments);
            while (nextSequence != null)
            {
                final Sequencable sequence = nextSequence;
                final String sequenceName = sequence.getClass().getName();

                Verbose.info(SEQUENCE_START, sequenceName);
                sequence.start(screen);

                Verbose.info(SEQUENCE_END, sequenceName);

                nextSequence = sequence.getNextSequence();
                sequence.onTerminated(nextSequence != null);
            }
        }
        finally
        {
            screen.dispose();
        }
    }

    /**
     * Check thread execution by waiting its end, and re-throw exception if has.
     * 
     * @param thread The thread reference.
     * @param reference The thread exception referencer.
     */
    private static void check(Thread thread, AtomicReference<Throwable> reference)
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
            Engine.terminate();
            if (throwable instanceof LionEngineException)
            {
                throw (LionEngineException) throwable;
            }
            throw new LionEngineException(throwable);
        }
    }

    /**
     * Private constructor.
     */
    private Loader()
    {
        throw new LionEngineException(LionEngineException.ERROR_PRIVATE_CONSTRUCTOR);
    }
}
