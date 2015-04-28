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

import java.util.Locale;

import com.b3dgs.lionengine.core.EngineCore;

/**
 * List of available operating systems.
 * <p>
 * This class is Thread-Safe.
 * </p>
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public enum OperatingSystem
{
    /** Window family. */
    WINDOWS,
    /** Unix family. */
    UNIX,
    /** Mac */
    MAC,
    /** Sun solaris. */
    SOLARIS,
    /** Unknown system. */
    UNKNOWN;

    /** The OS enum. */
    private static final OperatingSystem OS;
    /** The architecture used. */
    private static final Architecture ARCHI;
    /** Unknown item. */
    private static final String DEFAULT = "unknown";

    /**
     * Static init.
     */
    static
    {
        OS = findOs(EngineCore.getSystemProperty("os.name", DEFAULT).toLowerCase(Locale.getDefault()));
        ARCHI = findArchitecture(EngineCore.getSystemProperty("sun.arch.data.model", DEFAULT));
    }

    /**
     * Find the current OS.
     * 
     * @param os The system short name (usually <code>win</code> or <code>mac</code> or <code>nux</code> or
     *            <code>sunos</code>...).
     * @return The OS found.
     */
    public static OperatingSystem findOs(String os)
    {
        if (os != null)
        {
            if (os.indexOf("win") >= 0)
            {
                return WINDOWS;
            }
            else if (os.indexOf("mac") >= 0)
            {
                return MAC;
            }
            else if (os.indexOf("nix") >= 0 || os.indexOf("nux") >= 0 || os.indexOf("bsd") >= 0
                    || os.indexOf("aix") >= 0)
            {
                return UNIX;
            }
            else if (os.indexOf("sunos") >= 0)
            {
                return SOLARIS;
            }
        }
        return UNKNOWN;
    }

    /**
     * Find the current architecture.
     * 
     * @param arch The architecture short name (usually <code>32</code> or <code>86</code> or <code>64</code>).
     * @return The current architecture.
     */
    public static Architecture findArchitecture(String arch)
    {
        if (arch != null)
        {
            if (arch.contains("64"))
            {
                return Architecture.X64;
            }
            else if (arch.contains("32") || arch.contains("86"))
            {
                return Architecture.X86;
            }
        }
        return Architecture.UNKNOWN;
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

    /**
     * Get java current running architecture.
     * 
     * @return The jvm architecture.
     */
    public static Architecture getArchitecture()
    {
        return ARCHI;
    }
}
