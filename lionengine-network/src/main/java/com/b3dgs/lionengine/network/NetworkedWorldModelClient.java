/*
 * Copyright (C) 2013-2015 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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

import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.network.message.NetworkMessageDecoder;

/**
 * Networkable world implementation client side.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public class NetworkedWorldModelClient extends NetworkedWorldModel<ConnectionListener, ClientImpl>
                                       implements NetworkedWorldClient
{
    /**
     * Constructor.
     * 
     * @param decoder The decoder reference.
     */
    public NetworkedWorldModelClient(NetworkMessageDecoder decoder)
    {
        super(new ClientImpl(decoder));
    }

    /*
     * NetworkedWorld
     */

    @Override
    public void connect(String ip, int port) throws LionEngineException
    {
        network.connect(ip, port);
        for (final ConnectionListener listener : listeners)
        {
            network.addListener(listener);
        }
        network.addListener(this);
    }

    @Override
    public void disconnect()
    {
        super.disconnect();
        network.removeListener(this);
    }

    @Override
    public void setName(String name)
    {
        network.setName(name);
    }

    @Override
    public String getName()
    {
        return network.getName();
    }

    @Override
    public int getPing()
    {
        return network.getPing();
    }

    @Override
    public byte getId()
    {
        return network.getId();
    }

    /*
     * ConnectionListener
     */

    @Override
    public void notifyConnectionEstablished(Byte id, String name)
    {
        // Nothing to do
    }

    @Override
    public void notifyMessageOfTheDay(String messageOfTheDay)
    {
        // Nothing to do
    }

    @Override
    public void notifyConnectionTerminated(Byte id)
    {
        // Nothing to do
    }
}
