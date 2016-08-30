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
package com.b3dgs.lionengine.core;

import com.b3dgs.lionengine.Check;
import com.b3dgs.lionengine.Timing;
import com.b3dgs.lionengine.graphic.Filter;
import com.b3dgs.lionengine.graphic.Graphic;
import com.b3dgs.lionengine.graphic.ImageBuffer;
import com.b3dgs.lionengine.graphic.Transform;
import com.b3dgs.lionengine.graphic.Transparency;

/**
 * Sequence class is used for each derived sequence, such as Introduction, Menu, Scene... It contains a reference to the
 * screen used, the current configuration, input references ({@link #getInputDevice(Class)}), and it includes
 * a standard game loop ({@link #update(double)} and {@link #render(Graphic)}), synchronized to a specified frame rate.
 * <p>
 * Here a blank sequence implementation:
 * </p>
 * 
 * <pre>
 * public class MySequence extends Sequence
 * {
 *     private static final Resolution NATIVE = new Resolution(320, 240, 60);
 * 
 *     public MySequence(Context context)
 *     {
 *         super(context, MySequence.NATIVE);
 *         // Initialize variables here
 *     }
 * 
 *     &#064;Override
 *     public void load()
 *     {
 *         // Load resources here
 *     }
 * 
 *     &#064;Override
 *     public void update(double extrp)
 *     {
 *         // Update routine
 *     }
 * 
 *     &#064;Override
 *     public void render(Graphic g)
 *     {
 *         // Render routine
 *     }
 * }
 * </pre>
 * 
 * <p>
 * This class is Thread-Safe.
 * </p>
 * 
 * @see Loader
 * @see Resolution
 * @see InputDevice
 */
public abstract class Sequence implements Sequencable, ScreenListener
{
    /** One second in milli. */
    private static final long ONE_SECOND_IN_MILLI = 1000L;
    /** One second in nano. */
    private static final long ONE_SECOND_IN_NANO = 1000000000L;
    /** Extrapolation standard. */
    private static final double EXTRP = 1.0;

    /** Context reference. */
    private final Context context;
    /** Native resolution. */
    private final Resolution resolution;
    /** Config reference. */
    private final Config config;
    /** Filter graphic. */
    private final Graphic graphic;
    /** Loop time for desired rate. */
    private final long frameDelay;
    /** Has sync. */
    private final boolean sync;
    /** Output resolution reference. */
    private final Resolution output;
    /** Filter reference. */
    private volatile Filter filter = Filter.NO_FILTER;
    /** Rendering width. */
    private int width;
    /** Rendering height. */
    private int height;
    /** Next sequence pointer. */
    private Sequence nextSequence;
    /** Thread running flag. */
    private boolean isRunning;
    /** Extrapolation flag. */
    private boolean extrapolated;
    /** Current frame rate. */
    private int currentFrameRate;
    /** Image buffer. */
    private ImageBuffer buf;
    /** Filter used. */
    private Transform transform;
    /** Direct rendering. */
    private boolean directRendering;
    /** Source resolution reference. */
    private Resolution source;
    /** Current screen used. */
    private Screen screen;
    /** Pending cursor visibility. */
    private Boolean cursorVisibility;

    /**
     * Constructor base. Resolution will be based on {@link Config#getOutput()}.
     * 
     * @param context The context reference.
     */
    public Sequence(Context context)
    {
        this(context, context.getConfig().getOutput());
    }

    /**
     * Constructor base.
     * 
     * @param context The context reference.
     * @param resolution The resolution source reference.
     */
    public Sequence(Context context, Resolution resolution)
    {
        this.context = context;
        this.resolution = resolution;
        config = context.getConfig();
        source = resolution;
        output = config.getOutput();
        sync = config.isWindowed() && output.getRate() > 0;
        width = resolution.getWidth();
        height = resolution.getHeight();

        // Time needed for a loop to reach desired rate
        if (output.getRate() == 0)
        {
            frameDelay = 0;
        }
        else
        {
            frameDelay = ONE_SECOND_IN_NANO / output.getRate();
        }

        graphic = Graphics.createGraphic();
        config.setSource(resolution);
    }

    /**
     * Loading sequence data.
     */
    public abstract void load();

    /**
     * Set the filter to use.
     * 
     * @param filter The filter to use (if <code>null</code> then {@link Filter#NO_FILTER} is used).
     */
    public final void setFilter(Filter filter)
    {
        if (filter == null)
        {
            this.filter = Filter.NO_FILTER;
        }
        else
        {
            this.filter = filter;
        }
        transform = getTransform(this.filter);
    }

    /**
     * Get the rendering width.
     * 
     * @return The rendering width.
     */
    protected final int getWidth()
    {
        return width;
    }

