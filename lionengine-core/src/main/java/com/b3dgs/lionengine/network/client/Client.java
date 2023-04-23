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
package com.b3dgs.lionengine.network.client;

import java.io.IOException;

import com.b3dgs.lionengine.Listenable;
import com.b3dgs.lionengine.network.Message;
import com.b3dgs.lionengine.network.server.Server;

/**
 * Client representation, able to connect to a {@link Server} to exchange synchronized data with other {@link Client}.
 */
public interface Client extends Listenable<ClientListener>
{
    /**
     * Connect to server at specified network location.
     * 
     * @param ip The server ip address [x.x.x.x].
     * @param port The server port number [1-65535].
     * @throws IOException If error.
     */
    void connect(String ip, int port) throws IOException;

    /**
     * Leave server and notify disconnection.
     */
    void disconnect();

    /**
     * Send message to the server for global sync.
     * 
     * @param message The Message to send.
     * @throws IOException If error.
     */
    void send(Message message) throws IOException;

    /**
     * Ping remove server.
     * 
     * @return The ping delay in milliseconds (negative if error).
     */
    long ping();

    /**
     * Set network name.
     * 
     * @param name The name to use.
     * @throws IOException If error.
     */
    void setName(String name) throws IOException;

    /**
     * Get unique network id number, obtained after {@link #connect(String, int)}.
     * 
     * @return The network id number, -1 if not connected.
     */
    Integer getClientId();
}
