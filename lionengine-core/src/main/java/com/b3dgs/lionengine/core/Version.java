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
package com.b3dgs.lionengine.core;

import com.b3dgs.lionengine.Constant;

/**
 * Represents a program version.
 * <p>
 * This class is Thread-Safe.
 * </p>
 */
public final class Version
{
    /** Create the default version (1.0.0). */
    public static final Version DEFAULT = create(1, 0, 0);
    /** Version string length. */
    private static final int LENGTH = 5;

    /**
     * Create a new version descriptor.
     * 
     * @param major The major version.
     * @param minor The minor version.
     * @param micro The micro version.
     * @return The version descriptor.
     */
    public static Version create(int major, int minor, int micro)
    {
        return new Version(major, minor, micro);
    }

    /** Major version. */
    private final int major;
    /** Minor version. */
    private final int minor;
    /** Micro version. */
    private final int micro;

    /**
     * Create a new version descriptor.
     * 
     * @param major The major version.
     * @param minor The minor version.
     * @param micro The micro version.
     */
    private Version(int major, int minor, int micro)
    {
        this.major = major;
        this.minor = minor;
        this.micro = micro;
    }

    /**
     * Get the major number of the version.
     * 
     * @return The major number of the version.
     */
    public int getMajor()
    {
        return major;
    }

    /**
     * Get the minor number of the version.
     * 
     * @return The minor number of the version.
     */
    public int getMinor()
    {
        return minor;
    }

    /**
     * Get the micro number of the version.
     * 
     * @return The micro number of the version.
     */
    public int getMicro()
    {
        return micro;
    }

    /*
     * Object
     */

    @Override
    public String toString()
    {
        return new StringBuilder(LENGTH).append(String.valueOf(major))
                                        .append(Constant.DOT)
                                        .append(minor)
                                        .append(Constant.DOT)
                                        .append(micro)
                                        .toString();
    }
}
