package com.b3dgs.lionengine.network.purview;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.b3dgs.lionengine.network.message.NetworkMessage;

/**
 * Networkable model implementation.
 */
public class NetworkableModel
        implements Networkable
{
    /** List of messages. */
    private final List<NetworkMessage> messages;
    /** The client id. */
    private Byte clientId;

    /**
     * Constructor.
     */
    public NetworkableModel()
    {
        messages = new ArrayList<>(4);
        clientId = Byte.valueOf((byte) -1);
    }

    @Override
    public void applyMessage(NetworkMessage message)
    {
        // Nothing to do
    }

    @Override
    public void addNetworkMessage(NetworkMessage message)
    {
        messages.add(message);
    }

    @Override
    public Collection<NetworkMessage> getNetworkMessages()
    {
        return messages;
    }

    @Override
    public void clearNetworkMessages()
    {
        messages.clear();
    }

    @Override
    public void setClientId(Byte id)
    {
        clientId = id;
    }

    @Override
    public Byte getClientId()
    {
        return clientId;
    }
}
