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
package com.b3dgs.lionengine.game.feature.networkable;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.b3dgs.lionengine.Verbose;
import com.b3dgs.lionengine.game.feature.ComponentUpdater;
import com.b3dgs.lionengine.game.feature.Factory;
import com.b3dgs.lionengine.game.feature.Featurable;
import com.b3dgs.lionengine.game.feature.Handlables;
import com.b3dgs.lionengine.game.feature.Handler;
import com.b3dgs.lionengine.game.feature.HandlerListener;
import com.b3dgs.lionengine.game.feature.Identifiable;
import com.b3dgs.lionengine.game.feature.Services;
import com.b3dgs.lionengine.network.Channel;
import com.b3dgs.lionengine.network.Message;
import com.b3dgs.lionengine.network.MessageAbstract;
import com.b3dgs.lionengine.network.Packet;
import com.b3dgs.lionengine.network.UtilNetwork;
import com.b3dgs.lionengine.network.client.Client;
import com.b3dgs.lionengine.network.client.ClientListener;
import com.b3dgs.lionengine.network.server.Server;
import com.b3dgs.lionengine.network.server.ServerListener;

/**
 * Default network component implementation.
 */
public class ComponentNetwork implements ComponentUpdater, HandlerListener
{
    /** Mode identifiable get. */
    public static final int MODE_IDENTIFIABLE_GET = UtilNetwork.MODE_DISCONNECT + 1;
    /** Mode identifiable create. */
    public static final int MODE_IDENTIFIABLE_CREATE = MODE_IDENTIFIABLE_GET + 1;
    /** Mode identifiable set. */
    public static final int MODE_IDENTIFIABLE_SET = MODE_IDENTIFIABLE_CREATE + 1;

    private final Map<Integer, Map<Integer, Networkable>> networkables = new HashMap<>();
    private final Map<Integer, Set<Integer>> networkablesPending = new HashMap<>();
    /** Server reference (<code>null</code> if unavailable). */
    private final Server server;
    /** Client reference (<code>null</code> if unavailable). */
    private final Client client;
    private final Channel channel;
    private final Factory factory;
    private final Handler handler;

    /**
     * Create component.
     * <p>
     * The {@link Services} must provide the following services:
     * </p>
     * <ul>
     * <li>{@link Channel}</li>
     * </ul>
     * 
     * @param services The services reference.
     */
    public ComponentNetwork(Services services)
    {
        super();

        server = services.getOptional(Server.class).orElse(null);
        client = services.getOptional(Client.class).orElse(null);
        channel = services.get(Channel.class);
        factory = services.get(Factory.class);
        handler = services.get(Handler.class);

        if (server != null)
        {
            server.addListener(new ServerListener()
            {
                @Override
                public void notifyServerStarted(String ip, int port)
                {
                    // Nothing
                }

                @Override
                public void notifyServerStopped()
                {
                    clearAll();
                }
            });
        }
        if (client != null)
        {
            client.addListener(new ClientListener()
            {
                @Override
                public void notifyConnected(String ip, int port, Integer id)
                {
                    // Nothing
                }

                @Override
                public void notifyDisconnected(String ip, int port, Integer id)
                {
                    clearAll();
                }
            });
        }
    }

    /**
     * Clear all elements.
     */
    private void clearAll()
    {
        for (final Map<Integer, Networkable> networkable : networkables.values())
        {
            networkable.values().forEach(Networkable::onDisconnected);
        }
        networkables.clear();
    }

    private void send(Message message, Integer clientId)
    {
        try
        {
            server.send(message, clientId);
        }
        catch (final IOException exception)
        {
            Verbose.exception(exception);
        }
    }

    private void send(Message message)
    {
        try
        {
            client.send(message);
        }
        catch (final IOException exception)
        {
            Verbose.exception(exception);
        }
    }

    private void setNetworkable(Integer clientId, Integer dataId, Networkable networkable)
    {
        Map<Integer, Networkable> map = networkables.get(clientId);
        if (map == null)
        {
            map = new HashMap<>();
            networkables.put(clientId, map);
        }
        map.put(dataId, networkable);
    }

    private Networkable getNetworkable(Integer clientId, Integer dataId)
    {
        Map<Integer, Networkable> map = networkables.get(clientId);
        if (map == null)
        {
            map = new HashMap<>();
            networkables.put(clientId, map);
        }
        return map.get(dataId);
    }

    private boolean containsPending(Integer clientId, Integer dataId)
    {
        final Set<Integer> pending = networkablesPending.get(clientId);
        if (pending != null)
        {
            return pending.contains(dataId);
        }
        return false;
    }

    private boolean addPending(Integer clientId, Integer dataId)
    {
        final Set<Integer> pending = networkablesPending.get(clientId);
        if (pending != null)
        {
            return pending.add(dataId);
        }
        networkablesPending.put(clientId, new HashSet<>());
        networkablesPending.get(clientId).add(dataId);
        return true;
    }

    private boolean removePending(Integer clientId, Integer dataId)
    {
        final Set<Integer> pending = networkablesPending.get(clientId);
        if (pending != null)
        {
            return pending.remove(dataId);
        }
        return false;
    }

