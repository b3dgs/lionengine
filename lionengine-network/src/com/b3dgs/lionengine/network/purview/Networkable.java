package com.b3dgs.lionengine.network.purview;

import java.util.Collection;

import com.b3dgs.lionengine.network.message.NetworkMessage;

/**
 * Describe an object that can be networked.
 */
public interface Networkable
{
    /**
     * Apply message.
     * 
     * @param message The message.
     */
    void applyMessage(NetworkMessage message);

    /**
     * Add a message to the queue.
     * 
     * @param message The message to add.
     */
    void addNetworkMessage(NetworkMessage message);

    /**
     * Get the messages list.
     * 
     * @return The messages list.
     */
    Collection<NetworkMessage> getNetworkMessages();

    /**
     * Clear the network messages list.
     */
    void clearNetworkMessages();

    /**
     * Set the client id.
     * 
     * @param id The client id.
     */
    void setClientId(Byte id);

    /**
     * Get the client id.
     * 
     * @return The client id.
     */
    Byte getClientId();
}
