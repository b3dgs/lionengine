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
package com.b3dgs.lionengine.awt.graphic;

import java.awt.Component;
import java.awt.Cursor;
import java.awt.GraphicsEnvironment;
import java.awt.IllegalComponentStateException;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.image.BufferStrategy;

import com.b3dgs.lionengine.Check;
import com.b3dgs.lionengine.Config;
import com.b3dgs.lionengine.InputDeviceKeyListener;
import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Resolution;
import com.b3dgs.lionengine.Verbose;
import com.b3dgs.lionengine.awt.Keyboard;
import com.b3dgs.lionengine.awt.KeyboardAwt;
import com.b3dgs.lionengine.awt.KeyboardAwtListener;
import com.b3dgs.lionengine.awt.Mouse;
import com.b3dgs.lionengine.awt.MouseAwt;
import com.b3dgs.lionengine.graphic.ScreenAbstract;
import com.b3dgs.lionengine.graphic.ScreenListener;
import com.b3dgs.lionengine.io.InputDeviceDirectional;
import com.b3dgs.lionengine.io.InputDevicePointer;

/**
 * Screen base implementation.
 * 
 * @see Keyboard
 * @see Mouse
 */
abstract class ScreenAwtAbstract extends ScreenAbstract implements FocusListener
{
    /** Error message display. */
    private static final String ERROR_DISPLAY = "No available display !";
    /** Hidden cursor instance. */
    private static final Cursor CURSOR_HIDDEN = ToolsAwt.createHiddenCursor();
    /** Default cursor instance. */
    private static final Cursor CURSOR_DEFAULT = Cursor.getDefaultCursor();
    /** Max ready time in millisecond. */
    private static final long READY_TIMEOUT = 5000L;

    /** Buffer strategy reference. */
    protected BufferStrategy buf;
    /** Component listener for keyboard inputs. */
    protected Component componentForKeyboard;
    /** Component listener for mouse inputs. */
    protected Component componentForMouse;
    /** Component listener for cursor. */
    protected Component componentForCursor;
    /** Width. */
    private int width;
    /** Height. */
    private int height;

    /**
     * Constructor base.
     * 
     * @param config The config reference (must not be <code>null</code>).
     * @throws LionEngineException If renderer is <code>null</code> or no available display.
     */
    protected ScreenAwtAbstract(Config config)
    {
        super(config, READY_TIMEOUT);

        if (GraphicsEnvironment.isHeadless())
        {
            throw new LionEngineException(ScreenAwtAbstract.ERROR_DISPLAY);
        }
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
     * Link keyboard to the screen (listening to).
     * 
     * @param keyboard The keyboard reference.
     */
    private void addKeyboardListener(KeyboardAwt keyboard)
    {
        componentForKeyboard.addKeyListener(keyboard);
        componentForKeyboard.requestFocus();
        componentForKeyboard.setFocusTraversalKeysEnabled(false);
    }

    /**
     * Link keyboard to the screen (listening to).
     * 
     * @param mouse The mouse reference.
     */
    private void addMouseListener(MouseAwt mouse)
    {
        componentForMouse.addMouseListener(mouse.getClicker());
        componentForMouse.addMouseMotionListener(mouse.getMover());
        componentForMouse.requestFocus();
    }

    /**
     * Add a keyboard device.
     */
    private void addDeviceKeyboard()
    {
        final KeyboardAwt keyboard = new KeyboardAwt();
        addKeyboardListener(keyboard);
        devices.put(Keyboard.class, keyboard);
        devices.put(InputDeviceDirectional.class, keyboard);
    }

    /**
     * Add a keyboard device.
     */
    private void addDeviceMouse()
    {
        final MouseAwt mouse = new MouseAwt();
        addMouseListener(mouse);
        devices.put(Mouse.class, mouse);
        devices.put(InputDevicePointer.class, mouse);
    }

    /**
     * Prepare the focus listener.
     */
    private void prepareFocusListener()
    {
        componentForMouse.addFocusListener(this);
    }

    /*
     * Screen
     */

    @Override
    public void start()
    {
        super.start();
        setResolution(config.getOutput());
        prepareFocusListener();
        addDeviceKeyboard();
        addDeviceMouse();
        if (!config.hasApplet())
        {
            buf.show();
            graphics.setGraphic(buf.getDrawGraphics());
        }
    }

    @Override
    public void preUpdate()
    {
        // Nothing to do
    }

    @Override
    public void update()
    {
        buf.show();
        graphics.setGraphic(buf.getDrawGraphics());
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
        componentForMouse.requestFocus();
    }

    @Override
    public void hideCursor()
    {
        componentForCursor.setCursor(ScreenAwtAbstract.CURSOR_HIDDEN);
    }

    @Override
    public void showCursor()
    {
        componentForCursor.setCursor(ScreenAwtAbstract.CURSOR_DEFAULT);
    }

    @Override
    public void addKeyListener(InputDeviceKeyListener listener)
    {
        componentForKeyboard.addKeyListener(new KeyboardAwtListener(listener));
    }

    @Override
    public int getX()
    {
        try
        {
            return (int) componentForMouse.getLocationOnScreen().getX();
        }
        catch (final IllegalComponentStateException exception)
        {
            Verbose.exception(exception);
            return 0;
        }
    }

    @Override
    public int getY()
    {
        try
        {
            return (int) componentForMouse.getLocationOnScreen().getY();
        }
        catch (final IllegalComponentStateException exception)
        {
            Verbose.exception(exception);
            return 0;
        }
    }

    @Override
    public boolean isReady()
    {
        return buf != null;
    }

    @Override
    public void onSourceChanged(Resolution source)
    {
        ((MouseAwt) getInputDevice(Mouse.class)).setResolution(config.getOutput(), source);
    }

    /*
     * FocusListener
     */

    @Override
    public void focusGained(FocusEvent event)
    {
        listeners.forEach(ScreenListener::notifyFocusGained);
    }

    @Override
    public void focusLost(FocusEvent event)
    {
        listeners.forEach(ScreenListener::notifyFocusLost);
    }
}
