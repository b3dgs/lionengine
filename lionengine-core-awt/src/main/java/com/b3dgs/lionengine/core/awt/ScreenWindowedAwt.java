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
package com.b3dgs.lionengine.core.awt;

import java.awt.AWTException;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.WindowConstants;

import com.b3dgs.lionengine.Constant;
import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Verbose;
import com.b3dgs.lionengine.core.Config;
import com.b3dgs.lionengine.core.Engine;
import com.b3dgs.lionengine.core.Resolution;
import com.b3dgs.lionengine.core.ScreenListener;

/**
 * Windowed screen implementation.
 * 
 * @see Keyboard
 * @see Mouse
 */
final class ScreenWindowedAwt extends ScreenAwt
{
    /** Error message windowed. */
    private static final String ERROR_WINDOWED = "Windowed mode initialization failed !";

    /** Graphic configuration reference. */
    private final GraphicsConfiguration conf;
    /** Frame reference. */
    private final JFrame frame;
    /** Windowed canvas. */
    private Canvas canvas;

    /**
     * Internal constructor.
     * 
     * @param config The config reference.
     * @throws LionEngineException If renderer is <code>null</code> or no available display.
     */
    ScreenWindowedAwt(Config config)
    {
        super(config);

        final GraphicsEnvironment env = GraphicsEnvironment.getLocalGraphicsEnvironment();
        final GraphicsDevice dev = env.getDefaultScreenDevice();
        conf = dev.getDefaultConfiguration();
        frame = initMainFrame();
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
     * Prepare windowed mode.
     * 
     * @param output The output resolution
     * @throws LionEngineException If unable to initialize windowed mode.
     */
    private void initWindowed(Resolution output)
    {
        try
        {
            canvas = new Canvas(conf);
            canvas.setBackground(Color.BLACK);
            canvas.setEnabled(true);
            canvas.setVisible(true);
            canvas.setIgnoreRepaint(true);

            frame.add(canvas);

            canvas.setPreferredSize(new Dimension(output.getWidth(), output.getHeight()));
            frame.pack();
            frame.setLocationRelativeTo(null);

            createBufferStrategy();
            buf = canvas.getBufferStrategy();

            // Set input listeners
            componentForKeyboard = canvas;
            componentForMouse = canvas;
            componentForCursor = frame;
            frame.validate();
        }
        catch (final IllegalStateException exception)
        {
            throw new LionEngineException(exception, ScreenWindowedAwt.ERROR_WINDOWED);
        }
    }

    /**
     * Create the buffer strategy using default capabilities.
     */
    private void createBufferStrategy()
    {
        try
        {
            canvas.createBufferStrategy(2, conf.getBufferCapabilities());
        }
        catch (final AWTException exception)
        {
            Verbose.exception(exception);
            canvas.createBufferStrategy(1);
        }
    }

    /**
     * Initialize the main frame.
     * 
     * @return The created main frame.
     * @throws LionEngineException If engine has not been started.
     */
    private JFrame initMainFrame()
    {
        final String title = new StringBuilder().append(Engine.getProgramName())
                                                .append(Constant.SPACE)
                                                .append(Engine.getProgramVersion())
                                                .toString();
        final JFrame frame = new JFrame(title, conf);
        frame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        frame.addWindowListener(new WindowAdapter()
        {
            @Override
            public void windowClosing(WindowEvent event)
            {
                onDisposed();
            }
        });
        frame.setResizable(false);
        frame.setIgnoreRepaint(true);

        return frame;
    }

    @Override
    protected void setResolution(Resolution output)
    {
        initWindowed(output);
        super.setResolution(output);
    }

    /*
     * Screen
     */

    @Override
    public void start()
    {
        super.start();
        frame.validate();
        frame.setEnabled(true);
        frame.setVisible(true);
    }

    @Override
    public void dispose()
    {
        super.dispose();
        buf.dispose();
        frame.dispose();
    }

    @Override
    public void requestFocus()
    {
        frame.requestFocus();
        super.requestFocus();
    }

    @Override
    public void setIcon(String filename)
    {
        final ImageIcon icon = new ImageIcon(filename);
        frame.setIconImage(icon.getImage());
    }
}
