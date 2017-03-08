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
 * List of standard ratios.
 * <p>
 * This class is Thread-Safe.
 * </p>
 */
public final class Ratio
{
    /** Constant representing the 4/3 screen ratio. */
    public static final double R4_3 = 4.0 / 3.0;
    /** Constant representing the 5/4 screen ratio. */
    public static final double R5_4 = 5.0 / 4.0;
    /** Constant representing the 16/9 screen ratio. */
    public static final double R16_9 = 16.0 / 9.0;
    /** Constant representing the 16/10 screen ratio. */
    public static final double R16_10 = 16.0 / 10.0;

    /**
     * Get the ratio enum from its value.
     * 
     * @param ratio1 The ratio1 value.
     * @param ratio2 The ratio2 value.
     * @return <code>true</code> if equals, <code>false</code> else.
     */
    public static boolean same(double ratio1, double ratio2)
    {
        return Double.compare(ratio1, ratio2) == 0;
    }

    /**
     * Private constructor.
     */
    private Ratio()
    {
        throw new LionEngineException(LionEngineException.ERROR_PRIVATE_CONSTRUCTOR);
    }
}
