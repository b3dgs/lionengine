/*
 * Copyright (C) 2013 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.example.game.network.entity;

/**
 * List of actions shared.
 */
enum MessageEntityElement
{
    /** Up flag (boolean). */
    UP,
    /** Down flag (boolean). */
    DOWN,
    /** Right flag (boolean). */
    RIGHT,
    /** Left flag (boolean). */
    LEFT,
    /** Location adjuster x (integer). */
    LOCATION_X,
    /** Location adjuster y (integer). */
    LOCATION_Y,
    /** Call jump method. */
    JUMP,
    /** Call die method. */
    DIE;

    /** Values. */
    private static final MessageEntityElement[] VALUES = MessageEntityElement.values();

    /**
     * Get the message type from its ordinal.
     * 
     * @param ordinal The ordinal.
     * @return The enum.
     */
    public static MessageEntityElement fromOrdinal(int ordinal)
    {
        return MessageEntityElement.VALUES[ordinal];
    }
}
