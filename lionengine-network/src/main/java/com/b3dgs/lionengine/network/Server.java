/*
 * Copyright (C) 2013-2014 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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

import com.b3dgs.lionengine.LionEngineException;

/**
 * List of services provided by a server.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
interface Server
        extends Networker<ClientListener>
{
    /**
     * Set the message of the day (sent to a new connected client).
     * 
     * @param message The message.
     */
    void setMessageOfTheDay(String message);

    /**
     * Start the server and listen to client connection.
     * 
     * @param name The server name.
     * @param port The port number.
     * @throws LionEngineException If unable to start server.
     */
    void start(String name, int port) throws LionEngineException;

    /**
     * Remove a client from its id.
     * 
     * @param clientId The client id.
     */
    void removeClient(Byte clientId);

    /**
     * Get the number of client.
     * 
     * @return The number of clients.
     */
    int getNumberOfClients();

    /**
     * Get the server port.
     * 
     * @return The server port.
     */
    int getPort();
}
