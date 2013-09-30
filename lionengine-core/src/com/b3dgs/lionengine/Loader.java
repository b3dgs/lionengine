/*
 * Copyright (C) 2013 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine;

import com.b3dgs.lionengine.input.Input;
import com.b3dgs.lionengine.input.Keyboard;
import com.b3dgs.lionengine.input.Mouse;

/**
 * Engine starter, need to be called for sequence selection and screen initializer. It already handles a screen using a
 * specified configuration. Any implemented sequence has to be added into the loader. It just needs a single instance, a
 * first sequence reference, and a call to start(), in the main.
 * <p>
 * Example:
 * </p>
 * 
 * <pre>
 * Engine.start(&quot;First Code&quot;, Version.create(1, 0, 0), &quot;resources&quot;);
 * final Resolution output = new Resolution(640, 480, 60);
 * final Config config = new Config(output, 16, true);
 * final Loader loader = new Loader(config);
 * loader.start(new Scene(loader));
 * </pre>
 */
public final class Loader
{
    /** Error message config. */
    private static final String MESSAGE_ERROR_CONFIG = "Configuration must not be null !";
    /** Error message already started. */
    private static final String MESSAGE_ERROR_STARTED = "Loader has already been started !";
    /** Error message sequence interrupted. */
    private static final String MESSAGE_ERROR_SEQUENCE = "Sequence badly interrupted !";

    /** Config reference. */
    final Config config;
    /** Screen reference. */
    final Screen screen;
    /** Keyboard reference. */
    final Keyboard keyboard;
    /** Mouse reference. */
    final Mouse mouse;
    /** Thread loader. */
    private final LoaderThread thread;
    /** Next sequence pointer. */
    private Sequence nextSequence;
    /** Started flag. */
    private boolean started;

    /**
     * Create a loader.
     * 
     * @param config The configuration used (must not be null).
     */
    public Loader(Config config)
    {
        Check.notNull(config, Loader.MESSAGE_ERROR_CONFIG);
        this.config = config;
        screen = new Screen(config);
        keyboard = Input.createKeyboard();
        mouse = Input.createMouse();
        screen.prepareFocusListener();
        screen.addKeyboard(keyboard);
        screen.addMouse(mouse);
        thread = new LoaderThread(this);
        nextSequence = null;
    }

    /**
     * Start the loader. Has to be called only one time.
     * 
     * @param sequence The the next sequence to start.
     */
    public void start(Sequence sequence)
    {
        if (!started)
        {
            nextSequence = sequence;
            screen.start();
            started = true;
            thread.start();
        }
        else
        {
            throw new LionEngineException(Loader.MESSAGE_ERROR_STARTED);
        }
    }

    /**
     * Run loader.
     */
    void run()
    {
        while (nextSequence != null)
        {
            // Select next sequence
            final Sequence sequence = nextSequence;
            screen.setSequence(sequence);
            final String sequenceName = sequence.getClass().getName();
            Verbose.info("Starting sequence ", sequenceName);
            if (!sequence.isStarted())
            {
                sequence.setTitle(sequenceName);
                sequence.start();
            }

            // Wait for sequence end
            try
            {
                synchronized (sequence)
                {
                    sequence.wait();
                }
            }
            catch (final InterruptedException exception)
            {
                throw new LionEngineException(exception, Loader.MESSAGE_ERROR_SEQUENCE);
            }
            nextSequence = sequence.getNextSequence();
            Verbose.info("Sequence ", sequence.getClass().getName(), " terminated");
        }
        screen.dispose();
        Verbose.info("LionEngine terminated");
    }
}
