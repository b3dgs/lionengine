package com.b3dgs.lionengine.example.f_network;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;

import com.b3dgs.lionengine.Graphic;
import com.b3dgs.lionengine.Sequence;
import com.b3dgs.lionengine.file.FileReading;
import com.b3dgs.lionengine.file.FileWriting;
import com.b3dgs.lionengine.game.WorldGame;
import com.b3dgs.lionengine.network.message.NetworkMessage;
import com.b3dgs.lionengine.network.purview.Networkable;
import com.b3dgs.lionengine.network.purview.NetworkableModel;
import com.b3dgs.lionengine.network.world.NetworkedWorld;
import com.b3dgs.lionengine.network.world.NetworkedWorldModel;
import com.b3dgs.lionengine.network.world.NetworkedWorldModelServer;

/**
 * World base implementation.
 * 
 * @param <N> The network type.
 */
abstract class World<N extends NetworkedWorldModel<?, ?>>
        extends WorldGame
        implements NetworkedWorld
{
    /** Networkable model. */
    protected final Networkable networkableModel;
    /** Map reference. */
    protected final Map map;
    /** Factory reference. */
    protected final FactoryEntity factory;
    /** Handler reference. */
    protected final HandlerEntity handler;
    /** Mario client reference. */
    protected final HashMap<Byte, Mario> marioClients;
    /** Networkable world reference. */
    protected N networkedWorld;

    /**
     * @see WorldGame#WorldGame(Sequence)
     */
    public World(final Sequence sequence)
    {
        super(sequence);
        this.map = new Map();
        this.marioClients = new HashMap<>(1);
        this.factory = new FactoryEntity(source.getRate(), this.map);
        this.handler = new HandlerEntity(this.marioClients);
        this.networkableModel = new NetworkableModel();
    }

    /**
     * Get the client from its id.
     * 
     * @param id The client id.
     * @return The client name
     */
    public String getClientName(byte id)
    {
        return this.marioClients.get(Byte.valueOf(id)).getName();
    }

    /**
     * Apply the specified command.
     * 
     * @param command The command.
     */
    public abstract void applyCommand(String command);

    /*
     * WorldGame
     */

    @Override
    public void update(double extrp)
    {
        this.handler.update(extrp);
    }

    @Override
    public void render(Graphic g)
    {
        // Nothing to do
    }

    @Override
    protected void saving(FileWriting file) throws IOException
    {
        this.map.save(file);
    }

    @Override
    protected void loading(FileReading file) throws IOException
    {
        this.map.load(file);
    }

    /*
     * NetworkedWorld
     */

    @Override
    public void disconnect()
    {
        this.networkedWorld.disconnect();
    }

    @Override
    public void addNetworkable(Networkable networkable)
    {
        this.networkedWorld.addNetworkable(networkable);
    }

    @Override
    public void removeNetworkable(Networkable networkable)
    {
        this.networkedWorld.removeNetworkable(networkable);
    }

    @Override
    public void addMessage(NetworkMessage message)
    {
        this.networkedWorld.addMessage(message);
    }

    @Override
    public void addMessages(Collection<NetworkMessage> messages)
    {
        this.networkedWorld.addMessages(messages);
    }

    @Override
    public void sendMessages()
    {
        this.networkedWorld.sendMessages();
    }

    @Override
    public void receiveMessages()
    {
        this.networkedWorld.receiveMessages();
    }

    /*
     * ClientConnectedListener
     */

    @Override
    public void notifyClientConnected(Byte id, String name)
    {
        boolean server = false;
        if (this.networkedWorld instanceof NetworkedWorldModelServer)
        {
            server = true;
        }
        final Mario mario = this.factory.createMario(server);
        mario.setLocation(64, 32);
        mario.setName(name);
        mario.setClientId(id);
        this.addNetworkable(mario);
        this.marioClients.put(id, mario);
        this.sendMessages();
    }

    @Override
    public void notifyClientDisconnected(Byte id, String name)
    {
        final Mario mario = this.marioClients.get(id);
        this.removeNetworkable(mario);
        this.marioClients.remove(id);
    }

    @Override
    public void notifyClientNameChanged(Byte id, String name)
    {
        final Mario mario = this.marioClients.get(id);
        mario.setName(name);
    }
}
