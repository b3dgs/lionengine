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
package com.b3dgs.lionengine.core.swt;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import com.b3dgs.lionengine.Check;
import com.b3dgs.lionengine.Config;
import com.b3dgs.lionengine.Constant;
import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Resolution;
import com.b3dgs.lionengine.core.EngineCore;
import com.b3dgs.lionengine.core.Graphic;
import com.b3dgs.lionengine.core.Graphics;
import com.b3dgs.lionengine.core.ImageBuffer;
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
abstract class ScreenSwt implements Screen, FocusListener
{
    /** Renderer reference. */
    protected final Renderer renderer;
    /** Current display. */
    protected final Display display;
    /** Hidden cursor instance. */
    protected final Cursor cursorHidden;
    /** Default cursor instance. */
    protected final Cursor cursorDefault;
    /** Configuration reference. */
    protected final Config config;
    /** Frame reference. */
    protected final Shell frame;
    /** Input devices. */
    private final Map<Class<? extends InputDevice>, InputDevice> devices;
    /** Active graphic buffer reference. */
    private final Graphic graphics;
    /** Buffer reference. */
    protected Canvas buf;
    /** Image buffer reference. */
    protected ImageBuffer buffer;
    /** Windowed canvas. */
    protected Canvas canvas;
    /** Active sequence reference. */
    private Sequence sequence;
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
     * @param renderer The renderer reference.
     * @throws LionEngineException If renderer is <code>null</code>, engine has not been started or resolution is not
     *             supported.
     */
    protected ScreenSwt(Renderer renderer) throws LionEngineException
    {
        Check.notNull(renderer);

        this.renderer = renderer;
        display = UtilityImage.getDisplay();
        config = renderer.getConfig();
        cursorHidden = ToolsSwt.createHiddenCursor(display);
        cursorDefault = display.getSystemCursor(0);
        graphics = Graphics.createGraphic();
        devices = new HashMap<Class<? extends InputDevice>, InputDevice>(2);
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
     * @throws LionEngineException If engine has not been started.
     */
    private Shell initMainFrame(boolean windowed) throws LionEngineException
    {
        final Shell shell;
        if (windowed)
        {
            shell = new Shell(display, SWT.CLOSE | SWT.TITLE | SWT.MIN | SWT.NO_BACKGROUND);
        }
        else
        {
            shell = new Shell(display, SWT.NO_TRIM | SWT.ON_TOP);
            shell.setBounds(display.getPrimaryMonitor().getBounds());
        }
        shell.setText(EngineCore.getProgramName() + Constant.SPACE + EngineCore.getProgramVersion());
        shell.addDisposeListener(new DisposeListener()
        {
            @Override
            public void widgetDisposed(DisposeEvent event)
            {
                renderer.end();
            }
        });
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
        final MouseSwt mouse = new MouseSwt(display);
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
        canvas.addMouseListener(mouse);
        canvas.addMouseMoveListener(mouse);
        canvas.addMouseWheelListener(mouse);
        canvas.forceFocus();
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

    /*
     * Screen
     */

    @Override
    public void start()
    {
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
        display.readAndDispatch();
        if (!canvas.isDisposed())
        {
            final GC gc = new GC(canvas);
            gc.drawImage(UtilityImage.getBuffer(buffer), 0, 0);
            gc.dispose();
            if (lastGc != null)
            {
                lastGc.dispose();
            }
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
        display.syncExec(new Runnable()
        {
            @Override
            public void run()
            {
                if (!frame.isDisposed())
                {
                    frame.forceFocus();
                }
            }
        });
    }

    @Override
    public void hideCursor()
    {
        display.syncExec(new Runnable()
        {
            @Override
            public void run()
            {
                if (!frame.isDisposed())
                {
                    frame.setCursor(cursorHidden);
                }
            }
        });
    }

    @Override
    public void showCursor()
    {
        display.syncExec(new Runnable()
        {
            @Override
            public void run()
            {
                if (!frame.isDisposed())
                {
                    frame.setCursor(cursorDefault);
                }
            }
        });
    }

    @Override
    public void addKeyListener(final InputDeviceKeyListener listener)
    {
        display.syncExec(new Runnable()
        {
            @Override
            public void run()
            {
                frame.addKeyListener(new KeyListener(listener));
            }
        });
    }

    @Override
    public void setSequence(Sequence sequence)
    {
        this.sequence = sequence;
    }

    @Override
    public void setIcon(final String filename)
    {
        display.syncExec(new Runnable()
        {
            @Override
            public void run()
            {
                if (!frame.isDisposed())
                {
                    final Image icon = new Image(display, filename);
                    frame.setImage(icon);
                }
            }
        });
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
        final AtomicInteger x = new AtomicInteger(0);
        display.syncExec(new Runnable()
        {
            @Override
            public void run()
            {
                if (!frame.isDisposed())
                {
                    x.set(frame.getLocation().x);
                }
            }
        });
        return x.get();
    }

    @Override
    public int getY()
    {
        final AtomicInteger y = new AtomicInteger(0);
        display.syncExec(new Runnable()
        {
            @Override
            public void run()
            {
                if (!frame.isDisposed())
                {
                    y.set(frame.getLocation().y);
                }
            }
        });
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
        ((MouseSwt) getInputDevice(Mouse.class)).setConfig(config);
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
