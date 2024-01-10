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
package com.b3dgs.lionengine.awt.graphic;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.DisplayMode;
import java.awt.event.InputEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import com.b3dgs.lionengine.Check;
import com.b3dgs.lionengine.Config;
import com.b3dgs.lionengine.Constant;
import com.b3dgs.lionengine.Generated;
import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Resolution;
import com.b3dgs.lionengine.awt.Keyboard;
import com.b3dgs.lionengine.awt.KeyboardAwt;
import com.b3dgs.lionengine.awt.Mouse;

/**
 * Screen implementation.
 * 
 * @see Keyboard
 * @see Mouse
 */
final class ScreenAwt extends ScreenBaseAwt
{
    /** Error message unsupported full screen. */
    static final String ERROR_UNSUPPORTED_FULLSCREEN = "Unsupported resolution: ";
    /** Unable to switch to full screen. */
    static final String ERROR_SWITCH = "Unable to switch to full screen mode !";
    /** Minimum length. */
    private static final int MIN_LENGTH = 21;

    /**
     * Format resolution to string.
     * 
     * @param resolution The resolution reference.
     * @param depth The depth reference.
     * @return The formatted string.
     */
    private static String formatResolution(Resolution resolution, int depth)
    {
        return new StringBuilder(MIN_LENGTH).append(String.valueOf(resolution.getWidth()))
                                            .append(Constant.STAR)
                                            .append(String.valueOf(resolution.getHeight()))
                                            .append(Constant.STAR)
                                            .append(depth)
                                            .append(Constant.SPACE)
                                            .append(Constant.AT)
                                            .append(String.valueOf(resolution.getRate()))
                                            .append(Constant.UNIT_RATE)
                                            .toString();
    }

    /** Fullscreen mode. */
    private java.awt.Window window;
    /** Windowed mode. */
    private Canvas canvas;
    /** Flag to request switch. */
    private boolean requestWindowed;
    /** Flag to request switch release. */
    private boolean requestAltEnter;
    /** Flag to allow request. */
    private boolean requestAllowed = true;

    /**
     * Internal constructor.
     * 
     * @param config The config reference.
     * @throws LionEngineException If renderer is <code>null</code> or no available display.
     */
    ScreenAwt(Config config)
    {
        super(config);

        requestWindowed = config.isWindowed();
    }

    /**
     * Prepare fullscreen mode.
     * 
     * @param output The output resolution
     * @param depth The bit depth color.
     * @throws LionEngineException If unsupported resolution.
     */
    private void initFullscreen(Resolution output, int depth)
    {
        window = new java.awt.Window(frame, conf);
        window.setBackground(Color.BLACK);
        window.setIgnoreRepaint(true);
        window.setPreferredSize(new Dimension(output.getWidth(), output.getHeight()));
        dev.setFullScreenWindow(window);

        final DisplayMode disp = isSupported(new DisplayMode(output.getWidth(),
                                                             output.getHeight(),
                                                             depth,
                                                             output.getRate()));
        if (disp == null)
        {
            throw new LionEngineException(ScreenAwt.ERROR_UNSUPPORTED_FULLSCREEN
                                          + formatResolution(output, depth)
                                          + System.lineSeparator()
                                          + getSupportedResolutions());
        }
        checkDisplayChangeSupport();

        dev.setDisplayMode(disp);
        window.validate();

        ToolsAwt.createBufferStrategy(window, conf);
        buf = window.getBufferStrategy();

        // Set input listeners
        componentForKeyboard = frame;
        componentForMouse = window;
        componentForCursor = window;
        frame.validate();

        // CHECKSTYLE IGNORE LINE: AnonInnerLength
        componentForKeyboard.addKeyListener(new KeyAdapter()
        {
            @Override
            public void keyPressed(KeyEvent event)
            {
                if (requestAllowed
                    && event.getModifiersEx() == InputEvent.ALT_DOWN_MASK
                    && event.getKeyCode() == KeyboardAwt.ENTER.intValue())
                {
                    requestAllowed = false;
                    requestAltEnter = true;
                    requestWindowed = true;
                    dev.setFullScreenWindow(null);
                }
            }

            @Override
            public void keyReleased(KeyEvent e)
            {
                requestAllowed = true;
            }
        });
    }

