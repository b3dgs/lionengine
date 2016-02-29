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
package com.b3dgs.lionengine.game.map.transition;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;

import com.b3dgs.lionengine.LionEngineException;

/**
 * Represents the list of supported angles for transition linking.
 * <p>
 * Each enum will show an example, assuming:
 * </p>
 * <ul>
 * <li><code>[ ]</code> represents the transition position of the group.</li>
 * <li><code>[1]</code> represents the group in.</li>
 * <li><code>[2]</code> represents the group out.</li>
 * <li><code>[0]</code> represents both in or out.</li>
 * </ul>
 */
public enum TransitionType
{
    /*
     * Horizontal - Vertical
     */

    /**
     * Transition on top.
     * 
     * <pre>
     * [0][2][0]
     * [1][ ][1]
     * [1][1][1]
     * </pre>
     */
    TOP(true, null, FALSE, null, TRUE, TRUE, TRUE, TRUE, TRUE, TRUE),

    /**
     * Transition on bottom.
     * 
     * <pre>
     * [1][1][1]
     * [1][ ][1]
     * [0][2][0]
     * </pre>
     */
    BOTTOM(true, TRUE, TRUE, TRUE, TRUE, TRUE, TRUE, null, FALSE, null),

    /**
     * Transition on left.
     * 
     * <pre>
     * [0][1][1]
     * [2][ ][1]
     * [0][1][1]
     * </pre>
     */
    LEFT(true, null, TRUE, TRUE, FALSE, TRUE, TRUE, null, TRUE, TRUE),

    /**
     * Transition on right.
     * 
     * <pre>
     * [1][1][0]
     * [1][ ][2]
     * [1][1][0]
     * </pre>
     */
    RIGHT(true, TRUE, TRUE, null, TRUE, TRUE, FALSE, TRUE, TRUE, null),

    /*
     * Borders
     */

    /**
     * Transition on top-left.
     * 
     * <pre>
     * [0][2][0]
     * [2][ ][1]
     * [0][1][1]
     * </pre>
     */
    TOP_LEFT(true, null, FALSE, null, FALSE, TRUE, TRUE, null, TRUE, TRUE),

    /**
     * Transition on top-right.
     * 
     * <pre>
     * [0][2][0]
     * [1][ ][2]
     * [1][1][0]
     * </pre>
     */
    TOP_RIGHT(true, null, FALSE, null, TRUE, TRUE, FALSE, TRUE, TRUE, null),

    /**
     * Transition on bottom-left.
     * 
     * <pre>
     * [0][1][1]
     * [2][ ][1]
     * [0][2][0]
     * </pre>
     */
    BOTTOM_LEFT(true, null, TRUE, TRUE, FALSE, TRUE, TRUE, null, FALSE, null),

    /**
     * Transition on bottom-right.
     * 
     * <pre>
     * [1][1][0]
     * [1][ ][2]
     * [0][2][0]
     * </pre>
     */
    BOTTOM_RIGHT(true, TRUE, TRUE, null, TRUE, TRUE, FALSE, null, FALSE, null),

    /*
     * Corners
     */

    /**
     * Transition on corner top-left.
     * 
     * <pre>
     * [2][1][1]
     * [1][ ][1]
     * [1][1][1]
     * </pre>
     */
    CORNER_TOP_LEFT(false, FALSE, TRUE, TRUE, TRUE, TRUE, TRUE, TRUE, TRUE, TRUE),

    /**
     * Transition on corner top-right.
     * 
     * <pre>
     * [1][1][2]
     * [1][ ][1]
     * [1][1][1]
     * </pre>
     */
    CORNER_TOP_RIGHT(false, TRUE, TRUE, FALSE, TRUE, TRUE, TRUE, TRUE, TRUE, TRUE),

    /**
     * Transition on corner down-left.
     * 
     * <pre>
     * [1][1][1]
     * [1][ ][1]
     * [2][1][1]
     * </pre>
     */
    CORNER_BOTTOM_LEFT(false, TRUE, TRUE, TRUE, TRUE, TRUE, TRUE, FALSE, TRUE, TRUE),

    /**
     * Transition on corner down-right.
     * 
     * <pre>
     * [1][1][1]
     * [1][ ][1]
     * [1][1][2]
     * </pre>
     */
    CORNER_BOTTOM_RIGHT(false, TRUE, TRUE, TRUE, TRUE, TRUE, TRUE, TRUE, TRUE, FALSE),

    /*
     * Corner inside
     */

