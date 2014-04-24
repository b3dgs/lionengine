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

import com.b3dgs.lionengine.Align;
import com.b3dgs.lionengine.ColorRgba;
import com.b3dgs.lionengine.TextStyle;
import com.b3dgs.lionengine.core.Graphic;
import com.b3dgs.lionengine.core.Sequence;
import com.b3dgs.lionengine.core.Text;
import com.b3dgs.lionengine.file.FileReading;
import com.b3dgs.lionengine.game.TextGame;
import com.b3dgs.lionengine.game.WorldGame;
import com.b3dgs.lionengine.game.platform.CameraPlatform;
import com.b3dgs.lionengine.network.NetworkedWorldClient;
import com.b3dgs.lionengine.network.NetworkedWorldModelClient;
import com.b3dgs.lionengine.network.message.NetworkMessage;
import com.b3dgs.lionengine.network.purview.Networkable;
import com.b3dgs.lionengine.network.purview.NetworkableModel;

/**
 * World implementation using AbstractWorld.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
final class WorldClient
        extends World<NetworkedWorldModelClient>
        implements NetworkedWorldClient, Networkable
{
    /** Camera reference. */
    private final CameraPlatform camera;
    /** Text game. */
    private final TextGame textGame;
    /** Handler reference. */
    private final HandlerEntity handler;
    /** Input. */
    private final ClientInput input;
    /** Networkable. */
    private final Networkable networkableModel;
    /** Background color. */
    private final ColorRgba backgroundColor = new ColorRgba(107, 136, 255);

    /**
     * @see WorldGame#WorldGame(Sequence)
     */
    WorldClient(Sequence sequence)
    {
        super(sequence);
        textGame = new TextGame(Text.SANS_SERIF, 10, TextStyle.NORMAL);
        input = new ClientInput();
        camera = new CameraPlatform(width, height);
        handler = new HandlerEntity(camera, marioClients);
        networkableModel = new NetworkableModel();
        networkedWorld = new NetworkedWorldModelClient(new MessageDecoder());
        networkedWorld.addListener(this);
        networkedWorld.addListener(chat);
        factory.setServer(false);
        sequence.addKeyListener(input);
        sequence.addKeyListener(chat);
    }

    /*
     * World
     */

    @Override
    public void applyCommand(String command)
    {
        if (command.startsWith("/name"))
        {
            final String name = command.substring(6);
            setName(name);
        }
    }

    @Override
    public void update(double extrp)
    {
        textGame.update(camera);
        handler.update(extrp);
        for (final Byte id : marioClients.keySet())
        {
            final Mario mario = marioClients.get(id);
            mario.update(extrp);
            if (id.byteValue() == getId())
            {
                camera.follow(mario);
            }
        }
    }

    @Override
    public void render(Graphic g)
    {
        g.setColor(backgroundColor);
        g.drawRect(0, 0, width, height, true);
        // Draw the map
        map.render(g, camera);
        handler.render(g);
        // Draw the hero
        for (final Byte id : marioClients.keySet())
        {
            final Mario mario = marioClients.get(id);
            mario.render(g, camera);
            final String name = String.valueOf(mario.getName());
            final int x = mario.getLocationIntX() - mario.getWidth() / 2 + 8;
            final int y = mario.getLocationIntY() + 38;
            textGame.draw(g, x, y, Align.CENTER, name);
        }
        super.render(g);
        text.draw(g, width, 12, Align.RIGHT, "Ping=" + getPing() + "ms");
    }

    @Override
    protected void loading(FileReading file) throws IOException
    {
        super.loading(file);
        camera.setLimits(map);
        camera.setView(0, 0, width, height);
        map.adjustCollisions();
    }

    /*
     * NetworkedWorld
     */

    @Override
    public void connect(String ip, int port)
    {
        networkedWorld.connect(ip, port);
    }

    @Override
    public void setName(String name)
    {
        networkedWorld.setName(name);
    }

    @Override
    public String getName()
    {
        return networkedWorld.getName();
    }

    @Override
    public int getPing()
    {
        return networkedWorld.getPing();
    }

    @Override
    public byte getId()
    {
        return networkedWorld.getId();
    }

    /*
     * ClientConnectedListener
     */

    @Override
    public void notifyConnectionEstablished(Byte id, String name)
    {
        final Mario mario = factory.create(Mario.class);
        mario.respawn();
        mario.setName(name);
        marioClients.put(id, mario);
        mario.setClientId(id);
        chat.setClientId(id);
        input.setClientId(id);
        setClientId(id);
        addNetworkable(mario);
        addNetworkable(this);
        addNetworkable(chat);
        addNetworkable(input);
    }

    @Override
    public void notifyMessageOfTheDay(String messageOfTheDay)
    {
        // Nothing to do
    }

    @Override
    public void notifyConnectionTerminated(Byte id)
    {
        marioClients.remove(id);
        removeNetworkable(this);
        removeNetworkable(chat);
        removeNetworkable(input);
    }

    /*
     * Networkable
     */

    @Override
    public void applyMessage(NetworkMessage message)
    {
        if (!(message instanceof MessageFactory))
        {
            return;
        }

        final MessageFactory msg = (MessageFactory) message;
        if (msg.hasAction(EntityType.fromClass(Goomba.class)))
        {
            final Goomba goomba = factory.create(Goomba.class);
            goomba.setNetworkId(msg.getEntityId());
            handler.add(goomba);
            addNetworkable(goomba);
        }
    }

    @Override
    public void addNetworkMessage(NetworkMessage message)
    {
        networkableModel.addNetworkMessage(message);
    }

    @Override
    public Collection<NetworkMessage> getNetworkMessages()
    {
        return networkableModel.getNetworkMessages();
    }

    @Override
    public void clearNetworkMessages()
    {
        networkableModel.clearNetworkMessages();
    }

    @Override
    public void setClientId(Byte id)
    {
        networkableModel.setClientId(id);
    }

    @Override
    public Byte getClientId()
    {
        return networkableModel.getClientId();
    }
}
