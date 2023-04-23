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

import com.b3dgs.lionengine.UtilConversion;

/**
 * Message data implementation.
 */
public class Data extends MessageAbstract
{
    /**
     * Decode expected message.
     * 
     * @param buffer The buffer to read.
     * @param clientId The expected client id.
     * @return The packet read.
     * @throws IOException If invalid.
     */
    public static Packet decode(ByteBuffer buffer, Integer clientId) throws IOException
    {
        MessageAbstract.decode(buffer, MessageType.DATA, clientId);
        return new Packet(clientId,
                          Integer.valueOf(UtilConversion.toUnsignedByte(buffer.get(UtilNetwork.INDEX_CLIENT_SRC_ID))),
                          buffer.getInt(UtilNetwork.INDEX_DATA_ID),
                          buffer);
    }

    private final int dataId;
    private final ByteBuffer data;

    /**
     * Create message.
     * 
     * @param clientId The client id.
     * @param dataId The data id.
     * @param data The data.
     */
    public Data(Integer clientId, int dataId, ByteBuffer data)
    {
        this(clientId, dataId, data, false);
    }

    /**
     * Create message.
     * 
     * @param clientId The client id.
     * @param dataId The data id.
     * @param data The data.
     * @param forward <code>true</code> to forward, <code>false</code> for direct.
     */
    public Data(Integer clientId, int dataId, ByteBuffer data, boolean forward)
    {
        super(forward ? MessageType.DATA : MessageType.DIRECT, clientId);

        this.dataId = dataId;
        this.data = data;
    }

    /**
     * Get the data id.
     * 
     * @return The data id.
     */
    public int getDataId()
    {
        return dataId;
    }

    @Override
    protected ByteBuffer content()
    {
        final ByteBuffer buffer = ByteBuffer.allocate(2 + Integer.BYTES + data.capacity());
        buffer.put(UtilConversion.fromUnsignedByte(UtilNetwork.MODE_DATA));
        buffer.put(UtilConversion.fromUnsignedByte(getClientId().intValue()));
        buffer.putInt(dataId);
        buffer.put(data.array());
        return buffer;
    }
}
