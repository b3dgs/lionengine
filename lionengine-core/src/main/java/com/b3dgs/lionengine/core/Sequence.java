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

import com.b3dgs.lionengine.Config;
import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Resolution;

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
 *     public MySequence(Loader loader)
 *     {
 *         super(loader, MySequence.NATIVE);
 *         // Initialize variables here
 *     }
 * 
 *     &#064;Override
 *     protected void load()
 *     {
 *         // Load resources here
 *     }
 * 
 *     &#064;Override
 *     protected void update(double extrp)
 *     {
 *         // Update routine
 *     }
 * 
 *     &#064;Override
 *     protected void render(Graphic g)
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
 * @author Pierre-Alexandre (contact@b3dgs.com)
 * @see Loader
 * @see Resolution
 * @see InputDevice
 */
public abstract class Sequence implements Sequencable
{
    /** Native resolution. */
    final Resolution resolution;
    /** Renderer. */
    private final Renderer renderer;
    /** Rendering width. */
    private volatile int width;
    /** Rendering height. */
    private volatile int height;

    /**
     * Constructor base.
     * 
     * @param resolution The resolution source reference.
     * @param loader The loader reference.
     */
    public Sequence(Loader loader, Resolution resolution)
    {
        this.resolution = resolution;
        renderer = loader.getRenderer();
        renderer.getConfig().setSource(resolution);
        width = resolution.getWidth();
        height = resolution.getHeight();
    }

    /**
     * Loading sequence data.
     */
    protected abstract void load();

    /**
     * Called when sequence is focused (screen). Does nothing by default.
     */
    public void onFocusGained()
    {
        // Nothing by default
    }

    /**
     * Called when sequence lost focus (screen). Does nothing by default.
     */
    public void onLostFocus()
    {
        // Nothing by default
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
        return renderer.getX();
    }

    /**
     * Get main frame location y.
     * 
     * @return The main frame location y.
     */
    protected final int getY()
    {
        return renderer.getY();
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
     * Called when sequence is closing. Does nothing by default.
     * 
     * @param hasNextSequence <code>true</code> if there is a next sequence, <code>false</code> else (then application
     *            will end definitely).
     */
    protected void onTerminate(boolean hasNextSequence)
    {
        // Nothing by default
    }

    /**
     * Start the sequence and load it.
     */
    final synchronized void start()
    {
        load();
    }

    /**
     * Set the resolution. Must only be called by {@link Renderer#setResolution(Resolution)}.
     * 
     * @param width The new screen width.
     * @param height The new screen height.
     */
    final void setResolution(int width, int height)
    {
        this.width = width;
        this.height = height;
        onResolutionChanged(width, height, renderer.getConfig().getSource().getRate());
    }

    /*
     * Sequencable
     */

    @Override
    public final void end(Class<? extends Sequence> nextSequenceClass, Object... arguments) throws LionEngineException
    {
        renderer.end(nextSequenceClass, arguments);
    }

    @Override
    public final void end()
    {
        renderer.end();
    }

    @Override
    public final void addKeyListener(InputDeviceKeyListener listener)
    {
        renderer.addKeyListener(listener);
    }

    @Override
    public final void setSystemCursorVisible(boolean visible)
    {
        renderer.setSystemCursorVisible(visible);
    }

    @Override
    public final void setExtrapolated(boolean extrapolated)
    {
        renderer.setExtrapolated(extrapolated);
    }

    @Override
    public final <T extends InputDevice> T getInputDevice(Class<T> type) throws LionEngineException
    {
        return renderer.getInputDevice(type);
    }

    @Override
    public final Config getConfig()
    {
        return renderer.getConfig();
    }

    @Override
    public final int getFps()
    {
        return renderer.getFps();
    }

    @Override
    public final void setResolution(Resolution newSource) throws LionEngineException
    {
        renderer.setResolution(newSource);
    }
}
