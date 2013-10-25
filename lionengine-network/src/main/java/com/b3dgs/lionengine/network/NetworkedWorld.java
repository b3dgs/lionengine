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

import java.util.Collection;

import com.b3dgs.lionengine.network.message.NetworkMessage;
import com.b3dgs.lionengine.network.purview.Networkable;

/**
 * Networkable world interface.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public interface NetworkedWorld
        extends ClientListener
{
    /**
     * Disconnect.
     */
    void disconnect();

    /**
     * Add a networkable entity to the world.
     * 
     * @param networkable The networkable entity.
     */
    void addNetworkable(Networkable networkable);

    /**
     * Remove a networkable entity to the world.
     * 
     * @param networkable The networkable entity.
     */
    void removeNetworkable(Networkable networkable);

    /**
     * Add a network message.
     * 
     * @param message The message.
     */
    void addMessage(NetworkMessage message);

    /**
     * Add a list of network messages.
     * 
     * @param messages The messages list.
     */
    void addMessages(Collection<NetworkMessage> messages);

    /**
     * Send all messages to the network.
     */
    void sendMessages();

    /**
     * Receive all messages from the network.
     */
    void receiveMessages();

    /**
     * Get the amount of bytes sent per second.
     * 
     * @return The number of bytes sent per second.
     */
    int getBandwidth();
}
