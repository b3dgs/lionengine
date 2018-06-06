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

import java.util.Optional;

/**
 * Describe the engine screen configuration. It allows to define different parameters:
 * <ul>
 * <li>output : It is corresponding to the desired screen resolution output.</li>
 * <li>windowed : Allows to set the screen output mode (<code>true</code> for windowed, <code>false</code> for
 * fullscreen)</li>
 * <li>applet : Can be used to set the applet reference in case of applet mode</li>
 * </ul>
 */
public final class Config
{
    /** Default color depth. */
    private static final int DEPTH_DEFAULT = 32;

    /**
     * Create a 32 bits color depth and windowed configuration using output resolution.
     * 
     * @param output The output resolution used on rendering (must not be <code>null</code>).
     * @return The created windowed configuration.
     * @throws LionEngineException If invalid argument.
     */
    public static Config windowed(Resolution output)
    {
        return new Config(output, DEPTH_DEFAULT, true);
    }

    /**
     * Create a 32 bits color depth and fullscreen configuration using output resolution.
     * 
     * @param output The output resolution used on rendering (must not be <code>null</code>).
     * @return The created fullscreen configuration.
     * @throws LionEngineException If invalid argument.
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
    /** Icon media. */
    private final Optional<Media> icon;

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
     * @param icon The icon media (can be <code>null</code>).
     * @throws LionEngineException If invalid arguments.
     */
    public Config(Resolution output, int depth, boolean windowed, Media icon)
    {
        super();

        Check.notNull(output);
        Check.superiorStrict(depth, 0);

        this.output = output;
        this.depth = depth;
        this.windowed = windowed;
        this.icon = Optional.ofNullable(icon);
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
     * Get the application icon.
     * 
     * @return The application icon.
     */
    public Optional<Media> getIcon()
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
}
