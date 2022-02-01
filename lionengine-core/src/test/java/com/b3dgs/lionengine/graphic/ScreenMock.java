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
package com.b3dgs.lionengine.graphic;

import java.util.Collection;
import java.util.concurrent.atomic.AtomicBoolean;

import com.b3dgs.lionengine.Config;
import com.b3dgs.lionengine.InputDeviceKeyListener;
import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.Resolution;

/**
 * Screen mock.
 */
public final class ScreenMock extends ScreenAbstract
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
    public void setIcons(Collection<Media> icons)
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
    public int getWidth()
    {
        return 0;
    }

    @Override
    public int getHeight()
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
