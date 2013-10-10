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

/**
 * List of services provided by a client.
 */
public interface Client
        extends Networker
{
    /**
     * Connect to a server.
     * 
     * @param ip The server ip.
     * @param port The server port.
     */
    void connect(String ip, int port);

    /**
     * Check if the client is connected to a server.
     * 
     * @return <code>true</code> if connected, <code>false</code> else.
     */
    boolean isConnected();

    /**
     * Set a new client name, and notify the server.
     * 
     * @param name The new client name.
     */
    void setName(String name);

    /**
     * Get the client name.
     * 
     * @return The client name.
     */
    String getName();

    /**
     * Get the ping from the server (time elapsed between the ping request and response).
     * 
     * @return The ping from the server.
     */
    int getPing();

    /**
     * Get the client id.
     * 
     * @return The client id.
     */
    byte getId();
}
