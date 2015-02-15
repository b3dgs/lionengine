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
package com.b3dgs.lionengine.core.awt;

import java.awt.Component;
import java.awt.Cursor;
import java.awt.GraphicsEnvironment;
import java.awt.IllegalComponentStateException;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.image.BufferStrategy;
import java.util.HashMap;

import com.b3dgs.lionengine.Check;
import com.b3dgs.lionengine.Config;
import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Resolution;
import com.b3dgs.lionengine.core.Core;
import com.b3dgs.lionengine.core.Graphic;
import com.b3dgs.lionengine.core.InputDevice;
import com.b3dgs.lionengine.core.InputDeviceKeyListener;
import com.b3dgs.lionengine.core.Media;
import com.b3dgs.lionengine.core.Renderer;
import com.b3dgs.lionengine.core.Screen;
import com.b3dgs.lionengine.core.Sequence;
import com.b3dgs.lionengine.core.Verbose;

/**
 * Screen base implementation.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 * @see Keyboard
 * @see Mouse
 */
abstract class ScreenAwt
        implements Screen, FocusListener
{
    /** Error message display. */
    private static final String ERROR_DISPLAY = "No available display !";
    /** Error transversal keys. */
    private static final String ERROR_TRANSVERSAL_KEYS = "Transversal keys are not available !";
    /** Error mouse focus listener. */
    private static final String ERROR_MOUSE_FOCUS_LISTENER = "Mouse focus listener can not be added !";
    /** Hidden cursor instance. */
    private static final Cursor CURSOR_HIDDEN = ToolsAwt.createHiddenCursor();
    /** Default cursor instance. */
    private static final Cursor CURSOR_DEFAULT = Cursor.getDefaultCursor();

    /**
     * Create a screen depending of the configuration.
     * 
     * @param renderer The renderer reference.
     * @return The created screen.
     * @throws LionEngineException If resolution is not supported.
     */
    static Screen createScreen(Renderer renderer) throws LionEngineException
    {
        final Config config = renderer.getConfig();
        if (config.getApplet(AppletAwt.class) != null)
        {
            return new ScreenAppletAwt(renderer);
        }
        if (config.isWindowed())
        {
            return new ScreenWindowedAwt(renderer);
        }
        return new ScreenFullAwt(renderer);
    }

    /** Active graphic buffer reference. */
    protected final Graphic graphics;
    /** Configuration reference. */
    protected final Config config;
    /** Input devices. */
    private final HashMap<Class<? extends InputDevice>, InputDevice> devices;
    /** Buffer strategy reference. */
    protected BufferStrategy buf;
    /** Component listener for keyboard inputs. */
    protected Component componentForKeyboard;
    /** Component listener for mouse inputs. */
    protected Component componentForMouse;
    /** Component listener for cursor. */
    protected Component componentForCursor;
    /** Active sequence reference. */
    private Sequence sequence;
    /** Width. */
    private int width;
    /** Height. */
    private int height;

    /**
     * Constructor base.
     * 
     * @param renderer The renderer reference.
     * @throws LionEngineException If renderer is <code>null</code> or no available display.
     */
    protected ScreenAwt(Renderer renderer) throws LionEngineException
    {
        Check.notNull(renderer);
        if (GraphicsEnvironment.isHeadless())
        {
            throw new LionEngineException(ScreenAwt.ERROR_DISPLAY);
        }
        config = renderer.getConfig();
        graphics = Core.GRAPHIC.createGraphic();
        devices = new HashMap<>(2);
    }

    /**
     * Set the screen config. Initialize the display.
     * 
     * @param output The output resolution
     * @throws LionEngineException If resolution is not supported.
     */
    protected void setResolution(Resolution output) throws LionEngineException
    {
        width = output.getWidth();
        height = output.getHeight();
    }

    /**
     * Link keyboard to the screen (listening to).
     * 
     * @param keyboard The keyboard reference.
     */
    private void addKeyboardListener(Keyboard keyboard)
    {
        componentForKeyboard.addKeyListener(keyboard);
        componentForKeyboard.requestFocus();
        try
        {
            componentForKeyboard.setFocusTraversalKeysEnabled(false);
        }
        catch (final Exception exception)
        {
            Verbose.info(ScreenAwt.ERROR_TRANSVERSAL_KEYS);
        }
    }

    /**
     * Link keyboard to the screen (listening to).
     * 
     * @param mouse The mouse reference.
     */
    private void addMouseListener(Mouse mouse)
    {
        componentForMouse.addMouseListener(mouse);
        componentForMouse.addMouseMotionListener(mouse);
        componentForMouse.addMouseWheelListener(mouse);
        componentForMouse.requestFocus();
    }

    /**
     * Add a keyboard device.
     */
    private void addDeviceKeyboard()
    {
        final Keyboard keyboard = new Keyboard();
        addKeyboardListener(keyboard);
        devices.put(keyboard.getClass(), keyboard);
    }

    /**
     * Add a keyboard device.
     * 
     * @throws LionEngineException If mouse is not supported.
     */
    private void addDeviceMouse() throws LionEngineException
    {
        final Mouse mouse = new Mouse();
        addMouseListener(mouse);
        devices.put(mouse.getClass(), mouse);
    }

    /**
     * Prepare the focus listener.
     */
    private void prepareFocusListener()
    {
        try
        {
            componentForMouse.addFocusListener(this);
        }
        catch (final Exception exception)
        {
            Verbose.critical(Screen.class, "constructor", ScreenAwt.ERROR_MOUSE_FOCUS_LISTENER);
        }
    }

    /*
     * Screen
     */

    @Override
    public void start()
    {
        setResolution(config.getOutput());
        prepareFocusListener();
        addDeviceKeyboard();
        try
        {
            addDeviceMouse();
        }
        catch (final LionEngineException exception)
        {
            Verbose.exception(ScreenAwt.class, "start", exception);
        }
        if (!config.hasApplet())
        {
            buf.show();
            graphics.setGraphic(buf.getDrawGraphics());
        }
        final Media icon = config.getIcon();
        if (icon != null)
        {
            setIcon(icon.getPath());
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
        componentForCursor.setCursor(ScreenAwt.CURSOR_HIDDEN);
    }

    @Override
    public void showCursor()
    {
        componentForCursor.setCursor(ScreenAwt.CURSOR_DEFAULT);
    }

    @Override
    public void addKeyListener(InputDeviceKeyListener listener)
    {
        componentForKeyboard.addKeyListener(new KeyListener(listener));
    }

    @Override
    public void setSequence(Sequence sequence)
    {
        this.sequence = sequence;
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
        return type.cast(devices.get(type));
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
            return 0;
        }
    }

    @Override
    public boolean isReady()
    {
        return buf != null;
    }

    /*
     * FocusListener
     */

    @Override
    public void focusGained(FocusEvent event)
    {
        if (sequence != null)
        {
            sequence.onFocusGained();
        }
    }

    @Override
    public void focusLost(FocusEvent event)
    {
        if (sequence != null)
        {
            sequence.onLostFocus();
        }
    }
}
