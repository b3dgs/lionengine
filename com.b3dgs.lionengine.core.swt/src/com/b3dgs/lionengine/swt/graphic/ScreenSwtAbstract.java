/*
 * Copyright (C) 2013-2024 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
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
package com.b3dgs.lionengine.swt.graphic;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.concurrent.atomic.AtomicInteger;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.b3dgs.lionengine.Config;
import com.b3dgs.lionengine.Constant;
import com.b3dgs.lionengine.Engine;
import com.b3dgs.lionengine.InputDeviceKeyListener;
import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.Resolution;
import com.b3dgs.lionengine.graphic.Graphic;
import com.b3dgs.lionengine.graphic.ScreenAbstract;
import com.b3dgs.lionengine.graphic.ScreenListener;
import com.b3dgs.lionengine.swt.Keyboard;
import com.b3dgs.lionengine.swt.Mouse;

/**
 * Screen implementation.
 * 
 * @see Keyboard
 * @see Mouse
 */
public abstract class ScreenSwtAbstract extends ScreenAbstract implements FocusListener
{
    /** Max ready time in millisecond. */
    private static final long READY_TIMEOUT = 5000L;
    /** Logger. */
    private static final Logger LOGGER = LoggerFactory.getLogger(ScreenSwtAbstract.class);

    /**
     * Get screen title.
     * 
     * @return The screen title.
     */
    private static String getTitle()
    {
        final StringBuilder builder = new StringBuilder(Constant.BYTE_4);
        if (Engine.isStarted())
        {
            builder.append(Engine.getProgramName()).append(Constant.SPACE).append(Engine.getProgramVersion());
        }
        else
        {
            builder.append(Constant.ENGINE_NAME).append(Constant.SPACE).append(Constant.ENGINE_VERSION);
        }
        return builder.toString();
    }

    /** Current display. */
    protected final Display display;
    /** Hidden cursor instance. */
    protected final Cursor cursorHidden;
    /** Default cursor instance. */
    protected final Cursor cursorDefault;
    /** Frame reference. */
    protected final Shell frame;
    /** Buffer reference. */
    protected Canvas buf;
    /** Image buffer reference. */
    protected ImageBufferSwt buffer;
    /** Windowed canvas. */
    protected Canvas canvas;
    /** Graphic buffer reference. */
    private Graphic gbuf;
    /** Last GC used. */
    private GC lastGc;
    /** Width. */
    private int width;
    /** Height. */
    private int height;

    /**
     * Internal base constructor.
     * 
     * @param config The config reference.
     * @throws LionEngineException If renderer is <code>null</code>, engine has not been started or resolution is not
     *             supported.
     */
    protected ScreenSwtAbstract(Config config)
    {
        super(config, READY_TIMEOUT);

        display = ToolsSwt.getDisplay();
        cursorHidden = ToolsSwt.createHiddenCursor(display);
        cursorDefault = display.getSystemCursor(0);
        frame = initMainFrame(config);

        final Resolution output = config.getOutput();
        width = output.getWidth();
        height = output.getHeight();
    }

    /**
     * Initialize the main frame.
     * 
     * @param config The config reference.
     * @return The created main frame.
     * @throws LionEngineException If engine has not been started.
     */
    private Shell initMainFrame(Config config)
    {
        final Shell shell;
        if (config.isWindowed())
        {
            shell = new Shell(display, SWT.CLOSE | SWT.TITLE | SWT.MIN | SWT.NO_BACKGROUND);
        }
        else
        {
            shell = new Shell(display, SWT.NO_TRIM | SWT.ON_TOP);
            shell.setBounds(display.getPrimaryMonitor().getBounds());
        }
        shell.setText(getTitle());
        shell.addDisposeListener(event -> onDisposed());
        return shell;
    }

    /**
     * Add a keyboard device.
     */
    private void addDeviceKeyboard()
    {
        final KeyboardSwt keyboard = new KeyboardSwt();
        addKeyboardListener(keyboard);
        devices.put(Keyboard.class, keyboard);
    }

    /**
     * Add a keyboard device.
     */
    private void addDeviceMouse()
    {
        final MouseSwt mouse = new MouseSwt();
        addMouseListener(mouse);
        devices.put(Mouse.class, mouse);
    }

    /**
     * Prepare the focus listener.
     */
    private void prepareFocusListener()
    {
        frame.addFocusListener(this);
    }

