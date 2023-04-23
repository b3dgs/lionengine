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
import com.b3dgs.lionengine.network.MessageAbstract;
import com.b3dgs.lionengine.network.MessageType;
import com.b3dgs.lionengine.network.UtilNetwork;

/**
 * Info get implementation.
 */
public final class InfoGet
{
    /**
     * Decode expected message.
     * 
     * @param socket The socket to read.
     * @return The info data.
     * @throws IOException If invalid.
     */
    public static ByteBuffer decode(DatagramSocket socket) throws IOException
    {
        final DatagramPacket packet = UtilNetwork.receive(socket);
        final ByteBuffer buffer = UtilNetwork.getBuffer(packet);
        final int size = buffer.capacity();

        if (size < MessageAbstract.SIZE_MIN || UtilNetwork.equalsByte(buffer, UtilNetwork.INDEX_TYPE, MessageType.INFO))
        {
            throw new IOException("Invalid info answer ! " + size);
        }

        buffer.position(1);

        return buffer;
    }

    /**
     * Private.
     */
    private InfoGet()
    {
        throw new LionEngineException(LionEngineException.ERROR_PRIVATE_CONSTRUCTOR);
    }
}
