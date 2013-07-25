package com.b3dgs.lionengine.network.impl;

import java.net.ServerSocket;

/**
 * Client connection listener thread.
 */
class ClientConnecter
        extends Thread
{
    /** Server socket. */
    private final ServerSocket serverSocket;
    /** Server reference. */
    private final ServerImpl server;
    /** Running flag. */
    private boolean isRunning;

    /**
     * Constructor.
     * 
     * @param serverSocket The server socket.
     * @param server The server reference.
     */
    ClientConnecter(final ServerSocket serverSocket, final ServerImpl server)
    {
        super("Client Connection Listener");
        this.serverSocket = serverSocket;
        this.server = server;
    }

    @Override
    public void run()
    {
        isRunning = true;
        while (isRunning)
        {
            try
            {
                server.notifyNewClientConnected(serverSocket.accept());
            }
            catch (final Exception exception)
            {
                // Ignore
            }
        }
    }

    /**
     * Terminate the thread.
     */
    public void terminate()
    {
        isRunning = false;
    }
}