    /**
     * Add keyboard.
     * 
     * @param keyboard The keyboard to add.
     */
    private void addKeyboardListener(KeyboardSwt keyboard)
    {
        frame.addKeyListener(keyboard);
        frame.forceFocus();
    }

    /**
     * Add mouse.
     * 
     * @param mouse The mouse to add.
     */
    private void addMouseListener(MouseSwt mouse)
    {
        canvas.addMouseListener(mouse.getClicker());
        canvas.addMouseMoveListener(mouse.getMover());
        canvas.addMouseWheelListener(mouse.getClicker());
        canvas.forceFocus();
    }

    /**
     * Called when screen is disposed.
     */
    void onDisposed()
    {
        for (final ScreenListener listener : listeners)
        {
            listener.notifyClosed();
        }
    }

    /**
     * Set the screen config. Initialize the display.
     * 
     * @param output The output resolution
     * @throws LionEngineException If resolution is not supported.
     */
    protected void setResolution(Resolution output)
    {
        width = output.getWidth();
        height = output.getHeight();
    }

    @Override
    public void start()
    {
        super.start();

        setResolution(config.getOutput());
        prepareFocusListener();
        addDeviceKeyboard();
        addDeviceMouse();

        buf.setVisible(true);
        buf.update();
        gbuf = buffer.createGraphic();
        lastGc = (GC) gbuf.getGraphic();
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
        if (!canvas.isDisposed())
        {
            display.readAndDispatch();

            final GC gc = new GC(canvas);
            gc.drawImage(buffer.getSurface(), 0, 0);
            gc.dispose();
            lastGc.dispose();
            gbuf = buffer.createGraphic();
            lastGc = (GC) gbuf.getGraphic();
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
        display.dispose();
    }

    @Override
    public void requestFocus()
    {
        if (!display.isDisposed())
        {
            display.syncExec(frame::forceFocus);
        }
    }

    @Override
    public void hideCursor()
    {
        if (!display.isDisposed())
        {
            display.syncExec(() -> frame.setCursor(cursorHidden));
        }
    }

    @Override
    public void showCursor()
    {
        if (!display.isDisposed())
        {
            display.syncExec(() -> frame.setCursor(cursorDefault));
        }
    }

    @Override
    public void addKeyListener(final InputDeviceKeyListener listener)
    {
        if (!display.isDisposed())
        {
            display.syncExec(() -> frame.addKeyListener(new KeyboardSwtListener(listener)));
        }
    }

    @Override
    public void setIcons(final Collection<Media> icons)
    {
        if (!display.isDisposed() && !icons.isEmpty())
        {
            display.syncExec(() ->
            {
                try (InputStream input = icons.iterator().next().getInputStream())
                {
                    final Image icon = new Image(display, input);
                    frame.setImage(icon);
                }
                catch (final IOException exception)
                {
                    LOGGER.error("setIcons error", exception);
                }
            });
        }
    }

    @Override
    public int getX()
    {
        final AtomicInteger x = new AtomicInteger(0);
        if (!display.isDisposed())
        {
            display.syncExec(() -> x.set(frame.getLocation().x));
        }
        return x.get();
    }

    @Override
    public int getY()
    {
        final AtomicInteger y = new AtomicInteger(0);
        if (!display.isDisposed())
        {
            display.syncExec(() -> y.set(frame.getLocation().y));
        }
        return y.get();
    }

    @Override
    public int getWidth()
    {
        final AtomicInteger x = new AtomicInteger(0);
        if (!display.isDisposed())
        {
            display.syncExec(() -> x.set(frame.getSize().x));
        }
        return x.get();
    }

    @Override
    public int getHeight()
    {
        final AtomicInteger y = new AtomicInteger(0);
        if (!display.isDisposed())
        {
            display.syncExec(() -> y.set(frame.getSize().y));
        }
        return y.get();
    }

    @Override
    public boolean isReady()
    {
        return buf != null;
    }

    @Override
    public void onSourceChanged(Resolution source)
    {
        ((MouseSwt) getInputDevice(Mouse.class)).setConfig(config.getOutput(), source);
    }

    @Override
    public void focusGained(FocusEvent event)
    {
        for (final ScreenListener listener : listeners)
        {
            listener.notifyFocusGained();
        }
    }

    @Override
    public void focusLost(FocusEvent event)
    {
        for (final ScreenListener listener : listeners)
        {
            listener.notifyFocusLost();
        }
    }
}
