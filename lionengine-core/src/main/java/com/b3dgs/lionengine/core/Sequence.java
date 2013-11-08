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
package com.b3dgs.lionengine.core;

import java.lang.Thread.State;
import java.util.concurrent.Semaphore;

import com.b3dgs.lionengine.Check;
import com.b3dgs.lionengine.Filter;
import com.b3dgs.lionengine.Graphic;
import com.b3dgs.lionengine.Keyboard;
import com.b3dgs.lionengine.Mouse;
import com.b3dgs.lionengine.Resolution;
import com.b3dgs.lionengine.Transparency;

/**
 * Sequence class is used for each derived sequence, such as Introduction, Menu, Scene... It contains a reference to the
 * screen used, the current configuration, input references ({@link Keyboard}, {@link Mouse}), and it includes a
 * standard game loop ({@link Sequence#update(double)} and {@link Sequence#render(Graphic)}), synchronized to a
 * specified frame rate.
 * <p>
 * Here a blank sequence implementation:
 * </p>
 * 
 * <pre>
 * final class Scene
 *         extends Sequence
 * {
 *     private static final Resolution NATIVE = new Resolution(320, 240, 60);
 * 
 *     Scene(Loader loader)
 *     {
 *         super(loader, Scene.NATIVE);
 *     }
 * 
 *     &#064;Override
 *     protected void load()
 *     {
 *         // Load
 *     }
 * 
 *     &#064;Override
 *     protected void update(double extrp)
 *     {
 *         if (keyboard.isPressed(Key.ESCAPE))
 *         {
 *             end();
 *         }
 *         // Update
 *     }
 * 
 *     &#064;Override
 *     protected void render(Graphic g)
 *     {
 *         // Render
 *     }
 * }
 * </pre>
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 * @see Loader
 * @see Resolution
 * @see Graphic
 */
public abstract class Sequence
{
    /** Error message loader. */
    private static final String ERROR_LOADER = "Loader must not be null !";
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

    /** Keyboard reference. */
    protected final Keyboard keyboard;
    /** Mouse reference. */
    protected final Mouse mouse;
    /** Loader reference. */
    protected final Loader loader;
    /** Synchronize two sequences. */
    private final Semaphore semaphore;
    /** Config reference. */
    private final Config config;
    /** Screen reference. */
    private final Screen screen;
    /** Output resolution reference. */
    private final Resolution output;
    /** Filter reference. */
    private final Filter filter;
    /** Filter graphic. */
    private final Graphic graphic;
    /** Loop time for desired rate. */
    private final long frameDelay;
    /** Sequence thread. */
    private final SequenceThread thread;
    /** Has sync. */
    private final boolean sync;
    /** Source resolution reference. */
    protected Resolution source;
    /** Screen width. */
    protected int width;
    /** Screen height. */
    protected int height;
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
    /** Previous sequence pointer. */
    private Sequence previousSequence;
    /** Next sequence pointer. */
    private Sequence nextSequence;
    /** Extrapolation flag. */
    private boolean extrapolated;
    /** Current frame rate. */
    private double currentFrameRate;
    /** Thread running flag. */
    private boolean isRunning;
    /** Will stop flag. */
    private boolean willStop;

    /**
     * Constructor.
     * 
     * @param loader The loader reference.
     * @param newSource The resolution source reference.
     */
    public Sequence(final Loader loader, Resolution newSource)
    {
        Check.notNull(loader, Sequence.ERROR_LOADER);

        // Initialize
        this.loader = loader;
        semaphore = new Semaphore(0);
        config = loader.config;
        screen = loader.screen;
        keyboard = loader.keyboard;
        mouse = loader.mouse;
        output = config.getOutput();
        filter = config.getFilter();
        sync = config.isWindowed() && output.getRate() > 0;
        graphic = EngineImpl.factoryGraphic.createGraphic();
        extrapolated = true;

        // Time needed for a loop to reach desired rate
        if (output.getRate() == 0)
        {
            frameDelay = 0;
        }
        else
        {
            frameDelay = Sequence.TIME_LONG / output.getRate();
        }

        setResolution(newSource);
        thread = new SequenceThread(this);
    }

