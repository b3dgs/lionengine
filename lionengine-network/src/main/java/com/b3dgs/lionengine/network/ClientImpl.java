/*
 * Copyright (C) 2013-2022 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.network;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;

import com.b3dgs.lionengine.Check;
import com.b3dgs.lionengine.Constant;
import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Timing;
import com.b3dgs.lionengine.Verbose;
import com.b3dgs.lionengine.network.message.NetworkMessage;
import com.b3dgs.lionengine.network.message.NetworkMessageDecoder;

/**
 * Client implementation.
 */
final class ClientImpl extends NetworkModel<ConnectionListener> implements Client
{
    /** Ping timer. */
    private final Timing pingTimer;
    /** Ping request timer. */
    private final Timing pingRequestTimer;
    /** Average bandwidth. */
    private final Timing bandwidthTimer;
    /** Socket. */
    private Socket socket;
    /** Output stream. */
    private ObjectOutputStream out;
    /** Input stream. */
    private ObjectInputStream in;
    /** Client id. */
    private byte clientId;
    /** Client name. */
    private String clientName;
    /** Disconnect flag. */
    private boolean connected;
    /** Ping. */
    private int ping;
    /** Bandwidth size. */
    private int bandwidth;
    /** Bandwidth per second. */
    private int bandwidthPerSecond;

    /**
     * Internal constructor.
     * 
     * @param decoder The message decoder.
     */
    ClientImpl(NetworkMessageDecoder decoder)
    {
        super(decoder);
        pingTimer = new Timing();
        pingRequestTimer = new Timing();
        bandwidthTimer = new Timing();
        connected = false;
        clientId = -1;
        clientName = null;
        bandwidth = 0;
    }

    /**
     * Terminate connection.
     */
    private void kick()
    {
        if (!connected)
        {
            return;
        }
        messagesIn.clear();
        messagesOut.clear();
        try
        {
            out.close();
        }
        catch (final IOException exception)
        {
            Verbose.exception(exception, "Error on closing output");
        }
        try
        {
            in.close();
        }
        catch (final IOException exception)
        {
            Verbose.exception(exception, "Error on closing input");
        }
        try
        {
            socket.close();
        }
        catch (final IOException exception)
        {
            Verbose.exception(exception, "Error on closing socket");
        }
        for (final ConnectionListener listener : listeners)
        {
            listener.notifyConnectionTerminated(Byte.valueOf(getId()));
        }
        listeners.clear();
        connected = false;
        Verbose.info("Disconnected from the server !");
    }

    /**
     * Get the name value read from the stream.
     * 
     * @return The name string.
     * @throws IOException In case of error.
     */
    private String readString() throws IOException
    {
        final int size = in.readByte();
        if (size > 0)
        {
            final byte[] name = new byte[size];
            if (in.read(name) != -1)
            {
                return new String(name, NetworkMessage.CHARSET);
            }
        }
        return null;
    }

    /**
     * Update the message from its id.
     * 
     * @param messageSystemId The message system ID.
     * @throws IOException If error when writing data.
     */
    private void updateMessage(byte messageSystemId) throws IOException
    {
        switch (messageSystemId)
        {
            case NetworkMessageSystemId.CONNECTING:
                updateConnecting();
                break;
            case NetworkMessageSystemId.CONNECTED:
                updateConnected();
                break;
            case NetworkMessageSystemId.PING:
                ping = (int) pingTimer.elapsed();
                break;
            case NetworkMessageSystemId.KICKED:
                kick();
                break;
            case NetworkMessageSystemId.OTHER_CLIENT_CONNECTED:
                updateOtherClientConnected();
                break;
            case NetworkMessageSystemId.OTHER_CLIENT_DISCONNECTED:
                updateOtherClientDisconnected();
                break;
            case NetworkMessageSystemId.OTHER_CLIENT_RENAMED:
                updateOtherClientRenamed();
                break;
            case NetworkMessageSystemId.USER_MESSAGE:
                updateUserMessage();
                break;
            default:
                break;
        }
    }

