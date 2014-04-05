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

import java.util.concurrent.Semaphore;

import com.b3dgs.lionengine.Graphic;
import com.b3dgs.lionengine.Resolution;

/**
 * Sequence class is used for each derived sequence, such as Introduction, Menu, Scene... It contains a reference to the
 * screen used, the current configuration, input references ({@link #getInputDevice(InputDeviceType)}), and it includes
 * a standard game loop ({@link Sequence#update(double)} and {@link Sequence#render(Graphic)}), synchronized to a
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
 * @see InputDevice
 */
public abstract class Sequence
{
    /** Async loaded semaphore. */
    final Semaphore loadedSemaphore;
    /** Native resolution. */
    final Resolution resolution;
    /** Loader reference. Must be used only to create new sequence by giving it as argument. */
    private final Loader loader;
    /** Renderer. */
    private final Renderer renderer;
    /** Rendering width. */
    private int width;
    /** Rendering height. */
    private int height;
    /** Loaded state. */
    private boolean loaded;

    /**
     * Constructor.
     * 
     * @param resolution The resolution source reference.
     * @param loader The loader reference.
     */
    public Sequence(Loader loader, Resolution resolution)
    {
        this.loader = loader;
        this.resolution = resolution;
        loadedSemaphore = new Semaphore(0);
        renderer = loader.renderer;
        renderer.getConfig().setSource(resolution);
        width = resolution.getWidth();
        height = resolution.getHeight();
    }

    /**
     * Start the next sequence and wait for current sequence to end before next sequence continues. This function should
     * be used to synchronize two sequences (eg: load a next sequence while being in a menu). Do not forget to call
     * {@link #end()} in order to give control to the next sequence. The next sequence should override
     * {@link #onLoaded(double, Graphic)} for special load just before enter in the loop.
     * 
     * @param wait <code>true</code> to wait for the next sequence to be loaded, <code>false</code> else.
     * @param nextSequence The next sequence reference (must not be <code>null</code>).
     * @param arguments The arguments list.
     */
    public final void start(boolean wait, Class<? extends Sequence> nextSequence, Object... arguments)
    {
        renderer.start(Loader.createSequence(nextSequence, loader, arguments), wait);
    }

    /**
     * Terminate sequence.
     */
    public final void end()
    {
        renderer.end();
    }

    /**
     * Terminate sequence, and set the next sequence.
     * 
     * @param nextSequence The next sequence reference.
     * @param arguments The sequence arguments list if needed by its constructor.
     */
    public final void end(Class<? extends Sequence> nextSequence, Object... arguments)
    {
        renderer.end(Loader.createSequence(nextSequence, loader, arguments));
    }

    /**
     * Add a key listener.
     * 
     * @param listener The listener to add.
     */
    public final void addKeyListener(InputDeviceKeyListener listener)
    {
        renderer.addKeyListener(listener);
    }

    /**
     * Set the system cursor visibility.
     * 
     * @param visible <code>true</code> if visible, <code>false</code> else.
     */
    public final void setSystemCursorVisible(boolean visible)
    {
        renderer.setSystemCursorVisible(visible);
    }

    /**
     * Set the extrapolation flag.
     * 
     * @param extrapolated <code>true</code> will activate it, <code>false</code> will disable it.
     */
    public final void setExtrapolated(boolean extrapolated)
    {
        renderer.setExtrapolated(extrapolated);
    }

    /**
     * Get the input device instance from its type.
     * 
     * @param <T> The input device.
     * @param type The input device type.
     * @return The input instance reference, <code>null</code> if not found.
     */
    public final <T extends InputDevice> T getInputDevice(InputDeviceType type)
    {
        return renderer.getInputDevice(type);
    }

    /**
     * Get the configuration.
     * 
     * @return The configuration.
     */
    public final Config getConfig()
    {
        return renderer.getConfig();
    }

    /**
     * Get the rendering width.
     * 
     * @return The rendering width.
     */
    public final int getWidth()
    {
        return width;
    }

    /**
     * Get the rendering height.
     * 
     * @return The rendering height.
     */
    public final int getHeight()
    {
        return height;
    }

    /**
     * Get current frame rate (number of image per second).
     * 
     * @return The current number of image per second.
     */
    public final int getFps()
    {
        return renderer.getFps();
    }

    /**
     * Set the new resolution used by the sequence.
     * 
     * @param newSource The new resolution used.
     */
    protected final void setResolution(Resolution newSource)
    {
        renderer.setResolution(newSource);
    }

    /**
     * Loading sequence data.
     */
    protected abstract void load();

    /**
     * Update sequence.
     * 
     * @param extrp The extrapolation value. Can be used to have an non dependent machine speed calculation.
     *            Example: x += 5.0 * extrp
     */
    protected abstract void update(double extrp);

    /**
     * Render sequence.
     * 
     * @param g The graphic output.
     */
    protected abstract void render(Graphic g);

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
     * {@link #start(boolean, Class, Object...)} has been used by another sequence. Does nothing by default.
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
     * Start the sequence and load it.
     */
    final void start()
    {
        if (!loaded)
        {
            load();
            loaded = true;
        }
    }

    /**
     * Load the sequence internally. Must only be called by {@link Renderer#asyncLoad(Sequence)} implementation in order
     * to synchronize loading process when it is called asynchronously.
     */
    final void loadInternal()
    {
        if (!loaded)
        {
            load();
            loaded = true;
            loadedSemaphore.release();
        }
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
    }
}
