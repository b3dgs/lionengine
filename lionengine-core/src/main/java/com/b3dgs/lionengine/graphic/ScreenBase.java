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
package com.b3dgs.lionengine.graphic;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedQueue;

import com.b3dgs.lionengine.Check;
import com.b3dgs.lionengine.Config;
import com.b3dgs.lionengine.Constant;
import com.b3dgs.lionengine.InputDevice;
import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.Timing;

/**
 * Screen base implementation.
 */
public abstract class ScreenBase implements Screen
{
    /** Error screen ready. */
    private static final String ERROR_SCREEN_READY = "Unable to get screen ready !";

    /** Screen listeners. */
    protected final Collection<ScreenListener> listeners = new ConcurrentLinkedQueue<ScreenListener>();
    /** Input devices. */
    protected final Map<Class<? extends InputDevice>, InputDevice> devices;
    /** Configuration reference. */
    protected final Config config;
    /** Active graphic buffer reference. */
    protected final Graphic graphics;
    /** Ready timeout. */
    private final long readyTimeout;

    /**
     * Create the screen.
     * 
     * @param config The config reference.
     * @param readyTimeout The time out before considering screen never ready.
     * @throws LionEngineException If <code>null</code> config.
     */
    protected ScreenBase(Config config, long readyTimeout)
    {
        Check.notNull(config);

        this.config = config;
        this.readyTimeout = readyTimeout;
        devices = new HashMap<Class<? extends InputDevice>, InputDevice>(1);
        graphics = Graphics.createGraphic();
    }

    /*
     * Screen
     */

    /**
     * {@inheritDoc}
     * Set the icon if has.
     */
    @Override
    public void start()
    {
        final Media icon = config.getIcon();
        if (icon != null && icon.getFile().exists())
        {
            setIcon(icon.getFile().getPath());
        }
    }

    @Override
    public final void awaitReady()
    {
        final Timing timeout = new Timing();
        timeout.start();
        while (!isReady())
        {
            if (timeout.elapsed(getReadyTimeOut()))
            {
                throw new LionEngineException(ERROR_SCREEN_READY);
            }
            try
            {
                Thread.sleep(Constant.DECADE);
            }
            catch (final InterruptedException exception)
            {
                Thread.currentThread().interrupt();
                throw new LionEngineException(exception, ERROR_SCREEN_READY);
            }
        }
    }

    @Override
    public final void addListener(ScreenListener listener)
    {
        listeners.add(listener);
    }

    @Override
    public final void removeListener(ScreenListener listener)
    {
        listeners.remove(listener);
    }

    @Override
    public final Graphic getGraphic()
    {
        return graphics;
    }

    @Override
    public final Config getConfig()
    {
        return config;
    }

    @Override
    public final <T extends InputDevice> T getInputDevice(Class<T> type)
    {
        return type.cast(devices.get(type));
    }

    @Override
    public final long getReadyTimeOut()
    {
        return readyTimeout;
    }
}
