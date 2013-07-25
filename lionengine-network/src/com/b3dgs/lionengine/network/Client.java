package com.b3dgs.lionengine.network;

/**
 * List of services provided by a client.
 */
public interface Client
        extends Networker
{
    /**
     * Connect to a server.
     * 
     * @param ip The server ip.
     * @param port The server port.
     */
    void connect(String ip, int port);

    /**
     * Check if the client is connected to a server.
     * 
     * @return <code>true</code> if connected, <code>false</code> else.
     */
    boolean isConnected();

    /**
     * Set a new client name, and notify the server.
     * 
     * @param name The new client name.
     */
    void setName(String name);

    /**
     * Get the client name.
     * 
     * @return The client name.
     */
    String getName();

    /**
     * Get the ping from the server (time elapsed between the ping request and response).
     * 
     * @return The ping from the server.
     */
    int getPing();

    /**
     * Get the amount of bytes sent per second.
     * 
     * @return The number of bytes sent per second.
     */
    int getBandwidth();

    /**
     * Get the client id.
     * 
     * @return The client id.
     */
    byte getId();
}
