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
import org.eclipse.swt.SWTException;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Monitor;
import org.eclipse.swt.widgets.Shell;

import com.b3dgs.lionengine.Check;
import com.b3dgs.lionengine.Graphic;
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
final class ScreenSwt
        implements Screen, FocusListener
{
    /** Error message config. */
    private static final String ERROR_CONFIG = "The configuration must exists !";
    /** Error message display. */
    private static final String ERROR_DISPLAY = "No available display !";
    /** Error message windowed. */
    private static final String ERROR_WINDOWED = "Windowed mode initialization failed !";

    /** Display. */
    static Display display;
    /** Renderer reference. */
    final Renderer renderer;
    /** Hidden cursor instance. */
    private final Cursor cursorHidden;
    /** Default cursor instance. */
    private final Cursor cursorDefault;
    /** Input devices. */
    private final HashMap<Class<? extends InputDevice>, InputDevice> devices;
    /** Active graphic buffer reference. */
    private final Graphic graphics;
    /** Configuration reference. */
    private final Config config;
    /** Frame reference. */
    private final Shell frame;
    /** Active sequence reference. */
    private Sequence sequence;
    /** Buffer reference. */
    private Canvas buf;
    /** Image buffer reference. */
    private ImageBuffer buffer;
    /** Graphic buffer reference. */
    private Graphic gbuf;
    /** Windowed canvas. */
    private Canvas canvas;
    /** Last GC used. */
    private GC lastGc;
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
    ScreenSwt(Renderer renderer, Config config)
    {
        Check.notNull(config, ScreenSwt.ERROR_CONFIG);

        // Initialize environment
        try
        {
            ScreenSwt.display = new Display();
        }
        catch (final SWTException exception)
        {
            throw new LionEngineException(exception, ScreenSwt.ERROR_DISPLAY);
        }
        this.renderer = renderer;
        this.config = config;

        cursorHidden = FactoryGraphicSwt.createHiddenCursor();
        cursorDefault = ScreenSwt.display.getSystemCursor(0);
        graphics = UtilityImage.createGraphic();
        devices = new HashMap<>(2);

        // Prepare main frame
        frame = initMainFrame(config.isWindowed());
        setResolution(config.getOutput());
        prepareFocusListener();
        addDeviceKeyboard();
        addDeviceMouse();
    }

    /**
     * Initialize the main frame.
     * 
     * @param windowed <code>true</code> if windowed, <code>false</code> else.
     * @return The created main frame.
     */
    private Shell initMainFrame(boolean windowed)
    {
        final Shell shell;
        if (windowed)
        {
            shell = new Shell(ScreenSwt.display, SWT.CLOSE | SWT.TITLE | SWT.MIN | SWT.NO_BACKGROUND);
        }
        else
        {
            shell = new Shell(ScreenSwt.display, SWT.NO_TRIM | SWT.ON_TOP);
            shell.setBounds(ScreenSwt.display.getPrimaryMonitor().getBounds());
        }
        shell.setText(EngineCore.getProgramName() + " " + EngineCore.getProgramVersion());
        shell.addDisposeListener(new DisposeListener()
        {
            @Override
            public void widgetDisposed(DisposeEvent event)
            {
                renderer.end(null);
            }
        });
        return shell;
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
        final Mouse mouse = new Mouse(ScreenSwt.display);
        addMouseListener(mouse);
        devices.put(mouse.getClass(), mouse);
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
                canvas.setEnabled(true);
                canvas.setVisible(true);
            }
            canvas.setSize(output.getWidth(), output.getHeight());
            buffer = UtilityImage.createImageBuffer(output.getWidth(), output.getHeight(), Transparency.OPAQUE);
            frame.pack();

            final Monitor primary = ScreenSwt.display.getPrimaryMonitor();
            final Rectangle bounds = primary.getBounds();
            final Rectangle rect = frame.getBounds();
            final int x = bounds.x + (bounds.width - rect.width) / 2;
            final int y = bounds.y + (bounds.height - rect.height) / 2;
            frame.setLocation(x, y);

            buf = canvas;
        }
        catch (final Exception exception)
        {
            throw new LionEngineException(exception, ScreenSwt.ERROR_WINDOWED);
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
            throw new LionEngineException(exception, ScreenSwt.ERROR_WINDOWED);
        }
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
        canvas.addMouseListener(mouse);
        canvas.addMouseMoveListener(mouse);
        canvas.addMouseWheelListener(mouse);
        canvas.forceFocus();
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
        buf.setVisible(true);
        buf.update();
        gbuf = buffer.createGraphic();
        lastGc = gbuf.getGraphic();
        graphics.setGraphic(lastGc);
        frame.update();
        frame.setEnabled(true);
        frame.setVisible(true);
    }

    @Override
    public void preUpdate()
    {
        // Nothing to do
    }

    @Override
    public void update()
    {
        ScreenSwt.display.readAndDispatch();
        if (!canvas.isDisposed())
        {
            final GC gc = new GC(canvas);
            gc.drawImage(((ImageBufferSwt) buffer).getBuffer(), 0, 0);
            gc.dispose();
            if (lastGc != null)
            {
                lastGc.dispose();
            }
            gbuf = buffer.createGraphic();
            lastGc = gbuf.getGraphic();
            graphics.setGraphic(lastGc);
        }
    }

    @Override
    public void dispose()
    {
        graphics.clear(0, 0, width, height);
        update();
        buf.dispose();
        frame.dispose();
        ScreenSwt.display.dispose();
    }

    @Override
    public void requestFocus()
    {
        if (!frame.isDisposed())
        {
            frame.forceFocus();
        }
    }

    @Override
    public void hideCursor()
    {
        if (!frame.isDisposed())
        {
            frame.setCursor(cursorHidden);
        }
    }

    @Override
    public void showCursor()
    {
        if (!frame.isDisposed())
        {
            frame.setCursor(cursorDefault);
        }
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
        if (!frame.isDisposed())
        {
            final Image icon = new Image(ScreenSwt.display, filename);
            frame.setImage(icon);
        }
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
        if (!frame.isDisposed())
        {
            return frame.getLocation().x;
        }
        return 0;
    }

    @Override
    public int getY()
    {
        if (!frame.isDisposed())
        {
            return frame.getLocation().y;
        }
        return 0;
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
