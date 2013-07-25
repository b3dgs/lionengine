package com.b3dgs.lionengine.network;

import com.b3dgs.lionengine.network.impl.ClientImpl;
import com.b3dgs.lionengine.network.impl.ServerImpl;
import com.b3dgs.lionengine.network.message.NetworkMessageDecoder;

/**
 * Network factory implementation.
 */
public final class Network
{
    /**
     * Private constructor.
     */
    private Network()
    {
        // Nothing to do
    }

    /**
     * Create a server.
     * 
     * @param decoder The message type decoder.
     * @return The created server.
     */
    public static Server createServer(NetworkMessageDecoder decoder)
    {
        return new ServerImpl(decoder);
    }

    /**
     * Create a client which will be connected to a Server.
     * 
     * @param decoder The message decoder.
     * @return The created client (connected to the server).
     */
    public static Client createClient(NetworkMessageDecoder decoder)
    {
        return new ClientImpl(decoder);
    }
}
