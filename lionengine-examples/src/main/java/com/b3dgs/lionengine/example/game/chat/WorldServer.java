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
package com.b3dgs.lionengine.example.game.chat;

import com.b3dgs.lionengine.Context;
import com.b3dgs.lionengine.game.feature.WorldGame;
import com.b3dgs.lionengine.network.NetworkedWorldModelServer;
import com.b3dgs.lionengine.network.NetworkedWorldServer;

/**
 * World server implementation.
 */
class WorldServer extends World<NetworkedWorldModelServer> implements NetworkedWorldServer
{
    /**
     * @see WorldGame#WorldGame(Context)
     */
    public WorldServer(Context context)
    {
        super(context);
        networkedWorld = new NetworkedWorldModelServer(new MessageDecoder());
        networkedWorld.addListener(this);
        networkedWorld.addListener(chat);
    }

    /*
     * World
     */

    @Override
    public void applyCommand(String command)
    {
        // Nothing to do
    }

    /*
     * NetworkedWorld
     */

    @Override
    public void startServer(String name, int port, String messageOfTheDay)
    {
        networkedWorld.startServer(name, port, messageOfTheDay);
    }
}
