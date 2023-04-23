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
import java.nio.charset.StandardCharsets;

import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.UtilConversion;
import com.b3dgs.lionengine.network.MessageAbstract;
import com.b3dgs.lionengine.network.MessageType;
import com.b3dgs.lionengine.network.UtilNetwork;

/**
 * Name set message.
 */
final class NameSet
{
    /**
     * Encode message.
     * 
     * @param clientId The client id.
     * @param name The name.
     * @return The created message.
     */
    public static ByteBuffer encode(Integer clientId, String name)
    {
        final int length = name.length();
        final ByteBuffer nameBuffer = StandardCharsets.UTF_8.encode(name);
        final ByteBuffer buffer = ByteBuffer.allocate(2 + Integer.BYTES + length);

        buffer.put(UtilNetwork.toByte(MessageType.NAME_SET));
        buffer.put(UtilConversion.fromUnsignedByte(clientId.intValue()));
        buffer.putInt(length);
        buffer.put(nameBuffer);

        return UtilNetwork.createPacket(buffer);
    }

    /**
     * Decode expected message.
     * 
     * @param buffer The buffer to read.
     * @param clientId The client id.
     * @return The connect id.
     * @throws IOException If invalid.
     */
    public static String decode(ByteBuffer buffer, Integer clientId) throws IOException
    {
        final int size = buffer.capacity();

        MessageAbstract.decode(buffer, size, MessageType.NAME_SET, clientId);

        buffer.position(UtilNetwork.INDEX_MODE + 1);
        final int length = buffer.getInt();
        final byte[] data = new byte[length];
        buffer.get(data);
        return StandardCharsets.UTF_8.decode(ByteBuffer.wrap(data)).toString();
    }

    /**
     * Private.
     */
    private NameSet()
    {
        throw new LionEngineException(LionEngineException.ERROR_PRIVATE_CONSTRUCTOR);
    }
}
