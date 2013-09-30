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
package com.b3dgs.lionengine.network.world;

import com.b3dgs.lionengine.network.ConnectionListener;

/**
 * Networkable world interface client side.
 */
public interface NetworkedWorldClient
        extends NetworkedWorld, ConnectionListener
{
    /**
     * Connect to a server.
     * 
     * @param ip The server ip.
     * @param port The server port.
     */
    void connect(String ip, int port);

    /**
     * Set the client new name.
     * 
     * @param name The new name.
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
     * Get the amount of bytes sent per second.
     * 
     * @return The number of bytes sent per second.
     */
    int getBandwidth();

    /**
     * Get the network id.
     * 
     * @return The network id.
     */
    byte getId();
}
