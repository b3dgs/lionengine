/*
 * Copyright (C) 2013-2023 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.network.client;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.nio.ByteBuffer;

import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.UtilConversion;
import com.b3dgs.lionengine.network.MessageAbstract;
import com.b3dgs.lionengine.network.MessageType;
import com.b3dgs.lionengine.network.UtilNetwork;

/**
 * Connect implementation.
 */
public final class Connect
{
    /**
     * Encode message.
     * 
     * @return The created message.
     */
    public static ByteBuffer encode()
    {
        final ByteBuffer buffer = ByteBuffer.allocate(1);
        buffer.put(UtilNetwork.toByte(MessageType.CONNECT));

        return UtilNetwork.createPacket(buffer);
    }

    /**
     * Decode expected message.
     * 
     * @param socket The socket to read.
     * @return The connect id.
     * @throws IOException If invalid.
     */
    public static Integer decode(DatagramSocket socket) throws IOException
    {
        final DatagramPacket packet = UtilNetwork.receive(socket);
        final ByteBuffer buffer = UtilNetwork.getBuffer(packet);
        final int size = buffer.capacity();

        if (size != MessageAbstract.SIZE_MIN
            || UtilNetwork.equalsByte(buffer, UtilNetwork.INDEX_TYPE, MessageType.CONNECT))
        {
            throw new IOException("Invalid connect answer !");
        }

        return Integer.valueOf(UtilConversion.toUnsignedByte(buffer.get(UtilNetwork.INDEX_CLIENT_ID)));
    }

    /**
     * Private.
     */
    private Connect()
    {
        throw new LionEngineException(LionEngineException.ERROR_PRIVATE_CONSTRUCTOR);
    }
}
