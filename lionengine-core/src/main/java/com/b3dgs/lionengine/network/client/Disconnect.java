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
import java.nio.ByteBuffer;

import com.b3dgs.lionengine.UtilConversion;
import com.b3dgs.lionengine.network.MessageAbstract;
import com.b3dgs.lionengine.network.MessageType;
import com.b3dgs.lionengine.network.UtilNetwork;

/**
 * Message disconnect implementation.
 */
public class Disconnect extends MessageAbstract
{
    private static final int SIZE_RECEIVE = SIZE_MIN + 2;

    /**
     * Decode expected message.
     * 
     * @param buffer The buffer to read.
     * @param clientId expected The client id.
     * @return The disconnected id.
     * @throws IOException If invalid.
     */
    public static Integer decode(ByteBuffer buffer, Integer clientId) throws IOException
    {
        MessageAbstract.decode(buffer, SIZE_RECEIVE, MessageType.DISCONNECT, clientId);

        if (UtilConversion.toUnsignedByte(buffer.get(UtilNetwork.INDEX_MODE)) != UtilNetwork.MODE_DISCONNECT)
        {
            throw new IOException("Invalid mode!");
        }
        return Integer.valueOf(UtilConversion.toUnsignedByte(buffer.get(UtilNetwork.INDEX_MODE + 1)));
    }

    /**
     * Create message.
     * 
     * @param clientId The client id.
     */
    public Disconnect(Integer clientId)
    {
        super(MessageType.DISCONNECT, clientId);
    }
}