    /**
     * Set the new resolution used by the sequence.
     * 
     * @param newSource The new resolution used.
     */
    protected final void setResolution(Resolution newSource)
    {
        Check.notNull(newSource, Sequence.ERROR_RESOLUTION);
        config.setSource(newSource);
        if (mouse != null)
        {
            mouse.setConfig(config);
        }
        source = config.getSource();
        // Scale factor
        final double scaleX = output.getWidth() / (double) source.getWidth();
        final double scaleY = output.getHeight() / (double) source.getHeight();
        Transform transform = EngineImpl.factoryGraphic.createTransform();

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
            buf = EngineImpl.factoryGraphic.createCompatibleImage(width, height, Transparency.OPAQUE);
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
        onResolutionChanged(width, height, source.getRate());
    }

    /**
     * Loading sequence data.
     */
    protected abstract void load();

    /**
     * Update sequence.
     * 
     * @param extrp The extrapolation value. Can be used to have an non dependent machine speed calculation. Example: x
     *            += 5.0 * extrp
     */
    protected abstract void update(final double extrp);

    /**
     * Render sequence.
     * 
     * @param g The graphic output.
     */
    protected abstract void render(final Graphic g);

    /**
     * Terminate sequence, close screen, and start launcher if exists.
     */
    public final void end()
    {
        end(nextSequence);
    }

    /**
     * Terminate sequence, and set the next sequence.
     * 
     * @param nextSequence The next sequence reference.
     */
    public final void end(final Sequence nextSequence)
    {
        this.nextSequence = nextSequence;
        isRunning = false;
        willStop = true;
    }

    /**
     * Start the next sequence, call the {@link #load()} function, and wait for current sequence to end before next
     * sequence continues. This function should be used to synchronize two sequences (eg: load a next sequence while
     * being in a menu). Do not forget to call {@link #end()} in order to give control to the next sequence. The next
     * sequence should override {@link #onLoaded(double, Graphic)} for special load just before enter in the loop.
     * 
     * @param nextSequence The next sequence reference.
     * @param wait <code>true</code> to wait for the next sequence to be loaded, <code>false</code> else.
     */
    public final void start(final Sequence nextSequence, boolean wait)
    {
        this.nextSequence = nextSequence;
        nextSequence.previousSequence = this;
        nextSequence.setTitle(nextSequence.getClass().getName());
        nextSequence.start();
        if (wait)
        {
            try
            {
                nextSequence.semaphore.acquire();
            }
            catch (final InterruptedException exception)
            {
                Thread.currentThread().interrupt();
                Verbose.exception(Sequence.class, "start", exception);
            }
        }
    }

    /**
     * Add a key listener.
     * 
     * @param listener The listener to add.
     */
    public final void addKeyListener(KeyboardListener listener)
    {
        screen.addKeyListener(listener);
    }

    /**
     * Set the mouse visibility.
     * 
     * @param visible The visibility state.
     */
    public final void setMouseVisible(boolean visible)
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
     * Set the extrapolation flag.
     * 
     * @param extrapolated <code>true</code> will activate it, <code>false</code> will disable it.
     */
    public final void setExtrapolated(boolean extrapolated)
    {
        this.extrapolated = extrapolated;
    }

    /**
     * Get current frame rate (number of image per second).
     * 
     * @return The current number of image per second.
     */
    public final int getFps()
    {
        return (int) currentFrameRate;
    }

    /**
     * Get the configuration.
     * 
     * @return The configuration.
     */
    public Config getConfig()
    {
        return config;
    }

    /**
     * Get the keyboard reference.
     * 
     * @return The keyboard reference.
     */
    public Keyboard getKeyboard()
    {
        return keyboard;
    }

    /**
     * Get the mouse reference.
     * 
     * @return The mouse reference.
     */
    public Mouse getMouse()
    {
        return mouse;
    }

    /**
     * Clear the screen.
     * 
     * @param g The graphics output.
     */
    protected final void clearScreen(Graphic g)
    {
        g.clear(source);
    }

    /**
     * Called when the sequence has been loaded. Does nothing by default.
     * 
     * @param extrp The extrapolation value.
     * @param g The graphic output.
     */
    protected void onLoaded(double extrp, Graphic g)
    {
        // Nothing by default
    }

    /**
     * Called when the resolution changed. Does nothing by default.
     * 
     * @param width The new screen width.
     * @param height The new screen height.
     * @param rate The new rate.
     */
    protected void onResolutionChanged(int width, int height, int rate)
    {
        // Nothing by default
    }

