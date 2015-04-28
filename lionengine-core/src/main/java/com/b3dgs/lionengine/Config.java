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
package com.b3dgs.lionengine;

import com.b3dgs.lionengine.core.Applet;
import com.b3dgs.lionengine.core.Media;
import com.b3dgs.lionengine.core.Sequence;

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
 * <p>
 * This class is Thread-Safe.
 * </p>
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 * @see Resolution
 * @see Filter
 * @see Ratio
 */
public final class Config
{
    /** Applet lock. */
    private final Object lockApplet = new Object();
    /** Source lock. */
    private final Object sourceLock = new Object();
    /** Output resolution reference. */
    private final Resolution output;
    /** Filter reference. */
    private final Filter filter;
    /** Display depth. */
    private final int depth;
    /** Windowed mode. */
    private final boolean windowed;
    /** Icon media. */
    private volatile Media icon;
    /** Ratio desired (locked by {@link #sourceLock}). */
    private double ratio;
    /** Source resolution reference (locked by {@link #sourceLock}). */
    private Resolution source;
    /** Applet reference (locked by {@link #lockApplet}). */
    private Applet<?> applet;

    /**
     * Create a configuration without filter.
     * 
     * @param output The output resolution (used on rendering).
     * @param depth The screen color depth in bits (usually 16 or 32).
     * @param windowed The windowed mode: <code>true</code> for windowed, <code>false</code> for fullscreen.
     * @throws LionEngineException If arguments are <code>null</code> or invalid.
     */
    public Config(Resolution output, int depth, boolean windowed) throws LionEngineException
    {
        this(output, depth, windowed, Filter.NONE);
    }

    /**
     * Create a configuration.
     * 
     * @param output The output resolution (used on rendering).
     * @param depth The screen color depth in bits (usually 16 or 32).
     * @param windowed The windowed mode: <code>true</code> for windowed, <code>false</code> for fullscreen.
     * @param filter The filter mode (must not be null).
     * @throws LionEngineException If arguments are <code>null</code> or invalid.
     */
    public Config(Resolution output, int depth, boolean windowed, Filter filter) throws LionEngineException
    {
        Check.notNull(output);
        Check.notNull(filter);
        Check.superiorStrict(depth, 0);

        this.output = output;
        this.depth = depth;
        this.windowed = windowed;
        this.filter = filter;

        setRatioValue(output.getWidth() / (double) output.getHeight());
    }

    /**
     * Set the ratio and adapt the resolution to the new ratio (based on the height value).
     * 
     * @param ratio The new ratio [> 0].
     * @throws LionEngineException If ratio is not strictly positive.
     */
    public void setRatio(double ratio) throws LionEngineException
    {
        synchronized (sourceLock)
        {
            setRatioValue(ratio);
        }
    }

    /**
     * Set applet reference, and enable applet mode.
     * 
     * @param applet The applet reference.
     */
    public void setApplet(Applet<?> applet)
    {
        synchronized (lockApplet)
        {
            this.applet = applet;
        }
    }

    /**
     * Set the application icon.
     * 
     * @param icon The icon media.
     */
    public void setIcon(Media icon)
    {
        this.icon = icon;
    }

    /**
     * Set the resolution source.
     * 
     * @param source The source resolution (native).
     * @throws LionEngineException If source is <code>null</code>.
     */
    public void setSource(Resolution source) throws LionEngineException
    {
        Check.notNull(source);

        synchronized (sourceLock)
        {
            this.source = new Resolution(source.getWidth(), source.getHeight(), source.getRate());
            this.source.setRatio(ratio);
        }
    }

    /**
     * Get the resolution source.
     * 
     * @return The source resolution.
     */
    public Resolution getSource()
    {
        synchronized (sourceLock)
        {
            return source;
        }
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
     * @param <A> The applet type used.
     * @param appletClass The applet class.
     * @return The applet reference.
     */
    public <A extends Applet<A>> A getApplet(Class<A> appletClass)
    {
        A cast = null;
        if (appletClass != null)
        {
            synchronized (lockApplet)
            {
                if (applet != null)
                {
                    cast = appletClass.cast(applet.getApplet());
                }
            }
        }
        return cast;
    }

    /**
     * Get the application icon.
     * 
     * @return The application icon.
     */
    public Media getIcon()
    {
        return icon;
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
     * Check if has applet.
     * 
     * @return <code>true</code> if has applet, <code>false</code> else.
     */
    public boolean hasApplet()
    {
        synchronized (lockApplet)
        {
            return applet != null;
        }
    }

    /**
     * Set the ratio value.
     * 
     * @param ratio The new ratio [> 0].
     * @throws LionEngineException If ratio is not strictly positive.
     */
    private void setRatioValue(double ratio) throws LionEngineException
    {
        Check.superiorStrict(ratio, 0);

        this.ratio = ratio;
        output.setRatio(ratio);
    }
}
