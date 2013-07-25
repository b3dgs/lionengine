package com.b3dgs.lionengine.network.impl;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Verbose;
import com.b3dgs.lionengine.network.ClientListener;
import com.b3dgs.lionengine.network.Server;
import com.b3dgs.lionengine.network.message.NetworkMessage;
import com.b3dgs.lionengine.network.message.NetworkMessageDecoder;

/**
 * Server implementation.
 */
public class ServerImpl
        extends NetworkModel<ClientListener>
        implements Server
{
    /** Client list. */
    private final HashMap<Byte, ClientSocket> clientsList;
    /** Remove list. */
    private final Set<ClientSocket> removeList;
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

    /**
     * Constructor.
     * 
     * @param decoder The message decoder.
     * @throws LionEngineException if cannot create the server.
     */
    public ServerImpl(NetworkMessageDecoder decoder) throws LionEngineException
    {
        super(decoder);
        clientsList = new HashMap<>(1);
        removeList = new HashSet<>(1);
        willRemove = false;
        clientsNumber = 0;
        messageOfTheDay = null;
        port = -1;
        started = false;
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
                if (secure > 127)
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
        catch (final Exception exception)
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
        final byte[] data = name.getBytes();
        client.getOut().writeByte(data.length);
        client.getOut().write(data);
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
    public void start(String name, int port)
    {
        if (!started)
        {
            try
            {
                serverSocket = new ServerSocket(port);
                clientConnectionListener = new ClientConnecter(serverSocket, this);
                clientConnectionListener.start();
                this.port = port;
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
        this.removeClient(clientsList.get(clientId));
    }

    @Override
    public int getNumberOfClients()
    {
        return clientsNumber;
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
        final List<ClientSocket> delete = new ArrayList<>(clientsList.size());
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
                    // Ignore
                }
            }
            delete.add(client);
        }
        for (final ClientSocket client : delete)
        {
            client.sendMessage(NetworkMessageSystemId.KICKED);
            this.removeClient(client);
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
            try (DataInputStream buffer = new DataInputStream(new ByteArrayInputStream(data));)
            {
                final byte messageSystemId = buffer.readByte();
                final byte from = buffer.readByte();

                // Check id
                if (from != client.getId())
                {
                    continue;
                }
                // Check message header type
                switch (messageSystemId)
                {
                    case NetworkMessageSystemId.CONNECTING:
                    {
                        // Check id and state validity
                        if (from < 0 || client.getState() != StateConnection.CONNECTING)
                        {
                            break;
                        }
                        // Receive the name
                        final byte[] name = new byte[buffer.readByte()];
                        buffer.read(name);
                        client.setName(new String(name));

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
                            final byte[] motd = messageOfTheDay.getBytes();
                            client.getOut().writeByte(motd.length);
                            client.getOut().write(motd);
                        }
                        // Send
                        client.getOut().flush();
                        break;
                    }
                    case NetworkMessageSystemId.CONNECTED:
                    {
                        // Check id and state validity
                        if (from < 0 || client.getState() != StateConnection.CONNECTED)
                        {
                            break;
                        }
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
                        break;
                    }
                    case NetworkMessageSystemId.PING:
                    {
                        client.getOut().writeByte(NetworkMessageSystemId.PING);
                        client.getOut().flush();
                        break;
                    }
                    case NetworkMessageSystemId.OTHER_CLIENT_DISCONNECTED:
                    {
                        // Check id and state validity
                        if (from < 0 || client.getState() != StateConnection.CONNECTED)
                        {
                            break;
                        }
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
                        this.removeClient(Byte.valueOf(from));
                        break;
                    }
                    case NetworkMessageSystemId.OTHER_CLIENT_RENAMED:
                    {
                        // Check id and state validity
                        if (from < 0 || client.getState() != StateConnection.CONNECTED)
                        {
                            break;
                        }
                        // Receive the name
                        final byte[] name = new byte[buffer.readByte()];
                        buffer.read(name);
                        final String newName = new String(name);
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
                        break;
                    }
                    // Standard user message
                    case NetworkMessageSystemId.USER_MESSAGE:
                    {
                        // Check id and state validity
                        if (from < 0 || client.getState() != StateConnection.CONNECTED)
                        {
                            break;
                        }
                        final byte dest = buffer.readByte();
                        final byte type = buffer.readByte();
                        final int size = buffer.readInt();
                        if (size > 0)
                        {
                            final byte[] clientData = new byte[size];
                            buffer.read(clientData);
                            final DataInputStream clientBuffer = new DataInputStream(new ByteArrayInputStream(
                                    clientData));
                            decodeMessage(type, from, dest, clientBuffer);
                        }
                        break;
                    }
                    default:
                        break;
                }
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
                try (ByteArrayOutputStream encode = message.encode();)
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
                }
                catch (final IOException exception)
                {
                    Verbose.warning(Server.class, "sendMessage", "Unable to send the messages for client: ",
                            String.valueOf(client.getId()));
                }
            }
        }
        messagesOut.clear();
    }
}
