package com.b3dgs.lionengine.network;

import java.util.Collection;

import com.b3dgs.lionengine.network.message.NetworkMessage;

/**
 * List of services provided by a networker (could be a client or a server).
 */
public interface Networker
{
    /**
     * Terminate connection and close socket.
     */
    void disconnect();

    /**
     * Add a message to the send list.
     * 
     * @param message The message to add to the send list.
     */
    void addMessage(NetworkMessage message);

    /**
     * Add a list of messages to the send list.
     * 
     * @param messages The messages to add to the send list.
     */
    void addMessages(Collection<NetworkMessage> messages);

    /**
     * Get the received messages.
     * 
     * @return The list of received messages.
     */
    Collection<NetworkMessage> getMessages();

    /**
     * Send messages list to the network.
     */
    void sendMessages();

    /**
     * Receive messages from network.
     */
    void receiveMessages();
}
