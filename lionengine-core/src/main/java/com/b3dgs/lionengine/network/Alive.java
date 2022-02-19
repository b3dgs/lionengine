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

/**
 * Message alive implementation.
 */
public class Alive extends MessageAbstract
{
    /**
     * Decode expected message.
     * 
     * @param buffer The buffer to read.
     * @param clientId The expected client id.
     * @throws IOException If invalid.
     */
    public static void decode(ByteBuffer buffer, Integer clientId) throws IOException
    {
        MessageAbstract.decode(buffer, SIZE_MIN, MessageType.ALIVE, clientId);
    }

    /**
     * Create message.
     * 
     * @param clientId The client id.
     */
    public Alive(Integer clientId)
    {
        super(MessageType.ALIVE, clientId);
    }
}
