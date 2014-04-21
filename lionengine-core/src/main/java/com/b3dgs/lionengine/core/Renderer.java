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

import com.b3dgs.lionengine.Check;
import com.b3dgs.lionengine.Filter;
import com.b3dgs.lionengine.Graphic;
import com.b3dgs.lionengine.Resolution;
import com.b3dgs.lionengine.Timing;
import com.b3dgs.lionengine.Transparency;

/**
 * Main thread renderer, which can handle a {@link Sequence}.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public abstract class Renderer
        extends Thread
{
    /** Error message loader. */
    private static final String ERROR_SEQUENCE = "Sequence must not be null !";
    /** Error message loader. */
    private static final String ERROR_RESOLUTION = "Resolution must not be null !";
    /** One nano second. */
    private static final long TIME_LONG = 1000000000L;
    /** One nano second. */
    private static final double TIME_DOUBLE = 1000000000.0;
    /** One millisecond. */
    private static final long TIME_INT = 1000000L;
    /** Extrapolation standard. */
    private static final double EXTRP = 1.0;

    /** Next sequence pointer. */
    Sequence nextSequence;
    /** Started. */
    boolean started;
    /** Config reference. */
    private final Config config;
    /** Filter reference. */
    private final Filter filter;
    /** Filter graphic. */
    private final Graphic graphic;
    /** Loop time for desired rate. */
    private final long frameDelay;
    /** Has sync. */
    private final boolean sync;
    /** Output resolution reference. */
    private final Resolution output;
    /** Source resolution reference. */
    protected Resolution source;
    /** Screen width. */
    protected int width;
    /** Screen height. */
    protected int height;
    /** Loader. */
    private Loader loader;
    /** Screen reference. */
    private Screen screen;
    /** First sequence. */
    private Class<? extends Sequence> firstSequence;
    /** First sequence arguments. */
    private Object[] arguments;
    /** Current sequence. */
    private Sequence sequence;
    /** Image buffer. */
    private ImageBuffer buf;
    /** Graphic buffer. */
    private Graphic gbuf;
    /** Hq3x use flag. */
    private int hqx;
    /** Filter used. */
    private Transform op;
    /** Direct rendering. */
    private boolean directRendering;
    /** Current frame rate. */
    private double currentFrameRate;
    /** Extrapolation flag. */
    private boolean extrapolated;
    /** Async load. */
    private boolean asyncLoadFlag;
    /** Thread running flag. */
    private boolean isRunning;

    /**
     * Constructor.
     * 
     * @param config The config reference.
     * @param name The renderer name.
     */
    protected Renderer(Config config, String name)
    {
        super("LionEngine " + name + " Renderer");
        this.config = config;
        filter = config.getFilter();
        output = config.getOutput();
        sync = config.isWindowed() && output.getRate() > 0;
        extrapolated = true;

        // Time needed for a loop to reach desired rate
        if (output.getRate() == 0)
        {
            frameDelay = 0;
        }
        else
        {
            frameDelay = Renderer.TIME_LONG / output.getRate();
        }
        graphic = EngineCore.factoryGraphic.createGraphic();
    }

    /**
     * Allow to load another sequence without blocking the current one.
     * 
     * @param nextSequence The next sequence to load.
     */
    protected abstract void asyncLoad(Sequence nextSequence);

    /**
     * Start with the first sequence.
     * 
     * @param sequence The first sequence to start.
     * @param loader The loader reference.
     * @param arguments The sequence arguments list if needed by its constructor.
     */
    final void startFirstSequence(Class<? extends Sequence> sequence, Loader loader, Object... arguments)
    {
        if (!started)
        {
            this.loader = loader;
            this.arguments = arguments;
            firstSequence = sequence;
            start();
        }
    }

    /**
     * Start a new sequence and can wait for it to be loaded before continuing.
     * 
     * @param nextSequence The next sequence reference (must not be <code>null</code>).
     * @param wait <code>true</code> to wait for the next sequence to be loaded, <code>false</code> else.
     */
    final void start(Sequence nextSequence, boolean wait)
    {
        Check.notNull(nextSequence, Renderer.ERROR_SEQUENCE);

        this.nextSequence = nextSequence;
        if (wait)
        {
            nextSequence.start();
        }
        else
        {
            asyncLoad(nextSequence);
            asyncLoadFlag = true;
        }
    }

    /**
     * Terminate sequence.
     */
    final void end()
    {
        end(nextSequence);
    }

    /**
     * Terminate sequence, and set the next sequence.
     * 
     * @param nextSequence The next sequence reference.
     */
    final void end(Sequence nextSequence)
    {
        this.nextSequence = nextSequence;
        isRunning = false;
    }

    /**
     * Add a key listener.
     * 
     * @param listener The listener to add.
     */
    final void addKeyListener(InputDeviceKeyListener listener)
    {
        screen.addKeyListener(listener);
    }

    /**
     * Set the extrapolation flag.
     * 
     * @param extrapolated <code>true</code> will activate it, <code>false</code> will disable it.
     */
    final void setExtrapolated(boolean extrapolated)
    {
        this.extrapolated = extrapolated;
    }

    /**
     * Set the new resolution used by the sequence.
     * 
     * @param newSource The new resolution used.
     */
    final void setResolution(Resolution newSource)
    {
        Check.notNull(newSource, Renderer.ERROR_RESOLUTION);

        config.setSource(newSource);
        source = config.getSource();

        // Scale factor
        final double scaleX = output.getWidth() / (double) source.getWidth();
        final double scaleY = output.getHeight() / (double) source.getHeight();
        Transform transform = EngineCore.factoryGraphic.createTransform();

        // Filter level
        switch (filter)
        {
            case HQ2X:
                hqx = 2;
                transform.scale(scaleX / 2, scaleY / 2);
                break;
            case HQ3X:
                hqx = 3;
                transform.scale(scaleX / 3, scaleY / 3);
                break;
            default:
                hqx = 0;
                transform.scale(scaleX, scaleY);
                break;
        }

        // Store source size
        width = source.getWidth();
        height = source.getHeight();

        // Standard rendering
        if (hqx == 0 && source.getWidth() == output.getWidth() && source.getHeight() == output.getHeight())
        {
            buf = null;
            gbuf = null;
            transform = null;
            graphic.setGraphic(gbuf);
        }
        // Scaled rendering
        else
        {
            buf = EngineCore.factoryGraphic.createImageBuffer(width, height, Transparency.OPAQUE);
            gbuf = buf.createGraphic();
            if (hqx > 1 || filter == Filter.NONE)
            {
                transform.setInterpolation(false);
            }
            else
            {
                transform.setInterpolation(true);
            }
            graphic.setGraphic(gbuf.getGraphic());
        }
        op = transform;
        directRendering = hqx == 0 && (op == null || buf == null);
        sequence.setResolution(width, height);
        sequence.onResolutionChanged(width, height, source.getRate());
    }

    /**
     * Set the system cursor visibility.
     * 
     * @param visible <code>true</code> if visible, <code>false</code> else.
     */
    final void setSystemCursorVisible(boolean visible)
    {
        if (visible)
        {
            screen.showCursor();
        }
        else
        {
            screen.hideCursor();
        }
    }

    /**
     * Get the configuration.
     * 
     * @return The configuration.
     */
    final Config getConfig()
    {
        return config;
    }

    /**
     * Get current frame rate (number of image per second).
     * 
     * @return The current number of image per second.
     */
    final int getFps()
    {
        return (int) currentFrameRate;
    }

    /**
     * Get the input device instance from its type.
     * 
     * @param <T> The input device.
     * @param type The input device type.
     * @return The input instance reference, <code>null</code> if not found.
     */
    final <T extends InputDevice> T getInputDevice(Class<T> type)
    {
        return screen.getInputDevice(type);
    }

    /**
     * Render handler.
     * 
     * @param g The graphic output.
     */
    private void preRender(final Graphic g)
    {
        if (directRendering)
        {
            sequence.render(g);
        }
        else
        {
            sequence.render(graphic);
            switch (hqx)
            {
                case 2:
                    g.drawImage(new Hq2x(buf).getScaledImage(), op, 0, 0);
                    break;
                case 3:
                    g.drawImage(new Hq3x(buf).getScaledImage(), op, 0, 0);
                    break;
                default:
                    g.drawImage(buf, op, 0, 0);
                    break;
            }
        }
    }

    /**
     * Sync frame rate to desired if possible.
     * 
     * @param time The update tile.
     */
    private void sync(final long time)
    {
        if (sync)
        {
            try
            {
                final long waitTime = frameDelay - time;
                if (waitTime > 0)
                {
                    Thread.sleep(waitTime / Renderer.TIME_INT, (int) (waitTime % Renderer.TIME_INT));
                }
            }
            catch (final InterruptedException exception)
            {
                Verbose.critical(Renderer.class, "sync", "Renderer interrupted !");
                isRunning = false;
            }
        }
    }

    /**
     * Update routine.
     * 
     * @param sequence The sequence to update.
     */
    private void update(Sequence sequence)
    {
        this.sequence = sequence;
        nextSequence = null;
        screen.setSequence(sequence);
        sequence.start();
        setResolution(sequence.resolution);

        // Prepare sequence to be started
        double extrp = Renderer.EXTRP;
        long updateFpsTimer = 0L;
        currentFrameRate = output.getRate();
        screen.requestFocus();

        sequence.onLoaded(extrp, screen.getGraphic());

        // Main loop
        isRunning = true;
        while (isRunning)
        {
            final long lastTime = System.nanoTime();
            if (screen.isReady())
            {
                sequence.update(extrp);
                screen.preUpdate();
                preRender(screen.getGraphic());
                screen.update();
            }
            sync(System.nanoTime() - lastTime);

            // Perform extrapolation and frame rate calculation
            final long currentTime = System.nanoTime();
            if (extrapolated)
            {
                extrp = source.getRate() / Renderer.TIME_DOUBLE * (currentTime - lastTime);
            }
            else
            {
                extrp = Renderer.EXTRP;
            }
            if (currentTime - updateFpsTimer > Renderer.TIME_LONG)
            {
                currentFrameRate = Renderer.TIME_DOUBLE / (currentTime - lastTime);
                updateFpsTimer = currentTime;
            }
            if (!EngineCore.isStarted())
            {
                isRunning = false;
            }
        }
        sequence.onTerminate(nextSequence != null);
    }

    /**
     * Wait for screen to be ready before continuing.
     */
    private void waitForScreenReady()
    {
        final Timing timeout = new Timing();
        timeout.start();
        while (!screen.isReady())
        {
            try
            {
                Thread.sleep(100);
                if (timeout.elapsed(5000))
                {
                    Thread.currentThread().interrupt();
                }
            }
            catch (final InterruptedException exception)
            {
                Verbose.critical(Renderer.class, "run", "Unable to get screen ready !");
                nextSequence = null;
                break;
            }
        }
    }

    /**
     * Wait for the async loading to be finished before running the sequence.
     * 
     * @param nextSequence The next sequence reference.
     * @param sequenceName The sequence name to wait for.
     * @return <code>true</code> if correctly loaded, <code>false</code> if error.
     */
    private boolean waitForAsyncLoad(Sequence nextSequence, String sequenceName)
    {
        if (asyncLoadFlag)
        {
            try
            {
                nextSequence.loadedSemaphore.acquire();
                asyncLoadFlag = false;
            }
            catch (final InterruptedException exception)
            {
                Verbose.critical(Renderer.class, "run", "Sequence async loading interrupted: ", sequenceName);
                return false;
            }
        }
        return true;
    }

    /*
     * Thread
     */

    @Override
    public void run()
    {
        // First init
        started = true;
        screen = EngineCore.factoryGraphic.createScreen(this, config);
        screen.start();
        nextSequence = Loader.createSequence(firstSequence, loader, arguments);
        waitForScreenReady();
        firstSequence = null;

        while (nextSequence != null)
        {
            final Sequence sequence = nextSequence;
            final String sequenceName = sequence.getClass().getName();
            if (!waitForAsyncLoad(sequence, sequenceName))
            {
                break;
            }
            Verbose.info("Starting sequence: ", sequenceName);

            update(sequence);

            Verbose.info("Ending sequence: ", sequenceName);
        }
        screen.dispose();
        EngineCore.terminate();
    }
}
