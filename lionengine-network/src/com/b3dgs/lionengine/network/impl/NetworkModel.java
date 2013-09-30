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
package com.b3dgs.lionengine.network.impl;

import java.io.DataInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.b3dgs.lionengine.network.ClientListener;
import com.b3dgs.lionengine.network.Networker;
import com.b3dgs.lionengine.network.message.NetworkMessage;
import com.b3dgs.lionengine.network.message.NetworkMessageDecoder;

/**
 * Network base implementation.
 * 
 * @param <L> Client listener type used.
 */
public abstract class NetworkModel<L extends ClientListener>
        implements Networker
{
    /** Messages list. */
    protected final List<NetworkMessage> messagesOut;
    /** Messages list. */
    protected final List<NetworkMessage> messagesIn;
    /** Message decoder. */
    protected final NetworkMessageDecoder decoder;
    /** The client connection listener. */
    protected final List<L> listeners;

    /**
     * Constructor.
     * 
     * @param decoder The message decoder.
     */
    public NetworkModel(NetworkMessageDecoder decoder)
    {
        this.messagesOut = new ArrayList<>(4);
        this.messagesIn = new ArrayList<>(4);
        this.listeners = new ArrayList<>(1);
        this.decoder = decoder;
    }

    /**
     * Add a client connection listener.
     * 
     * @param listener The listener.
     */
    public void addListener(L listener)
    {
        this.listeners.add(listener);
    }

    /**
     * Remove a client connection listener.
     * 
     * @param listener The listener.
     */
    public void removeListener(L listener)
    {
        this.listeners.remove(listener);
    }

    /**
     * Decode a message from its type.
     * 
     * @param type The message type.
     * @param from The client id source.
     * @param dest The client id destination (-1 if all).
     * @param buffer The data.
     * @throws IOException Error on reading.
     */
    protected void decodeMessage(byte type, byte from, byte dest, DataInputStream buffer) throws IOException
    {
        final NetworkMessage message = this.decoder.getNetworkMessageFromType(type);
        if (message != null)
        {
            buffer.skipBytes(3);
            message.decode(type, from, dest, buffer);
            this.messagesIn.add(message);
        }
    }

    /*
     * Networker
     */

    @Override
    public void addMessage(NetworkMessage message)
    {
        this.messagesOut.add(message);
    }

    @Override
    public void addMessages(Collection<NetworkMessage> messages)
    {
        this.messagesOut.addAll(messages);
    }

    @Override
    public Collection<NetworkMessage> getMessages()
    {
        return this.messagesIn;
    }
}
