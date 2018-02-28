/*
 * Copyright (C) 2013-2017 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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

/**
 * Describe the engine screen configuration. It allows to define different parameters:
 * <ul>
 * <li>source : It is corresponding to the native screen resolution for what the program was designed for (the source is
 * defined by the {@link com.b3dgs.lionengine.core.sequence.Sequence})</li>
 * <li>output : It is corresponding to the desired screen resolution output. If source and output are not equal, the
 * screen will be stretched</li>
 * <li>windowed : Allows to set the screen output mode (<code>true</code> for windowed, <code>false</code> for
 * fullscreen)</li>
 * <li>applet : Can be used to set the applet reference in case of applet mode</li>
 * </ul>
 * 
 * @see Resolution
 * @see Ratio
 */
public final class Config
{
    /** Default color depth. */
    private static final int DEPTH_DEFAULT = 32;

    /**
     * Create a 32 bits color depth and windowed configuration using output resolution.
     * 
     * @param output The output resolution (used on rendering).
     * @return The created windowed configuration.
     */
    public static Config windowed(Resolution output)
    {
        return new Config(output, DEPTH_DEFAULT, true);
    }

    /**
     * Create a 32 bits color depth and fullscreen configuration using output resolution.
     * 
     * @param output The output resolution (used on rendering).
     * @return The created fullscreen configuration.
     */
    public static Config fullscreen(Resolution output)
    {
        return new Config(output, DEPTH_DEFAULT, false);
    }

    /** Output resolution reference. */
    private final Resolution output;
    /** Display depth. */
    private final int depth;
    /** Windowed mode. */
    private final boolean windowed;
    /** Icon media (<code>null</code> if none). */
    private final Media icon;
    /** Source resolution reference. */
    private volatile Resolution source;
    /** Applet reference (<code>null</code> if none). */
    private Applet<?> applet;

    /**
     * Create a configuration without icon.
     * 
     * @param output The output resolution used on rendering (must not be <code>null</code>).
     * @param depth The screen color depth in bits, usually 16 or 32 (strictly positive).
     * @param windowed The windowed mode: <code>true</code> for windowed, <code>false</code> for fullscreen.
     * @throws LionEngineException If invalid arguments.
     */
    public Config(Resolution output, int depth, boolean windowed)
    {
        this(output, depth, windowed, null);
    }

    /**
     * Create a configuration.
     * 
     * @param output The output resolution used on rendering (must not be <code>null</code>).
     * @param depth The screen color depth in bits, usually 16 or 32 (strictly positive).
     * @param windowed The windowed mode: <code>true</code> for windowed, <code>false</code> for fullscreen.
     * @param icon The icon media (<code>null</code> if none).
     * @throws LionEngineException If invalid arguments.
     */
    public Config(Resolution output, int depth, boolean windowed, Media icon)
    {
        Check.notNull(output);
        Check.superiorStrict(depth, 0);

        this.output = output;
        this.depth = depth;
        this.windowed = windowed;
        this.icon = icon;
    }

    /**
     * Set applet reference, and enable applet mode.
     * 
     * @param applet The applet reference (<code>null</code> to disable).
     */
    public void setApplet(Applet<?> applet)
    {
        this.applet = applet;
    }

    /**
     * Set the resolution source.
     * 
     * @param source The native source resolution (must not be <code>null</code>).
     * @throws LionEngineException If source is <code>null</code>.
     */
    public void setSource(Resolution source)
    {
        Check.notNull(source);

        this.source = source;
    }

    /**
     * Get the resolution source.
     * 
     * @return The resolution source, <code>null</code> if none defined.
     */
    public Resolution getSource()
    {
        return source;
    }

    /**
     * Get the resolution output.
     * 
     * @return The resolution output.
     */
    public Resolution getOutput()
    {
        return output;
    }

    /**
     * Get the applet reference.
     * 
     * @param <A> The applet type used.
     * @param appletClass The applet class.
     * @return The applet reference, <code>null</code> if none.
     */
    public <A extends Applet<A>> A getApplet(Class<A> appletClass)
    {
        A cast = null;
        if (appletClass != null && applet != null)
        {
            cast = appletClass.cast(applet.getApplet());
        }
        return cast;
    }

    /**
     * Get the application icon.
     * 
     * @return The application icon, <code>null</code> if none.
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
     * Get the windowed mode.
     * 
     * @return <code>true</code> if windowed, <code>false</code> fullscreen.
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
        return applet != null;
    }
}
