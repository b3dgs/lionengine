package com.b3dgs.lionengine.network.message;

/**
 * The network message decoder will return the network message instance from its type.
 */
public interface NetworkMessageDecoder
{
    /**
     * Get the network message instance from its type.
     * 
     * @param type The message type.
     * @return The message instance.
     */
    NetworkMessage getNetworkMessageFromType(int type);
}