    /**
     * Called when sequence is closing. Should be used in case on special loading (such as music starting) when
     * {@link #start(Sequence, boolean)} has been used by another sequence. Does nothing by default.
     * 
     * @param hasNextSequence <code>true</code> if there is a next sequence, <code>false</code> else (then application
     *            will end definitely).
     */
    protected void onTerminate(boolean hasNextSequence)
    {
        // Nothing by default
    }

    /**
     * Called when sequence is focused (screen). Does nothing by default.
     */
    protected void onFocusGained()
    {
        // Nothing by default
    }

    /**
     * Called when sequence lost focus (screen). Does nothing by default.
     */
    protected void onLostFocus()
    {
        // Nothing by default
    }

    /**
     * Start sequence.
     */
    final void start()
    {
        thread.start();
    }

    /**
     * Run sequence.
     */
    final void run()
    {
        // Load sequence
        load();

        // Check previous sequence
        if (previousSequence != null)
        {
            notifyPreviousSequenceAndWait();
        }

        // Prepare sequence to be started
        double extrp = Sequence.EXTRP;
        long updateFpsTimer = 0L;
        currentFrameRate = output.getRate();
        screen.requestFocus();
        final int mcx = screen.getLocationX() + output.getWidth() / 2;
        final int mcy = screen.getLocationY() + output.getHeight() / 2;
        if (mouse != null)
        {
            mouse.setCenter(mcx, mcy);
        }
        onLoaded(extrp, screen.getGraphic());

        // Main loop
        isRunning = true;
        while (isRunning)
        {
            final long lastTime = System.nanoTime();
            mouse.update();
            if (screen.isReady())
            {
                update(extrp);
                screen.preUpdate();
                preRender(screen.getGraphic());
                screen.update();
            }
            sync(System.nanoTime() - lastTime);

            // Perform extrapolation and frame rate calculation
            final long currentTime = System.nanoTime();
            if (extrapolated)
            {
                extrp = source.getRate() / Sequence.TIME_DOUBLE * (currentTime - lastTime);
            }
            else
            {
                extrp = Sequence.EXTRP;
            }
            if (currentTime - updateFpsTimer > Sequence.TIME_LONG)
            {
                currentFrameRate = Sequence.TIME_DOUBLE / (currentTime - lastTime);
                updateFpsTimer = currentTime;
            }
            if (willStop && nextSequence != null)
            {
                waitForNextSequenceWaiting();
            }
            if (!EngineImpl.started)
            {
                isRunning = false;
            }
        }
        onTerminate(nextSequence != null);
        semaphore.release();
        loader.semaphore.release();
    }

    /**
     * Set thread title.
     * 
     * @param title The title.
     */
    final void setTitle(String title)
    {
        thread.setName(title);
    }

    /**
     * Get next sequence.
     * 
     * @return The next sequence.
     */
    final Sequence getNextSequence()
    {
        return nextSequence;
    }

    /**
     * Get the started state.
     * 
     * @return <code>true</code> if started, <code>false</code> else.
     */
    final boolean isStarted()
    {
        return thread.isAlive();
    }

    /**
     * Notify the previous sequence and wait for its termination.
     */
    private void notifyPreviousSequenceAndWait()
    {
        semaphore.release();
        synchronized (previousSequence)
        {
            try
            {
                previousSequence.semaphore.acquire();
            }
            catch (final InterruptedException exception)
            {
                Thread.currentThread().interrupt();
                Verbose.exception(Sequence.class, "run", exception);
            }
        }
    }

    /**
     * Active wait for the next sequence to wait for this sequence before allow ending.
     */
    private void waitForNextSequenceWaiting()
    {
        if (!isRunning && nextSequence.isStarted())
        {
            isRunning = true;
        }
        if (nextSequence.thread.getState() == State.WAITING)
        {
            isRunning = false;
        }
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
            // Direct rendering
            render(g);
        }
        else
        {
            // Intermediate rendering (use of filter)
            render(graphic);
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
        // Sync only if windowed and has a desired rate
        if (sync)
        {
            try
            {
                // Time to wait
                final long wait = frameDelay - time;

                // Need to wait
                if (wait > 0)
                {
                    Thread.sleep(wait / Sequence.TIME_INT, (int) (wait % Sequence.TIME_INT));
                }
            }
            catch (final InterruptedException exception)
            {
                Thread.currentThread().interrupt();
                Verbose.exception(Sequence.class, "sync", exception);
            }
        }
    }
}