    /**
     * Prepare windowed mode.
     * 
     * @param output The output resolution
     * @throws LionEngineException If unable to initialize windowed mode.
     */
    private void initWindowed(Resolution output)
    {
        dev.setFullScreenWindow(null);

        canvas = new Canvas(conf);
        canvas.setBackground(Color.BLACK);
        canvas.setEnabled(true);
        canvas.setVisible(true);
        canvas.setIgnoreRepaint(true);

        frame.add(canvas, 0);

        canvas.setPreferredSize(new Dimension(output.getWidth(), output.getHeight()));
        frame.pack();
        frame.setLocationRelativeTo(null);

        ToolsAwt.createBufferStrategy(canvas, conf);
        buf = canvas.getBufferStrategy();

        // Set input listeners
        componentForKeyboard = canvas;
        componentForMouse = canvas;
        componentForCursor = frame;
        frame.validate();

        componentForKeyboard.addKeyListener(new KeyAdapter()
        {
            @Override
            public void keyPressed(KeyEvent event)
            {
                if (requestAllowed
                    && event.getModifiersEx() == InputEvent.ALT_DOWN_MASK
                    && event.getKeyCode() == KeyboardAwt.ENTER.intValue())
                {
                    requestAllowed = false;
                    requestAltEnter = true;
                    requestWindowed = false;
                }
            }

            @Override
            public void keyReleased(KeyEvent e)
            {
                requestAllowed = true;
            }
        });
    }

    /**
     * Check support of display change.
     */
    @Generated
    private void checkDisplayChangeSupport()
    {
        if (!dev.isDisplayChangeSupported())
        {
            throw new LionEngineException(ScreenAwt.ERROR_SWITCH);
        }
    }

    /**
     * Get the supported resolution information.
     * 
     * @return The supported resolutions.
     */
    private String getSupportedResolutions()
    {
        final StringBuilder builder = new StringBuilder("Supported resolution(s):" + System.lineSeparator());
        int i = 0;
        for (final DisplayMode display : dev.getDisplayModes())
        {
            final StringBuilder widthSpace = new StringBuilder();
            final int width = display.getWidth();
            if (width < Constant.THOUSAND)
            {
                widthSpace.append(Constant.SPACE);
            }
            final StringBuilder heightSpace = new StringBuilder();
            final int height = display.getHeight();
            if (height < Constant.THOUSAND)
            {
                heightSpace.append(Constant.SPACE);
            }
            final StringBuilder freqSpace = new StringBuilder();
            final int freq = display.getRefreshRate();
            if (freq < Constant.HUNDRED)
            {
                freqSpace.append(Constant.SPACE);
            }
            builder.append('[')
                   .append(widthSpace)
                   .append(width)
                   .append(Constant.STAR)
                   .append(heightSpace)
                   .append(height)
                   .append(Constant.STAR)
                   .append(display.getBitDepth())
                   .append(Constant.SPACE)
                   .append(Constant.AT)
                   .append(freqSpace)
                   .append(freq)
                   .append(Constant.UNIT_RATE)
                   .append(']')
                   .append(Constant.SPACE);
            i++;
            final int linesPerDisplay = 5;
            if (i % linesPerDisplay == 0)
            {
                builder.append(System.lineSeparator());
            }
        }
        return builder.toString();
    }

    /**
     * Check if the display mode is supported.
     * 
     * @param display The display mode to check.
     * @return Supported display, <code>null</code> else.
     */
    private DisplayMode isSupported(DisplayMode display)
    {
        final DisplayMode[] supported = dev.getDisplayModes();
        for (final DisplayMode current : supported)
        {
            if (ToolsAwt.sameDisplay(display, current))
            {
                return current;
            }
        }
        return null;
    }

    @Override
    protected void setResolution(Resolution output)
    {
        Check.notNull(output);

        if (requestWindowed)
        {
            initWindowed(output);
        }
        else
        {
            initFullscreen(output, config.getDepth());
        }
        super.setResolution(output);
    }

    @Override
    public void start()
    {
        frame.setVisible(true);

        super.start();
    }

    @Override
    public void preUpdate()
    {
        if (requestAltEnter)
        {
            if (buf != null)
            {
                buf.dispose();
                buf = null;
            }
            while (!requestAllowed)
            {
                try
                {
                    Thread.sleep(Constant.DECADE);
                }
                catch (@SuppressWarnings("unused") final InterruptedException exception)
                {
                    Thread.currentThread().interrupt();
                }
            }
            frame.removeNotify();
            if (window != null)
            {
                window.dispose();
            }
            canvas = null;
            try
            {
                Thread.sleep(Constant.HUNDRED);
            }
            catch (@SuppressWarnings("unused") final InterruptedException exception)
            {
                Thread.currentThread().interrupt();
            }
            frame.addNotify();
            start();
            requestAltEnter = false;
        }
    }
}
