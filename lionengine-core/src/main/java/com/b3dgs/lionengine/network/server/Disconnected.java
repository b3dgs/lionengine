/*
 * Copyright (C) 2013-2024 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.network.server;

import java.io.IOException;
import java.nio.ByteBuffer;

import com.b3dgs.lionengine.UtilConversion;
import com.b3dgs.lionengine.network.MessageAbstract;
import com.b3dgs.lionengine.network.MessageType;
import com.b3dgs.lionengine.network.UtilNetwork;

/**
 * Message disconnected implementation.
 */
public class Disconnected extends MessageAbstract
{
    /**
     * Decode expected message.
     * 
     * @param buffer The buffer to read.
     * @param clientId expected The client id.
     * @throws IOException If invalid.
     */
    public static void decode(ByteBuffer buffer, Integer clientId) throws IOException
    {
        MessageAbstract.decode(buffer, MessageType.DISCONNECT, clientId);
    }

    private final Integer disconnected;

    /**
     * Create message.
     * 
     * @param clientId The client id.
     * @param disconnected The disconnected client id (0 for server).
     */
    public Disconnected(Integer clientId, Integer disconnected)
    {
        super(MessageType.DISCONNECT, clientId);

        this.disconnected = disconnected;
    }

    @Override
    public ByteBuffer content()
    {
        final ByteBuffer buffer = ByteBuffer.allocate(2);
        buffer.put(UtilConversion.fromUnsignedByte(UtilNetwork.MODE_DISCONNECT));
        buffer.put(UtilConversion.fromUnsignedByte(disconnected.intValue()));
        return buffer;
    }
}
