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

import java.util.Locale;

/**
 * List of available operating systems.
 * <p>
 * This class is Thread-Safe.
 * </p>
 */
public enum OperatingSystem
{
    /** Window family. */
    WINDOWS,
    /** Unix family. */
    UNIX,
    /** Mac. */
    MAC,
    /** Sun solaris. */
    SOLARIS,
    /** Unknown system. */
    UNKNOWN;

    /** Unknown item. */
    private static final String DEFAULT = "unknown";
    /** The OS enum. */
    private static final OperatingSystem OS;

    /**
     * Specific case to not inline for test purpose.
     */
    static
    {
        OS = find(Constant.getSystemProperty("os.name", DEFAULT).toLowerCase(Locale.ENGLISH));
    }

    /**
     * Find the current system.
     * 
     * @param os The system short name (usually <code>win</code> or <code>mac</code> or <code>nux</code> or
     *            <code>sunos</code>...).
     * @return The OS found.
     */
    public static OperatingSystem find(String os)
    {
        final OperatingSystem found;
        if (os == null)
        {
            return UNKNOWN;
        }
        else if (os.indexOf("win") >= 0)
        {
            found = WINDOWS;
        }
        else if (os.indexOf("mac") >= 0)
        {
            found = MAC;
        }
        else if (os.indexOf("nix") >= 0 || os.indexOf("nux") >= 0 || os.indexOf("bsd") >= 0 || os.indexOf("aix") >= 0)
        {
            found = UNIX;
        }
        else if (os.indexOf("sunos") >= 0)
        {
            found = SOLARIS;
        }
        else
        {
            found = UNKNOWN;
        }
        return found;
    }

    /**
     * Get operating system name.
     * 
     * @return The operating system name.
     */
    public static OperatingSystem getOperatingSystem()
    {
        return OS;
    }
}
