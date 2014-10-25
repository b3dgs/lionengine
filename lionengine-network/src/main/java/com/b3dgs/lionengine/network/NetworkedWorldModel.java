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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;

import com.b3dgs.lionengine.network.message.NetworkMessage;
import com.b3dgs.lionengine.network.purview.Networkable;

/**
 * Networked world base implementation.
 * 
 * @param <L> Listener type.
 * @param <N> Network implementation.
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
abstract class NetworkedWorldModel<L extends ClientListener, N extends NetworkModel<L>>
        implements NetworkedWorld
{
    /** List of networkable objects. */
    protected final Collection<Networkable> networkables;
    /** List of networkable objects. */
    protected final Collection<Networkable> toAdd;
    /** The client connection listener. */
    protected final Collection<L> listeners;
    /** The network. */
    protected final N network;
    /** Will add. */
    private boolean willAdd;

    /**
     * Internal constructor.
     * 
     * @param network The network reference.
     */
    NetworkedWorldModel(N network)
    {
        this.network = network;
        networkables = new HashSet<>(1);
        toAdd = new ArrayList<>(1);
        listeners = new ArrayList<>(1);
        willAdd = false;
    }

    /**
     * Add a client connection listener.
     * 
     * @param listener The listener.
     */
    public void addListener(L listener)
    {
        listeners.add(listener);
    }

    /**
     * Remove a client connection listener.
     * 
     * @param listener The listener.
     */
    public void removeListener(L listener)
    {
        listeners.remove(listener);
    }

    /*
     * NetworkedWorld
     */

    @Override
    public void disconnect()
    {
        network.disconnect();
        for (final L listener : listeners)
        {
            network.removeListener(listener);
        }
        listeners.clear();
    }

    @Override
    public void addNetworkable(Networkable networkable)
    {
        toAdd.add(networkable);
        willAdd = true;
    }

    @Override
    public void removeNetworkable(Networkable networkable)
    {
        networkables.remove(networkable);
    }

    @Override
    public void addMessage(NetworkMessage message)
    {
        network.addMessage(message);
    }

    @Override
    public void addMessages(Collection<NetworkMessage> messages)
    {
        network.addMessages(messages);
    }

    @Override
    public void sendMessages()
    {
        for (final Networkable networkable : networkables)
        {
            network.addMessages(networkable.getNetworkMessages());
            networkable.clearNetworkMessages();
        }
        network.sendMessages();
    }

    @Override
    public void receiveMessages()
    {
        if (willAdd)
        {
            for (final Networkable networkable : toAdd)
            {
                networkables.add(networkable);
            }
            toAdd.clear();
            willAdd = false;
        }
        network.receiveMessages();
        for (final NetworkMessage message : network.getMessages())
        {
            for (final Networkable networkable : networkables)
            {
                networkable.applyMessage(message);
            }
        }
    }

    @Override
    public int getBandwidth()
    {
        return network.getBandwidth();
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
