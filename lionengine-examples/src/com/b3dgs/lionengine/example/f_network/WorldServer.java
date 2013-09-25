package com.b3dgs.lionengine.example.f_network;

import java.io.IOException;

import com.b3dgs.lionengine.Sequence;
import com.b3dgs.lionengine.file.FileReading;
import com.b3dgs.lionengine.game.WorldGame;
import com.b3dgs.lionengine.network.world.NetworkedWorldModelServer;
import com.b3dgs.lionengine.network.world.NetworkedWorldServer;

/**
 * World implementation using AbstractWorld.
 */
class WorldServer
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
