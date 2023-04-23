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
package com.b3dgs.lionengine.network.server;

import java.io.IOException;
import java.nio.ByteBuffer;

import com.b3dgs.lionengine.network.MessageType;
import com.b3dgs.lionengine.network.UtilNetwork;

/**
 * Info give message request.
 */
public final class Info
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

        if (size != 1 || UtilNetwork.equalsByte(buffer, UtilNetwork.INDEX_TYPE, MessageType.INFO))
        {
            throw new IOException("Invalid info request: " + size + "/" + (UtilNetwork.HEADER_BYTES_NUMBER + 1));
        }
    }

    /**
     * Private.
     */
    private Info()
    {
        super();
    }
}
