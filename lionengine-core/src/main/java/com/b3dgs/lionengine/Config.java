/*
 * Copyright (C) 2013-2021 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Describe the engine screen configuration. It allows to define different parameters:
 * <ul>
 * <li>output : It is corresponding to the desired screen resolution output.</li>
 * <li>windowed : Allows to set the screen output mode (<code>true</code> for windowed, <code>false</code> for
 * fullscreen).</li>
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
     * Create a 32 bits color depth and windowed configuration using output resolution.
     * 
     * @param output The output resolution used on rendering (must not be <code>null</code>).
     * @param icons The windows icons (must not be <code>null</code>).
     * @return The created windowed configuration.
     * @throws LionEngineException If invalid argument.
     */
    public static Config windowed(Resolution output, Media... icons)
    {
        return new Config(output, DEPTH_DEFAULT, true, icons);
    }

    /**
     * Create a 32 bits color depth and windowed configuration using output resolution.
     * 
     * @param output The output resolution used on rendering (must not be <code>null</code>).
     * @param devices The devices reference.
     * @param icons The windows icons (must not be <code>null</code>).
     * @return The created windowed configuration.
     * @throws LionEngineException If invalid argument.
     */
    public static Config windowed(Resolution output, List<InputDevice> devices, Media... icons)
    {
        return new Config(output, DEPTH_DEFAULT, true, devices, icons);
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
    private final Collection<Media> icons;
    /** Devices. */
    private final List<InputDevice> devices;

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
        this(output, depth, windowed, new Media[0]);
    }

    /**
     * Create a configuration.
     * 
     * @param output The output resolution used on rendering (must not be <code>null</code>).
     * @param depth The screen color depth in bits, usually 16 or 32 (strictly positive).
     * @param windowed The windowed mode: <code>true</code> for windowed, <code>false</code> for fullscreen.
     * @param icons The icons media (can be <code>null</code>).
     * @throws LionEngineException If invalid arguments.
     */
    public Config(Resolution output, int depth, boolean windowed, Media... icons)
    {
        this(output, depth, windowed, Collections.emptyList(), icons);
    }

    /**
     * Create a configuration.
     * 
     * @param output The output resolution used on rendering (must not be <code>null</code>).
     * @param depth The screen color depth in bits, usually 16 or 32 (strictly positive).
     * @param windowed The windowed mode: <code>true</code> for windowed, <code>false</code> for fullscreen.
     * @param devices The devices reference.
     * @param icons The icons media (can be <code>null</code>).
     * @throws LionEngineException If invalid arguments.
     */
    public Config(Resolution output, int depth, boolean windowed, List<InputDevice> devices, Media... icons)
    {
        Check.notNull(output);
        Check.superiorOrEqual(depth, -1);

        this.output = output;
        this.depth = depth;
        this.windowed = windowed;
        this.devices = devices;
        this.icons = Arrays.asList(icons);
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
     * Get the application icons.
     * 
     * @return The application icons.
     */
    public Collection<Media> getIcons()
    {
        return icons;
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
     * Get custom devices.
     * 
     * @return The custom devices.
     */
    public List<InputDevice> getDevices()
    {
        return devices;
    }
}
