package com.b3dgs.lionengine.network;

/**
 * Listen to connection state and other client connection.
 */
public interface ConnectionListener
        extends ClientListener
{
    /**
     * Notify when the connection to the server is established.
     * 
     * @param id The id received.
     * @param name The client name.
     */
    void notifyConnectionEstablished(Byte id, String name);

    /**
     * Notify the message of the day.
     * 
     * @param messageOfTheDay The message of the day.
     */
    void notifyMessageOfTheDay(String messageOfTheDay);

    /**
     * Notify when the connection to the server is terminated.
     * 
     * @param id The id received.
     */
    void notifyConnectionTerminated(Byte id);
}
