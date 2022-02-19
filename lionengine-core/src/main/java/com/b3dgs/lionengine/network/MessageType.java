/*
 * Copyright (C) 2013-2022 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */
package com.b3dgs.lionengine.network;

import java.nio.ByteBuffer;

import com.b3dgs.lionengine.UtilConversion;

/**
 * Network message types.
 */
public enum MessageType
{
    /** Connection order. */
    CONNECT,
    /** Disconnect order. */
    DISCONNECT,
    /** Alive periodic sent. */
    ALIVE,
    /** Ping request to check latency. */
    PING,
    /** List of connected clients. */
    CLIENTS_LIST,
    /** Direct message exchange without forwarding. */
    DIRECT,
    /** Custom user data pre message. */
    DATA,
    /** Unknown type. */
    UNKNOWN;

    private static final MessageType[] VALUES = MessageType.values();

    /**
     * Get message type from buffer.
     * 
     * @param buffer The buffer reference.
     * @return The message type found.
     */
    public static MessageType from(ByteBuffer buffer)
    {
        return from(UtilConversion.toUnsignedByte(buffer.get(UtilNetwork.INDEX_TYPE)));
    }

    /**
     * Get message type from value.
     * 
     * @param value The value reference.
     * @return The message type found.
     */
    public static MessageType from(int value)
    {
        if (value < 0 || value > VALUES.length)
        {
            return UNKNOWN;
        }
        return VALUES[value];
    }
}
