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
package com.b3dgs.lionengine.core;

import java.util.HashMap;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import com.b3dgs.lionengine.Check;
import com.b3dgs.lionengine.Graphic;
import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Resolution;

/**
 * Screen implementation.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 * @see Keyboard
 * @see Mouse
 */
final class ScreenImpl
        implements Screen, FocusListener
{
    /** Error message config. */
    private static final String ERROR_CONFIG = "The configuration must exists !";
    /** Error message display. */
    private static final String ERROR_DISPLAY = "No available display !";
    /** Error message applet. */
    private static final String ERROR_APPLET = "Applet mode initialization failed !";
    /** Error message windowed. */
    private static final String ERROR_WINDOWED = "Windowed mode initialization failed !";
    /** Error message unsupported fullscreen. */
    private static final String ERROR_UNSUPPORTED_FULLSCREEN = "Unsupported fullscreen mode: ";

    /** Hidden cursor instance. */
    // private static final Cursor CURSOR_HIDDEN = ScreenImpl.createHiddenCursor();
    /** Default cursor instance. */
    // private static final Cursor CURSOR_DEFAULT = Display.getDefault().getSystemCursor(0);

    /**
     * Create instance for hidden cursor.
     * 
     * @return The hidden cursor.
     */
    private static Cursor createHiddenCursor()
    {
        return FactoryGraphicImpl.createHiddenCursor();
    }

    /** Display. */
    public static Display display;
    /** Frame reference. */
    private final Shell frame;
    /** Input devices. */
    private final HashMap<InputDeviceType, InputDevice> devices;
    /** Active graphic buffer reference. */
    private final Graphic graphics;
    /** Applet flag. */
    private final boolean hasApplet;
    /** Configuration reference. */
    private final Config config;
    /** Active sequence reference. */
    Sequence sequence;
    /** Buffer strategy reference. */
    private Canvas buf;
    /** Image buffer reference. */
    private ImageBuffer buffer;
    /** Graphic buffer reference. */
    private Graphic gbuf;
    /** Windowed canvas. */
    private Canvas canvas;
    /** Width. */
    private int width;
    /** Height. */
    private int height;
    GC last;

    Image image;

    /**
     * Constructor.
     * 
     * @param renderer The renderer reference.
     * @param config The config reference.
     */
    ScreenImpl(Renderer renderer, Config config)
    {
        Check.notNull(config, ScreenImpl.ERROR_CONFIG);

        // Initialize environment
        ScreenImpl.display = new Display();
        graphics = UtilityImage.createGraphic();
        devices = new HashMap<>(2);
        hasApplet = false;
        this.config = config;

        // Prepare main frame
        frame = hasApplet ? null : initMainFrame();
        setResolution(config.getOutput());
        prepareFocusListener();
        addDeviceKeyboard();
        addDeviceMouse();
    }

    /**
     * Add a keyboard device.
     */
    private void addDeviceKeyboard()
    {
        final Keyboard keyboard = new Keyboard();
        addKeyboardListener(keyboard);
        devices.put(keyboard.getType(), keyboard);
    }

    /**
     * Add a keyboard device.
     */
    private void addDeviceMouse()
    {
        final Mouse mouse = new Mouse(ScreenImpl.display);
        addMouseListener(mouse);
        devices.put(mouse.getType(), mouse);
    }

    /**
     * Prepare windowed mode.
     * 
     * @param output The output resolution
     */
    private void initWindowed(Resolution output)
    {
        try
        {
            if (canvas == null)
            {
                canvas = new Canvas(frame, SWT.DOUBLE_BUFFERED);
                canvas.setBackground(new Color(ScreenImpl.display, 0, 0, 0));
                canvas.setEnabled(true);
                canvas.setVisible(true);
            }
            canvas.setSize(output.getWidth(), output.getHeight());
            image = new Image(ScreenImpl.display, output.getWidth(), output.getHeight());
            frame.pack();

            buf = canvas;
        }
        catch (final Exception exception)
        {
            throw new LionEngineException(exception, ScreenImpl.ERROR_WINDOWED);
        }
    }

    /**
     * Prepare fullscreen mode.
     * 
     * @param output The output resolution
     * @param depth The bit depth color.
     */
    private void initFullscreen(Resolution output, int depth)
    {
        try
        {
            if (canvas == null)
            {
                canvas = new Canvas(frame, SWT.DOUBLE_BUFFERED);
                canvas.setEnabled(true);
                canvas.setVisible(true);
            }
            canvas.setSize(output.getWidth(), output.getHeight());
            frame.pack();

            buf = canvas;
            frame.setFullScreen(true);
        }
        catch (final Exception exception)
        {
            throw new LionEngineException(exception, ScreenImpl.ERROR_WINDOWED);
        }
    }

    /**
     * Initialize the main frame.
     * 
     * @return The created main frame.
     */
    private Shell initMainFrame()
    {
        final Shell shell = new Shell(ScreenImpl.display, SWT.DOUBLE_BUFFERED);

        return shell;
    }

    /**
     * Prepare the focus listener.
     */
    private void prepareFocusListener()
    {
        try
        {
            frame.addFocusListener(this);
        }
        catch (final Exception exception)
        {
            Verbose.critical(Screen.class, "constructor", "Mouse focus listener can not be added !");
        }
    }

    /**
     * Add keyboard.
     * 
     * @param keyboard The keyboard to add.
     */
    private void addKeyboardListener(Keyboard keyboard)
    {
        frame.addKeyListener(keyboard);
        frame.forceFocus();
    }

    /**
     * Add mouse.
     * 
     * @param mouse The mouse to add.
     */
    private void addMouseListener(Mouse mouse)
    {
        frame.addMouseListener(mouse);
        frame.addMouseMoveListener(mouse);
        frame.addMouseWheelListener(mouse);
        frame.forceFocus();
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
        else
        {
            initFullscreen(output, config.getDepth());
        }
        width = output.getWidth();
        height = output.getHeight();
    }

    /*
     * Screen
     */

    @Override
    public void start()
    {
        if (!hasApplet)
        {
            buf.setVisible(true);
            buf.update();
            last = new GC(image);
            graphics.setGraphic(last);
            frame.update();
            frame.setEnabled(true);
            frame.setVisible(true);
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
        ScreenImpl.display.readAndDispatch();
        final GC gc = new GC(canvas);
        gc.drawImage(image, 0, 0);
        gc.dispose();
        if (last != null)
        {
            last.dispose();
        }
        last = new GC(image);
        graphics.setGraphic(last);
    }

    @Override
    public void dispose()
    {
        graphics.clear(0, 0, width, height);
        update();
        buf.dispose();
        frame.dispose();
    }

    @Override
    public void requestFocus()
    {
        frame.forceFocus();
    }

    @Override
    public void hideCursor()
    {
        // frame.setCursor(ScreenImpl.CURSOR_HIDDEN);
    }

    @Override
    public void showCursor()
    {
        // frame.setCursor(ScreenImpl.CURSOR_DEFAULT);
    }

    @Override
    public void addKeyListener(InputDeviceKeyListener listener)
    {
        frame.addKeyListener(new KeyListener(listener));
    }

    @Override
    public void setSequence(Sequence sequence)
    {
        this.sequence = sequence;
    }

    @Override
    public void setIcon(String filename)
    {
        // final Image icon = new Image(Display.getDefault(), filename);
        // frame.setImage(icon);
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
    public <T extends InputDevice> T getInputDevice(InputDeviceType type)
    {
        return (T) devices.get(type);
    }

    @Override
    public int getX()
    {
        return frame.getLocation().x;
    }

    @Override
    public int getY()
    {
        return frame.getLocation().y;
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