    /**
     * Update the connecting case.
     * 
     * @throws IOException If error when writing data.
     */
    private void updateConnecting() throws IOException
    {
        if (clientId == -1)
        {
            // Receive id
            clientId = in.readByte();
            // Send the name
            out.writeByte(NetworkMessageSystemId.CONNECTING);
            out.writeByte(clientId);
            final byte[] data = clientName.getBytes(NetworkMessage.CHARSET);
            out.writeByte(data.length);
            out.write(data);
            out.flush();
            Verbose.info("Client: Performing connection to the server...");
        }
    }

    /**
     * Update the connected case.
     *
     * @throws IOException If error when writing data.
     */
    private void updateConnected() throws IOException
    {
        byte cid = in.readByte();
        // Ensure the client id is the same
        if (cid != clientId)
        {
            return;
        }
        for (final ConnectionListener listener : listeners)
        {
            listener.notifyConnectionEstablished(Byte.valueOf(clientId), clientName);
        }
        // Read the client list
        final int clientsNumber = in.readByte();
        for (int i = 0; i < clientsNumber; i++)
        {
            cid = in.readByte();
            final String cname = readString();
            for (final ConnectionListener listener : listeners)
            {
                listener.notifyClientConnected(Byte.valueOf(cid), cname);
            }
        }
        // Message of the day if has
        if (in.available() > 0)
        {
            final String motd = readString();
            for (final ConnectionListener listener : listeners)
            {
                listener.notifyMessageOfTheDay(motd);
            }
        }
        // Send the last answer
        out.write(NetworkMessageSystemId.CONNECTED);
        out.write(clientId);
        out.flush();
        Verbose.info("Client: Connected to the server !");
    }

    /**
     * Update the other client connected case.
     * 
     * @throws IOException If error when writing data.
     */
    private void updateOtherClientConnected() throws IOException
    {
        final byte cid = in.readByte();
        final String cname = readString();
        for (final ConnectionListener listener : listeners)
        {
            listener.notifyClientConnected(Byte.valueOf(cid), cname);
        }
    }

    /**
     * Update the other client disconnected case.
     * 
     * @throws IOException If error when writing data.
     */
    private void updateOtherClientDisconnected() throws IOException
    {
        final byte cid = in.readByte();
        final String cname = readString();
        for (final ConnectionListener listener : listeners)
        {
            listener.notifyClientDisconnected(Byte.valueOf(cid), cname);
        }
    }

    /**
     * Update the other client renamed case.
     * 
     * @throws IOException If error when writing data.
     */
    private void updateOtherClientRenamed() throws IOException
    {
        final byte cid = in.readByte();
        final String cname = readString();
        for (final ConnectionListener listener : listeners)
        {
            listener.notifyClientNameChanged(Byte.valueOf(cid), cname);
        }
    }

    /**
     * Update the other client renamed case.
     * 
     * @throws IOException If error when writing data.
     */
    private void updateUserMessage() throws IOException
    {
        final byte from = in.readByte();
        final byte dest = in.readByte();
        final byte type = in.readByte();
        final int size = in.readInt();
        if (size > 0)
        {
            final byte[] data = new byte[size];
            if (in.read(data) != -1)
            {
                try (DataInputStream buffer = new DataInputStream(new ByteArrayInputStream(data)))
                {
                    decodeMessage(type, from, dest, buffer);
                }
            }
        }
        final int headerSize = 4;
        bandwidth += headerSize + size;
    }

