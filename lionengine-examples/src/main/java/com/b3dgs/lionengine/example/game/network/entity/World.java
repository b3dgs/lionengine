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
import java.util.Collection;
import java.util.HashMap;

import com.b3dgs.lionengine.Align;
import com.b3dgs.lionengine.TextStyle;
import com.b3dgs.lionengine.core.Core;
import com.b3dgs.lionengine.core.Graphic;
import com.b3dgs.lionengine.core.Sequence;
import com.b3dgs.lionengine.core.Text;
import com.b3dgs.lionengine.game.WorldGame;
import com.b3dgs.lionengine.network.NetworkedWorld;
import com.b3dgs.lionengine.network.NetworkedWorldModelServer;
import com.b3dgs.lionengine.network.message.NetworkMessage;
import com.b3dgs.lionengine.network.purview.Networkable;
import com.b3dgs.lionengine.network.purview.NetworkableModel;
import com.b3dgs.lionengine.stream.FileReading;
import com.b3dgs.lionengine.stream.FileWriting;

/**
 * World base implementation.
 * 
 * @param <N> The network type.
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
abstract class World<N extends NetworkedWorld>
        extends WorldGame
        implements NetworkedWorld
{
    /** Networkable model. */
    protected final Networkable networkableModel;
    /** Map reference. */
    protected final Map map;
    /** Factory reference. */
    protected final FactoryEntity factory;
    /** Mario client reference. */
    protected final HashMap<Byte, Mario> marioClients;
    /** Text. */
    protected final Text text;
    /** Chat. */
    protected final Chat chat;
    /** Networkable world reference. */
    protected N networkedWorld;

    /**
     * @param sequence The sequence reference.
     */
    World(final Sequence sequence)
    {
        super(sequence);
        map = new Map();
        marioClients = new HashMap<>(1);
        factory = new FactoryEntity(source.getRate(), map);
        networkableModel = new NetworkableModel();
        text = Core.GRAPHIC.createText(Text.SANS_SERIF, 10, TextStyle.NORMAL);
        chat = new Chat(this);
    }

    /**
     * Apply the specified command.
     * 
     * @param command The command.
     */
    public abstract void applyCommand(String command);

    /**
     * Get the client from its id.
     * 
     * @param id The client id.
     * @return The client name
     */
    public String getClientName(byte id)
    {
        return marioClients.get(Byte.valueOf(id)).getName();
    }

    /*
     * WorldGame
     */

    @Override
    public void render(Graphic g)
    {
        text.draw(g, width, 0, Align.RIGHT, "Bandwidth=" + getBandwidth() + "byte/sec");
        text.draw(g, 0, 0, "Number of connected clients: " + marioClients.size());
        text.draw(g, 0, 12, "Clients:");
        int i = 0;
        for (final Mario client : marioClients.values())
        {
            text.draw(g, 0, 24 + i * 12, " - " + client.getName());
            i++;
        }
        chat.render(g);
    }

    @Override
    protected void saving(FileWriting file) throws IOException
    {
        map.save(file);
    }

    @Override
    protected void loading(FileReading file) throws IOException
    {
        map.load(file);
    }

    /*
     * NetworkedWorld
     */

    @Override
    public void disconnect()
    {
        networkedWorld.disconnect();
    }

    @Override
    public void addNetworkable(Networkable networkable)
    {
        networkedWorld.addNetworkable(networkable);
    }

    @Override
    public void removeNetworkable(Networkable networkable)
    {
        networkedWorld.removeNetworkable(networkable);
    }

    @Override
    public void addMessage(NetworkMessage message)
    {
        networkedWorld.addMessage(message);
    }

    @Override
    public void addMessages(Collection<NetworkMessage> messages)
    {
        networkedWorld.addMessages(messages);
    }

    @Override
    public void sendMessages()
    {
        networkedWorld.sendMessages();
    }

    @Override
    public void receiveMessages()
    {
        networkedWorld.receiveMessages();
    }

    @Override
    public int getBandwidth()
    {
        return networkedWorld.getBandwidth();
    }

    /*
     * ClientConnectedListener
     */

    @Override
    public void notifyClientConnected(Byte id, String name)
    {
        boolean server = false;
        if (networkedWorld instanceof NetworkedWorldModelServer)
        {
            server = true;
        }
        factory.setServer(server);
        final Mario mario = factory.create(Mario.class);
        mario.respawn();
        mario.setName(name);
        mario.setClientId(id);
        addNetworkable(mario);
        addNetworkable(chat);
        marioClients.put(id, mario);
        sendMessages();

    }

    @Override
    public void notifyClientDisconnected(Byte id, String name)
    {
        final Mario mario = marioClients.get(id);
        removeNetworkable(mario);
        marioClients.remove(id);
    }

    @Override
    public void notifyClientNameChanged(Byte id, String name)
    {
        final Mario mario = marioClients.get(id);
        mario.setName(name);
    }
}
