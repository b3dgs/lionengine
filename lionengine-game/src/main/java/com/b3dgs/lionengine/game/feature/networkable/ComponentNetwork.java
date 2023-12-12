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
package com.b3dgs.lionengine.game.feature.networkable;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.b3dgs.lionengine.game.feature.ComponentUpdater;
import com.b3dgs.lionengine.game.feature.Factory;
import com.b3dgs.lionengine.game.feature.Featurable;
import com.b3dgs.lionengine.game.feature.Handlables;
import com.b3dgs.lionengine.game.feature.Handler;
import com.b3dgs.lionengine.game.feature.HandlerListener;
import com.b3dgs.lionengine.game.feature.Identifiable;
import com.b3dgs.lionengine.game.feature.Services;
import com.b3dgs.lionengine.game.feature.Transformable;
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
    /** Logger. */
    private static final Logger LOGGER = LoggerFactory.getLogger(ComponentNetwork.class);

    /** Networkable by dataId. */
    private final Map<Integer, Networkable> networkables = new HashMap<>();
    /** Client by pending id. */
    private final Set<Integer> synced = new HashSet<>();
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
        for (final Networkable networkable : networkables.values())
        {
            networkable.onDisconnected();
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
            LOGGER.error("send client error", exception);
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
            LOGGER.error("send error", exception);
        }
    }

    private void setNetworkable(Integer dataId, Networkable networkable)
    {
        networkables.put(dataId, networkable);
    }

    private Networkable getNetworkable(Integer dataId)
    {
        return networkables.get(dataId);
    }

    private void handleDisconnect(Packet packet)
    {
        final Integer clientId = Integer.valueOf(packet.getDataId());
        final List<Integer> toRemove = new ArrayList<>();
        for (final Networkable networkable : networkables.values())
        {
            if (clientId.equals(networkable.getClientId()))
            {
                toRemove.add(Integer.valueOf(networkable.getDataId()));
                networkable.onDisconnected();
            }
        }
        for (final Integer id : toRemove)
        {
            networkables.remove(id);
        }
    }

    private void handleIdentifiableGet(Packet packet)
    {
        final int dataId = packet.getDataId();
        final Featurable featurable = handler.get(Integer.valueOf(dataId));
        if (featurable != null && server != null && featurable.getFeature(Networkable.class).isSynced())
        {
            send(new IdentifiableCreate(featurable.getFeature(Networkable.class).getClientId(), featurable),
                 packet.getClientId());
        }
    }

    private void handleIdentifiableCreate(Packet packet)
    {
        packet.buffer().position(MessageAbstract.SIZE_MIN + UtilNetwork.INDEX_MODE);
        final Integer clientSourceId = packet.getClientSourceId();
        final int dataId = packet.readInt();
        final Integer dataIdKey = Integer.valueOf(dataId);
        if (!synced.contains(dataIdKey))
        {
            synced.add(dataIdKey);

            final float x = packet.readFloat();
            final float y = packet.readFloat();
            final Featurable featurable = factory.create(packet.readMedia());
            final Networkable networkable = featurable.getFeature(Networkable.class);

            if (client != null)
            {
                setNetworkable(Integer.valueOf(dataId), networkable);
                networkable.setDataId(dataId);
                networkable.setClientId(clientSourceId);
                networkable.setSynced(true);
                networkable.ifIs(Transformable.class, t -> t.teleport(x, y));
                handler.add(featurable);
            }
        }
    }

    private void handleData(Packet packet)
    {
        packet.buffer().position(MessageAbstract.SIZE_MIN + UtilNetwork.INDEX_DATA_ID + 2);

        final Integer dataId = Integer.valueOf(packet.getDataId());
        final Networkable networkable = getNetworkable(dataId);

        if (networkable != null)
        {
            networkable.onReceived(packet);
        }
        else if (client != null)
        {
            send(new IdentifiableGet(client.getClientId(), packet.getDataId()));
        }
        else if (server != null)
        {
            LOGGER.error("Unknown id: {}" + packet.getDataId());
        }
    }

    @Override
    public void update(double extrp, Handlables featurables)
    {
        Packet packet;
        while ((packet = channel.read()) != null)
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

    @Override
    public void notifyHandlableAdded(Featurable featurable)
    {
        featurable.ifIs(Networkable.class, n ->
        {
            final Integer id = featurable.getFeature(Identifiable.class).getId();
            if (server != null)
            {
                if (n.getClientId() == null)
                {
                    n.setClientId(UtilNetwork.SERVER_ID);
                }
                n.setDataId(id.intValue());
                n.setSynced(true);
                setNetworkable(id, n);
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
            networkables.remove(id);
            n.setClientId(null);
            n.setSynced(false);
        });
    }
}