    /**
     * Send message over the network.
     * 
     * @param message The message to send.
     */
    private void sendMessage(NetworkMessage message)
    {
        try (ByteArrayOutputStream encode = message.encode())
        {
            final byte[] encoded = encode.toByteArray();
            // Message header
            out.writeByte(NetworkMessageSystemId.USER_MESSAGE);
            out.writeByte(message.getClientId());
            out.writeByte(message.getClientDestId());
            out.writeByte(message.getType());
            // Message content
            out.writeInt(encoded.length);
            out.write(encoded);
            out.flush();

            final int headerSize = 8;
            bandwidth += headerSize + encoded.length;
        }
        catch (final IOException exception)
        {
            Verbose.exception(exception, "Unable to send the message for client: ", String.valueOf(clientId));
        }
    }

    /*
     * Client
     */

    @Override
    public void connect(String ip, int port)
    {
        Check.notNull(ip);
        Check.superiorOrEqual(port, 0);
        Check.inferiorOrEqual(port, Constant.MAX_PORT);

        try
        {
            socket = new Socket(InetAddress.getByName(ip), port);
            out = new ObjectOutputStream(socket.getOutputStream());
            in = new ObjectInputStream(socket.getInputStream());
            connected = true;
            clientId = -1;
            pingRequestTimer.start();
            bandwidthTimer.start();
        }
        catch (final IOException exception)
        {
            throw new LionEngineException(exception, "Cannot connect to the server !");
        }
    }

    @Override
    public boolean isConnected()
    {
        return connected;
    }

    @Override
    public void setName(String name)
    {
        clientName = name;
        if (!connected)
        {
            return;
        }
        try
        {
            out.write(NetworkMessageSystemId.OTHER_CLIENT_RENAMED);
            out.write(clientId);
            final byte[] data = clientName.getBytes(NetworkMessage.CHARSET);
            out.writeByte(data.length);
            out.write(data);
            out.flush();
        }
        catch (final IOException exception)
        {
            Verbose.exception(exception, "Unable to set a new client name !");
        }
    }

    @Override
    public String getName()
    {
        return clientName;
    }

    @Override
    public int getPing()
    {
        return ping;
    }

    @Override
    public int getBandwidth()
    {
        return bandwidthPerSecond;
    }

    @Override
    public byte getId()
    {
        return clientId;
    }

    /*
     * Network
     */

    @Override
    public void disconnect()
    {
        try
        {
            out.write(NetworkMessageSystemId.OTHER_CLIENT_DISCONNECTED);
            out.write(clientId);
            out.flush();
            kick();
        }
        catch (final SocketException exception)
        {
            Verbose.exception(exception);
            connected = false;
        }
        catch (final IOException exception)
        {
            Verbose.exception(exception);
        }
        connected = false;
    }

    @Override
    public void sendMessages()
    {
        if (!connected)
        {
            return;
        }
        // Ping
        final long pingMilli = 1000L;
        if (pingRequestTimer.elapsed(pingMilli))
        {
            try
            {
                out.writeByte(NetworkMessageSystemId.PING);
                out.writeByte(clientId);
                out.flush();
                pingTimer.restart();
                pingRequestTimer.restart();
                bandwidth += 2;
            }
            catch (final IOException exception)
            {
                Verbose.exception(exception, "Unable to send the messages for client: ", String.valueOf(clientId));
            }
        }
        // Send messages
        for (final NetworkMessage message : messagesOut)
        {
            sendMessage(message);
        }
        final long bandwidthMilli = 1000L;
        if (bandwidthTimer.elapsed(bandwidthMilli))
        {
            bandwidthPerSecond = bandwidth;
            bandwidth = 0;
            bandwidthTimer.stop();
            bandwidthTimer.start();
        }
        messagesOut.clear();
    }

    @Override // CHECKSTYLE IGNORE LINE: TrailingComment|ReturnCount
    public void receiveMessages()
    {
        if (!connected)
        {
            return;
        }
        messagesIn.clear();
        try
        {
            if (in.available() == 0)
            {
                return;
            }
            final byte messageSystemId = in.readByte();
            updateMessage(messageSystemId);
        }
        catch (final IOException exception)
        {
            Verbose.exception(exception, "Unable to receive the messages for client: ", String.valueOf(clientId));
        }
    }
}
