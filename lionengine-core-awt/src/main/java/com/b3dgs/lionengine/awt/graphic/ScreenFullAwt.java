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

import java.awt.Color;
import java.awt.Dimension;
import java.awt.DisplayMode;

import com.b3dgs.lionengine.Check;
import com.b3dgs.lionengine.Config;
import com.b3dgs.lionengine.Constant;
import com.b3dgs.lionengine.Generated;
import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Resolution;
import com.b3dgs.lionengine.awt.Keyboard;
import com.b3dgs.lionengine.awt.Mouse;

/**
 * Full screen implementation.
 * 
 * @see Keyboard
 * @see Mouse
 */
final class ScreenFullAwt extends ScreenBaseAwt
{
    /** Error message unsupported full screen. */
    static final String ERROR_UNSUPPORTED_FULLSCREEN = "Unsupported resolution: ";
    /** Unable to switch to full screen. */
    static final String ERROR_SWITCH = "Unable to switch to full screen mode !";
    /** Minimum length. */
    private static final int MIN_LENGTH = 18;

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

    /**
     * Internal constructor.
     * 
     * @param config The config reference.
     * @throws LionEngineException If renderer is <code>null</code> or no available display.
     */
    ScreenFullAwt(Config config)
    {
        super(config);

        frame.setUndecorated(true);
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
        final java.awt.Window window = new java.awt.Window(frame, conf);
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
            throw new LionEngineException(ScreenFullAwt.ERROR_UNSUPPORTED_FULLSCREEN
                                          + formatResolution(output, depth)
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
    }

    /**
     * Check support of display change.
     */
    @Generated
    private void checkDisplayChangeSupport()
    {
        if (!dev.isDisplayChangeSupported())
        {
            throw new LionEngineException(ScreenFullAwt.ERROR_SWITCH);
        }
    }

    /**
     * Get the supported resolution information.
     * 
     * @return The supported resolutions.
     */
    private String getSupportedResolutions()
    {
        final StringBuilder builder = new StringBuilder(Constant.HUNDRED);
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
                heightSpace.append(System.lineSeparator());
            }
            final StringBuilder freqSpace = new StringBuilder();
            final int freq = display.getRefreshRate();
            if (freq < Constant.HUNDRED)
            {
                freqSpace.append(Constant.SPACE);
            }
            builder.append("Supported display mode:")
                   .append(System.lineSeparator())
                   .append('[')
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

        initFullscreen(output, config.getDepth());
        super.setResolution(output);
    }

    @Override
    public void start()
    {
        frame.setVisible(true);

        super.start();
    }
}
