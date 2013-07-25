package com.b3dgs.lionengine.network.world;

import com.b3dgs.lionengine.network.ConnectionListener;
import com.b3dgs.lionengine.network.impl.ClientImpl;
import com.b3dgs.lionengine.network.message.NetworkMessageDecoder;

/**
 * Networkable world implementation client side.
 */
public class NetworkedWorldModelClient
        extends NetworkedWorldModel<ConnectionListener, ClientImpl>
        implements NetworkedWorldClient
{
    /**
     * Constructor.
     * 
     * @param decoder The decoder reference.
     */
    public NetworkedWorldModelClient(NetworkMessageDecoder decoder)
    {
        super(new ClientImpl(decoder));
    }

    /*
     * NetworkedWorld
     */

    @Override
    public void connect(String ip, int port)
    {
        network.connect(ip, port);
        for (final ConnectionListener listener : listeners)
        {
            network.addListener(listener);
        }
        network.addListener(this);
    }

    @Override
    public void disconnect()
    {
        super.disconnect();
        network.removeListener(this);
    }

    @Override
    public void setName(String name)
    {
        network.setName(name);
    }

    @Override
    public String getName()
    {
        return network.getName();
    }

    @Override
    public int getPing()
    {
        return network.getPing();
    }

    @Override
    public int getBandwidth()
    {
        return network.getBandwidth();
    }

    @Override
    public byte getId()
    {
        return network.getId();
    }

    /*
     * ConnectionListener
     */

    @Override
    public void notifyConnectionEstablished(Byte id, String name)
    {
        // Nothing to do
    }

    @Override
    public void notifyMessageOfTheDay(String messageOfTheDay)
    {
        // Nothing to do
    }

    @Override
    public void notifyConnectionTerminated(Byte id)
    {
        // Nothing to do
    }
}
