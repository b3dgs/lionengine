package com.b3dgs.lionengine.example.f_network;

import java.awt.Color;
import java.awt.Font;
import java.util.Collection;

import com.b3dgs.lionengine.Align;
import com.b3dgs.lionengine.Graphic;
import com.b3dgs.lionengine.Sequence;
import com.b3dgs.lionengine.Text;
import com.b3dgs.lionengine.game.TextGame;
import com.b3dgs.lionengine.game.platform.CameraPlatform;
import com.b3dgs.lionengine.network.message.NetworkMessage;
import com.b3dgs.lionengine.network.purview.Networkable;
import com.b3dgs.lionengine.network.purview.NetworkableModel;
import com.b3dgs.lionengine.network.world.NetworkedWorldClient;
import com.b3dgs.lionengine.network.world.NetworkedWorldModelClient;

/**
 * World implementation using AbstractWorld.
 */
class WorldClient
        extends World<NetworkedWorldModelClient>
        implements NetworkedWorldClient, Networkable
{
    /** Camera reference. */
    private final CameraPlatform camera;
    /** Text game. */
    private final TextGame textGame;
    /** Text game. */
    private final Text text;
    /** Chat. */
    private final Chat chat;
    /** Input. */
    private final ClientInput input;
    /** Networkable. */
    private final Networkable networkableModel;
    /** Background color. */
    private final Color backgroundColor = new Color(107, 136, 255);

    /**
     * Default constructor.
     * 
     * @param sequence The sequence reference.
     */
    public WorldClient(final Sequence sequence)
    {
        super(sequence);
        textGame = new TextGame(Font.SANS_SERIF, 10, Text.NORMAL);
        text = new TextGame(Font.SANS_SERIF, 10, Text.NORMAL);
        chat = new Chat(this);
        input = new ClientInput();
        camera = new CameraPlatform(width, height);
        networkableModel = new NetworkableModel();
        networkedWorld = new NetworkedWorldModelClient(new MessageDecoder());
        networkedWorld.addListener(this);
        networkedWorld.addListener(chat);
        sequence.addKeyListener(input);
        sequence.addKeyListener(chat);
    }

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
    public void render(final Graphic g)
    {
        super.render(g);
        g.setColor(backgroundColor);
        g.drawRect(0, 0, width, height, true);
        // Draw the map
        map.render(g, camera);
        handler.render(g, camera);
        // Draw the hero
        for (final Byte id : marioClients.keySet())
        {
            final Mario mario = marioClients.get(id);
            mario.render(g, camera);
            textGame.draw(g, mario.getLocationIntX(), height - mario.getLocationIntY() - 28, Align.CENTER,
                    String.valueOf(mario.getName()));
        }
        chat.render(g);
        text.draw(g, 0, 0, "Ping=" + getPing() + "ms");
        text.draw(g, 0, 12, "Bandwidth=" + getBandwidth() + "byte/sec");
    }

    @Override
    protected void loaded()
    {
        super.loaded();
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
    public int getBandwidth()
    {
        return networkedWorld.getBandwidth();
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
        final Mario mario = factory.createMario(false);
        mario.setLocation(64, 32);
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
        if (msg.hasAction(TypeEntity.goomba))
        {
            final Goomba goomba = factory.createGoomba(false);
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