    /**
     * Transition on corner in top-left, bottom-right.
     * 
     * <pre>
     * [2][1][1]
     * [1][ ][1]
     * [1][1][2]
     * </pre>
     */
    CORNER_IN_TOP_LEFT_BOTTOM_RIGHT(false, FALSE, TRUE, TRUE, TRUE, TRUE, TRUE, TRUE, TRUE, FALSE),

    /**
     * Transition on corner in top-right, bottom-left.
     * 
     * <pre>
     * [1][1][2]
     * [1][ ][1]
     * [2][1][1]
     * </pre>
     */
    CORNER_IN_TOP_RIGHT_BOTTOM_LEFT(false, TRUE, TRUE, FALSE, TRUE, TRUE, TRUE, FALSE, TRUE, TRUE),

    /*
     * Corners outside
     */

    /**
     * Transition on corner out top-right, bottom-left.
     * 
     * <pre>
     * [2][2][1]
     * [2][ ][2]
     * [1][2][2]
     * </pre>
     */
    CORNER_OUT_TOP_LEFT_BOTTOM_RIGHT(false, FALSE, FALSE, TRUE, FALSE, TRUE, FALSE, TRUE, FALSE, FALSE),

    /**
     * Transition on corner out top-left, bottom-right.
     * 
     * <pre>
     * [1][2][2]
     * [2][ ][2]
     * [2][2][1]
     * </pre>
     */
    CORNER_OUT_TOP_RIGHT_BOTTOM_LEFT(false, TRUE, FALSE, FALSE, FALSE, TRUE, FALSE, FALSE, FALSE, TRUE),

    /*
     * Horizontal - Vertical Middle
     */

    /**
     * Transition on top middle.
     * 
     * <pre>
     * [2][2][2]
     * [2][ ][2]
     * [2][1][2]
     * </pre>
     */
    TOP_MIDDLE(false, FALSE, FALSE, FALSE, FALSE, TRUE, FALSE, FALSE, TRUE, FALSE),

    /**
     * Transition on bottom middle.
     * 
     * <pre>
     * [2][1][2]
     * [2][ ][2]
     * [2][2][2]
     * </pre>
     */
    BOTTOM_MIDDLE(false, FALSE, TRUE, FALSE, FALSE, TRUE, FALSE, FALSE, FALSE, FALSE),

    /**
     * Transition on left middle.
     * 
     * <pre>
     * [2][2][2]
     * [2][ ][1]
     * [2][2][2]
     * </pre>
     */
    LEFT_MIDDLE(false, FALSE, FALSE, FALSE, FALSE, TRUE, TRUE, FALSE, FALSE, FALSE),

    /**
     * Transition on right middle.
     * 
     * <pre>
     * [2][2][2]
     * [1][ ][2]
     * [2][2][2]
     * </pre>
     */
    RIGHT_MIDDLE(false, FALSE, FALSE, FALSE, TRUE, TRUE, FALSE, FALSE, FALSE, FALSE),

    /*
     * Axis
     */

    /**
     * Vertical.
     * 
     * <pre>
     * [0][1][0]
     * [2][ ][2]
     * [0][1][0]
     * </pre>
     */
    VERTICAL(false, null, TRUE, null, FALSE, TRUE, FALSE, null, TRUE, null),

    /**
     * Horizontal.
     * 
     * <pre>
     * [0][2][0]
     * [1][ ][1]
     * [0][2][0]
     * </pre>
     */
    HORIZONTAL(false, null, FALSE, null, TRUE, TRUE, TRUE, null, FALSE, null),

    /**
     * Angle top left.
     * 
     * <pre>
     * [2][2][0]
     * [2][ ][1]
     * [2][1][2]
     * </pre>
     */
    ANGLE_TOP_LEFT(false, FALSE, FALSE, null, FALSE, TRUE, TRUE, FALSE, TRUE, FALSE),

    /**
     * Angle top right.
     * 
     * <pre>
     * [2][2][0]
     * [1][ ][2]
     * [2][1][2]
     * </pre>
     */
    ANGLE_TOP_RIGHT(false, FALSE, FALSE, null, TRUE, TRUE, FALSE, FALSE, TRUE, FALSE),

    /**
     * Angle down left.
     * 
     * <pre>
     * [2][1][2]
     * [2][ ][1]
     * [2][2][0]
     * </pre>
     */
    ANGLE_DOWN_LEFT(false, FALSE, TRUE, FALSE, FALSE, TRUE, TRUE, FALSE, FALSE, null),

    /**
     * Angle down right.
     * 
     * <pre>
     * [2][1][2]
     * [1][ ][2]
     * [2][2][0]
     * </pre>
     */
    ANGLE_DOWN_RIGHT(false, FALSE, TRUE, FALSE, TRUE, TRUE, FALSE, FALSE, FALSE, null),

