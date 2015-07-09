/*
 * Copyright (C) 2013-2015 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Timing;
import com.b3dgs.lionengine.core.Verbose;
import com.b3dgs.lionengine.network.message.NetworkMessage;
import com.b3dgs.lionengine.network.message.NetworkMessageDecoder;

/**
 * Server implementation.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
final class ServerImpl extends NetworkModel<ClientListener> implements Server
{
    /**
     * Send the id and the name to the client.
     * 
     * @param client The client to send to.
     * @param id The id to send.
     * @param name The name to send.
     * @throws IOException In case of error.
     */
    private static void writeIdAndName(ClientSocket client, int id, String name) throws IOException
    {
        // New client id
        client.getOut().writeByte(id);
        // New client name
        final byte[] data = name.getBytes(NetworkMessage.CHARSET);
        client.getOut().writeByte(data.length);
        client.getOut().write(data);
    }

    /**
     * Check if the client is in a valid state.
     * 
     * @param client The client to test.
     * @param from The client id.
     * @param expected The expected client state.
     * @return <code>true</code> if valid, <code>false</code> else.
     */
    private static boolean checkValidity(ClientSocket client, byte from, StateConnection expected)
    {
        return from >= 0 && client.getState() == expected;
    }

    /** Client list. */
    private final Map<Byte, ClientSocket> clientsList;
    /** Remove list. */
    private final Collection<ClientSocket> removeList;
    /** Average bandwidth. */
    private final Timing bandwidthTimer;
    /** Connection listener. */
    private ClientConnecter clientConnectionListener;
    /** Server socket. */
    private ServerSocket serverSocket;
    /** Current port. */
    private int port;
    /** Message of the day. */
    private String messageOfTheDay;
    /** Number of clients. */
    private int clientsNumber;
    /** Started flag. */
    private boolean started;
    /** Last id. */
    private byte lastId;
    /** Will remove a client. */
    private boolean willRemove;
    /** Bandwidth size. */
    private int bandwidth;
    /** Bandwidth per second. */
    private int bandwidthPerSecond;

    /**
     * Internal constructor.
     * 
     * @param decoder The message decoder.
     */
    ServerImpl(NetworkMessageDecoder decoder)
    {
        super(decoder);
        clientsList = new HashMap<>(1);
        removeList = new HashSet<>(1);
        bandwidthTimer = new Timing();
        willRemove = false;
        clientsNumber = 0;
        messageOfTheDay = null;
        port = -1;
        started = false;
        bandwidth = 0;
        lastId = 0;
    }

    /**
     * Add a client.
     * 
     * @param socket The socket to add.
     */
    void notifyNewClientConnected(Socket socket)
    {
        try
        {
            int secure = 0;
            while (clientsList.containsKey(Byte.valueOf(lastId)))
            {
                lastId++;
                secure++;
                final int max = 127;
                if (secure > max)
                {
                    break;
                }
            }
            // Prepare first data
            final ClientSocket client = new ClientSocket(lastId, socket, this);
            client.setState(StateConnection.CONNECTING);
            client.getOut().writeByte(NetworkMessageSystemId.CONNECTING);
            client.getOut().writeByte(client.getId());
            client.getOut().flush();

            // Update list
            clientsList.put(Byte.valueOf(client.getId()), client);
            clientsNumber++;
        }
        catch (final IOException | LionEngineException exception)
        {
            Verbose.warning(Server.class, "addClient", "Error on adding client: ", exception.getMessage());
            if (clientsList.remove(Byte.valueOf(lastId)) != null)
            {
                clientsNumber--;
            }
        }
    }

    /**
     * Remove a client from the server.
     * 
     * @param client The client to remove.
     */
    void removeClient(ClientSocket client)
    {
        if (client != null)
        {
            removeList.add(client);
            client.terminate();
            clientsNumber--;
            willRemove = true;
            Verbose.info("Server: ", client.getName() + " disconnected");
        }
    }

    /**
     * Update the receive connecting state.
     * 
     * @param client The current client.
     * @param buffer The data buffer.
     * @param from The id from.
     * @param expected The expected client state.
     * @throws IOException If error.
     */
    private void receiveConnecting(ClientSocket client, DataInputStream buffer, byte from, StateConnection expected)
            throws IOException
    {
        if (ServerImpl.checkValidity(client, from, expected))
        {
            // Receive the name
            final byte[] name = new byte[buffer.readByte()];
            if (buffer.read(name) == -1)
            {
                throw new IOException("Unable to read client name !");
            }
            client.setName(new String(name, NetworkMessage.CHARSET));

            // Send new state
            client.setState(StateConnection.CONNECTED);
            client.getOut().writeByte(NetworkMessageSystemId.CONNECTED);
            client.getOut().writeByte(client.getId());
            client.getOut().writeByte(clientsNumber - 1);

            // Send the list of other clients
            for (final ClientSocket other : clientsList.values())
            {
                if (other.getId() != from)
                {
                    ServerImpl.writeIdAndName(client, other.getId(), other.getName());
                }
            }
            // Send message of the day if has
            if (messageOfTheDay != null)
            {
                final byte[] motd = messageOfTheDay.getBytes(NetworkMessage.CHARSET);
                client.getOut().writeByte(motd.length);
                client.getOut().write(motd);
            }
            // Send
            client.getOut().flush();
        }
    }

    /**
     * Update the receive connected state.
     * 
     * @param client The current client.
     * @param from The id from.
     * @param expected The expected client state.
     * @throws IOException If error.
     */
    private void receiveConnected(ClientSocket client, byte from, StateConnection expected) throws IOException
    {
        if (ServerImpl.checkValidity(client, from, expected))
        {
            // Terminate last connection step and accept it
            Verbose.info("Server: ", client.getName(), " connected");
            for (final ClientListener listener : listeners)
            {
                listener.notifyClientConnected(Byte.valueOf(client.getId()), client.getName());
            }

            // Notify other clients
            for (final ClientSocket other : clientsList.values())
            {
                if (other.getId() == from)
                {
                    continue;
                }
                other.getOut().writeByte(NetworkMessageSystemId.OTHER_CLIENT_CONNECTED);
                ServerImpl.writeIdAndName(other, client.getId(), client.getName());
                // Send
                other.getOut().flush();
            }
        }
    }

    /**
     * Update the receive disconnected state.
     * 
     * @param client The current client.
     * @param from The id from.
     * @param expected The expected client state.
     * @throws IOException If error.
     */
    private void receiveDisconnected(ClientSocket client, byte from, StateConnection expected) throws IOException
    {
        if (ServerImpl.checkValidity(client, from, expected))
        {
            // Notify other clients
            client.setState(StateConnection.DISCONNECTED);
            for (final ClientListener listener : listeners)
            {
                listener.notifyClientDisconnected(Byte.valueOf(client.getId()), client.getName());
            }
            for (final ClientSocket other : clientsList.values())
            {
                if (other.getId() == from || other.getState() != StateConnection.CONNECTED)
                {
                    continue;
                }
                other.getOut().writeByte(NetworkMessageSystemId.OTHER_CLIENT_DISCONNECTED);
                ServerImpl.writeIdAndName(other, client.getId(), client.getName());
                // Send
                other.getOut().flush();
            }
            removeClient(Byte.valueOf(from));
        }
    }

    /**
     * Update the receive renamed state.
     * 
     * @param client The current client.
     * @param buffer The data buffer.
     * @param from The id from.
     * @param expected The expected client state.
     * @throws IOException If error.
     */
    private void receiveRenamed(ClientSocket client, DataInputStream buffer, byte from, StateConnection expected)
            throws IOException
    {
        if (ServerImpl.checkValidity(client, from, expected))
        {
            // Receive the name
            final byte[] name = new byte[buffer.readByte()];
            if (buffer.read(name) == -1)
            {
                throw new IOException("Unable to read client name on rename !");
            }
            final String newName = new String(name, NetworkMessage.CHARSET);
            Verbose.info("Server: ", client.getName(), " rennamed to " + newName);
            client.setName(newName);

            for (final ClientListener listener : listeners)
            {
                listener.notifyClientNameChanged(Byte.valueOf(client.getId()), client.getName());
            }

            // Notify all clients
            for (final ClientSocket other : clientsList.values())
            {
                other.getOut().writeByte(NetworkMessageSystemId.OTHER_CLIENT_RENAMED);
                ServerImpl.writeIdAndName(other, client.getId(), client.getName());
                other.getOut().flush();
            }
        }
    }

    /**
     * Update the receive standard message state.
     * 
     * @param client The client to test.
     * @param buffer The data buffer.
     * @param from The id from.
     * @param expected The expected client state.
     * @throws IOException If error.
     */
    private void receiveMessage(ClientSocket client, DataInputStream buffer, byte from, StateConnection expected)
            throws IOException
    {
        if (ServerImpl.checkValidity(client, from, expected))
        {
            final byte dest = buffer.readByte();
            final byte type = buffer.readByte();
            final int size = buffer.readInt();
            if (size > 0)
            {
                final byte[] clientData = new byte[size];
                if (buffer.read(clientData) != -1)
                {
                    final DataInputStream clientBuffer = new DataInputStream(new ByteArrayInputStream(clientData));
                    decodeMessage(type, from, dest, clientBuffer);
                }
            }
            final int headerSize = 4;
            bandwidth += headerSize + size;
        }
    }

    /**
     * Update the message depending of its ID.
     * 
     * @param client The client socket.
     * @param buffer The buffer input.
     * @param messageSystemId The message system ID.
     * @param from The source ID.
     * @throws IOException If error when reading.
     */
    private void updateMessage(ClientSocket client, DataInputStream buffer, byte messageSystemId, byte from)
            throws IOException
    {
        switch (messageSystemId)
        {
            case NetworkMessageSystemId.CONNECTING:
                receiveConnecting(client, buffer, from, StateConnection.CONNECTING);
                break;
            case NetworkMessageSystemId.CONNECTED:
                receiveConnected(client, from, StateConnection.CONNECTED);
                break;
            case NetworkMessageSystemId.PING:
                client.getOut().writeByte(NetworkMessageSystemId.PING);
                client.getOut().flush();
                bandwidth += 1;
                break;
            case NetworkMessageSystemId.OTHER_CLIENT_DISCONNECTED:
                receiveDisconnected(client, from, StateConnection.CONNECTED);
                break;
            case NetworkMessageSystemId.OTHER_CLIENT_RENAMED:
                receiveRenamed(client, buffer, from, StateConnection.CONNECTED);
                break;
            case NetworkMessageSystemId.USER_MESSAGE:
                receiveMessage(client, buffer, from, StateConnection.CONNECTED);
                break;
            default:
                break;
        }
    }

    /*
     * Server
     */

    @Override
    public void setMessageOfTheDay(String message)
    {
        messageOfTheDay = message;
    }

    @Override
    public void start(String name, int port) throws LionEngineException
    {
        if (!started)
        {
            try
            {
                serverSocket = new ServerSocket(port);
                clientConnectionListener = new ClientConnecter(serverSocket, this);
                clientConnectionListener.start();
                this.port = port;
                bandwidthTimer.start();
                started = true;
            }
            catch (final Exception exception)
            {
                throw new LionEngineException(exception, "Cannot create the server !");
            }
        }
    }

    @Override
    public void removeClient(Byte clientId)
    {
        removeClient(clientsList.get(clientId));
    }

    @Override
    public int getNumberOfClients()
    {
        return clientsNumber;
    }

    @Override
    public int getBandwidth()
    {
        return bandwidthPerSecond;
    }

    @Override
    public int getPort()
    {
        return port;
    }

    /*
     * Networker
     */

    @Override
    public void disconnect()
    {
        if (!started)
        {
            return;
        }
        receiveMessages();
        clientConnectionListener.terminate();

        // Disconnect all clients
        final Collection<ClientSocket> delete = new ArrayList<>(clientsList.size());
        for (final ClientSocket client : clientsList.values())
        {
            for (final ClientSocket other : clientsList.values())
            {
                if (other.getId() == client.getId())
                {
                    continue;
                }
                try
                {
                    other.getOut().writeByte(NetworkMessageSystemId.OTHER_CLIENT_DISCONNECTED);
                    ServerImpl.writeIdAndName(other, client.getId(), client.getName());
                    other.getOut().flush();
                }
                catch (final IOException exception)
                {
                    Verbose.exception(ServerImpl.class, "disconnect", exception);
                }
            }
            delete.add(client);
        }
        for (final ClientSocket client : delete)
        {
            client.sendMessage(NetworkMessageSystemId.KICKED);
            removeClient(client);
        }
        delete.clear();
        clientsList.clear();
        try
        {
            serverSocket.close();
        }
        catch (final Exception exception)
        {
            Verbose.warning(Server.class, "stop", "Error on closing server: ", exception.getMessage());
        }
        started = false;
    }

    @Override
    public void receiveMessages()
    {
        messagesIn.clear();
        for (final ClientSocket client : clientsList.values())
        {
            // Get client data from socket
            final byte[] data = client.receiveMessages();
            if (data == null)
            {
                continue;
            }
            try (DataInputStream buffer = new DataInputStream(new ByteArrayInputStream(data)))
            {
                final byte messageSystemId = buffer.readByte();
                final byte from = buffer.readByte();

                // Check id
                if (from != client.getId())
                {
                    continue;
                }
                // Check message header type
                updateMessage(client, buffer, messageSystemId, from);
            }
            catch (final IOException exception)
            {
                Verbose.warning(Server.class, "update", "Error on updating server: ", exception.getMessage());
            }
        }
        // Remove deleted clients
        if (willRemove)
        {
            for (final ClientSocket client : removeList)
            {
                clientsList.remove(Byte.valueOf(client.getId()));
            }
            removeList.clear();
            willRemove = false;
        }
    }

    @Override
    public void sendMessages()
    {
        // Send messages
        messagesOut.addAll(messagesIn);
        for (final NetworkMessage message : messagesOut)
        {
            for (final ClientSocket client : clientsList.values())
            {
                if (!(message.getClientDestId() == client.getId() || message.getClientDestId() == -1))
                {
                    continue;
                }
                try (ByteArrayOutputStream encode = message.encode())
                {
                    final byte[] encoded = encode.toByteArray();
                    // Message header
                    client.getOut().writeByte(NetworkMessageSystemId.USER_MESSAGE);
                    client.getOut().writeByte(message.getClientId());
                    client.getOut().writeByte(message.getClientDestId());
                    client.getOut().writeByte(message.getType());
                    // Message content
                    client.getOut().writeInt(encoded.length);
                    client.getOut().write(encoded);
                    client.getOut().flush();

                    final int headerSize = 4;
                    bandwidth += headerSize + encoded.length;
                }
                catch (final IOException exception)
                {
                    Verbose.warning(Server.class, "sendMessage", "Unable to send the messages for client: ",
                            String.valueOf(client.getId()));
                }
            }
        }
        final long bandwidthMilli = 1000L;
        if (bandwidthTimer.elapsed(bandwidthMilli))
        {
            bandwidthPerSecond = bandwidth;
            bandwidth = 0;
            bandwidthTimer.restart();
        }
        messagesOut.clear();
    }
}