    /**
     * Get the rendering height.
     * 
     * @return The rendering height.
     */
    protected final int getHeight()
    {
        return height;
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
     * Called when the sequence has been loaded. Does nothing by default.
     * 
     * @param extrp The extrapolation value.
     * @param g The graphic output.
     */
    @SuppressWarnings("unused")
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
    @SuppressWarnings("unused")
    protected void onResolutionChanged(int width, int height, int rate)
    {
        // Nothing by default
    }

    /**
     * Get the transform associated to the filter keeping screen scale independent.
     * 
     * @param filter The filter reference.
     * @return The associated transform instance.
     */
    private Transform getTransform(Filter filter)
    {
        final double scaleX = output.getWidth() / (double) source.getWidth();
        final double scaleY = output.getHeight() / (double) source.getHeight();
        return filter.getTransform(scaleX, scaleY);
    }

    /**
     * Local render routine.
     */
    private void render()
    {
        final Graphic g = screen.getGraphic();
        if (directRendering)
        {
            render(g);
        }
        else
        {
            render(graphic);
            g.drawImage(filter.filter(buf), transform, 0, 0);
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
            return source.getRate() / (double) ONE_SECOND_IN_NANO * (currentTime - lastTime);
        }
        return EXTRP;
    }

    /**
     * Compute the frame rate depending of the game loop speed.
     * 
     * @param lastTime The last time value before game loop.
     * @param currentTime The current time after game loop.
     * @param updateFpsTimer The last fps update time.
     */
    private void computeFrameRate(long lastTime, long currentTime, Timing updateFpsTimer)
    {
        if (updateFpsTimer.elapsed(ONE_SECOND_IN_MILLI))
        {
            currentFrameRate = (int) (ONE_SECOND_IN_NANO / (double) (currentTime - lastTime));
            updateFpsTimer.restart();
        }
    }

    /*
     * Sequencable
     */

    @Override
    public void start(Screen screen)
    {
        this.screen = screen;
        screen.addListener(this);
        if (cursorVisibility != null)
        {
            setSystemCursorVisible(cursorVisibility.booleanValue());
        }

        nextSequence = null;
        load();
        setResolution(resolution);

        // Prepare sequence to be started
        currentFrameRate = output.getRate();
        screen.requestFocus();
        final Timing updateFpsTimer = new Timing();
        updateFpsTimer.start();

        double extrp = EXTRP;
        onLoaded(extrp, screen.getGraphic());

        // Main loop
        isRunning = true;
        while (isRunning)
        {
            final long lastTime = System.nanoTime();
            if (screen.isReady())
            {
                update(extrp);
                screen.preUpdate();
                render();
                screen.update();
            }
            sync(System.nanoTime() - lastTime);

            final long currentTime = Math.max(lastTime + 1, System.nanoTime());
            extrp = computeExtrapolation(lastTime, currentTime);
            computeFrameRate(lastTime, currentTime, updateFpsTimer);

            if (!Engine.isStarted())
            {
                isRunning = false;
            }
        }
        screen.removeListener(this);
    }

    @Override
    public final void end()
    {
        isRunning = false;
    }

    @Override
    public final void end(Class<? extends Sequencable> nextSequenceClass, Object... arguments)
    {
        Check.notNull(nextSequenceClass);

        nextSequence = UtilSequence.create(nextSequenceClass, context, arguments);
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
    public final void setResolution(Resolution newSource)
    {
        Check.notNull(newSource);

        config.setSource(newSource);
        source = config.getSource();
        screen.onSourceChanged(source);

        // Store source size
        width = source.getWidth();
        height = source.getHeight();

        // Standard rendering
        if (Filter.NO_FILTER.equals(filter)
            && source.getWidth() == output.getWidth()
            && source.getHeight() == output.getHeight())
        {
            buf = null;
            transform = null;
            graphic.setGraphic(null);
            directRendering = true;
        }
        // Scaled rendering
        else
        {
            buf = Graphics.createImageBuffer(width, height, Transparency.OPAQUE);
            transform = getTransform(filter);
            final Graphic gbuf = buf.createGraphic();
            graphic.setGraphic(gbuf.getGraphic());
            directRendering = false;
        }

        onResolutionChanged(width, height, config.getSource().getRate());
    }

    @Override
    public final void setSystemCursorVisible(boolean visible)
    {
        if (screen == null)
        {
            cursorVisibility = Boolean.valueOf(visible);
        }
        else
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
    public final <T extends InputDevice> T getInputDevice(Class<T> type)
    {
        return context.getInputDevice(type);
    }

    @Override
    public final Sequencable getNextSequence()
    {
        return nextSequence;
    }

    /**
     * {@inheritDoc}
     * Does nothing by default.
     */
    @Override
    public void onTerminated(boolean hasNextSequence)
    {
        // Nothing by default
    }

    /*
     * ScreenListener
     */

    /**
     * {@inheritDoc}
     * Does nothing by default.
     */
    @Override
    public void notifyFocusGained()
    {
        // Nothing by default
    }

    /**
     * {@inheritDoc}
     * Does nothing by default.
     */
    @Override
    public void notifyFocusLost()
    {
        // Nothing by default
    }

    @Override
    public final void notifyClosed()
    {
        end();
    }
}