    /**
     * Diagonal top left.
     * 
     * <pre>
     * [2][2][0]
     * [2][ ][1]
     * [0][1][2]
     * </pre>
     */
    DIAGONAL_TOP_LEFT(false, FALSE, FALSE, null, FALSE, TRUE, TRUE, null, TRUE, FALSE),

    /**
     * Diagonal top right.
     * 
     * <pre>
     * [0][2][2]
     * [1][ ][2]
     * [2][1][0]
     * </pre>
     */
    DIAGONAL_TOP_RIGHT(false, null, FALSE, FALSE, TRUE, TRUE, FALSE, FALSE, TRUE, null),

    /**
     * Diagonal bottom left.
     * 
     * <pre>
     * [0][1][2]
     * [2][ ][1]
     * [2][2][0]
     * </pre>
     */
    DIAGONAL_BOTTOM_LEFT(false, null, TRUE, FALSE, FALSE, TRUE, TRUE, FALSE, FALSE, null),

    /**
     * Diagonal bottom right.
     * 
     * <pre>
     * [2][1][0]
     * [1][ ][2]
     * [0][2][2]
     * </pre>
     */
    DIAGONAL_BOTTOM_RIGHT(false, FALSE, TRUE, null, TRUE, TRUE, FALSE, null, FALSE, FALSE),

    /**
     * No transition, center part.
     * 
     * <pre>
     * [1][1][1]
     * [1][ ][1]
     * [1][1][1]
     * </pre>
     */
    CENTER(false, TRUE, TRUE, TRUE, TRUE, TRUE, TRUE, TRUE, TRUE, TRUE),

    /** None. */
    NONE(false);

    /** Bit numbers. */
    public static final int BITS = 9;
    /** Area transitions count. Number of transition for area linking (horizontal, vertical and borders). */
    public static final int AREA_TRANSITIONS = 8;
    /** Error transition name. */
    private static final String ERROR_TRANSITION_NAME = "Unknown transition name: ";

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
     * @param bits The bits sequence representing the transition.
     * @return The transition enum.
     */
    public static TransitionType from(Boolean... bits)
    {
        for (final TransitionType transition : values())
        {
            if (transition.is(bits))
            {
                return transition;
            }
        }
        return TransitionType.NONE;
    }

    /**
     * Get the transition type from its bytes array.
     * 
     * @param bytes The bytes array.
     * @param inverted <code>true</code> to get inverted transition, <code>false</code> for normal.
     * @return The transition type.
     */
    public static TransitionType from(Boolean[] bytes, boolean inverted)
    {
        if (inverted)
        {
            final Boolean[] bitsInv = new Boolean[bytes.length];
            for (int j = 0; j < bitsInv.length; j++)
            {
                bitsInv[j] = bytes[bytes.length - j - 1];
            }
            return TransitionType.from(bitsInv);
        }
        return TransitionType.from(bytes);
    }

    /**
     * Check if equals, considering <code>null</code> as possible.
     * 
     * @param a The first boolean.
     * @param b The second boolean.
     * @return <code>true</code> if equals (same value or both <code>null</code>), <code>false</code> else.
     */
    private static boolean equalsOrNull(Boolean a, Boolean b)
    {
        final boolean equals;
        if (a != null && b != null)
        {
            equals = a.equals(b);
        }
        else
        {
            equals = true;
        }
        return equals;
    }

    /** Area flag. */
    private final boolean area;
    /** Bit table representing the transition. */
    private final Boolean[] table;

    /**
     * Create the tile transition.
     * 
     * @param area <code>true</code> if represents the minimum transition area, <code>false</code> else.
     * @param bits Bits defining transition.
     * @throws LionEngineException If invalid bits number.
     */
    TransitionType(boolean area, Boolean... bits)
    {
        this.area = area;
        table = bits;
    }

    /**
     * Check if the sequence of bit has the same representation of this transition.
     * 
     * @param bits The bits sequence.
     * @return <code>true</code> if equals, <code>false</code> else.
     */
    public boolean is(Boolean... bits)
    {
        final boolean equals;
        if (table.length != bits.length)
        {
            equals = false;
        }
        else
        {
            for (int i = 0; i < table.length; i++)
            {
                if (!equalsOrNull(table[i], bits[i]))
                {
                    return false;
                }
            }
            equals = true;
        }
        return equals;
    }

    /**
     * Get the reversed transition type.
     * 
     * @return The reversed transition type.
     */
    public TransitionType reverse()
    {
        return from(table, true);
    }

    /**
     * Check if transition is area.
     * 
     * @return <code>true</code> if area, <code>false</code> else.
     */
    public boolean isArea()
    {
        return area;
    }
}
