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
package com.b3dgs.lionengine;

/**
 * Represents a program version.
 * <p>
 * This class is Thread-Safe.
 * </p>
 * 
 * @param major The major version.
 * @param minor The minor version.
 * @param micro The micro version.
 */
public record Version(int major, int minor, int micro)
{
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

    @Override
    public String toString()
    {
        return major + Constant.DOT + minor + Constant.DOT + micro;
    }
}
