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
package com.b3dgs.lionengine.network.client;

/**
 * Listen to client events.
 */
public interface ClientListener
{
    /**
     * Called on connected at specified server.
     * 
     * @param ip The server ip.
     * @param port The server port.
     * @param id The client id.
     */
    void notifyConnected(String ip, int port, Integer id);

    /**
     * Called on disconnected from specified server.
     * <p>
     * Does nothing by default.
     * </p>
     * 
     * @param ip The server ip.
     * @param port The server port.
     * @param id The client id.
     */
    default void notifyDisconnected(String ip, int port, Integer id)
    {
        // Nothing
    }

    /**
     * Called on client connected on server.
     * <p>
     * Does nothing by default.
     * </p>
     * 
     * @param id The client id.
     */
    default void notifyClientConnected(Integer id)
    {
        // Nothing
    }

    /**
     * Called on client disconnected from server.
     * <p>
     * Does nothing by default.
     * </p>
     * 
     * @param id The client id.
     */
    default void notifyClientDisconnected(Integer id)
    {
        // Nothing
    }

    /**
     * Called on client set name.
     * <p>
     * Does nothing by default.
     * </p>
     * 
     * @param id The client id.
     * @param name The client name.
     */
    default void notifyClientNamed(Integer id, String name)
    {
        // Nothing
    }
}
