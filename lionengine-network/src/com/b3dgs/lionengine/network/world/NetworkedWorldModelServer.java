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

import com.b3dgs.lionengine.network.ClientListener;
import com.b3dgs.lionengine.network.impl.ServerImpl;
import com.b3dgs.lionengine.network.message.NetworkMessageDecoder;

/**
 * Networkable world implementation server side.
 */
public class NetworkedWorldModelServer
        extends NetworkedWorldModel<ClientListener, ServerImpl>
        implements NetworkedWorldServer
{
    /**
     * Constructor.
     * 
     * @param decoder The decoder reference.
     */
    public NetworkedWorldModelServer(NetworkMessageDecoder decoder)
    {
        super(new ServerImpl(decoder));
    }

    /*
     * NetworkedWorld
     */

    @Override
    public void startServer(String name, int port, String messageOfTheDay)
    {
        for (final ClientListener listener : listeners)
        {
            network.addListener(listener);
        }
        network.addListener(this);
        network.setMessageOfTheDay(messageOfTheDay);
        network.start(name, port);
    }

    @Override
    public void disconnect()
    {
        super.disconnect();
        network.removeListener(this);
    }
}
