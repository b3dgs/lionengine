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

    /** Operating system name. */
    private static final String SYSTEM_OS;
    /** System architecture. */
    private static final String SYSTEM_ARCHI;
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
        SYSTEM_OS = EngineCore.getSystemProperty("os.name", DEFAULT).toLowerCase(Locale.getDefault());
        OS = findOs();
        SYSTEM_ARCHI = EngineCore.getSystemProperty("sun.arch.data.model", DEFAULT);
        ARCHI = findArchitecture();
    }

    /**
     * Find the current OS.
     * 
     * @return The OS found.
     */
    private static OperatingSystem findOs()
    {
        if (SYSTEM_OS.indexOf("win") >= 0)
        {
            return WINDOWS;
        }
        else if (SYSTEM_OS.indexOf("mac") >= 0)
        {
            return MAC;
        }
        else if (SYSTEM_OS.indexOf("nix") >= 0 || SYSTEM_OS.indexOf("nux") >= 0 || SYSTEM_OS.indexOf("bsd") >= 0
                || SYSTEM_OS.indexOf("aix") >= 0)
        {
            return UNIX;
        }
        else if (SYSTEM_OS.indexOf("sunos") >= 0)
        {
            return SOLARIS;
        }
        else
        {
            return UNKNOWN;
        }
    }

    /**
     * Find the current architecture.
     * 
     * @return The current architecture.
     */
    private static Architecture findArchitecture()
    {
        if (SYSTEM_ARCHI.contains("64"))
        {
            return Architecture.X64;
        }
        else if (SYSTEM_ARCHI.contains("32") || SYSTEM_ARCHI.contains("86"))
        {
            return Architecture.X86;
        }
        else
        {
            return Architecture.UNKNOWN;
        }
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
