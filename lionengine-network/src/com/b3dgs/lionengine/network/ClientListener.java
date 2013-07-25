package com.b3dgs.lionengine.network;

/**
 * Listen to new client connection.
 */
public interface ClientListener
{
    /**
     * Notify when a new client is connected.
     * 
     * @param id The client id.
     * @param name The client name.
     */
    void notifyClientConnected(Byte id, String name);

    /**
     * Notify when a client is disconnected.
     * 
     * @param id The client id.
     * @param name The client name.
     */
    void notifyClientDisconnected(Byte id, String name);

    /**
     * Notify when a client changed its name.
     * 
     * @param id The client id.
     * @param name The client new name.
     */
    void notifyClientNameChanged(Byte id, String name);
}
