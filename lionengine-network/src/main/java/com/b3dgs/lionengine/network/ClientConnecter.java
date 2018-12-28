/*
 * Copyright (C) 2013-2018 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
 */
package com.b3dgs.lionengine.network;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.SocketException;

import com.b3dgs.lionengine.Verbose;

/**
 * Client connection listener thread.
 */
final class ClientConnecter extends Thread
{
    /** Server socket. */
    private final ServerSocket serverSocket;
    /** Server reference. */
    private final ServerImpl server;
    /** Running flag. */
    private boolean isRunning;

    /**
     * Internal constructor.
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

    /**
     * Terminate the thread.
     */
    public void terminate()
    {
        isRunning = false;
    }

    /*
     * Thread
     */

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
            catch (final SocketException exception)
            {
                Verbose.exception(exception);
                isRunning = false;
            }
            catch (final IOException exception)
            {
                Verbose.exception(exception);
            }
        }
    }
}
