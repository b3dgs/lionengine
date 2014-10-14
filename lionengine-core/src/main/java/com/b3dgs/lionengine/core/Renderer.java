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
import com.b3dgs.lionengine.Config;
import com.b3dgs.lionengine.Filter;
import com.b3dgs.lionengine.Hq2x;
import com.b3dgs.lionengine.Hq3x;
import com.b3dgs.lionengine.LionEngineException;
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
        implements Sequencable
{
    /** Screen ready timeout in milli second. */
    static final int SCREEN_READY_TIME_OUT = 5000;
    /** Error message already started. */
    private static final String ERROR_STARTED = "Renderer has already been started !";
    /** One nano second. */
    private static final long TIME_LONG = 1000000000L;
    /** One nano second. */
    private static final double TIME_DOUBLE = 1000000000.0;
    /** One millisecond. */
    private static final long TIME_INT = 1000000L;
    /** Extrapolation standard. */
    private static final double EXTRP = 1.0;

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
    /** Next sequence pointer. */
    private Sequence nextSequence;
    /** Started. */
    private boolean started;
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
            frameDelay = TIME_LONG / output.getRate();
        }
        graphic = Core.GRAPHIC.createGraphic();
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
     * @throws LionEngineException If the renderer has already been started.
     */
    public final void startFirstSequence(Class<? extends Sequence> sequence, Loader loader, Object... arguments)
            throws LionEngineException
    {
        if (!started)
        {
            this.loader = loader;
            this.arguments = arguments;
            firstSequence = sequence;
            started = true;
            start();
        }
        else
        {
            throw new LionEngineException(ERROR_STARTED);
        }
    }

    /**
     * Get main frame location x.
     * 
     * @return The main frame location x.
     */
    public final int getX()
    {
        return screen.getX();
    }

    /**
     * Get main frame location y.
     * 
     * @return The main frame location y.
     */
    public final int getY()
    {
        return screen.getY();
    }

    /**
     * Check if the renderer is started.
     * 
     * @return <code>true</code> if started, <code>false</code> else.
     */
    public final boolean isStarted()
    {
        return started;
    }

    /**
     * Get the next sequence pointer.
     * 
     * @return The next sequence pointer.
     */
    public final Sequence getNextSequence()
    {
        return nextSequence;
    }

    /**
     * Terminate the renderer.
     */
    void terminate()
    {
        screen.dispose();
        EngineCore.terminate();
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
                    Thread.sleep(waitTime / TIME_INT, (int) (waitTime % TIME_INT));
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
        // Sequence may have already been loaded in case of async load or if started from another sequence
        if (!sequence.isLoaded())
        {
            sequence.start();
        }
        setResolution(sequence.resolution);

        // Prepare sequence to be started
        double extrp = EXTRP;
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
                extrp = source.getRate() / TIME_DOUBLE * (currentTime - lastTime);
            }
            else
            {
                extrp = EXTRP;
            }
            if (currentTime - updateFpsTimer > TIME_LONG)
            {
                currentFrameRate = TIME_DOUBLE / (currentTime - lastTime);
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
                if (timeout.elapsed(SCREEN_READY_TIME_OUT))
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
     * Sequencable
     */

    @Override
    public final void start(boolean wait, Class<? extends Sequence> nextSequenceClass, Object... arguments)
            throws LionEngineException
    {
        Check.notNull(nextSequenceClass);

        nextSequence = Loader.createSequence(nextSequenceClass, loader, arguments);
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

    @Override
    public final void end()
    {
        isRunning = false;
    }

    @Override
    public final void end(Class<? extends Sequence> nextSequenceClass, Object... arguments) throws LionEngineException
    {
        Check.notNull(nextSequenceClass);

        nextSequence = Loader.createSequence(nextSequenceClass, loader, arguments);
        isRunning = false;
    }

    @Override
    public final void addKeyListener(InputDeviceKeyListener listener)
    {
        screen.addKeyListener(listener);
    }

    @Override
    public final void setExtrapolated(boolean extrapolated)
    {
        this.extrapolated = extrapolated;
    }

    @Override
    public final void setResolution(Resolution newSource) throws LionEngineException
    {
        Check.notNull(newSource);

        config.setSource(newSource);
        source = config.getSource();

        // Scale factor
        final double scaleX = output.getWidth() / (double) source.getWidth();
        final double scaleY = output.getHeight() / (double) source.getHeight();
        Transform transform = Core.GRAPHIC.createTransform();

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
            graphic.setGraphic(null);
        }
        // Scaled rendering
        else
        {
            buf = Core.GRAPHIC.createImageBuffer(width, height, Transparency.OPAQUE);
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
    }

    @Override
    public final void setSystemCursorVisible(boolean visible)
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

    @Override
    public final Config getConfig()
    {
        return config;
    }

    @Override
    public final int getFps()
    {
        return (int) currentFrameRate;
    }

    @Override
    public final <T extends InputDevice> T getInputDevice(Class<T> type)
    {
        return screen.getInputDevice(type);
    }

    /*
     * Thread
     */

    @Override
    public final void run()
    {
        // First init
        started = true;
        screen = Core.GRAPHIC.createScreen(this);
        screen.start();
        sequence = Loader.createSequence(firstSequence, loader, arguments);
        nextSequence = sequence;
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
        terminate();
    }
}
