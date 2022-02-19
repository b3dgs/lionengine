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

import com.b3dgs.lionengine.network.MessageAbstract;
import com.b3dgs.lionengine.network.MessageType;
import com.b3dgs.lionengine.network.UtilNetwork;

/**
 * Connected message receive.
 */
public class Connected extends MessageAbstract
{
    /**
     * Decode expected message.
     * 
     * @param buffer The buffer to read.
     * @throws IOException If invalid.
     */
    public static void decode(ByteBuffer buffer) throws IOException
    {
        final int size = buffer.capacity();

        if (size != 1 || UtilNetwork.equalsByte(buffer, UtilNetwork.INDEX_TYPE, MessageType.CONNECT))
        {
            throw new IOException("Invalid connect answer: " + size + "/" + (UtilNetwork.HEADER_BYTES_NUMBER + 1));
        }
    }

    /**
     * Create message.
     * 
     * @param clientId The client id.
     */
    public Connected(Integer clientId)
    {
        super(MessageType.CONNECT, clientId);
    }
}
