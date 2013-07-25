package com.b3dgs.lionengine.network.world;

import com.b3dgs.lionengine.network.ConnectionListener;

/**
 * Networkable world interface client side.
 */
public interface NetworkedWorldClient
        extends NetworkedWorld, ConnectionListener
{
    /**
     * Connect to a server.
     * 
     * @param ip The server ip.
     * @param port The server port.
     */
    void connect(String ip, int port);

    /**
     * Set the client new name.
     * 
     * @param name The new name.
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
     * Get the network id.
     * 
     * @return The network id.
     */
    byte getId();
}
