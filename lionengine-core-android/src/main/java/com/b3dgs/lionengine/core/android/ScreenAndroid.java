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
package com.b3dgs.lionengine.core.android;

import java.util.HashMap;

import android.graphics.Canvas;
import android.graphics.Color;
import android.inputmethodservice.Keyboard;
import android.view.SurfaceHolder;

import com.b3dgs.lionengine.Check;
import com.b3dgs.lionengine.Config;
import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Resolution;
import com.b3dgs.lionengine.core.Core;
import com.b3dgs.lionengine.core.Graphic;
import com.b3dgs.lionengine.core.InputDevice;
import com.b3dgs.lionengine.core.InputDeviceKeyListener;
import com.b3dgs.lionengine.core.Renderer;
import com.b3dgs.lionengine.core.Screen;
import com.b3dgs.lionengine.core.Sequence;

/**
 * Screen implementation.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 * @see Keyboard
 * @see Mouse
 */
public final class ScreenAndroid
        implements Screen, SurfaceHolder.Callback
{
    /** View. */
    static ViewAndroid view;
    /** Holder. */
    static SurfaceHolder holder;

    /**
     * Set the view holder.
     * 
     * @param view The view holder.
     */
    public static void setView(ViewAndroid view)
    {
        ScreenAndroid.view = view;
        ScreenAndroid.holder = view.getHolder();
    }

    /** Input devices. */
    private final HashMap<Class<? extends InputDevice>, InputDevice> devices;
    /** Active graphic buffer reference. */
    private final Graphic graphics;
    /** Configuration reference. */
    private final Config config;
    /** Active sequence reference. */
    Sequence sequence;
    /** Windowed canvas. */
    private Canvas canvas;
    /** Ready flag. */
    private boolean ready;

    /**
     * Internal constructor.
     * 
     * @param renderer The renderer reference.
     */
    ScreenAndroid(Renderer renderer)
    {
        Check.notNull(renderer);

        config = renderer.getConfig();
        devices = new HashMap<Class<? extends InputDevice>, InputDevice>(1);
        graphics = Core.GRAPHIC.createGraphic();

        setResolution(config.getOutput());
        ScreenAndroid.holder.addCallback(this);
        addDeviceMouse();
    }

    /**
     * Add a keyboard device.
     */
    private void addDeviceMouse()
    {
        final Mouse mouse = new Mouse();
        ScreenAndroid.view.setMouse(mouse);
        devices.put(mouse.getClass(), mouse);
    }

    /**
     * Prepare windowed mode.
     * 
     * @param output The output resolution
     */
    private void init(Resolution output)
    {
        // Create canvas
        if (canvas == null)
        {
            ScreenAndroid.holder.setFixedSize(output.getWidth(), output.getHeight());
            canvas = new Canvas();
            canvas.drawColor(Color.RED);
            graphics.setGraphic(canvas);
        }
    }

    /**
     * Set the screen config. Initialize the display.
     * 
     * @param output The output resolution
     */
    private void setResolution(Resolution output)
    {
        init(output);
    }

    /*
     * Screen
     */

    @Override
    public void start()
    {
        ready = false;
    }

    @Override
    public void preUpdate()
    {
        canvas = ScreenAndroid.holder.lockCanvas();
        graphics.setGraphic(canvas);
    }

    @Override
    public void update()
    {
        ScreenAndroid.holder.unlockCanvasAndPost(canvas);
    }

    @Override
    public void dispose()
    {
        ScreenAndroid.holder.removeCallback(this);
    }

    @Override
    public void requestFocus()
    {
        // Nothing to do
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
    public void setSequence(Sequence sequence)
    {
        this.sequence = sequence;
    }

    @Override
    public void setIcon(String filename)
    {
        // Nothing to do
    }

    @Override
    public Graphic getGraphic()
    {
        return graphics;
    }

    @Override
    public Config getConfig()
    {
        return config;
    }

    @Override
    public <T extends InputDevice> T getInputDevice(Class<T> type) throws LionEngineException
    {
        return null;
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

    /*
     * SurfaceHolder.Callback
     */

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height)
    {
        ready = true;
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder)
    {
        ready = true;
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder)
    {
        ready = false;
    }
}
