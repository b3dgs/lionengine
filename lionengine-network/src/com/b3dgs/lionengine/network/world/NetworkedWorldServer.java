package com.b3dgs.lionengine.network.world;

/**
 * Networkable world interface server side.
 */
public interface NetworkedWorldServer
        extends NetworkedWorld
{
    /**
     * Start the server.
     * 
     * @param name The server name.
     * @param port The port number.
     * @param messageOfTheDay The message of the day.
     */
    void startServer(String name, int port, String messageOfTheDay);
}
