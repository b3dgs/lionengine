/*
 * Copyright (C) 2013-2021 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
 *
 * This program is free software: you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
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
package com.b3dgs.lionengine.android.graphic;

import android.graphics.Canvas;
import android.os.Build;
import android.view.SurfaceHolder;

import com.b3dgs.lionengine.Config;
import com.b3dgs.lionengine.InputDeviceKeyListener;
import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.Resolution;
import com.b3dgs.lionengine.android.Gamepad;
import com.b3dgs.lionengine.android.Mouse;
import com.b3dgs.lionengine.android.VirtualDeviceButton;
import com.b3dgs.lionengine.geom.Rectangle;
import com.b3dgs.lionengine.graphic.ScreenAbstract;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.function.Supplier;

/**
 * Screen implementation.
 */
public final class ScreenAndroid extends ScreenAbstract implements SurfaceHolder.Callback
{
    /** Max ready time in millisecond. */
    private static final long READY_TIMEOUT = 5000L;
    /** Logger. */
    private static final Logger LOGGER = LoggerFactory.getLogger(ScreenAndroid.class);
    /** View. */
    private static volatile ViewAndroid view;
    /** Holder. */
    private static volatile SurfaceHolder holder;
    /** Display size. */
    private static volatile Rectangle size;
    /** Virtual keyboard. */
    private static volatile Class<? extends VirtualDeviceButton> keyboardType;

    /**
     * Set custom virtual keyboard.
     * @param type The virtual keyboard type.
     */
    public static synchronized void setVirtualKeyboard(Class<? extends VirtualDeviceButton> type)
    {
        ScreenAndroid.keyboardType = type;
    }

    /**
     * Set the view holder.
     *
     * @param view The view holder.
     * @param size The display size.
     */
    static synchronized void setView(ViewAndroid view, Rectangle size)
    {
        ScreenAndroid.view = view;
        ScreenAndroid.size = size;
        holder = view.getHolder();
    }

    /**
     * Create virtual keyboard.
     * @param mouse The mouse reference.
     * @return The created virtual keyboard.
     */
    private static VirtualDeviceButton createKeyboard(Mouse mouse)
    {
        if (keyboardType == null)
        {
            return new VirtualDeviceButton(mouse);
        }
        else
        {
            try
            {
                return keyboardType.getConstructor(Mouse.class).newInstance(mouse);
            }
            catch (ReflectiveOperationException exception)
            {
                LOGGER.error("createKeyboard error", exception);
                return new VirtualDeviceButton(mouse);
            }
        }
    }

    /** Virtual keyboard. */
    private final VirtualDeviceButton keyboard;
    /** Mouse. */
    private final MouseAndroid mouse;
    /** Windowed canvas. */
    private volatile Canvas canvas;
    /** Ready flag. */
    private volatile boolean ready;
    /** Canvas getter. */
    private Supplier<Canvas> draw;

    /**
     * Internal constructor.
     *
     * @param config The config reference.
     */
    ScreenAndroid(Config config)
    {
        super(config, READY_TIMEOUT);

        mouse = view.getMouse();
        devices.put(Mouse.class, mouse);

        keyboard = createKeyboard(mouse);
        devices.put(VirtualDeviceButton.class, keyboard);

        view.getGamepad().setOnUsed(() -> keyboard.setEnabled(false));
        devices.put(Gamepad.class, view.getGamepad());

        holder.setFixedSize(config.getOutput().getWidth(), config.getOutput().getHeight());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {
            draw = holder::lockHardwareCanvas;
        }
        else
        {
            draw = holder::lockCanvas;
        }
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
        canvas = draw.get();
        if (canvas != null)
        {
            graphics.setGraphic(canvas);
        }
    }

    @Override
    public void update()
    {
        keyboard.update(1.0);
        keyboard.render(getGraphic());
        mouse.update(1.0);
        try
        {
            holder.unlockCanvasAndPost(canvas);
        }
        catch (@SuppressWarnings("unused") final IllegalStateException exception)
        {
            // Skip
        }
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
    public void setIcons(Collection<Media> icons)
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
    public int getWidth() {
        return holder.getSurfaceFrame().width();
    }

    @Override
    public int getHeight() {
        return holder.getSurfaceFrame().height();
    }

    @Override
    public boolean isReady()
    {
        return ready;
    }

    @Override
    public void onSourceChanged(Resolution source)
    {
        ((MouseAndroid) getInputDevice(Mouse.class)).setConfig(size.getWidth(), size.getHeight(), source);
        keyboard.setScale(config.getOutput().getHeight() / (double) source.getHeight());
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
