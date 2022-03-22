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
package com.b3dgs.lionengine.network.server;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

import com.b3dgs.lionengine.UtilConversion;
import com.b3dgs.lionengine.network.MessageAbstract;
import com.b3dgs.lionengine.network.MessageType;
import com.b3dgs.lionengine.network.UtilNetwork;

/**
 * Name set message.
 */
final class NameSet extends MessageAbstract
{
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

        buffer.position(UtilNetwork.INDEX_MODE);
        final int length = buffer.getInt();
        final byte[] data = new byte[length];
        buffer.get(data);
        return StandardCharsets.UTF_8.decode(ByteBuffer.wrap(data)).toString();
    }

    private final Integer clientId;
    private final String name;

    /**
     * Create message.
     * 
     * @param clientId The client id.
     * @param name The client name.
     */
    NameSet(Integer clientId, String name)
    {
        super(MessageType.NAME_SET, UtilNetwork.SERVER_ID);

        this.clientId = clientId;
        this.name = name;
    }

    @Override
    public ByteBuffer content()
    {
        final int length = name.length();
        final ByteBuffer nameBuffer = StandardCharsets.UTF_8.encode(name);
        final ByteBuffer buffer = ByteBuffer.allocate(1 + Integer.BYTES + length);

        buffer.put(UtilConversion.fromUnsignedByte(clientId.intValue()));
        buffer.putInt(length);
        buffer.put(nameBuffer);

        return buffer;
    }
}
