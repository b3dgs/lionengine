/*
 * Copyright (C) 2013-2022 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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

import com.b3dgs.lionengine.Check;
import com.b3dgs.lionengine.Config;
import com.b3dgs.lionengine.Constant;
import com.b3dgs.lionengine.Context;
import com.b3dgs.lionengine.InputDevice;
import com.b3dgs.lionengine.InputDeviceKeyListener;
import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Resolution;
import com.b3dgs.lionengine.Tick;
import com.b3dgs.lionengine.UtilMath;
import com.b3dgs.lionengine.graphic.Filter;
import com.b3dgs.lionengine.graphic.Graphic;
import com.b3dgs.lionengine.graphic.ImageBuffer;
import com.b3dgs.lionengine.graphic.Renderable;
import com.b3dgs.lionengine.graphic.Scanline;
import com.b3dgs.lionengine.graphic.Screen;
import com.b3dgs.lionengine.graphic.ScreenListener;

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
                               ScreenListener, Rasterbar
{
    /** Update fps delay in milli. */
    private static final int UPDATE_FPS_DELAY_MILLI = 500;

    /** Context reference. */
    private final Context context;
    /** Native resolution. */
    private final Resolution resolution;
    /** Config reference. */
    private final Config config;
    /** Loop mode. */
    private final Loop loop;
    /** Source resolution. */
    private final Resolution source;
    /** Sequence renderer. */
    private SequenceRenderer[] renderer;
    /** Next sequence pointer. */
    private Sequencable nextSequence;
    /** Current frame rate. */
    private int currentFrameRate;
    /** Current screen used (<code>null</code> if not started). */
    private Screen screen;
    /** Loaded flag. */
    private boolean loaded;
    /** Width. */
    private int width;
    /** Height. */
    private int height;
    /** Active split. */
    private int split;

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
        width = source.getWidth();
        height = source.getHeight();

        setSplit0();
    }

    @Override
    public final void setSplit(int split)
    {
        Check.superiorOrEqual(split, 0);
        Check.inferiorStrict(split, renderer.length);

        this.split = split;
    }

    /**
     * Configure with single screen.
     */
    public final void setSplit0()
    {
        renderer = new SequenceRenderer[1];
        renderer[0] = new SequenceRenderer(context, resolution, 0, 0, this::render);
    }

    /**
     * Configure with 2 split screens.
     * 
     * @param screen1 The screen rendering 1.
     * @param screen2 The screen rendering 2.
     * @param horizontal <code>true</code> for horizontal split, <code>false</code> for vertical split.
     */
    public final void setSplit(Renderable screen1, Renderable screen2, boolean horizontal)
    {
        final int dx;
        final int dy;
        if (horizontal)
        {
            dx = 1;
            dy = 2;
        }
        else
        {
            dx = 2;
            dy = 1;
        }

        renderer = new SequenceRenderer[2];
        renderer[0] = new SequenceRenderer(context, resolution, dx, dy, screen1);
        renderer[1] = new SequenceRenderer(context, resolution, dx, dy, screen2);

        renderer[0].setLocation(0);
        renderer[1].setLocation(1);
    }

    /**
     * Configure with 4 split screens.
     * 
     * @param screen1 The screen rendering 1.
     * @param screen2 The screen rendering 2.
     * @param screen3 The screen rendering 3.
     * @param screen4 The screen rendering 4.
     */
    public final void setSplit(Renderable screen1, Renderable screen2, Renderable screen3, Renderable screen4)
    {
        // CHECKSTYLE IGNORE LINE: Magic
        renderer = new SequenceRenderer[4];
        renderer[0] = new SequenceRenderer(context, resolution, 1, 1, screen1);
        renderer[1] = new SequenceRenderer(context, resolution, 1, 1, screen2);
        renderer[2] = new SequenceRenderer(context, resolution, 1, 1, screen3);
        // CHECKSTYLE IGNORE LINE: Magic
        renderer[3] = new SequenceRenderer(context, resolution, 1, 1, screen4);

        renderer[0].setLocation(0);
        renderer[1].setLocation(1);
        renderer[2].setLocation(2);
        // CHECKSTYLE IGNORE LINE: Magic
        renderer[3].setLocation(3);
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
        for (int i = 0; i < renderer.length; i++)
        {
            renderer[i].setFilter(filter);
        }
    }

    /**
     * Set the scanline to use.
     * 
     * @param scanline The scanline to use (if <code>null</code> then {@link ScanlineNone#INSTANCE} is used).
     */
    public final void setScanline(Scanline scanline)
    {
        for (int i = 0; i < renderer.length; i++)
        {
            renderer[i].setScanline(scanline);
        }
    }

    /**
     * Set the direct rendering.
     * 
     * @param direct <code>true</code> for direct rendering, <code>false</code> with buffer.
     */
    public final void setDirect(boolean direct)
    {
        for (int i = 0; i < renderer.length; i++)
        {
            renderer[i].setDirect(direct);
        }
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
     * Called when the resolution changed.
     * 
     * @param width The new screen width.
     * @param height The new screen height.
     */
    protected void onResolutionChanged(int width, int height)
    {
        this.width = width;
        this.height = height;
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
     * Compute the frame rate depending of the game loop speed.
     * 
     * @param updateFps The update timing
     * @param lastTime The last time value before game loop in nano.
     * @param currentTime The current time after game loop in nano (must be superior or equal to lastTime).
     */
    private void computeFrameRate(Tick updateFps, long lastTime, long currentTime)
    {
        if (updateFps.elapsedTime(getRate(), UPDATE_FPS_DELAY_MILLI))
        {
            currentFrameRate = (int) Math.round(Constant.ONE_SECOND_IN_NANO / (double) (currentTime - lastTime));
            updateFps.restart();
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

        for (int i = 0; i < renderer.length; i++)
        {
            renderer[i].setScreen(screen);
            renderer[i].initResolution(resolution);
        }
        currentFrameRate = config.getOutput().getRate();
        screen.requestFocus();

        if (!loaded)
        {
            load();
        }
        onLoaded(Constant.EXTRP, screen.getGraphic());

        // Main loop
        final Tick updateFps = new Tick();
        updateFps.start();
        // CHECKSTYLE IGNORE LINE: AnonIn
        loop.start(screen, new Frame()
        {
            @Override
            public void update(double extrp)
            {
                updateFps.update(extrp);
                Sequence.this.update(extrp);
            }

            @Override
            public void render()
            {
                for (int i = 0; i < renderer.length; i++)
                {
                    renderer[i].render();
                }
            }

            @Override
            public void computeFrameRate(long lastTime, long currentTime)
            {
                Sequence.this.computeFrameRate(updateFps, lastTime, currentTime);
            }
        });
        screen.removeListener(this);
    }

    @Override
    public void preload()
    {
        load();
        loaded = true;
    }

    @Override
    public final void end()
    {
        loop.stop();
    }

    @Override
    public final void end(Class<? extends Sequencable> nextSequenceClass, Object... arguments)
    {
        if (nextSequenceClass != null)
        {
            nextSequence = UtilSequence.create(nextSequenceClass, context, arguments);
        }
        else
        {
            nextSequence = null;
        }
        loop.stop();
    }

    @Override
    public void load(Class<? extends Sequencable> nextSequenceClass, Object... arguments)
    {
        if (nextSequence == null)
        {
            nextSequence = UtilSequence.create(nextSequenceClass, context, arguments);
            nextSequence.preload();
        }
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
        final Resolution zoomed = resolution.getScaled(scale);
        for (int i = 0; i < renderer.length; i++)
        {
            renderer[i].initResolution(zoomed);
        }
        onResolutionChanged(renderer[0].getWidth(), renderer[0].getHeight());
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
        for (int i = 0; i < renderer.length; i++)
        {
            renderer[i].setSystemCursorVisible(visible);
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
     * Rasterbar
     */

    @Override
    public void clearRasterbarColor()
    {
        for (int i = 0; i < renderer.length; i++)
        {
            renderer[i].clearRasterbarColor();
        }
    }

    @Override
    public void addRasterbarColor(ImageBuffer buffer)
    {
        for (int i = 0; i < renderer.length; i++)
        {
            renderer[i].addRasterbarColor(buffer);
        }
    }

    @Override
    public void setRasterbarOffset(int offsetY, int factorY)
    {
        for (int i = 0; i < renderer.length; i++)
        {
            renderer[i].setRasterbarOffset(offsetY, factorY);
        }
    }

    @Override
    public void setRasterbarY(int y1, int y2)
    {
        renderer[split].setRasterbarY(y1, y2);
    }

    @Override
    public void renderRasterbar()
    {
        renderer[split].renderRasterbar();
    }

    /*
     * SourceResolutionProvider
     */

    @Override
    public final int getWidth()
    {
        return width;
    }

    @Override
    public final int getHeight()
    {
        return height;
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
        end(null);
    }
}
