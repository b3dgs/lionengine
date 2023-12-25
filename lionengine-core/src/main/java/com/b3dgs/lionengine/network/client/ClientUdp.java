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
package com.b3dgs.lionengine.network.client;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicLong;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.b3dgs.lionengine.Check;
import com.b3dgs.lionengine.Constant;
import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.ListenableModel;
import com.b3dgs.lionengine.UtilConversion;
import com.b3dgs.lionengine.network.Alive;
import com.b3dgs.lionengine.network.Channel;
import com.b3dgs.lionengine.network.Data;
import com.b3dgs.lionengine.network.Direct;
import com.b3dgs.lionengine.network.Message;
import com.b3dgs.lionengine.network.MessageType;
import com.b3dgs.lionengine.network.Packet;
import com.b3dgs.lionengine.network.Ping;
import com.b3dgs.lionengine.network.UtilNetwork;

/**
 * UDP based client implementation.
 */
// CHECKSTYLE IGNORE LINE: DataAbstractionCoupling
public class ClientUdp implements Client
{
    private static final int ALIVE_DELAY_MS = 4000;
    private static final String SERVER_DISCONNECTED = "Server disconnected!";
    private static final Logger LOGGER = LoggerFactory.getLogger(ClientUdp.class);

    private final ListenableModel<ClientListener> listenable = new ListenableModel<>();
    private final Semaphore pingLock = new Semaphore(0);

    private final AtomicLong ping = new AtomicLong();
    private final Channel channel;
    private final Set<Integer> clientsNew = new HashSet<>();
    private final Set<Integer> clientsConnected = new HashSet<>();
    private final List<Integer> toRemove = new ArrayList<>();

    private Thread threadReceive;
    private Thread threadAlive;
    private volatile InetAddress address;
    private volatile DatagramSocket socket;
    private volatile int port;
    private volatile Integer clientId;
    private volatile boolean running;

    /**
     * Create client.
     * 
     * @param channel The channel reference (must not be <code>null</code>).
     * @throws LionEngineException If invalid argument.
     */
    public ClientUdp(Channel channel)
    {
        super();

        Check.notNull(channel);

        this.channel = channel;
    }

    private void taskReceive()
    {
        while (running)
        {
            try
            {
                final DatagramPacket packet = UtilNetwork.receive(socket);
                final ByteBuffer buffer = UtilNetwork.getBuffer(packet);
                final MessageType type = MessageType.from(buffer);

                if (MessageType.DIRECT == type)
                {
                    channel.write(Direct.decode(buffer, clientId));
                }
                else if (MessageType.DATA == type)
                {
                    channel.write(Data.decode(buffer, clientId));
                }
                else if (MessageType.CLIENTS_LIST == type)
                {
                    handleClientsList(buffer);
                }
                else if (MessageType.DISCONNECT == type)
                {
                    handleDisconnected(buffer);
                }
                else if (MessageType.PING == type)
                {
                    handlePing(buffer);
                }
                else if (MessageType.NAME_SET == type)
                {
                    handleNameSet(buffer);
                }
            }
            catch (final IOException exception)
            {
                if (running)
                {
                    LOGGER.error("Receive error", exception);
                }
            }
        }
    }

    private void handleClientsList(ByteBuffer buffer) throws IOException
    {
        final Set<Integer> list = ClientsList.decode(buffer, clientId);
        for (final Integer current : list)
        {
            if (!current.equals(clientId) && clientsNew.add(current))
            {
                notifyClientConnected(current);
                clientsConnected.add(current);
            }
        }
        for (final Integer current : clientsConnected)
        {
            if (!list.contains(current))
            {
                notifyClientDisconnected(current);
                toRemove.add(current);
            }
        }
        if (!toRemove.isEmpty())
        {
            clientsNew.removeAll(toRemove);
            clientsConnected.removeAll(toRemove);
            toRemove.clear();
        }
    }

    private void notifyClientConnected(Integer id)
    {
        final int n = listenable.size();
        for (int i = 0; i < n; i++)
        {
            listenable.get(i).notifyClientConnected(id);
        }
    }

    private void notifyClientDisconnected(Integer id)
    {
        final int n = listenable.size();
        for (int i = 0; i < n; i++)
        {
            listenable.get(i).notifyClientDisconnected(id);
        }
    }

    private void handleDisconnected(ByteBuffer buffer) throws IOException
    {
        final Integer disconnected = Disconnect.decode(buffer, clientId);
        channel.write(new Packet(clientId, disconnected.intValue(), UtilNetwork.MODE_DISCONNECT));
        if (disconnected.intValue() == 0)
        {
            close();
            LOGGER.info(SERVER_DISCONNECTED);
        }
        else
        {
            notifyClientDisconnected(disconnected);
        }
    }

