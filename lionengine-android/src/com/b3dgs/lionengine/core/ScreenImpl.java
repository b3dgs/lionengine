/*
 * Copyright (C) 2013 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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

import com.b3dgs.lionengine.Check;
import com.b3dgs.lionengine.Graphic;
import com.b3dgs.lionengine.Keyboard;
import com.b3dgs.lionengine.Mouse;
import com.b3dgs.lionengine.Resolution;

/**
 * Screen implementation.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 * @see Keyboard
 * @see Mouse
 */
public final class ScreenImpl
        implements Screen, SurfaceHolder.Callback
{
    /** Error message config. */
    private static final String ERROR_CONFIG = "The configuration must exists !";
    /** View. */
    private static SurfaceHolder view;

    /**
     * Set the view holder.
     * 
     * @param view The view holder.
     */
    public static void setView(SurfaceHolder view)
    {
        ScreenImpl.view = view;
    }

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
     * Constructor.
     * 
     * @param config The config reference.
     */
    ScreenImpl(Config config)
    {
        Check.notNull(config, ScreenImpl.ERROR_CONFIG);

        // Initialize environment
        graphics = UtilityImage.createGraphic();
        this.config = config;

        // Prepare main frame
        setResolution(config.getOutput());
        ScreenImpl.view.addCallback(this);
    }

    /**
     * Prepare windowed mode.
     * 
     * @param output The output resolution
     */
    private void initWindowed(Resolution output)
    {
        // Create canvas
        if (canvas == null)
        {
            ScreenImpl.view.setFixedSize(output.getWidth(), output.getHeight());
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
        if (config.isWindowed())
        {
            initWindowed(output);
        }
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
    public void show()
    {
        // Nothing to do
    }

    @Override
    public void preUpdate()
    {
        canvas = ScreenImpl.view.lockCanvas();
        graphics.setGraphic(canvas);
    }

    @Override
    public void update()
    {
        ScreenImpl.view.unlockCanvasAndPost(canvas);
    }

    @Override
    public void dispose()
    {
        ScreenImpl.view.removeCallback(this);
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
    public void addKeyListener(KeyboardListener listener)
    {
        // Nothing to do
    }

    @Override
    public void addKeyboard(Keyboard keyboard)
    {
        // Nothing to do
    }

    @Override
    public void addMouse(Mouse mouse)
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
    public int getLocationX()
    {
        return 0;
    }

    @Override
    public int getLocationY()
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
        // Nothing to do
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
