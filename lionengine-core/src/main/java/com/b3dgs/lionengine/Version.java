/*
 * Copyright (C) 2013-2023 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
    private static final int LENGTH = 15;

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
        super();

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
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + major;
        result = prime * result + minor;
        result = prime * result + micro;
        return result;
    }

    @Override
    public boolean equals(Object object)
    {
        if (this == object)
        {
            return true;
        }
        if (object == null || object.getClass() != getClass())
        {
            return false;
        }
        final Version other = (Version) object;
        return major == other.major && minor == other.minor && micro == other.micro;
    }

    @Override
    public String toString()
    {
        return new StringBuilder(LENGTH).append(major)
                                        .append(Constant.DOT)
                                        .append(minor)
                                        .append(Constant.DOT)
                                        .append(micro)
                                        .toString();
    }
}
