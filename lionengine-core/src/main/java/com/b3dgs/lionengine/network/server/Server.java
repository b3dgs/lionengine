/*
 * Copyright (C) 2013-2023 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */
package com.b3dgs.lionengine.network.server;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.function.Supplier;

import com.b3dgs.lionengine.Listenable;
import com.b3dgs.lionengine.network.Message;

/**
 * Server representation, able to receive client connection, used as reference for global synchronization.
 */
public interface Server extends Listenable<ServerListener>
{
    /**
     * Start server at specified network location.
     * 
     * @param ip The ip address [x.x.x.x].
     * @param port The port number [1-65535].
     * @throws IOException If error.
     */
    void start(String ip, int port) throws IOException;

    /**
     * Stop server and release resources.
     */
    void stop();

    /**
     * Send message to clients.
     * 
     * @param message The Message to send.
     * @throws IOException If error.
     */
    void send(Message message) throws IOException;

    /**
     * Send message to client.
     * 
     * @param message The Message to send.
     * @param clientId The client id to send.
     * @throws IOException If error.
     */
    void send(Message message, Integer clientId) throws IOException;

    /**
     * Set info supplier.
     * 
     * @param info The supplier.
     */
    void setInfoSupplier(Supplier<ByteBuffer> info);

    /**
     * Get the clients number.
     * 
     * @return The clients number.
     */
    int getClients();

    /**
     * Get bandwidth upstream.
     * 
     * @return The upstream bandwidth (in kB/sec, negative if not updated).
     */
    float getBandwidthUp();

    /**
     * Get bandwidth downstream.
     * 
     * @return The downstream bandwidth (in kB/sec, negative if not updated).
     */
    float getBandwidthDown();
}
