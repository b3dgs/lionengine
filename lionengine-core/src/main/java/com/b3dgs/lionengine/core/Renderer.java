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
 * <p>
 * This class is Thread-Safe.
 * </p>
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public class Renderer
        extends Thread
        implements Sequencable
{
    /** Screen ready timeout in milli second. */
    static final long SCREEN_READY_TIME_OUT = 50L;
    /** Error message already started. */
    private static final String ERROR_STARTED = "Renderer has already been started !";
    /** One nano second. */
    private static final long TIME_LONG = 1000000000L;
    /** One nano second. */
    private static final double TIME_DOUBLE = 1000000000.0;
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
    /** Started. */
    private volatile boolean started;
    /** Loader. */
    private volatile Loader loader;
    /** Next sequence pointer. */
    private volatile Sequence nextSequence;
    /** Current sequence. */
    private volatile Sequence sequence;
    /** Screen reference. */
    private volatile Screen screen;
    /** Thread running flag. */
    private volatile boolean isRunning;
    /** Extrapolation flag. */
    private volatile boolean extrapolated;
    /** Current frame rate. */
    private volatile int currentFrameRate;
    /** Image buffer. */
    private volatile ImageBuffer buf;
    /** Hq3x use flag. */
    private volatile int hqx;
    /** Filter used. */
    private volatile Transform op;
    /** Direct rendering. */
    private volatile boolean directRendering;
    /** Source resolution reference. */
    private volatile Resolution source;

    /**
     * Constructor base.
     * 
     * @param config The config reference.
     */
    public Renderer(Config config)
    {
        super("LionEngine Renderer");
        this.config = config;
        filter = config.getFilter();
        output = config.getOutput();
        sync = config.isWindowed() && output.getRate() > 0;
        extrapolated = false;

        // Time needed for a loop to reach desired rate
        if (output.getRate() == 0)
        {
            frameDelay = 0;
        }
        else
        {
            frameDelay = TIME_LONG / output.getRate();
        }
        graphic = Graphics.createGraphic();
    }

    /**
     * Start with the first sequence.
     * 
     * @param sequence The first sequence to start.
     * @throws LionEngineException If the renderer has already been started.
     */
    public final synchronized void startFirstSequence(Sequence sequence) throws LionEngineException
    {
        if (!started)
        {
            loader = loader;
            this.sequence = sequence;
            started = true;
            start();
        }
        else
        {
            throw new LionEngineException(ERROR_STARTED);
        }
    }

    /**
     * Check if the renderer is started.
     * 
     * @return <code>true</code> if started, <code>false</code> else.
     */
    public final synchronized boolean isStarted()
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
     * Get main frame location x.
     * 
     * @return The main frame location x.
     */
    protected final int getX()
    {
        return screen.getX();
    }

    /**
     * Get main frame location y.
     * 
     * @return The main frame location y.
     */
    protected final int getY()
    {
        return screen.getY();
    }

    /**
     * Terminate the renderer.
     */
    final synchronized void terminate()
    {
        if (sequence != null)
        {
            sequence.onTerminate(false);
        }
        if (screen != null)
        {
            screen.dispose();
        }
    }

    /**
     * Check the filter level, update the HQX value and apply transform.
     * 
     * @return The associated transform instance.
     */
    private Transform checkFilter()
    {
        final double scaleX = output.getWidth() / (double) source.getWidth();
        final double scaleY = output.getHeight() / (double) source.getHeight();
        final Transform transform = Graphics.createTransform();
        switch (filter)
        {
            case NONE:
            case BILINEAR:
                hqx = 0;
                transform.scale(scaleX, scaleY);
                break;
            case HQ2X:
                hqx = 2;
                transform.scale(scaleX / 2, scaleY / 2);
                break;
            case HQ3X:
                hqx = 3;
                transform.scale(scaleX / 3, scaleY / 3);
                break;
            default:
                throw new RuntimeException();
        }
        return transform;
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
                Thread.currentThread().interrupt();
                Verbose.critical(Renderer.class, "run", "Unable to get screen ready !");
                nextSequence = null;
                break;
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
                update(extrp);
                screen.preUpdate();
                render(screen.getGraphic());
                screen.update();
            }
            sync(System.nanoTime() - lastTime);

            final long currentTime = Math.max(lastTime + 1, System.nanoTime());
            extrp = computeExtrapolation(lastTime, currentTime);
            updateFpsTimer = computeFrameRate(lastTime, currentTime, updateFpsTimer);

            if (!EngineCore.isStarted())
            {
                isRunning = false;
            }
        }
        if (nextSequence != null)
        {
            sequence.onTerminate(true);
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
            final double waitTime = frameDelay - time;
            if (waitTime > 0.0)
            {
                final long prevTime = System.nanoTime();
                while (System.nanoTime() - prevTime < waitTime)
                {
                    Thread.yield();
                }
            }
        }
    }

    /**
     * Compute extrapolation value depending of the elapsed time.
     * 
     * @param lastTime The last time value before game loop.
     * @param currentTime The current time after game loop.
     * @return The computed extrapolation value.
     */
    private double computeExtrapolation(long lastTime, long currentTime)
    {
        if (extrapolated)
        {
            return source.getRate() / TIME_DOUBLE * (currentTime - lastTime);
        }
        return EXTRP;
    }

    /**
     * Compute the frame rate depending of the game loop speed.
     * 
     * @param lastTime The last time value before game loop.
     * @param currentTime The current time after game loop.
     * @param updateFpsTimer The last fps update time.
     * @return The next fps update time.
     */
    private long computeFrameRate(long lastTime, long currentTime, long updateFpsTimer)
    {
        if (currentTime - updateFpsTimer > TIME_LONG)
        {
            currentFrameRate = (int) (TIME_DOUBLE / (currentTime - lastTime));
            return currentTime;
        }
        return updateFpsTimer;
    }

    /*
     * Sequencable
     */

    @Override
    public final void end()
    {
        isRunning = false;
    }

    @Override
    public final synchronized void end(Class<? extends Sequence> nextSequenceClass, Object... arguments)
            throws LionEngineException
    {
        Check.notNull(nextSequenceClass);

        nextSequence = Loader.createSequence(nextSequenceClass, loader, arguments);
        isRunning = false;
    }

    @Override
    public void update(double extrp)
    {
        sequence.update(extrp);
    }

    @Override
    public void render(final Graphic g)
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
    public final synchronized void setResolution(Resolution newSource) throws LionEngineException
    {
        Check.notNull(newSource);

        config.setSource(newSource);
        source = config.getSource();
        screen.onSourceChanged(source);

        // Scale factor
        final Transform transform = checkFilter();

        // Store source size
        final int width = source.getWidth();
        final int height = source.getHeight();

        // Standard rendering
        if (hqx == 0 && source.getWidth() == output.getWidth() && source.getHeight() == output.getHeight())
        {
            buf = null;
            op = null;
            graphic.setGraphic(null);
        }
        // Scaled rendering
        else
        {
            buf = Graphics.createImageBuffer(width, height, Transparency.OPAQUE);
            if (hqx > 1 || filter == Filter.NONE)
            {
                transform.setInterpolation(false);
            }
            else
            {
                transform.setInterpolation(true);
            }
            op = transform;
            final Graphic gbuf = buf.createGraphic();
            graphic.setGraphic(gbuf.getGraphic());
        }
        directRendering = hqx == 0 && (op == null || buf == null);
        sequence.setResolution(width, height);
    }

    @Override
    public final synchronized void setSystemCursorVisible(boolean visible)
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
        return currentFrameRate;
    }

    @Override
    public final <T extends InputDevice> T getInputDevice(Class<T> type) throws LionEngineException
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
        screen = Graphics.createScreen(this);
        screen.start();
        nextSequence = sequence;
        waitForScreenReady();

        while (nextSequence != null)
        {
            final Sequence sequence = nextSequence;
            final String sequenceName = sequence.getClass().getName();
            Verbose.info("Starting sequence: ", sequenceName);

            update(sequence);

            Verbose.info("Ending sequence: ", sequenceName);
        }
        terminate();
    }
}
