package com.b3dgs.lionengine.network.world;

import java.util.Collection;

import com.b3dgs.lionengine.network.ClientListener;
import com.b3dgs.lionengine.network.message.NetworkMessage;
import com.b3dgs.lionengine.network.purview.Networkable;

/**
 * Networkable world interface.
 */
public interface NetworkedWorld
        extends ClientListener
{
    /**
     * Disconnect.
     */
    void disconnect();

    /**
     * Add a networkable entity to the world.
     * 
     * @param networkable The networkable entity.
     */
    void addNetworkable(Networkable networkable);

    /**
     * Remove a networkable entity to the world.
     * 
     * @param networkable The networkable entity.
     */
    void removeNetworkable(Networkable networkable);

    /**
     * Add a network message.
     * 
     * @param message The message.
     */
    void addMessage(NetworkMessage message);

    /**
     * Add a list of network messages.
     * 
     * @param messages The messages list.
     */
    void addMessages(Collection<NetworkMessage> messages);

    /**
     * Send all messages to the network.
     */
    void sendMessages();

    /**
     * Receive all messages from the network.
     */
    void receiveMessages();
}