    private void handleDisconnect(Packet packet)
    {
        final Integer clientId = Integer.valueOf(packet.getDataId());
        final Map<Integer, Networkable> map = networkables.get(clientId);
        if (map != null)
        {
            for (final Networkable networkable : map.values())
            {
                networkable.onDisconnected();
            }
            networkables.remove(clientId);
        }
    }

    private void handleIdentifiableGet(Packet packet)
    {
        final int dataId = packet.getDataId();
        final Integer dataIdKey = Integer.valueOf(dataId);
        final Featurable featurable = handler.get(dataIdKey);
        if (featurable != null && !containsPending(packet.getClientSourceId(), dataIdKey))
        {
            if (server != null)
            {
                send(new IdentifiableCreate(UtilNetwork.SERVER_ID,
                                            packet.getClientSourceId(),
                                            dataId,
                                            featurable.getMedia()),
                     packet.getClientId());
            }
            else if (client != null)
            {
                send(new IdentifiableCreate(client.getClientId(),
                                            packet.getClientSourceId(),
                                            dataId,
                                            featurable.getMedia()));
            }
        }
    }

    private void handleIdentifiableCreate(Packet packet)
    {
        packet.buffer().position(MessageAbstract.SIZE_MIN + UtilNetwork.INDEX_MODE);
        int dataId = packet.readInt();
        if (removePending(packet.getClientSourceId(), Integer.valueOf(dataId)))
        {
            final Featurable featurable = factory.create(packet.readMedia());
            final Networkable networkable = featurable.getFeature(Networkable.class);
            if (server != null)
            {
                dataId = featurable.getFeature(Identifiable.class).getId().intValue();
                send(new IdentifiableSet(UtilNetwork.SERVER_ID, packet.getClientSourceId(), packet.getDataId(), dataId),
                     packet.getClientId());
            }

            setNetworkable(packet.getClientSourceId(), Integer.valueOf(dataId), networkable);
            networkable.setDataId(dataId);
            handler.add(featurable);
        }
    }

    private void handleIdentifiableSet(Packet packet)
    {
        if (client != null)
        {
            packet.buffer().position(MessageAbstract.SIZE_MIN + UtilNetwork.INDEX_MODE);
            final Integer clientDataId = Integer.valueOf(packet.readInt());
            final Networkable networkable = handler.get(clientDataId).getFeature(Networkable.class);
            final int serverDataId = packet.readInt();
            networkable.setDataId(serverDataId);

            networkables.get(packet.getClientSourceId()).remove(clientDataId);
            setNetworkable(packet.getClientSourceId(), Integer.valueOf(serverDataId), networkable);
        }
    }

    private void handleData(Packet packet)
    {
        packet.buffer().position(MessageAbstract.SIZE_MIN + UtilNetwork.INDEX_DATA_ID + 2);
        final Integer clientId = packet.getClientSourceId();
        final Integer dataId = Integer.valueOf(packet.getDataId());

        final Networkable networkable = getNetworkable(clientId, dataId);
        if (networkable != null)
        {
            networkable.onReceived(packet);
        }
        else if (!containsPending(clientId, dataId))
        {
            addPending(clientId, dataId);
            if (server != null)
            {
                send(new IdentifiableGet(UtilNetwork.SERVER_ID, packet.getClientSourceId(), packet.getDataId()),
                     packet.getClientId());
            }
            else if (client != null)
            {
                send(new IdentifiableGet(client.getClientId(), packet.getClientSourceId(), packet.getDataId()));
            }
        }
    }

    @Override
    public void update(double extrp, Handlables featurables)
    {
        Packet packet;
        while ((packet = channel.read()) != null)
        {
            synchronized (this)
            {
                final int mode = packet.getMode();
                if (mode == UtilNetwork.MODE_DISCONNECT)
                {
                    handleDisconnect(packet);
                }
                else if (mode == MODE_IDENTIFIABLE_GET)
                {
                    handleIdentifiableGet(packet);
                }
                else if (mode == MODE_IDENTIFIABLE_SET)
                {
                    handleIdentifiableSet(packet);
                }
                else if (mode == MODE_IDENTIFIABLE_CREATE)
                {
                    handleIdentifiableCreate(packet);
                }
                else if (mode == UtilNetwork.MODE_DATA)
                {
                    handleData(packet);
                }
            }
        }
    }

    @Override
    public void notifyHandlableAdded(Featurable featurable)
    {
        featurable.ifIs(Networkable.class, n ->
        {
            final Integer id = featurable.getFeature(Identifiable.class).getId();
            if (server != null)
            {
                if (n.getDataId() == -1)
                {
                    n.setClientId(UtilNetwork.SERVER_ID);
                    n.setDataId(id.intValue());
                    setNetworkable(UtilNetwork.SERVER_ID, id, n);
                }
            }
            else if (client != null)
            {
                if (n.getDataId() == -1)
                {
                    n.setClientId(client.getClientId());
                    n.setDataId(id.intValue());
                    setNetworkable(client.getClientId(), id, n);
                }
            }
        });
    }

    @Override
    public void notifyHandlableRemoved(Featurable featurable)
    {
        featurable.ifIs(Networkable.class, n ->
        {
            n.setDataId(-1);
            final Integer id = featurable.getFeature(Identifiable.class).getId();
            final Map<Integer, Networkable> map = networkables.get(n.getClientId());
            if (map != null)
            {
                map.remove(id);
            }
            n.setClientId(null);
        });
    }
}
