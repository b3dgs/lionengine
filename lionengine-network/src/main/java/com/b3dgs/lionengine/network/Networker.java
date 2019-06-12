/*
 * Copyright (C) 2013-2019 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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

import java.util.Collection;

import com.b3dgs.lionengine.network.message.NetworkMessage;

/**
 * List of services provided by a networker (could be a client or a server).
 * 
 * @param <L> The client listener type used.
 */
interface Networker<L extends ClientListener>
{
    /**
     * Terminate connection and close socket.
     */
    void disconnect();

    /**
     * Add a client connection listener.
     * 
     * @param listener The listener.
     */
    void addListener(L listener);

    /**
     * Remove a client connection listener.
     * 
     * @param listener The listener.
     */
    void removeListener(L listener);

    /**
     * Add a message to the send list.
     * 
     * @param message The message to add to the send list.
     */
    void addMessage(NetworkMessage message);

    /**
     * Add a list of messages to the send list.
     * 
     * @param messages The messages to add to the send list.
     */
    void addMessages(Collection<NetworkMessage> messages);

    /**
     * Get the received messages.
     * 
     * @return The list of received messages.
     */
    Collection<NetworkMessage> getMessages();

    /**
     * Send messages list to the network.
     */
    void sendMessages();

    /**
     * Receive messages from network.
     */
    void receiveMessages();

    /**
     * Get the amount of bytes sent per second.
     * 
     * @return The number of bytes sent per second.
     */
    int getBandwidth();
}
