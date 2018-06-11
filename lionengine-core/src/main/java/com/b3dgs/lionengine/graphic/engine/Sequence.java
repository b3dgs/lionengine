/*
 * Copyright (C) 2013-2017 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.graphic.engine;

import java.util.Optional;

import com.b3dgs.lionengine.Check;
import com.b3dgs.lionengine.Config;
import com.b3dgs.lionengine.Constant;
import com.b3dgs.lionengine.Context;
import com.b3dgs.lionengine.InputDevice;
import com.b3dgs.lionengine.InputDeviceKeyListener;
import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Resolution;
import com.b3dgs.lionengine.Timing;
import com.b3dgs.lionengine.UtilMath;
import com.b3dgs.lionengine.graphic.Filter;
import com.b3dgs.lionengine.graphic.Graphic;
import com.b3dgs.lionengine.graphic.Graphics;
import com.b3dgs.lionengine.graphic.ImageBuffer;
import com.b3dgs.lionengine.graphic.Screen;
import com.b3dgs.lionengine.graphic.ScreenListener;
import com.b3dgs.lionengine.graphic.Transform;

/**
 * Sequence class is used for each derived sequence, such as Introduction, Menu, Scene... It contains a reference to the
 * screen used, the current configuration, input references ({@link #getInputDevice(Class)}), and it includes
 * a standard game loop ({@link #update(double)} and {@link #render(Graphic)}), synchronized to a specified frame rate.
 * <p>
 * This class is Thread-Safe.
 * </p>
 * 
 * @see Loader
 * @see Resolution
 * @see InputDevice
 */
