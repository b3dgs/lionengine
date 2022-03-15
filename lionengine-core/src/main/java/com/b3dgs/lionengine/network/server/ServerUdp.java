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
package com.b3dgs.lionengine.network.server;

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
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import com.b3dgs.lionengine.Check;
import com.b3dgs.lionengine.Constant;
import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.ListenableModel;
import com.b3dgs.lionengine.Timing;
import com.b3dgs.lionengine.UtilConversion;
import com.b3dgs.lionengine.Verbose;
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
 * UDP server based implementation.
 */
// CHECKSTYLE IGNORE LINE: DataAbstractionCoupling
public class ServerUdp implements Server
{
    private static final int TIMEOUT = 12_000;

    private static final String INFO_CONNECTED = " connected";
    private static final String INFO_DISCONNECTED = " disconnected";
    private static final String INFO_PING = " ping";
    private static final String INFO_STOPPED = "Server stopped";

    private static final String ERROR_MAX_CLIENTS = "Maximum clients reached!";
    private static final String ERROR_START_SERVER = "Unable to start server!";
    private static final String ERROR_ALREADY_CONNECTED = " already connected!";
    private static final String ERROR_NOT_CONNECTED = " not connected!";
    private static final String ERROR_TIMEOUT = " timeout!";

    private Integer getId(DatagramPacket packet)
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + packet.getAddress().hashCode();
        result = prime * result + packet.getPort();
        return Integer.valueOf(result);
    }

    private final ListenableModel<ServerListener> listenable = new ListenableModel<>();
    private final Map<Integer, ClientData> clients = new ConcurrentHashMap<>();
    private final Map<Integer, Integer> idToClientId = new ConcurrentHashMap<>();
    private final Map<Integer, Integer> clientIdtoId = new ConcurrentHashMap<>();
    private final Channel channel;

    private Thread threadReceive;
    private Thread threadAlive;
    private Thread threadBandwidth;
    private DatagramSocket socket;
    private boolean running;
    private final AtomicInteger bandwidthUpSum = new AtomicInteger();
    private final AtomicInteger bandwidthDownSum = new AtomicInteger();
    private volatile float bandwidthUp = -1;
    private volatile float bandwidthDown = -1;

    private Integer getNextClientId()
    {
        for (int i = UtilNetwork.SERVER_ID.intValue() + 1; i < Constant.UNSIGNED_BYTE; i++)
        {
            final Integer free = Integer.valueOf(i);
            if (!idToClientId.containsValue(free))
            {
                return free;
            }
        }
        throw new LionEngineException(ERROR_MAX_CLIENTS);
    }

    /**
     * Create server.
     * 
     * @param channel The channel reference (must not be <code>null</code>).
     * @throws LionEngineException If invalid argument.
     */
    public ServerUdp(Channel channel)
    {
        super();

        Check.notNull(channel);

        this.channel = channel;
    }

    private void connect(DatagramPacket packet, ByteBuffer buffer) throws IOException
    {
        final Integer id = getId(packet);
        Connected.decode(buffer);

        if (clients.containsKey(id))
        {
            Verbose.warning(ServerUdp.class, UtilNetwork.toString(packet) + ERROR_ALREADY_CONNECTED);
        }
        else
        {
            final ClientData client = new ClientData(packet.getAddress(), packet.getPort(), getNextClientId());
            clients.put(id, client);
            idToClientId.put(id, client.getClientId());
            clientIdtoId.put(client.getClientId(), id);

            send(client, new Connected(client.getClientId()));
            client.alive();
            notifyClientConnected(client);

            for (final ClientData c : clients.values())
            {
                final Set<Integer> other = new HashSet<>(idToClientId.values());
                other.remove(c.getClientId());
                if (!other.isEmpty())
                {
                    send(c, new ClientsList(c.getClientId(), other));
                }
            }

            Verbose.info(client + INFO_CONNECTED);
        }
    }

    private void notifyClientConnected(ClientData client)
    {
        final int n = listenable.size();
        for (int i = 0; i < n; i++)
        {
            listenable.get(i)
                      .notifyClientConnected(client.getIp().toString(),
                                             client.getPort(),
                                             client.getClientId().intValue());
        }
    }

    private void send(ClientData client, Message message) throws IOException
    {
        UtilNetwork.send(socket, client.getIp(), client.getPort(), message);
    }

    private void alive(DatagramPacket packet, ByteBuffer buffer) throws IOException
    {
        final Integer id = getId(packet);
        if (clients.containsKey(id))
        {
            final ClientData client = clients.get(id);
            Alive.decode(buffer, client.getClientId());
            client.alive();
        }
        else
        {
            Verbose.warning(ServerUdp.class, UtilNetwork.toString(packet) + ERROR_NOT_CONNECTED);
        }
    }

    private void disconnect(DatagramPacket packet, ByteBuffer buffer) throws IOException
    {
        final Integer id = getId(packet);
        if (clients.containsKey(id))
        {
            final Integer disconnected = clients.get(id).getClientId();
            Disconnected.decode(buffer, disconnected);

            final ClientData client = clients.remove(id);
            idToClientId.remove(id);
            clientIdtoId.remove(disconnected);

            for (final ClientData c : clients.values())
            {
                send(c, new Disconnected(c.getClientId(), disconnected));
            }

            channel.write(new Packet(client.getClientId(), disconnected.intValue(), UtilNetwork.MODE_DISCONNECT));

            notifyClientDisconnected(client);

            Verbose.info(client + INFO_DISCONNECTED);
        }
        else
        {
            Verbose.warning(ServerUdp.class, UtilNetwork.toString(packet) + ERROR_NOT_CONNECTED);
        }
    }

    private void notifyClientDisconnected(ClientData client)
    {
        final int n = listenable.size();
        for (int i = 0; i < n; i++)
        {
            listenable.get(i)
                      .notifyClientDisconnected(client.getIp().toString(),
                                                client.getPort(),
                                                client.getClientId().intValue());
        }
    }

    private void ping(DatagramPacket packet, ByteBuffer buffer) throws IOException
    {
        final Integer id = getId(packet);
        if (clients.containsKey(id))
        {
            final ClientData client = clients.get(id);
            final Integer clientId = client.getClientId();
            Ping.decode(buffer, clientId);

            send(client, new Ping(clientId));
            Verbose.info(client + INFO_PING);
        }
        else
        {
            Verbose.warning(ServerUdp.class, UtilNetwork.toString(packet) + ERROR_NOT_CONNECTED);
        }
    }

    private void direct(DatagramPacket packet, ByteBuffer buffer) throws IOException
    {
        final Integer id = getId(packet);
        if (clients.containsKey(id))
        {
            final ClientData client = clients.get(id);
            channel.write(Direct.decode(buffer, client.getClientId()));
        }
        else
        {
            Verbose.warning(ServerUdp.class, UtilNetwork.toString(packet) + ERROR_NOT_CONNECTED);
        }
    }

    private void data(DatagramPacket packet, ByteBuffer buffer) throws IOException
    {
        final Integer id = getId(packet);
        if (clients.containsKey(id))
        {
            final ClientData client = clients.get(id);
            channel.write(Data.decode(buffer, client.getClientId()));

            sendClients(buffer, client.getClientId());
        }
        else
        {
            Verbose.warning(ServerUdp.class, UtilNetwork.toString(packet) + ERROR_NOT_CONNECTED);
        }
    }

    private void sendClients(ByteBuffer buffer, Integer clientId) throws IOException
    {
        for (final ClientData client : clients.values())
        {
            if (!client.getClientId().equals(clientId))
            {
                final ByteBuffer send = UtilNetwork.createPacket(buffer);
                send.put(UtilNetwork.HEADER_BYTES_NUMBER + UtilNetwork.INDEX_CLIENT_ID,
                         UtilConversion.fromUnsignedByte(client.getClientId().intValue()));
                send.put(UtilNetwork.HEADER_BYTES_NUMBER + UtilNetwork.INDEX_CLIENT_SRC_ID,
                         UtilConversion.fromUnsignedByte(clientId.intValue()));

                socket.send(new DatagramPacket(send.array(), send.capacity(), client.getIp(), client.getPort()));
            }
        }
    }

    private void taskListen()
    {
        while (running)
        {
            try
            {
                final DatagramPacket packet = UtilNetwork.receive(socket);
                handleType(packet);
            }
            catch (final IOException exception)
            {
                if (running)
                {
                    Verbose.exception(exception);
                }
            }
        }
    }

    private void handleType(DatagramPacket packet) throws IOException
    {
        final ByteBuffer buffer = UtilNetwork.getBuffer(packet);
        bandwidthDownSum.addAndGet(buffer.capacity());

        final MessageType type = MessageType.from(buffer);
        switch (type)
        {
            case CONNECT:
                connect(packet, buffer);
                break;
            case DISCONNECT:
                disconnect(packet, buffer);
                break;
            case ALIVE:
                alive(packet, buffer);
                break;
            case PING:
                ping(packet, buffer);
                break;
            case DIRECT:
                direct(packet, buffer);
                break;
            case DATA:
                data(packet, buffer);
                break;
            case CLIENTS_LIST:
            case UNKNOWN:
                break;
            default:
                throw new LionEngineException(type);
        }
    }

    private void taskAlive()
    {
        final List<Integer> toRemove = new ArrayList<>();
        while (running)
        {
            try
            {
                Thread.sleep(TIMEOUT);
            }
            catch (@SuppressWarnings("unused") final InterruptedException exception)
            {
                Thread.currentThread().interrupt();
                break;
            }

            for (final ClientData client : clients.values())
            {
                if (!client.isAlive(TIMEOUT))
                {
                    toRemove.add(client.getClientId());
                    notifyClientDisconnected(client);
                    Verbose.info(UtilNetwork.toString(client.getIp().toString(), client.getPort()) + ERROR_TIMEOUT);
                }
            }
            for (final Integer id : toRemove)
            {
                clients.remove(id);
            }
            toRemove.clear();
        }
    }

    private void taskBandwidth()
    {
        final Timing timing = new Timing();
        while (running)
        {
            timing.start();
            try
            {
                Thread.sleep(Constant.THOUSAND);
            }
            catch (@SuppressWarnings("unused") final InterruptedException exception)
            {
                Thread.currentThread().interrupt();
                break;
            }
            final float elapsed = timing.elapsed() / (float) Constant.THOUSAND;
            timing.restart();

            final float factor = 1 / 1024f * elapsed;
            bandwidthUp = bandwidthUpSum.getAndSet(0) * factor;
            bandwidthDown = bandwidthDownSum.getAndSet(0) * factor;
        }
    }

    @Override
    public synchronized void start(String ip, int port) throws IOException
    {
        if (!running)
        {
            running = true;
            try
            {
                socket = new DatagramSocket(port, InetAddress.getByName(ip));
                socket.setReuseAddress(true);
            }
            catch (final SocketException | UnknownHostException e)
            {
                throw new IOException(ERROR_START_SERVER, e);
            }
            threadReceive = new Thread(this::taskListen, ServerUdp.class.getSimpleName() + "_listen");
            threadAlive = new Thread(this::taskAlive, ServerUdp.class.getSimpleName() + "_alive");
            threadBandwidth = new Thread(this::taskBandwidth, ServerUdp.class.getSimpleName() + "_bandwidth");

            threadReceive.start();
            threadAlive.start();
            threadBandwidth.start();

            notifyServerStarted(ip, port);
        }
    }

    private void notifyServerStarted(String ip, int port)
    {
        final int n = listenable.size();
        for (int i = 0; i < n; i++)
        {
            listenable.get(i).notifyServerStarted(ip, port);
        }
    }

    private void notifyServerStopped()
    {
        final int n = listenable.size();
        for (int i = 0; i < n; i++)
        {
            listenable.get(i).notifyServerStopped();
        }
    }

    @Override
    public void stop()
    {
        for (final ClientData c : clients.values())
        {
            try
            {
                send(c, new Disconnected(c.getClientId(), UtilNetwork.SERVER_ID));
            }
            catch (final IOException exception)
            {
                Verbose.exception(exception);
            }
        }

        socket.close();
        running = false;

        threadReceive.interrupt();
        threadAlive.interrupt();

        UtilNetwork.await(threadReceive);
        UtilNetwork.await(threadAlive);
        UtilNetwork.await(threadBandwidth);

        clients.clear();
        bandwidthUp = -1;
        bandwidthDown = -1;
        threadReceive = null;
        threadAlive = null;
        socket = null;

        notifyServerStopped();

        Verbose.info(INFO_STOPPED);
    }

    @Override
    public void send(Message message) throws IOException
    {
        final ByteBuffer buffer = UtilNetwork.createPacket(message.create());

        for (final ClientData client : clients.values())
        {
            buffer.put(UtilNetwork.HEADER_BYTES_NUMBER + UtilNetwork.INDEX_CLIENT_ID,
                       UtilConversion.fromUnsignedByte(client.getClientId().intValue()));

            socket.send(new DatagramPacket(buffer.array(), buffer.capacity(), client.getIp(), client.getPort()));

            bandwidthUpSum.addAndGet(buffer.capacity());
        }
    }

    @Override
    public void send(Message message, Integer clientId) throws IOException
    {
        final ClientData client = clients.get(clientIdtoId.get(clientId));

        final ByteBuffer buffer = UtilNetwork.createPacket(message.create());

        buffer.put(UtilNetwork.HEADER_BYTES_NUMBER + UtilNetwork.INDEX_CLIENT_ID,
                   UtilConversion.fromUnsignedByte(client.getClientId().intValue()));

        socket.send(new DatagramPacket(buffer.array(), buffer.capacity(), client.getIp(), client.getPort()));

        bandwidthUpSum.addAndGet(buffer.capacity());
    }

    @Override
    public int getClients()
    {
        return clients.size();
    }

    @Override
    public float getBandwidthUp()
    {
        return bandwidthDown;
    }

    @Override
    public float getBandwidthDown()
    {
        return bandwidthUp;
    }

    /*
     * Listenable
     */

    @Override
    public void addListener(ServerListener listener)
    {
        listenable.addListener(listener);
    }

    @Override
    public void removeListener(ServerListener listener)
    {
        listenable.removeListener(listener);
    }
}
