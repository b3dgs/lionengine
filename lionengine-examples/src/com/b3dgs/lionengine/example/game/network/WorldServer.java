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
package com.b3dgs.lionengine.example.game.network;

import java.io.IOException;

import com.b3dgs.lionengine.core.Sequence;
import com.b3dgs.lionengine.file.FileReading;
import com.b3dgs.lionengine.game.WorldGame;
import com.b3dgs.lionengine.network.NetworkedWorldModelServer;
import com.b3dgs.lionengine.network.NetworkedWorldServer;

/**
 * World server implementation.
 */
final class WorldServer
        extends World<NetworkedWorldModelServer>
        implements NetworkedWorldServer
{
    /**
     * @see WorldGame#WorldGame(Sequence)
     */
    public WorldServer(final Sequence sequence)
    {
        super(sequence);
        networkedWorld = new NetworkedWorldModelServer(new MessageDecoder());
        networkedWorld.addListener(this);
    }

    @Override
    public void applyCommand(String command)
    {
        // Nothing to do
    }

    @Override
    public void update(double extrp)
    {
        handler.update(extrp);
        for (final Mario client : marioClients.values())
        {
            client.update(extrp);
        }
    }

    @Override
    protected void loading(FileReading file) throws IOException
    {
        super.loading(file);
        // Create two goombas
        for (int i = 0; i < 2; i++)
        {
            final Goomba goomba = factory.createGoomba(true);
            goomba.setLocation(532 + i * 24, 32);
            goomba.setNetworkId(goomba.getId().shortValue());
            handler.add(goomba);
            addNetworkable(goomba);
        }
        map.adjustCollisions();
    }

    /*
     * NetworkedWorld
     */

    @Override
    public void startServer(String name, int port, String messageOfTheDay)
    {
        networkedWorld.startServer(name, port, messageOfTheDay);
    }

    /*
     * ClientConnectedListener
     */

    @Override
    public void notifyClientConnected(Byte id, String name)
    {
        super.notifyClientConnected(id, name);
        for (final Entity entity : handler.list())
        {
            final MessageFactory messageFactory = new MessageFactory(entity.getId().shortValue(), id.byteValue());
            messageFactory.addAction(entity.getType(), true);
            addMessage(messageFactory);

            final MessageEntity messageEntity = new MessageEntity(entity.getId().shortValue(), id.byteValue());
            messageEntity.addAction(MessageEntityElement.LOCATION_X, entity.getLocationIntX());
            messageEntity.addAction(MessageEntityElement.LOCATION_Y, entity.getLocationIntY());
            addMessage(messageEntity);
            sendMessages();
        }
    }
}
