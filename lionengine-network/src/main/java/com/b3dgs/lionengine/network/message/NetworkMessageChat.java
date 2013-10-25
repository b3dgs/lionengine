/*
 * Copyright (C) 2013 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
 */
package com.b3dgs.lionengine.network.message;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.IOException;

/**
 * Message chat implementation.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public class NetworkMessageChat
        extends NetworkMessage
{
    /** The message. */
    private String message;

    /**
     * Constructor.
     */
    public NetworkMessageChat()
    {
        super();
    }

    /**
     * Constructor.
     * 
     * @param type The message type.
     * @param clientId The client id.
     * @param message The message content.
     */
    public NetworkMessageChat(Enum<?> type, byte clientId, String message)
    {
        this(type, clientId, (byte) -1, message);
    }

    /**
     * Constructor.
     * 
     * @param type The message type.
     * @param clientId The client id.
     * @param clientDestId The client destination.
     * @param message The message content.
     */
    public NetworkMessageChat(Enum<?> type, byte clientId, byte clientDestId, String message)
    {
        super(type, clientId, clientDestId);
        this.message = message;
    }

    /**
     * Get the message.
     * 
     * @return The message reference.
     */
    public String getMessage()
    {
        return message;
    }

    /*
     * NetworkMessage
     */

    @Override
    protected void encode(ByteArrayOutputStream buffer) throws IOException
    {
        buffer.write(message.getBytes(NetworkMessage.CHARSET));
    }

    @Override
    protected void decode(DataInputStream buffer) throws IOException
    {
        final byte[] msg = new byte[buffer.available()];
        buffer.readFully(msg);
        message = new String(msg, NetworkMessage.CHARSET);
    }
}
