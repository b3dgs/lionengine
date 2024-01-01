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
package com.b3dgs.lionengine.network.client;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.HashSet;
import java.util.Set;

import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.UtilConversion;
import com.b3dgs.lionengine.network.MessageAbstract;
import com.b3dgs.lionengine.network.MessageType;
import com.b3dgs.lionengine.network.UtilNetwork;

/**
 * Message clients list implementation.
 */
public final class ClientsList
{
    /**
     * Decode expected message.
     * 
     * @param buffer The buffer to read.
     * @param clientId The expected client id.
     * @return The packet read.
     * @throws IOException If invalid.
     */
    public static Set<Integer> decode(ByteBuffer buffer, Integer clientId) throws IOException
    {
        final int size = buffer.capacity();

        if (size < MessageAbstract.SIZE_MIN
            || UtilNetwork.equalsByte(buffer, UtilNetwork.INDEX_TYPE, MessageType.CLIENTS_LIST)
            || UtilNetwork.equalsByte(buffer, UtilNetwork.INDEX_CLIENT_ID, clientId.intValue()))
        {
            throw new IOException("Invalid clients list answer: "
                                  + clientId
                                  + " "
                                  + UtilConversion.toUnsignedByte(buffer.get(UtilNetwork.INDEX_CLIENT_ID)));
        }

        buffer.position(MessageAbstract.SIZE_MIN);
        final int n = size - MessageAbstract.SIZE_MIN;
        final Set<Integer> ids = new HashSet<>(n);
        for (int i = 0; i < n; i++)
        {
            ids.add(Integer.valueOf(UtilConversion.toUnsignedByte(buffer.get())));
        }
        return ids;
    }

    /**
     * Private.
     */
    private ClientsList()
    {
        throw new LionEngineException(LionEngineException.ERROR_PRIVATE_CONSTRUCTOR);
    }
}
