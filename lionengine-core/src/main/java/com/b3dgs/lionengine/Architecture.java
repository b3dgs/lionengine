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
package com.b3dgs.lionengine;

/**
 * List of standard architectures.
 * <p>
 * This class is Thread-Safe.
 * </p>
 */
public enum Architecture
{
    /** 32 bits. */
    X86,
    /** 64 bits. */
    X64,
    /** Unknown architecture. */
    UNKNOWN;

    /** Unknown item. */
    private static final String DEFAULT = "unknown";
    /** The architecture used. */
    private static final Architecture ARCHI = Architecture.find(Constant.getSystemProperty("sun.arch.data.model",
                                                                                           DEFAULT));

    /**
     * Find the current architecture.
     * 
     * @param arch The architecture short name (usually <code>32</code> or <code>86</code> or <code>64</code>).
     * @return The current architecture.
     */
    public static Architecture find(String arch)
    {
        final Architecture architecture;
        if (arch == null)
        {
            architecture = Architecture.UNKNOWN;
        }
        else if (arch.contains("64"))
        {
            architecture = Architecture.X64;
        }
        else if (arch.contains("32") || arch.contains("86"))
        {
            architecture = Architecture.X86;
        }
        else
        {
            architecture = Architecture.UNKNOWN;
        }
        return architecture;
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
