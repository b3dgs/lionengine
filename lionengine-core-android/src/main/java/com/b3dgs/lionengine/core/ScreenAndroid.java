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
package com.b3dgs.lionengine.core;

import android.graphics.Canvas;
import android.graphics.Color;
import android.view.SurfaceHolder;

import com.b3dgs.lionengine.Config;
import com.b3dgs.lionengine.InputDeviceKeyListener;
import com.b3dgs.lionengine.Resolution;
import com.b3dgs.lionengine.geom.Rectangle;
import com.b3dgs.lionengine.graphic.ScreenBase;
import com.b3dgs.lionengine.io.InputDevicePointer;
import com.b3dgs.lionengine.io.Mouse;

/**
 * Screen implementation.
 */
public final class ScreenAndroid extends ScreenBase implements SurfaceHolder.Callback
{
    /** Max ready time in millisecond. */
    private static final long READY_TIMEOUT = 5000L;
    /** View. */
    private static volatile ViewAndroid view;
    /** Holder. */
    private static volatile SurfaceHolder holder;
    /** Display size. */
    private static volatile Rectangle size;

    /**
     * Set the view holder.
     * 
     * @param view The view holder.
     * @param size The display size.
     */
    public static synchronized void setView(ViewAndroid view, Rectangle size)
    {
        ScreenAndroid.view = view;
        ScreenAndroid.size = size;
        holder = view.getHolder();
    }

    /** Windowed canvas. */
    private volatile Canvas canvas;
    /** Ready flag. */
    private volatile boolean ready;

    /**
     * Internal constructor.
     * 
     * @param config The config reference.
     */
    ScreenAndroid(Config config)
    {
        super(config, READY_TIMEOUT);

        setResolution(config.getOutput());
        addDeviceMouse();
    }

    /**
     * Add a keyboard device.
     */
    private void addDeviceMouse()
    {
        final MouseAndroid mouse = new MouseAndroid();
        view.setMouse(mouse);
        devices.put(Mouse.class, mouse);
        devices.put(InputDevicePointer.class, mouse);
    }

    /**
     * Prepare windowed mode.
     * 
     * @param output The output resolution
     */
    private void init(Resolution output)
    {
        if (canvas == null)
        {
            holder.setFixedSize(output.getWidth(), output.getHeight());
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
        super.start();
        holder.addCallback(this);
    }

    @Override
    public void preUpdate()
    {
        canvas = holder.lockCanvas();
        graphics.setGraphic(canvas);
    }

    @Override
    public void update()
    {
        holder.unlockCanvasAndPost(canvas);
    }

    @Override
    public void dispose()
    {
        holder.removeCallback(this);
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
    public void setIcon(String filename)
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
        ((MouseAndroid) getInputDevice(Mouse.class)).setConfig(size.getWidth(), size.getHeight(), config);
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
