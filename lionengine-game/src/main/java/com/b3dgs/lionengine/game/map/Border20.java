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

/**
 * Represents the list of supported angles for area linking.
 * 
 * @see Border20Map
 */
public enum Border20
{
    /** Top. */
    TOP,
    /** Right. */
    RIGHT,
    /** Down. */
    DOWN,
    /** Left. */
    LEFT,
    /** Top left. */
    TOP_LEFT,
    /** Top right. */
    TOP_RIGHT,
    /** Down right. */
    DOWN_RIGHT,
    /** Down left. */
    DOWN_LEFT,
    /** Corner top left. */
    CORNER_TOP_LEFT,
    /** Corner top right. */
    CORNER_TOP_RIGHT,
    /** Corner down right. */
    CORNER_DOWN_RIGHT,
    /** Corner down left. */
    CORNER_DOWN_LEFT,
    /** Corner out top down. */
    CORNER_OUT_TOP_DOWN,
    /** Corner out down top. */
    CORNER_OUT_DOWN_TOP,
    /** Corner in down top. */
    CORNER_IN_DOWN_TOP,
    /** Corner in top down. */
    CORNER_IN_TOP_DOWN,
    /** Center. */
    CENTER,
    /** Top middle. */
    TOP_MIDDLE,
    /** Down middle. */
    DOWN_MIDDLE,
    /** Right middle. */
    RIGHT_MIDDLE,
    /** Left middle. */
    LEFT_MIDDLE,
    /** Unknown. */
    UNKNOWN,
    /** None. */
    NONE;
}
