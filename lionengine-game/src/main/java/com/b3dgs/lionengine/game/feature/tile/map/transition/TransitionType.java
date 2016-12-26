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
package com.b3dgs.lionengine.game.feature.tile.map.transition;

import java.util.HashMap;
import java.util.Map;

import com.b3dgs.lionengine.Check;
import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.util.UtilConversion;

/**
 * Represents the list of supported borders for transition linking.
 */
public enum TransitionType
{
    /** Center. */
    CENTER(false, false, false, false),
    /** UP left. */
    UP_LEFT(false, false, false, true),
    /** Up right. */
    UP_RIGHT(false, false, true, false),
    /** Up. */
    UP(false, false, true, true),
    /** Down. */
    DOWN(true, true, false, false),
    /** Down left. */
    DOWN_LEFT(false, true, false, false),
    /** Down right. */
    DOWN_RIGHT(true, false, false, false),
    /** Left. */
    LEFT(true, false, true, false),
    /** Right. */
    RIGHT(false, true, false, true),
    /** Up left and down right. */
    UP_LEFT_DOWN_RIGHT(true, false, false, true),
    /** Up right and down left. */
    UP_RIGHT_DOWN_LEFT(false, true, true, false),
    /** Up left. */
    CORNER_UP_LEFT(true, true, true, false),
    /** Up right. */
    CORNER_UP_RIGHT(true, true, false, true),
    /** Down left. */
    CORNER_DOWN_LEFT(true, false, true, true),
    /** Down right. */
    CORNER_DOWN_RIGHT(false, true, true, true);

    /** Total bits number. */
    public static final int BITS = 4;
    /** Bit down right. */
    private static final int BIT_UP_LEFT = 0;
    /** Bit down right. */
    private static final int BIT_UP_RIGHT = 1;
    /** Bit down right. */
    private static final int BIT_DOWN_LEFT = 2;
    /** Bit down right. */
    private static final int BIT_DOWN_RIGHT = 3;
    /** Transition types mapping with their index. */
    private static final Map<Integer, TransitionType> TYPES = new HashMap<Integer, TransitionType>();
    /** Error transition name. */
    private static final String ERROR_TRANSITION_NAME = "Unknown transition name: ";

    /**
     * Stores each transition with their integer value.
     */
    static
    {
        for (final TransitionType type : values())
        {
            final Integer index = Integer.valueOf(UtilConversion.fromBinary(type.table));
            TYPES.put(index, type);
        }
    }

    /**
     * Convert transition name to its enum value.
     * 
     * @param name The transition name.
     * @return The transition enum value.
     * @throws LionEngineException If invalid name.
     */
    public static TransitionType from(String name)
    {
        try
        {
            return TransitionType.valueOf(name);
        }
        catch (final IllegalArgumentException exception)
        {
            throw new LionEngineException(exception, ERROR_TRANSITION_NAME, name);
        }
    }

    /**
     * Get the transition representation from its bits sequence.
     * 
     * @param downRight The down right flag.
     * @param downLeft The down left flag.
     * @param upRight The up right flag.
     * @param upLeft The up left flag.
     * @return The transition enum.
     */
    public static TransitionType from(boolean downRight, boolean downLeft, boolean upRight, boolean upLeft)
    {
        return from(new boolean[]
        {
            upLeft, upRight, downLeft, downRight
        });
    }

    /**
     * Get the transition representation from its bits sequence.
     * 
     * @param binary The binary array (length must be equal to {@link #BITS}).
     * @return The transition enum.
     * @throws LionEngineException If invalid array size.
     */
    public static TransitionType from(boolean[] binary)
    {
        Check.equality(binary.length, BITS);

        final Integer index = Integer.valueOf(UtilConversion.fromBinary(binary));
        if (TYPES.containsKey(index))
        {
            return TYPES.get(index);
        }
        return CENTER;
    }

    /** Bit table representing the transition. */
    private final boolean[] table;

    /**
     * Create the tile transition.
     * 
     * @param downRight The down right flag.
     * @param downLeft The down left flag.
     * @param upRight The up right flag.
     * @param upLeft The up left flag.
     */
    TransitionType(boolean downRight, boolean downLeft, boolean upRight, boolean upLeft)
    {
        table = new boolean[]
        {
            upLeft, upRight, downLeft, downRight
        };
    }

    /**
     * Get the symmetric transition.
     * 
     * @return The symmetric transition.
     */
    public TransitionType getSymetric()
    {
        return from(UtilConversion.invert(table));
    }

    /**
     * Check if the sequence of bit has the same representation of this transition.
     * 
     * @param downRight The down right flag.
     * @param downLeft The down left flag.
     * @param upRight The up right flag.
     * @param upLeft The up left flag.
     * @return <code>true</code> if equals, <code>false</code> else.
     */
    public boolean is(boolean downRight, boolean downLeft, boolean upRight, boolean upLeft)
    {
        return getUpLeft() == upLeft
               && getUpRight() == upRight
               && getDownLeft() == downLeft
               && getDownRight() == downRight;
    }

    /**
     * Get bit at up left.
     * 
     * @return The up left bit.
     */
    public boolean getUpLeft()
    {
        return table[BIT_UP_LEFT];
    }

    /**
     * Get bit at up right.
     * 
     * @return The up right bit.
     */
    public boolean getUpRight()
    {
        return table[BIT_UP_RIGHT];
    }

    /**
     * Get bit at down left.
     * 
     * @return The down left bit.
     */
    public boolean getDownLeft()
    {
        return table[BIT_DOWN_LEFT];
    }

    /**
     * Get bit at down right.
     * 
     * @return The down right bit.
     */
    public boolean getDownRight()
    {
        return table[BIT_DOWN_RIGHT];
    }
}
