/*
 * Copyright (C) 2013-2023 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.headless.graphic;

import java.util.Collection;

import com.b3dgs.lionengine.Check;
import com.b3dgs.lionengine.Config;
import com.b3dgs.lionengine.InputDeviceKeyListener;
import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.Resolution;
import com.b3dgs.lionengine.graphic.ScreenAbstract;
import com.b3dgs.lionengine.graphic.ScreenListener;
import com.b3dgs.lionengine.graphic.Transparency;

/**
 * Screen base implementation.
 */
final class ScreenHeadless extends ScreenAbstract
{
    /** Max ready time in millisecond. */
    private static final long READY_TIMEOUT = 5000L;

    /** Width. */
    private int width;
    /** Height. */
    private int height;
    /** Ready. */
    private boolean ready;

    /**
     * Constructor base.
     * 
     * @param config The config reference (must not be <code>null</code>).
     * @throws LionEngineException If renderer is <code>null</code> or no available display.
     */
    ScreenHeadless(Config config)
    {
        super(config, READY_TIMEOUT);
    }

    /**
     * Set the screen config. Initialize the display.
     * 
     * @param output The output resolution (must not be <code>null</code>).
     * @throws LionEngineException If resolution is not supported.
     */
    private void setResolution(Resolution output)
    {
        Check.notNull(output);

        width = output.getWidth();
        height = output.getHeight();
    }

    /*
     * Screen
     */

    @Override
    public void start()
    {
        super.start();
        setResolution(config.getOutput());
        graphics.setGraphic(new ImageBufferHeadless(width, height, Transparency.BITMASK));
        ready = true;
    }

    @Override
    public void preUpdate()
    {
        // Nothing to do
    }

    @Override
    public void update()
    {
        // Nothing to do
    }

    @Override
    public void dispose()
    {
        graphics.clear(0, 0, width, height);
        update();
    }

    @Override
    public void requestFocus()
    {
        listeners.forEach(ScreenListener::notifyFocusGained);
    }

    @Override
    public void hideCursor()
    {
        // Nothing to do
    }

    @Override
    public void showCursor()
    {
        // Nothing to do
    }

    @Override
    public void addKeyListener(InputDeviceKeyListener listener)
    {
        // Nothing to do
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
        return ready;
    }

    @Override
    public void onSourceChanged(Resolution source)
    {
        // Nothing to do
    }

    @Override
    public void setIcons(Collection<Media> icons)
    {
        // Nothing to do
    }
}