public abstract class Sequence implements Sequencable, Sequencer, Zooming, TimeControl, SourceResolutionProvider,
                               ScreenListener
{
    /** Context reference. */
    private final Context context;
    /** Native resolution. */
    private final Resolution resolution;
    /** Config reference. */
    private final Config config;
    /** Filter graphic. */
    private final Graphic graphic;
    /** Loop mode. */
    private final Loop loop;
    /** Source resolution. */
    private Resolution source;
    /** Filter reference. */
    private volatile Filter filter = FilterNone.INSTANCE;
    /** Next sequence pointer. */
    private Optional<Sequencable> nextSequence = Optional.empty();
    /** Current frame rate. */
    private int currentFrameRate;
    /** Image buffer (can be <code>null</code> for direct rendering). */
    private ImageBuffer buf;
    /** Filter used (can be <code>null</code> for direct rendering). */
    private Transform transform;
    /** Current screen used (<code>null</code> if not started). */
    private Screen screen;
    /** Pending cursor visibility. */
    private Boolean cursorVisibility = Boolean.TRUE;

    /**
     * Constructor base. Resolution will be based on {@link Config#getOutput()}.
     * 
     * @param context The context reference (must not be <code>null</code>).
     * @throws LionEngineException If invalid argument.
     */
    protected Sequence(Context context)
    {
        this(context, context.getConfig().getOutput());
    }

    /**
     * Constructor base.
     * 
     * @param context The context reference (must not be <code>null</code>).
     * @param resolution The resolution source reference (must not be <code>null</code>).
     * @throws LionEngineException If invalid arguments.
     */
    protected Sequence(Context context, Resolution resolution)
    {
        this(context, resolution, new LoopFrameSkipping());
    }

    /**
     * Constructor base.
     * 
     * @param context The context reference (must not be <code>null</code>).
     * @param resolution The resolution source reference (must not be <code>null</code>).
     * @param loop The loop used (must not be <code>null</code>).
     * @throws LionEngineException If invalid arguments.
     */
    protected Sequence(Context context, Resolution resolution, Loop loop)
    {
        super();

        Check.notNull(context);
        Check.notNull(resolution);
        Check.notNull(loop);

        this.context = context;
        this.resolution = resolution;
        this.loop = loop;
        source = resolution;
        config = context.getConfig();
        graphic = Graphics.createGraphic();
    }

    /**
     * Loading sequence data.
     */
    public abstract void load();

    /**
     * Set the filter to use.
     * 
     * @param filter The filter to use (if <code>null</code> then {@link FilterNone#INSTANCE} is used).
     */
    public final void setFilter(Filter filter)
    {
        this.filter = Optional.ofNullable(filter).orElse(FilterNone.INSTANCE);
        transform = getTransform();
    }

    /**
     * Get main frame location x.
     * 
     * @return The main frame location x.
     */
    protected final int getX()
    {
        return context.getX();
    }

    /**
     * Get main frame location y.
     * 
     * @return The main frame location y.
     */
    protected final int getY()
    {
        return context.getY();
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
     */
    protected void onResolutionChanged(int width, int height)
    {
        // Nothing by default
    }

    /**
     * Called when the rate changed. Does nothing by default.
     * 
     * @param rate The new screen rate.
     */
    protected void onRateChanged(int rate)
    {
        // Nothing by default
    }

    /**
     * Initialize resolution.
     * 
     * @param source The resolution source (must not be <code>null</code>).
     * @throws LionEngineException If invalid argument.
     */
    private void initResolution(Resolution source)
    {
        Check.notNull(source);

        this.source = source;
        screen.onSourceChanged(source);
        final int width = source.getWidth();
        final int height = source.getHeight();

        // Standard rendering
        final Resolution output = config.getOutput();
        if (FilterNone.INSTANCE.equals(filter) && width == output.getWidth() && height == output.getHeight())
        {
            buf = null;
            transform = null;
        }
        // Scaled rendering
        else
        {
            buf = Graphics.createImageBuffer(width, height);
            transform = getTransform();
            final Graphic gbuf = buf.createGraphic();
            graphic.setGraphic(gbuf.getGraphic());
        }
    }

    /**
     * Get the transform associated to the filter keeping screen scale independent.
     * 
     * @return The associated transform instance.
     */
    private Transform getTransform()
    {
        final Resolution output = config.getOutput();

        final double scaleX = output.getWidth() / (double) source.getWidth();
        final double scaleY = output.getHeight() / (double) source.getHeight();

        return filter.getTransform(scaleX, scaleY);
    }

    /**
     * Local render routine.
     */
    private void render()
    {
        if (screen.isReady())
        {
            final Graphic g = screen.getGraphic();
            if (buf == null)
            {
                // Direct rendering
                render(g);
            }
            else
            {
                render(graphic);
                g.drawImage(filter.filter(buf), transform, 0, 0);
            }
        }
    }

    /**
     * Compute the frame rate depending of the game loop speed.
     * 
     * @param updateFpsTimer The update timing
     * @param lastTime The last time value before game loop in nano.
     * @param currentTime The current time after game loop in nano (must be superior or equal to lastTime).
     */
    private void computeFrameRate(Timing updateFpsTimer, long lastTime, long currentTime)
    {
        if (updateFpsTimer.elapsed(Constant.ONE_SECOND_IN_MILLI))
        {
            currentFrameRate = (int) Math.round(Constant.ONE_SECOND_IN_NANO / (double) (currentTime - lastTime));
            updateFpsTimer.restart();
        }
    }

    /*
     * Sequencable
     */

    @Override
    public void start(Screen screen)
    {
        Check.notNull(screen);

        this.screen = screen;
        screen.addListener(this);
        setSystemCursorVisible(cursorVisibility.booleanValue());
        initResolution(resolution);
        currentFrameRate = config.getOutput().getRate();
        screen.requestFocus();

        load();
        onLoaded(Constant.EXTRP, screen.getGraphic());

        // Main loop
        final Timing updateFpsTimer = new Timing();
        updateFpsTimer.start();
        loop.notifyRateChanged(resolution.getRate());
        loop.start(screen, new Frame()
        {
            @Override
            public void update(double extrp)
            {
                Sequence.this.update(extrp);
            }

            @Override
            public void render()
            {
                Sequence.this.render();
            }

            @Override
            public void computeFrameRate(long lastTime, long currentTime)
            {
                Sequence.this.computeFrameRate(updateFpsTimer, lastTime, currentTime);
            }
        });
        screen.removeListener(this);
    }

    @Override
    public final void end()
    {
        loop.stop();
    }

    @Override
    public final void end(Class<? extends Sequencable> nextSequenceClass, Object... arguments)
    {
        Check.notNull(nextSequenceClass);

        nextSequence = Optional.of(UtilSequence.create(nextSequenceClass, context, arguments));
        loop.stop();
    }

    @Override
    public final void addKeyListener(InputDeviceKeyListener listener)
    {
        screen.addKeyListener(listener);
    }

    @Override
    public final void setZoom(double factor)
    {
        final double scale = UtilMath.clamp(factor, 0.1, 5.0);
        final Resolution zoomed = resolution.getScaled(scale, scale);
        initResolution(zoomed);
        onResolutionChanged(zoomed.getWidth(), zoomed.getHeight());
    }

    @Override
    public void setTime(double factor)
    {
        final double scale = UtilMath.clamp(factor, 0.1, 5.0);
        final Resolution time = new Resolution(resolution.getWidth(),
                                               resolution.getHeight(),
                                               (int) (resolution.getRate() * scale));
        loop.notifyRateChanged(time.getRate());
        onRateChanged(time.getRate());
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
    public final Optional<Sequencable> getNextSequence()
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
     * SourceResolutionProvider
     */

    @Override
    public final int getWidth()
    {
        return source.getWidth();
    }

    @Override
    public final int getHeight()
    {
        return source.getHeight();
    }

    @Override
    public final int getRate()
    {
        return source.getRate();
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

    /**
     * {@inheritDoc}
     * Call {@link #end()} by default.
     */
    @Override
    public void notifyClosed()
    {
        end();
    }
}
