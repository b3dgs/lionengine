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
package com.b3dgs.lionengine.game.map;

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
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public enum TileTransition
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
    TOP(null, FALSE, null, TRUE, TRUE, TRUE, TRUE, TRUE, TRUE),

    /**
     * Transition on bottom.
     * 
     * <pre>
     * [1][1][1]
     * [1][ ][1]
     * [0][2][0]
     * </pre>
     */
    BOTTOM(TRUE, TRUE, TRUE, TRUE, TRUE, TRUE, null, FALSE, null),

    /**
     * Transition on left.
     * 
     * <pre>
     * [0][1][1]
     * [2][ ][1]
     * [0][1][1]
     * </pre>
     */
    LEFT(null, TRUE, TRUE, FALSE, TRUE, TRUE, null, TRUE, TRUE),

    /**
     * Transition on right.
     * 
     * <pre>
     * [1][1][0]
     * [1][ ][2]
     * [1][1][0]
     * </pre>
     */
    RIGHT(TRUE, TRUE, null, TRUE, TRUE, FALSE, TRUE, TRUE, null),

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
    TOP_LEFT(null, FALSE, null, FALSE, TRUE, TRUE, null, TRUE, TRUE),

    /**
     * Transition on top-right.
     * 
     * <pre>
     * [0][2][0]
     * [1][ ][2]
     * [1][1][0]
     * </pre>
     */
    TOP_RIGHT(null, FALSE, null, TRUE, TRUE, FALSE, TRUE, TRUE, null),

    /**
     * Transition on bottom-left.
     * 
     * <pre>
     * [0][1][1]
     * [2][ ][1]
     * [0][2][0]
     * </pre>
     */
    BOTTOM_LEFT(null, TRUE, TRUE, FALSE, TRUE, TRUE, null, FALSE, null),

    /**
     * Transition on bottom-right.
     * 
     * <pre>
     * [1][1][0]
     * [1][ ][2]
     * [0][2][0]
     * </pre>
     */
    BOTTOM_RIGHT(TRUE, TRUE, null, TRUE, TRUE, FALSE, null, FALSE, null),

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
    CORNER_TOP_LEFT(FALSE, TRUE, TRUE, TRUE, TRUE, TRUE, TRUE, TRUE, TRUE),

    /**
     * Transition on corner top-right.
     * 
     * <pre>
     * [1][1][2]
     * [1][ ][1]
     * [1][1][1]
     * </pre>
     */
    CORNER_TOP_RIGHT(TRUE, TRUE, FALSE, TRUE, TRUE, TRUE, TRUE, TRUE, TRUE),

    /**
     * Transition on corner down-left.
     * 
     * <pre>
     * [1][1][1]
     * [1][ ][1]
     * [2][1][1]
     * </pre>
     */
    CORNER_BOTTOM_LEFT(TRUE, TRUE, TRUE, TRUE, TRUE, TRUE, FALSE, TRUE, TRUE),

    /**
     * Transition on corner down-right.
     * 
     * <pre>
     * [1][1][1]
     * [1][ ][1]
     * [1][1][2]
     * </pre>
     */
    CORNER_BOTTOM_RIGHT(TRUE, TRUE, TRUE, TRUE, TRUE, TRUE, TRUE, TRUE, FALSE),

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
    CORNER_IN_TOP_LEFT_BOTTOM_RIGHT(FALSE, TRUE, TRUE, TRUE, TRUE, TRUE, TRUE, TRUE, FALSE),

    /**
     * Transition on corner in top-right, bottom-left.
     * 
     * <pre>
     * [1][1][2]
     * [1][ ][1]
     * [2][1][1]
     * </pre>
     */
    CORNER_IN_TOP_RIGHT_BOTTOM_LEFT(TRUE, TRUE, FALSE, TRUE, TRUE, TRUE, FALSE, TRUE, TRUE),

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
    CORNER_OUT_TOP_LEFT_BOTTOM_RIGHT(FALSE, FALSE, TRUE, FALSE, TRUE, FALSE, TRUE, FALSE, FALSE),

    /**
     * Transition on corner out top-left, bottom-right.
     * 
     * <pre>
     * [1][2][2]
     * [2][ ][2]
     * [2][2][1]
     * </pre>
     */
    CORNER_OUT_TOP_RIGHT_BOTTOM_LEFT(TRUE, FALSE, FALSE, FALSE, TRUE, FALSE, FALSE, FALSE, TRUE),

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
    TOP_MIDDLE(FALSE, FALSE, FALSE, FALSE, TRUE, FALSE, FALSE, TRUE, FALSE),

    /**
     * Transition on bottom middle.
     * 
     * <pre>
     * [2][1][2]
     * [2][ ][2]
     * [2][2][2]
     * </pre>
     */
    BOTTOM_MIDDLE(FALSE, TRUE, FALSE, FALSE, TRUE, FALSE, FALSE, FALSE, FALSE),

    /**
     * Transition on left middle.
     * 
     * <pre>
     * [2][2][2]
     * [2][ ][1]
     * [2][2][2]
     * </pre>
     */
    LEFT_MIDDLE(FALSE, FALSE, FALSE, FALSE, TRUE, TRUE, FALSE, FALSE, FALSE),

    /**
     * Transition on right middle.
     * 
     * <pre>
     * [2][2][2]
     * [1][ ][2]
     * [2][2][2]
     * </pre>
     */
    RIGHT_MIDDLE(FALSE, FALSE, FALSE, TRUE, TRUE, FALSE, FALSE, FALSE, FALSE),

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
    VERTICAL(null, TRUE, null, FALSE, TRUE, FALSE, null, TRUE, null),

    /**
     * Horizontal.
     * 
     * <pre>
     * [0][2][0]
     * [1][ ][1]
     * [0][2][0]
     * </pre>
     */
    HORIZONTAL(null, FALSE, null, TRUE, TRUE, TRUE, null, FALSE, null),

    /**
     * Angle top left.
     * 
     * <pre>
     * [2][2][0]
     * [2][ ][1]
     * [2][1][2]
     * </pre>
     */
    ANGLE_TOP_LEFT(FALSE, FALSE, null, FALSE, TRUE, TRUE, FALSE, TRUE, FALSE),

    /**
     * Angle top right.
     * 
     * <pre>
     * [2][2][0]
     * [1][ ][2]
     * [2][1][2]
     * </pre>
     */
    ANGLE_TOP_RIGHT(FALSE, FALSE, null, TRUE, TRUE, FALSE, FALSE, TRUE, FALSE),

    /**
     * Angle down left.
     * 
     * <pre>
     * [2][1][2]
     * [2][ ][1]
     * [2][2][0]
     * </pre>
     */
    ANGLE_DOWN_LEFT(FALSE, TRUE, FALSE, FALSE, TRUE, TRUE, FALSE, FALSE, null),

    /**
     * Angle down right.
     * 
     * <pre>
     * [2][1][2]
     * [1][ ][2]
     * [2][2][0]
     * </pre>
     */
    ANGLE_DOWN_RIGHT(FALSE, TRUE, FALSE, TRUE, TRUE, FALSE, FALSE, FALSE, null),

    /**
     * Diagonal top left.
     * 
     * <pre>
     * [2][2][0]
     * [2][ ][1]
     * [0][1][2]
     * </pre>
     */
    DIAGONAL_TOP_LEFT(FALSE, FALSE, null, FALSE, TRUE, TRUE, null, TRUE, FALSE),

    /**
     * Diagonal top right.
     * 
     * <pre>
     * [0][2][2]
     * [1][ ][2]
     * [2][1][0]
     * </pre>
     */
    DIAGONAL_TOP_RIGHT(null, FALSE, FALSE, TRUE, TRUE, FALSE, FALSE, TRUE, null),

    /**
     * Diagonal bottom left.
     * 
     * <pre>
     * [0][1][2]
     * [2][ ][1]
     * [2][2][0]
     * </pre>
     */
    DIAGONAL_BOTTOM_LEFT(null, TRUE, FALSE, FALSE, TRUE, TRUE, FALSE, FALSE, null),

    /**
     * Diagonal bottom right.
     * 
     * <pre>
     * [2][1][0]
     * [1][ ][2]
     * [0][2][2]
     * </pre>
     */
    DIAGONAL_BOTTOM_RIGHT(FALSE, TRUE, null, TRUE, TRUE, FALSE, null, FALSE, FALSE),

    /**
     * No transition, center part.
     * 
     * <pre>
     * [1][1][1]
     * [1][ ][1]
     * [1][1][1]
     * </pre>
     */
    CENTER(TRUE, TRUE, TRUE, TRUE, TRUE, TRUE, TRUE, TRUE, TRUE),

    /** None. */
    NONE;

    /** Bit numbers. */
    public static final int BITS = 9;

    /**
     * Get the transition representation from its bits sequence.
     * 
     * @param bits The bits sequence representing the transition.
     * @return The transition enum.
     */
    public static TileTransition from(Boolean... bits)
    {
        for (final TileTransition transition : values())
        {
            if (transition.is(bits))
            {
                return transition;
            }
        }
        return TileTransition.NONE;
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
        if (a != null && b == null || a == null && b != null)
        {
            equals = true;
        }
        else if (a != null && b != null)
        {
            equals = a.equals(b);
        }
        else
        {
            equals = true;
        }
        return equals;
    }

    /** Bit table representing the transition. */
    private final Boolean[] table;

    /**
     * Create the tile transition.
     * 
     * @param bits Bits defining transition.
     * @throws LionEngineException If invalid bits number.
     */
    TileTransition(Boolean... bits) throws LionEngineException
    {
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
}
