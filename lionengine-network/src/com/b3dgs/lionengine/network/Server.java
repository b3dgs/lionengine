package com.b3dgs.lionengine.network;

/**
 * List of services provided by a server.
 */
public interface Server
        extends Networker
{
    /**
     * Set the message of the day (sent to a new connected client).
     * 
     * @param message The message.
     */
    void setMessageOfTheDay(String message);

    /**
     * Start the server and listen to client connection.
     * 
     * @param name The server name.
     * @param port The port number.
     */
    void start(String name, int port);

    /**
     * Remove a client from its id.
     * 
     * @param clientId The client id.
     */
    void removeClient(Byte clientId);

    /**
     * Get the number of client.
     * 
     * @return The number of clients.
     */
    int getNumberOfClients();

    /**
     * Get the server port.
     * 
     * @return The server port.
     */
    int getPort();
}
