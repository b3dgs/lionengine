/*
 * Copyright (C) 2013-2017 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.network.purview;

import java.util.Collection;

import com.b3dgs.lionengine.network.message.NetworkMessage;

/**
 * Describe an object that can be networked.
 */
public interface Networkable
{
    /**
     * Apply message.
     * 
     * @param message The message.
     */
    void applyMessage(NetworkMessage message);

    /**
     * Add a message to the queue.
     * 
     * @param message The message to add.
     */
    void addNetworkMessage(NetworkMessage message);

    /**
     * Get the messages list.
     * 
     * @return The messages list.
     */
    Collection<NetworkMessage> getNetworkMessages();

    /**
     * Clear the network messages list.
     */
    void clearNetworkMessages();

    /**
     * Set the client id.
     * 
     * @param id The client id.
     */
    void setClientId(Byte id);

    /**
     * Get the client id.
     * 
     * @return The client id.
     */
    Byte getClientId();
}
