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
package com.b3dgs.lionengine.headless.graphic;

import com.b3dgs.lionengine.Check;
import com.b3dgs.lionengine.Config;
import com.b3dgs.lionengine.InputDeviceKeyListener;
import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Resolution;
import com.b3dgs.lionengine.graphic.ScreenAbstract;
import com.b3dgs.lionengine.graphic.ScreenListener;
import com.b3dgs.lionengine.graphic.Transparency;
import com.b3dgs.lionengine.headless.Keyboard;
import com.b3dgs.lionengine.headless.KeyboardHeadless;
import com.b3dgs.lionengine.headless.Mouse;
import com.b3dgs.lionengine.headless.MouseHeadless;
import com.b3dgs.lionengine.io.InputDeviceDirectional;
import com.b3dgs.lionengine.io.InputDevicePointer;

/**
 * Screen base implementation.
 * 
 * @see Keyboard
 * @see Mouse
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
    protected void setResolution(Resolution output)
    {
        Check.notNull(output);

        width = output.getWidth();
        height = output.getHeight();
    }

    /**
     * Add a keyboard device.
     */
    private void addDeviceKeyboard()
    {
        final KeyboardHeadless keyboard = new KeyboardHeadless();
        devices.put(Keyboard.class, keyboard);
        devices.put(InputDeviceDirectional.class, keyboard);
    }

    /**
     * Add a keyboard device.
     */
    private void addDeviceMouse()
    {
        final MouseHeadless mouse = new MouseHeadless();
        devices.put(Mouse.class, mouse);
        devices.put(InputDevicePointer.class, mouse);
    }

    /*
     * Screen
     */

    @Override
    public void start()
    {
        super.start();
        setResolution(config.getOutput());
        addDeviceKeyboard();
        addDeviceMouse();
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
    public void setIcon(String filename)
    {
        // Nothing to do
    }
}
