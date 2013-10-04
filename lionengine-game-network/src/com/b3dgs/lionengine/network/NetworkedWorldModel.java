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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.b3dgs.lionengine.network.message.NetworkMessage;
import com.b3dgs.lionengine.network.purview.Networkable;

/**
 * Networked world base implementation.
 * 
 * @param <L> Listener type.
 * @param <N> Network implementation.
 */
public class NetworkedWorldModel<L extends ClientListener, N extends NetworkModel<L>>
        implements NetworkedWorld
{
    /** List of networkable objects. */
    protected final Set<Networkable> networkables;
    /** List of networkable objects. */
    protected final List<Networkable> toAdd;
    /** The client connection listener. */
    protected final List<L> listeners;
    /** The network. */
    protected final N network;
    /** Will add. */
    private boolean willAdd;

    /**
     * Constructor.
     * 
     * @param network The network reference.
     */
    public NetworkedWorldModel(N network)
    {
        this.networkables = new HashSet<>(1);
        this.toAdd = new ArrayList<>(1);
        this.listeners = new ArrayList<>(1);
        this.network = network;
        this.willAdd = false;
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

    /*
     * NetworkedWorld
     */

    @Override
    public void disconnect()
    {
        this.network.disconnect();
        for (final L listener : this.listeners)
        {
            this.network.removeListener(listener);
        }
        this.listeners.clear();
    }

    @Override
    public void addNetworkable(Networkable networkable)
    {
        this.toAdd.add(networkable);
        this.willAdd = true;
    }

    @Override
    public void removeNetworkable(Networkable networkable)
    {
        this.networkables.remove(networkable);
    }

    @Override
    public void addMessage(NetworkMessage message)
    {
        this.network.addMessage(message);
    }

    @Override
    public void addMessages(Collection<NetworkMessage> messages)
    {
        this.network.addMessages(messages);
    }

    @Override
    public void sendMessages()
    {
        for (final Networkable networkable : this.networkables)
        {
            this.network.addMessages(networkable.getNetworkMessages());
            networkable.clearNetworkMessages();
        }
        this.network.sendMessages();
    }

    @Override
    public void receiveMessages()
    {
        if (this.willAdd)
        {
            for (final Networkable networkable : this.toAdd)
            {
                this.networkables.add(networkable);
            }
            this.toAdd.clear();
            this.willAdd = false;
        }
        this.network.receiveMessages();
        for (final NetworkMessage message : this.network.getMessages())
        {
            for (final Networkable networkable : this.networkables)
            {
                networkable.applyMessage(message);
            }
        }
    }

    @Override
    public void notifyClientConnected(Byte id, String name)
    {
        // Nothing to do
    }

    @Override
    public void notifyClientDisconnected(Byte id, String name)
    {
        // Nothing to do
    }

    @Override
    public void notifyClientNameChanged(Byte id, String name)
    {
        // Nothing to do
    }
}
