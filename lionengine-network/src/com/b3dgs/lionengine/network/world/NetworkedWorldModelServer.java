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
