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
package com.b3dgs.lionengine.mock;

import java.util.concurrent.atomic.AtomicBoolean;

import com.b3dgs.lionengine.core.Config;
import com.b3dgs.lionengine.core.InputDeviceKeyListener;
import com.b3dgs.lionengine.core.Resolution;
import com.b3dgs.lionengine.core.ScreenBase;
import com.b3dgs.lionengine.core.ScreenListener;

/**
 * Screen mock.
 */
public class ScreenMock extends ScreenBase
{
    /** Max ready time in millisecond. */
    public static final long READY_TIMEOUT = 100L;
    /** Wait for screen ready. */
    private static final AtomicBoolean READY = new AtomicBoolean(true);

    /**
     * Set the screen ready flag.
     * 
     * @param wait The wait flag.
     */
    public static void setScreenWait(boolean wait)
    {
        READY.set(!wait);
    }

    /**
     * Constructor.
     * 
     * @param config The config reference.
     */
    public ScreenMock(Config config)
    {
        super(config, READY_TIMEOUT);
    }

    /*
     * Screen
     */

    @Override
    public void preUpdate()
    {
        // Mock
    }

    @Override
    public void update()
    {
        for (final ScreenListener listener : listeners)
        {
            listener.notifyFocusGained();
            listener.notifyFocusLost();
        }
    }

    @Override
    public void dispose()
    {
        // Mock
    }

    @Override
    public void requestFocus()
    {
        // Mock
    }

    @Override
    public void hideCursor()
    {
        // Mock
    }

    @Override
    public void showCursor()
    {
        // Mock
    }

    @Override
    public void addKeyListener(InputDeviceKeyListener listener)
    {
        // Mock
    }

    @Override
    public void setIcon(String filename)
    {
        // Mock
    }

    @Override
    public int getX()
    {
        return 0;
    }

    @Override
    public int getY()
    {
        return 0;
    }

    @Override
    public boolean isReady()
    {
        return READY.get();
    }

    @Override
    public void onSourceChanged(Resolution source)
    {
        // Nothing to do
    }
}
