/*
 * Copyright (C) 2013-2024 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.game.Feature;
import com.b3dgs.lionengine.game.feature.Featurable;
import com.b3dgs.lionengine.game.feature.FeatureModel;
import com.b3dgs.lionengine.game.feature.Identifiable;
import com.b3dgs.lionengine.game.feature.Services;
import com.b3dgs.lionengine.game.feature.Setup;
import com.b3dgs.lionengine.network.Data;
import com.b3dgs.lionengine.network.Packet;
import com.b3dgs.lionengine.network.UtilNetwork;
import com.b3dgs.lionengine.network.client.Client;
import com.b3dgs.lionengine.network.server.Server;

/**
 * Abstract networkable implementation.
 */
public class NetworkableModel extends FeatureModel implements Networkable
{
    /** Logger. */
    private static final Logger LOGGER = LoggerFactory.getLogger(NetworkableModel.class);

    /** Server reference (<code>null</code> if unavailable). */
    protected final Server server = services.getOptional(Server.class).orElse(null);
    /** Client reference (<code>null</code> if unavailable). */
    protected final Client client = services.getOptional(Client.class).orElse(null);
    /** Feature mapping. */
    private final Map<Integer, Syncable> syncables = new HashMap<>();
    /** Client id (0 = default for server). */
    private Integer clientId;
    /** Data id. */
    private int dataId = -1;
    /** Synced on server flag. */
    private boolean synced;

    /**
     * Create feature.
     * 
     * <p>
     * The {@link Featurable} must have:
     * </p>
     * <ul>
     * <li>{@link Identifiable}</li>
     * </ul>
     * <p>
     * The {@link Services} must provide the following services:
     * </p>
     * <ul>
     * <li>{@link Server}</li>
     * <li>{@link Client}</li>
     * </ul>
     * 
     * @param services The services reference (must not be <code>null</code>).
     * @param setup The setup reference (must not be <code>null</code>).
     * @throws LionEngineException If invalid arguments.
     */
    public NetworkableModel(Services services, Setup setup)
    {
        super(services, setup);
    }

    @Override
    public void send(ByteBuffer buffer)
    {
        try
        {
            if (server != null)
            {
                server.send(new Data(UtilNetwork.SERVER_ID, getDataId(), buffer));
            }
            else if (isClient())
            {
                client.send(new Data(getClientId(), getDataId(), buffer, true));
            }
        }
        catch (final IOException exception)
        {
            LOGGER.error("send error", exception);
        }
    }

    /**
     * Called on connected. Does nothing by default.
     */
    @Override
    public void onConnected()
    {
        for (final Feature feature : getFeatures())
        {
            if (feature != this && feature instanceof final Syncable syncable)
            {
                syncables.put(Integer.valueOf(syncable.getSyncId()), syncable);
                syncable.onConnected();
            }
        }
    }

    @Override
    public void onReceived(Packet packet)
    {
        final Integer id = Integer.valueOf(packet.readInt());

        syncables.get(id).onReceived(packet);
    }

    @Override
    public void onDisconnected()
    {
        getFeature(Identifiable.class).destroy();
        syncables.values().forEach(Syncable::onDisconnected);
        syncables.clear();
    }

    @Override
    public void setClientId(Integer clientId)
    {
        this.clientId = clientId;
        if (clientId != null)
        {
            onConnected();
        }
    }

    @Override
    public final void setDataId(int dataId)
    {
        this.dataId = dataId;
    }

    @Override
    public void setSynced(boolean synced)
    {
        this.synced = synced;
    }

    @Override
    public Integer getClientId()
    {
        return clientId;
    }

    @Override
    public final int getDataId()
    {
        return dataId;
    }

    @Override
    public boolean isSynced()
    {
        return synced;
    }

    @Override
    public boolean isServer()
    {
        return server != null && UtilNetwork.SERVER_ID.equals(getClientId());
    }

    @Override
    public boolean isClient()
    {
        return client != null && client.getClientId() != null && client.getClientId().equals(getClientId());
    }

    @Override
    public boolean isOwner()
    {
        return isServer() || isClient();
    }

    @Override
    public boolean isConnected()
    {
        return server != null || client != null;
    }

    @Override
    public boolean isServerHandleClient()
    {
        return server != null && !UtilNetwork.SERVER_ID.equals(getClientId());
    }
}
