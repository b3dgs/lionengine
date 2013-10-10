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
package com.b3dgs.lionengine.network;

import com.b3dgs.lionengine.network.message.NetworkMessageDecoder;

/**
 * Network factory implementation.
 */
public final class Network
{
    /**
     * Create a server.
     * 
     * @param decoder The message type decoder.
     * @return The created server.
     */
    public static Server createServer(NetworkMessageDecoder decoder)
    {
        return new ServerImpl(decoder);
    }

    /**
     * Create a client which will be connected to a Server.
     * 
     * @param decoder The message decoder.
     * @return The created client (connected to the server).
     */
    public static Client createClient(NetworkMessageDecoder decoder)
    {
        return new ClientImpl(decoder);
    }

    /**
     * Private constructor.
     */
    private Network()
    {
        // Nothing to do
    }
}
