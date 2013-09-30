/*
 * Copyright (C) 2013 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine;

import javax.swing.JApplet;

/**
 * Describe the engine screen configuration. It allows to define different parameters:
 * <ul>
 * <li>source : It is corresponding to the native screen resolution for what the program was designed for (the source is
 * defined by the {@link Sequence})</li>
 * <li>output : It is corresponding to the desired screen resolution output. If source & output are not equal, the
 * screen will be stretched</li>
 * <li>windowed : Allows to set the screen output mode (<code>true</code> for windowed, <code>false</code> for
 * fullscreen)</li>
 * <li>filter : Used only in case of screen stretching, depending of the output resolution</li>
 * <li>applet : Can be used to set the applet reference in case of applet mode</li>
 * </ul>
 * <p>
 * Example:
 * </p>
 * 
 * <pre>
 * final Resolution output = new Resolution(640, 480, 60);
 * final Config config = new Config(output, 16, true);
 * </pre>
 */
public final class Config
{
    /** Error message source. */
    private static final String MESSAGE_ERROR_SOURCE = "The source resolution must not be null !";
    /** Error message output. */
    private static final String MESSAGE_ERROR_OUTPUT = "The output resolution must not be null !";
    /** Error message filter. */
    private static final String MESSAGE_ERROR_FILTER = "The filter must not be null !";

    /** Output resolution reference. */
    private final Resolution output;
    /** Filter reference. */
    private final Filter filter;
    /** Display depth. */
    private final int depth;
    /** Windowed mode. */
    private final boolean windowed;
    /** Source resolution reference. */
    private Resolution source;
    /** Ratio desired. */
    private double ratio;
    /** Applet reference. */
    private JApplet applet;

    /**
     * Create a config.
     * 
     * @param output The output resolution (used on rendering).
     * @param depth The screen color depth in bits (usually 16 or 32).
     * @param windowed The windowed mode: <code>true</code> for windowed, <code>false</code> for fullscreen.
     */
    public Config(Resolution output, int depth, boolean windowed)
    {
        this(output, depth, windowed, Filter.NONE);
    }

    /**
     * Create a config.
     * 
     * @param output The output resolution (used on rendering).
     * @param depth The screen color depth in bits (usually 16 or 32).
     * @param windowed The windowed mode: <code>true</code> for windowed, <code>false</code> for fullscreen.
     * @param filter The filter mode (must not be null).
     */
    public Config(Resolution output, int depth, boolean windowed, Filter filter)
    {
        Check.notNull(output, Config.MESSAGE_ERROR_OUTPUT);
        Check.notNull(filter, Config.MESSAGE_ERROR_FILTER);

        this.output = output;
        this.depth = depth;
        this.windowed = windowed;
        this.filter = filter;
        setRatio(output.getWidth() / (double) output.getHeight());
    }

    /**
     * Set the ratio and adapt the resolution to the new ratio (based on the height value).
     * 
     * @param ratio The new ratio.
     */
    public void setRatio(double ratio)
    {
        this.ratio = ratio;
        output.setRatio(ratio);
    }

    /**
     * Set applet reference, and enable applet mode.
     * 
     * @param applet The applet reference.
     */
    public void setApplet(JApplet applet)
    {
        this.applet = applet;
    }

    /**
     * Get the resolution source.
     * 
     * @return The source resolution.
     */
    public Resolution getSource()
    {
        return source;
    }

    /**
     * Get the resolution output.
     * 
     * @return The output resolution.
     */
    public Resolution getOutput()
    {
        return output;
    }

    /**
     * Get applet reference.
     * 
     * @return The applet reference.
     */
    public JApplet getApplet()
    {
        return applet;
    }

    /**
     * Get the display depth.
     * 
     * @return The display depth.
     */
    public int getDepth()
    {
        return depth;
    }

    /**
     * Get the filter.
     * 
     * @return The filter.
     */
    public Filter getFilter()
    {
        return filter;
    }

    /**
     * Get the windowed mode.
     * 
     * @return <code>true</code> if is windowed, <code>false</code> else.
     */
    public boolean isWindowed()
    {
        return windowed;
    }

    /**
     * Set the resolution source.
     * 
     * @param source The source resolution (native).
     */
    void setSource(Resolution source)
    {
        Check.notNull(source, Config.MESSAGE_ERROR_SOURCE);
        this.source = new Resolution(source.getWidth(), source.getHeight(), source.getRate());
        if (ratio > 0)
        {
            this.source.setRatio(ratio);
        }
    }
}
