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
package com.b3dgs.lionengine.game.feature.tile.map.transition.circuit;

import java.util.HashMap;
import java.util.Map;

import com.b3dgs.lionengine.Check;
import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.util.UtilConversion;

/**
 * Represents the list of supported circuit parts.
 * <p>
 * <code>true</code> represents the existence of another circuit part, <code>false</code> no circuit part.
 * </p>
 */
public enum CircuitType
{
    /** Top ending. */
    TOP(false, false, true, false),
    /** Bottom ending. */
    BOTTOM(true, false, false, false),
    /** Left ending. */
    LEFT(false, false, false, true),
    /** Right ending. */
    RIGHT(false, true, false, false),
    /** Horizontal rail. */
    HORIZONTAL(false, true, false, true),
    /** Vertical rail. */
    VERTICAL(true, false, true, false),
    /** Angle top left ending. */
    ANGLE_TOP_LEFT(false, false, true, true),
    /** Angle top right ending. */
    ANGLE_TOP_RIGHT(false, true, true, false),
    /** Angle bottom left ending. */
    ANGLE_BOTTOM_LEFT(true, false, false, true),
    /** Angle bottom right ending. */
    ANGLE_BOTTOM_RIGHT(true, true, false, false),
    /** Three way junction top ending. */
    T3J_TOP(false, true, true, true),
    /** Three way junction bottom ending. */
    T3J_BOTTOM(true, true, false, true),
    /** Three way junction left ending. */
    T3J_LEFT(true, false, true, true),
    /** Three way junction right ending. */
    T3J_RIGHT(true, true, true, false),
    /** Middle rail. */
    MIDDLE(true, true, true, true),
    /** Block ending. */
    BLOCK(false, false, false, false);

    /** Total bits number. */
    public static final int BITS = 4;
    /** Bit right. */
    private static final int BIT_RIGHT = 0;
    /** Bit bottom. */
    private static final int BIT_BOTTOM = 1;
    /** Bit left. */
    private static final int BIT_LEFT = 2;
    /** Bit top. */
    private static final int BIT_TOP = 3;

    /** Circuit part types mapping with their index. */
    private static final Map<Integer, CircuitType> TYPES = new HashMap<Integer, CircuitType>();
    /** Error circuit name. */
    private static final String ERROR_CIRCUIT_NAME = "Unknown circuit part name: ";

    /**
     * Stores each circuit parts with their integer value.
     */
    static
    {
        for (final CircuitType type : values())
        {
            final Integer index = Integer.valueOf(UtilConversion.fromBinary(type.table));
            TYPES.put(index, type);
        }
    }

    /**
     * Convert circuit part name to its enum value.
     * 
     * @param name The circuit part name.
     * @return The circuit part enum value.
     * @throws LionEngineException If invalid name.
     */
    public static CircuitType from(String name)
    {
        try
        {
            return CircuitType.valueOf(name);
        }
        catch (final IllegalArgumentException exception)
        {
            throw new LionEngineException(exception, ERROR_CIRCUIT_NAME, name);
        }
    }

    /**
     * Get the circuit part from its bits sequence.
     * 
     * @param top The top right flag.
     * @param left The left flag.
     * @param bottom The bottom flag.
     * @param right The right flag.
     * @return The circuit part enum.
     */
    public static CircuitType from(boolean top, boolean left, boolean bottom, boolean right)
    {
        return from(new boolean[]
        {
            right, bottom, left, top
        });
    }

    /**
     * Get the circuit part from its bits sequence.
     * 
     * @param binary The binary array (length must be equal to {@link #BITS}).
     * @return The circuit part enum.
     * @throws LionEngineException If invalid array size.
     */
    public static CircuitType from(boolean[] binary)
    {
        Check.equality(binary.length, BITS);

        final Integer index = Integer.valueOf(UtilConversion.fromBinary(binary));
        return TYPES.get(index);
    }

    /** Bit table representing the circuit part. */
    private final boolean[] table;

    /**
     * Create the circuit part.
     * 
     * @param top The top right flag.
     * @param left The left flag.
     * @param bottom The bottom flag.
     * @param right The right flag.
     */
    CircuitType(boolean top, boolean left, boolean bottom, boolean right)
    {
        table = new boolean[]
        {
            right, bottom, left, top
        };
    }

    /**
     * Get the symmetric circuit.
     * 
     * @return The symmetric circuit.
     */
    public CircuitType getSymetric()
    {
        return from(UtilConversion.invert(table));
    }

    /**
     * Check if is the circuit part.
     * 
     * @param top The top right flag.
     * @param left The left flag.
     * @param bottom The bottom flag.
     * @param right The right flag.
     * @return <code>true</code> if is circuit part, <code>false</code> else.
     */
    public boolean is(boolean top, boolean left, boolean bottom, boolean right)
    {
        return getTop() == top && getLeft() == left && getBottom() == bottom && getRight() == right;
    }

    /**
     * Get bit at top.
     * 
     * @return The top bit.
     */
    public boolean getTop()
    {
        return table[BIT_TOP];
    }

    /**
     * Get bit at left.
     * 
     * @return The left bit.
     */
    public boolean getLeft()
    {
        return table[BIT_LEFT];
    }

    /**
     * Get bit at bottom.
     * 
     * @return The bottom bit.
     */
    public boolean getBottom()
    {
        return table[BIT_BOTTOM];
    }

    /**
     * Get bit at right.
     * 
     * @return The right bit.
     */
    public boolean getRight()
    {
        return table[BIT_RIGHT];
    }
}
