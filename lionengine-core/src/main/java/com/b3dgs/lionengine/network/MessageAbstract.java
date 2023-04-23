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
package com.b3dgs.lionengine.network;

import java.io.IOException;
import java.nio.ByteBuffer;

import com.b3dgs.lionengine.Constant;
import com.b3dgs.lionengine.UtilConversion;

/**
 * Base message implementation with message type and client id as minimum content.
 */
public abstract class MessageAbstract implements Message
{
    /** Minimum packet size excluding header. */
    public static final int SIZE_MIN = 2;

    /** Error answer. */
    private static final String ERROR_ANSWER = "Invalid answer: ";
    /** Answer size inferior. */
    private static final String INFERIOR = "<";
    /** Answer size inferior. */
    private static final String TYPE = " type=";
    /** Client id. */
    private static final String CLIENT_ID = " clientId=";

    /**
     * Decode expected message.
     * 
     * @param buffer The buffer to read.
     * @param type The expected message type.
     * @param clientId The expected client id.
     * @return The packet size.
     * @throws IOException If invalid.
     */
    public static int decode(ByteBuffer buffer, MessageType type, Integer clientId) throws IOException
    {
        final int size = buffer.capacity();

        if (size < SIZE_MIN
            || UtilNetwork.equalsByte(buffer, UtilNetwork.INDEX_TYPE, type)
            || UtilNetwork.equalsByte(buffer, UtilNetwork.INDEX_CLIENT_ID, clientId.intValue()))
        {
            throw new IOException(ERROR_ANSWER
                                  + size
                                  + INFERIOR
                                  + SIZE_MIN
                                  + TYPE
                                  + type.ordinal()
                                  + Constant.SPACE
                                  + UtilConversion.toUnsignedByte(buffer.get(UtilNetwork.INDEX_TYPE))
                                  + CLIENT_ID
                                  + clientId
                                  + Constant.SPACE
                                  + +UtilConversion.toUnsignedByte(buffer.get(UtilNetwork.INDEX_CLIENT_ID)));
        }
        return size;
    }

    /**
     * Decode expected message.
     * 
     * @param buffer The buffer to read.
     * @param size The expected packet size.
     * @param type The expected message type.
     * @param clientId The expected client id.
     * @throws IOException If invalid.
     */
    public static void decode(ByteBuffer buffer, int size, MessageType type, Integer clientId) throws IOException
    {
        final int packetSize = buffer.capacity();

        if (packetSize != size
            || UtilNetwork.equalsByte(buffer, UtilNetwork.INDEX_TYPE, type)
            || UtilNetwork.equalsByte(buffer, UtilNetwork.INDEX_CLIENT_ID, clientId.intValue()))
        {
            throw new IOException(ERROR_ANSWER
                                  + packetSize
                                  + " != "
                                  + size
                                  + TYPE
                                  + UtilConversion.toUnsignedByte(buffer.get(UtilNetwork.INDEX_TYPE))
                                  + CLIENT_ID
                                  + clientId
                                  + " "
                                  + UtilConversion.toUnsignedByte(buffer.get(UtilNetwork.INDEX_CLIENT_ID)));
        }
    }

    private final MessageType type;
    private final Integer clientId;

    /**
     * Create message.
     * 
     * @param type The message type.
     * @param clientId The client id.
     */
    protected MessageAbstract(MessageType type, Integer clientId)
    {
        super();

        this.type = type;
        this.clientId = clientId;
    }

    /**
     * Get the id.
     * 
     * @return The id.
     */
    public Integer getClientId()
    {
        return clientId;
    }

    /**
     * Create message content.
     * 
     * @return The message content.
     */
    protected ByteBuffer content()
    {
        return ByteBuffer.allocate(0);
    }

    /*
     * Message
     */

    @Override
    public ByteBuffer create()
    {
        final ByteBuffer content = content();

        final ByteBuffer buffer = ByteBuffer.allocate(SIZE_MIN + content.capacity());
        buffer.put(UtilNetwork.toByte(type));
        buffer.put(UtilConversion.fromUnsignedByte(clientId.intValue()));
        buffer.put(content.array());

        return buffer;
    }
}
