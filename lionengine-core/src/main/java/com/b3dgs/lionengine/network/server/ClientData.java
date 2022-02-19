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

import java.net.InetAddress;

import com.b3dgs.lionengine.Timing;

/**
 * Client data representation used by server for identification.
 */
public class ClientData
{
    private final Timing alive = new Timing();
    private final Integer clientId;
    private final InetAddress ip;
    private final int port;

    /**
     * Create data.
     * 
     * @param ip The ip address.
     * @param port The port number.
     * @param id The unique id.
     */
    public ClientData(InetAddress ip, int port, Integer id)
    {
        super();

        clientId = id;
        this.ip = ip;
        this.port = port;
    }

    /**
     * Update alive flag.
     */
    public void alive()
    {
        alive.restart();
    }

    /**
     * Check alive flag.
     * 
     * @param timeout The timeout reference.
     * @return <code>true</code> if alive, <code>false</code> else.
     */
    public boolean isAlive(int timeout)
    {
        return !alive.elapsed(timeout);
    }

    /**
     * Get the ip address.
     * 
     * @return The ip address.
     */
    public InetAddress getIp()
    {
        return ip;
    }

    /**
     * Get the port number.
     * 
     * @return The port number.
     */
    public int getPort()
    {
        return port;
    }

    /**
     * Get the client id.
     * 
     * @return The client id.
     */
    public Integer getClientId()
    {
        return clientId;
    }

    @Override
    public String toString()
    {
        return "Client [ip=" + ip.getHostAddress() + ", port=" + port + ", clientId=" + clientId + "]";
    }

}
