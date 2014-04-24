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

import java.awt.AWTError;
import java.awt.AWTException;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.DisplayMode;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.HeadlessException;
import java.awt.IllegalComponentStateException;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferStrategy;
import java.util.HashMap;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.WindowConstants;

import com.b3dgs.lionengine.Check;
import com.b3dgs.lionengine.Config;
import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Resolution;
import com.b3dgs.lionengine.Transparency;

/**
 * Screen implementation.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 * @see Keyboard
 * @see Mouse
 */
final class ScreenAwt
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
    private static final Cursor CURSOR_HIDDEN = ScreenAwt.createHiddenCursor();
    /** Default cursor instance. */
    private static final Cursor CURSOR_DEFAULT = Cursor.getDefaultCursor();

    /**
     * Create instance for hidden cursor.
     * 
     * @return The hidden cursor.
     */
    private static Cursor createHiddenCursor()
    {
        try
        {
            return FactoryGraphicAwt.createHiddenCursor();
        }
        catch (final AWTError
                     | HeadlessException
                     | IndexOutOfBoundsException exception)
        {
            return Cursor.getDefaultCursor();
        }
    }

    /** Renderer reference. */
    final Renderer renderer;
    /** Graphics device reference. */
    private final GraphicsDevice dev;
    /** Graphic configuration reference. */
    private final GraphicsConfiguration conf;
    /** Input devices. */
    private final HashMap<Class<? extends InputDevice>, InputDevice> devices;
    /** Frame reference. */
    private final JFrame frame;
    /** Applet reference. */
    private final AppletAwt applet;
    /** Active graphic buffer reference. */
    private final Graphic graphics;
    /** Applet flag. */
    private final boolean hasApplet;
    /** Configuration reference. */
    private final Config config;
    /** Active sequence reference. */
    private Sequence sequence;
    /** Buffer strategy reference. */
    private BufferStrategy buf;
    /** Image buffer reference. */
    private ImageBuffer buffer;
    /** Graphic buffer reference. */
    private Graphic gbuf;
    /** Component listener for keyboard inputs. */
    private Component componentForKeyboard;
    /** Component listener for mouse inputs. */
    private Component componentForMouse;
    /** Component listener for cursor. */
    private Component componentForCursor;
    /** Windowed canvas. */
    private Canvas canvas;
    /** Fullscreen window. */
    private java.awt.Window window;
    /** Width. */
    private int width;
    /** Height. */
    private int height;

    /**
     * Constructor.
     * 
     * @param renderer The renderer reference.
     * @param config The config reference.
     */
    ScreenAwt(Renderer renderer, Config config)
    {
        Check.notNull(config, ScreenAwt.ERROR_CONFIG);
        if (GraphicsEnvironment.isHeadless())
        {
            throw new LionEngineException(ScreenAwt.ERROR_DISPLAY);
        }

        // Initialize environment
        final GraphicsEnvironment env = GraphicsEnvironment.getLocalGraphicsEnvironment();
        dev = env.getDefaultScreenDevice();
        conf = dev.getDefaultConfiguration();
        applet = config.getApplet(AppletAwt.class);
        graphics = Core.GRAPHIC.createGraphic();
        hasApplet = applet != null;
        devices = new HashMap<>(2);
        this.renderer = renderer;
        this.config = config;

        // Prepare main frame
        frame = hasApplet ? null : initMainFrame();
        setResolution(config.getOutput());
        prepareFocusListener();
        addDeviceKeyboard();
        addDeviceMouse();
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
            Verbose.info("Transversal keys are not available !");
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
     */
    private void addDeviceMouse()
    {
        final Mouse mouse = new Mouse();
        addMouseListener(mouse);
        devices.put(mouse.getClass(), mouse);
    }

    /**
     * Prepare applet.
     * 
     * @param output The output resolution
     */
    private void initApplet(Resolution output)
    {
        try
        {
            buffer = Core.GRAPHIC.createImageBuffer(output.getWidth(), output.getHeight(), Transparency.OPAQUE);
            gbuf = buffer.createGraphic();
            graphics.setGraphic(gbuf);
            componentForKeyboard = applet;
            componentForMouse = applet;
            componentForCursor = applet;
            applet.validate();
        }
        catch (final Exception exception)
        {
            throw new LionEngineException(exception, ScreenAwt.ERROR_APPLET);
        }
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
            // Create canvas
            if (canvas == null)
            {
                canvas = new Canvas(conf);
                canvas.setBackground(Color.BLACK);
                canvas.setEnabled(true);
                canvas.setVisible(true);
                canvas.setIgnoreRepaint(true);

                // Add to main frame
                frame.add(canvas);
            }
            canvas.setPreferredSize(new Dimension(output.getWidth(), output.getHeight()));
            frame.pack();
            frame.setLocationRelativeTo(null);

            // Create buffer
            try
            {
                canvas.createBufferStrategy(2, conf.getBufferCapabilities());
            }
            catch (final AWTException exception)
            {
                canvas.createBufferStrategy(1);
            }
            buf = canvas.getBufferStrategy();

            // Set input listeners
            componentForKeyboard = canvas;
            componentForMouse = canvas;
            componentForCursor = frame;
            frame.validate();
        }
        catch (final Exception exception)
        {
            throw new LionEngineException(exception, ScreenAwt.ERROR_WINDOWED);
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
            // Create window
            if (window == null)
            {
                window = new java.awt.Window(frame, conf);
                window.setBackground(Color.BLACK);
                window.setIgnoreRepaint(true);

                // Set display
                frame.setUndecorated(true);
            }
            window.setPreferredSize(new Dimension(output.getWidth(), output.getHeight()));

            dev.setFullScreenWindow(window);
            final DisplayMode disp = new DisplayMode(output.getWidth(), output.getHeight(), depth, output.getRate());
            dev.setDisplayMode(disp);
            window.validate();

            // Create buffer
            try
            {
                window.createBufferStrategy(2, conf.getBufferCapabilities());
            }
            catch (final AWTException exception)
            {
                window.createBufferStrategy(1);
            }
            buf = window.getBufferStrategy();

            // Set input listeners
            componentForKeyboard = frame;
            componentForMouse = window;
            componentForCursor = window;
            frame.validate();
        }
        catch (final UnsupportedOperationException
                     | IllegalComponentStateException
                     | IllegalArgumentException exception)
        {
            final StringBuilder builder = new StringBuilder("Supported display mode:\n");
            int i = 0;
            for (final DisplayMode display : dev.getDisplayModes())
            {
                final StringBuilder widthSpace = new StringBuilder("");
                final int width = display.getWidth();
                if (width < 1000)
                {
                    widthSpace.append(" ");
                }
                final StringBuilder heightSpace = new StringBuilder("");
                final int height = display.getHeight();
                if (height < 1000)
                {
                    heightSpace.append(" ");
                }
                final StringBuilder freqSpace = new StringBuilder("");
                final int freq = display.getRefreshRate();
                if (freq < 100)
                {
                    freqSpace.append(" ");
                }
                builder.append("[").append(widthSpace).append(width).append("*").append(heightSpace).append(height)
                        .append("*").append(display.getBitDepth()).append(" @").append(freqSpace).append(freq)
                        .append("Hz] ");
                i++;
                if (i % 5 == 0)
                {
                    builder.append("\n");
                }
            }
            throw new LionEngineException(ScreenAwt.ERROR_UNSUPPORTED_FULLSCREEN, String.valueOf(output.getWidth()),
                    "*", String.valueOf(output.getHeight()), "*", String.valueOf(depth), " @", String.valueOf(output
                            .getRate()), "Hz", "\n", builder.toString());
        }
    }

    /**
     * Initialize the main frame.
     * 
     * @return The created main frame.
     */
    private JFrame initMainFrame()
    {
        final JFrame frame = new JFrame(EngineCore.getProgramName() + " " + EngineCore.getProgramVersion(), conf);

        frame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        frame.addWindowListener(new WindowAdapter()
        {
            @Override
            public void windowClosing(WindowEvent event)
            {
                renderer.end(null);
            }
        });
        frame.setResizable(false);
        frame.setIgnoreRepaint(true);

        return frame;
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
            Verbose.critical(Screen.class, "constructor", "Mouse focus listener can not be added !");
        }
    }

    /**
     * Set the screen config. Initialize the display.
     * 
     * @param output The output resolution
     */
    private void setResolution(Resolution output)
    {
        if (hasApplet)
        {
            initApplet(output);
        }
        else
        {
            if (config.isWindowed())
            {
                initWindowed(output);
            }
            else
            {
                initFullscreen(output, config.getDepth());
            }
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
            buf.show();
            graphics.setGraphic(buf.getDrawGraphics());
            frame.validate();
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
        if (hasApplet)
        {
            applet.getGraphics().drawImage(((ImageBufferAwt) buffer).getBuffer(), 0, 0, null);
            graphics.setGraphic(gbuf);
        }
        else
        {
            buf.show();
            graphics.setGraphic(buf.getDrawGraphics());
        }
    }

    @Override
    public void dispose()
    {
        graphics.clear(0, 0, width, height);
        update();
        if (hasApplet)
        {
            applet.destroy();
        }
        else
        {
            buf.dispose();
            frame.dispose();
        }
    }

    @Override
    public void requestFocus()
    {
        if (hasApplet)
        {
            applet.requestFocus();
            applet.validate();
        }
        else
        {
            frame.requestFocus();
        }
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
    public void setIcon(String filename)
    {
        final ImageIcon icon = new ImageIcon(filename);
        frame.setIconImage(icon.getImage());
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
    public <T extends InputDevice> T getInputDevice(Class<T> type)
    {
        return type.cast(devices.get(type));
    }

    @Override
    public int getX()
    {
        try
        {
            if (hasApplet)
            {
                return (int) applet.getLocationOnScreen().getX();
            }
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
            if (hasApplet)
            {
                return (int) applet.getLocationOnScreen().getY();
            }
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
