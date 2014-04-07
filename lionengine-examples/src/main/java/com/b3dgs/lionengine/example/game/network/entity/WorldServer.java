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
package com.b3dgs.lionengine.example.game.network.entity;

import java.io.IOException;

import com.b3dgs.lionengine.Graphic;
import com.b3dgs.lionengine.core.Sequence;
import com.b3dgs.lionengine.file.FileReading;
import com.b3dgs.lionengine.game.WorldGame;
import com.b3dgs.lionengine.game.platform.CameraPlatform;
import com.b3dgs.lionengine.network.NetworkedWorldModelServer;
import com.b3dgs.lionengine.network.NetworkedWorldServer;

/**
 * World server implementation.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
final class WorldServer
        extends World<NetworkedWorldModelServer>
        implements NetworkedWorldServer
{
    /** Handler reference. */
    private final HandlerEntity handler;

    /**
     * @see WorldGame#WorldGame(Sequence)
     */
    WorldServer(Sequence sequence)
    {
        super(sequence);
        handler = new HandlerEntity(new CameraPlatform(width, height), marioClients);
        networkedWorld = new NetworkedWorldModelServer(new MessageDecoder());
        networkedWorld.addListener(this);
        networkedWorld.addListener(chat);
        factory.setServer(true);
    }

    /*
     * World
     */

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
    public void render(Graphic g)
    {
        g.clear(0, 0, width, height);
        super.render(g);
    }

    @Override
    protected void loading(FileReading file) throws IOException
    {
        super.loading(file);
        // Create two goombas
        for (int i = 0; i < 2; i++)
        {
            final Goomba goomba = factory.create(EntityType.GOOMBA);
            goomba.teleport(532 + i * 24, 25);
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
