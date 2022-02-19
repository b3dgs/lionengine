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
package com.b3dgs.lionengine.network;

import java.io.IOException;
import java.nio.ByteBuffer;

import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.UtilConversion;

/**
 * Message direct implementation.
 */
public final class Direct
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
        MessageAbstract.decode(buffer, MessageType.DIRECT, clientId);
        return new Packet(clientId,
                          Integer.valueOf(UtilConversion.toUnsignedByte(buffer.get(UtilNetwork.INDEX_CLIENT_SRC_ID))),
                          buffer.getInt(UtilNetwork.INDEX_DATA_ID),
                          buffer);
    }

    /**
     * Private.
     */
    private Direct()
    {
        throw new LionEngineException(LionEngineException.ERROR_PRIVATE_CONSTRUCTOR);
    }
}