    private void handlePing(ByteBuffer buffer) throws IOException
    {
        try
        {
            Ping.decode(buffer, clientId);
            ping.set((System.nanoTime() - ping.get()) / Constant.ONE_SECOND_IN_NANO);
        }
        catch (final IOException exception)
        {
            LOGGER.error("handlePing error", exception);
            ping.set(-1L);
            throw exception;
        }
        finally
        {
            pingLock.release();
        }
    }

    private void handleNameSet(ByteBuffer buffer) throws IOException
    {
        final String name = NameSet.decode(buffer, clientId);
        final Integer cid = Integer.valueOf(UtilConversion.toUnsignedByte(buffer.get(UtilNetwork.INDEX_MODE)));

        final int n = listenable.size();
        for (int i = 0; i < n; i++)
        {
            listenable.get(i).notifyClientNamed(cid, name);
        }
    }

    private void taskAlive()
    {
        while (running)
        {
            try
            {
                Thread.sleep(ALIVE_DELAY_MS);
            }
            catch (@SuppressWarnings("unused") final InterruptedException exception)
            {
                Thread.currentThread().interrupt();
                break;
            }
            try
            {
                send(new Alive(clientId));
            }
            catch (final IOException exception)
            {
                if (running)
                {
                    LOGGER.error("Alive error", exception);
                }
            }
        }
    }

    private void connectTo(String ip, int port) throws IOException
    {
        try
        {
            address = InetAddress.getByName(ip);
            socket = new DatagramSocket();
        }
        catch (final UnknownHostException | SocketException exception)
        {
            throw new IOException(exception);
        }
        this.port = port;

        final ByteBuffer send = Connect.encode();
        send(send, address, port);
        clientId = Connect.decode(socket);

        notifyConnected(ip, port, clientId);

        LOGGER.info("Connected to {} as {}", UtilNetwork.toString(ip, port), clientId);
    }

    private void notifyConnected(String ip, int port, Integer id)
    {
        final int n = listenable.size();
        for (int i = 0; i < n; i++)
        {
            listenable.get(i).notifyConnected(ip, port, id);
        }
    }

    private void notifyDisconnected(String ip, int port, Integer id)
    {
        final int n = listenable.size();
        for (int i = 0; i < n; i++)
        {
            listenable.get(i).notifyDisconnected(ip, port, id);
        }
    }

    private void send(ByteBuffer buffer, InetAddress address, int port) throws IOException
    {
        socket.send(new DatagramPacket(buffer.array(), buffer.capacity(), address, port));
    }

    @Override
    public synchronized void connect(String ip, int port) throws IOException
    {
        if (!running)
        {
            try
            {
                connectTo(ip, port);
            }
            catch (final IOException exception)
            {
                LOGGER.error("Connect error", exception);
            }
            running = true;

            threadReceive = new Thread(this::taskReceive, ClientUdp.class.getSimpleName() + "_receive");
            threadAlive = new Thread(this::taskAlive, ClientUdp.class.getSimpleName() + "_alive");

            threadReceive.start();
            threadAlive.start();
        }
    }

    @Override
    public synchronized void disconnect()
    {
        if (running)
        {
            try
            {
                send(new Disconnect(clientId));
            }
            catch (final IOException exception)
            {
                LOGGER.error("Disconnect error", exception);
            }
            close();
        }
    }

    /**
     * Close network.
     */
    private void close()
    {
        running = false;
        socket.close();

        threadReceive.interrupt();
        threadAlive.interrupt();

        UtilNetwork.await(threadReceive);
        UtilNetwork.await(threadAlive);

        notifyDisconnected(address.toString(), port, clientId);

        threadReceive = null;
        threadAlive = null;
        socket = null;
        clientId = null;
        address = null;
        port = -1;
    }

    @Override
    public synchronized long ping()
    {
        if (running)
        {
            try
            {
                ping.set(System.nanoTime());
                send(new Ping(clientId));
                pingLock.acquire();

                return ping.get();
            }
            catch (@SuppressWarnings("unused") final InterruptedException exception)
            {
                Thread.currentThread().interrupt();
            }
            catch (final IOException exception)
            {
                LOGGER.error("Ping error", exception);
            }
        }
        return -1;
    }

    @Override
    public void send(Message message) throws IOException
    {
        if (running)
        {
            final ByteBuffer send = UtilNetwork.createPacket(message.create());
            send(send, address, port);
        }
    }

    @Override
    public void setName(String name) throws IOException
    {
        final ByteBuffer send = NameSet.encode(clientId, name);
        send(send, address, port);
    }

    @Override
    public final Integer getClientId()
    {
        return clientId;
    }

    @Override
    public void addListener(ClientListener listener)
    {
        listenable.addListener(listener);
    }

    @Override
    public void removeListener(ClientListener listener)
    {
        listenable.removeListener(listener);
    }
}
