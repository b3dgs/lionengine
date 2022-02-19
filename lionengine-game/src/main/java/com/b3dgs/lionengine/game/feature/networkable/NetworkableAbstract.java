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
import java.nio.ByteBuffer;
import java.util.function.Supplier;

import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Verbose;
import com.b3dgs.lionengine.game.feature.Featurable;
import com.b3dgs.lionengine.game.feature.FeatureGet;
import com.b3dgs.lionengine.game.feature.FeatureInterface;
import com.b3dgs.lionengine.game.feature.FeatureModel;
import com.b3dgs.lionengine.game.feature.Identifiable;
import com.b3dgs.lionengine.game.feature.Services;
import com.b3dgs.lionengine.game.feature.Setup;
import com.b3dgs.lionengine.network.Data;
import com.b3dgs.lionengine.network.UtilNetwork;
import com.b3dgs.lionengine.network.client.Client;
import com.b3dgs.lionengine.network.server.Server;

/**
 * Abstract networkable implementation.
 */
@FeatureInterface
public abstract class NetworkableAbstract extends FeatureModel implements Networkable
{
    /** Server reference (<code>null</code> if unavailable). */
    protected final Server server = services.getOptional(Server.class).orElse(null);
    /** Client reference (<code>null</code> if unavailable). */
    protected final Client client = services.getOptional(Client.class).orElse(null);
    /** Client id (0 = default for server). */
    private Integer clientId;
    /** Client id (0 = default for server). */
    private int dataId = -1;

    @FeatureGet private Identifiable identifiable;

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
     * <li>{@link Client}</li>
     * </ul>
     * 
     * @param services The services reference (must not be <code>null</code>).
     * @param setup The setup reference (must not be <code>null</code>).
     * @throws LionEngineException If invalid arguments.
     */
    public NetworkableAbstract(Services services, Setup setup)
    {
        super(services, setup);
    }

    /**
     * Check if owned by server.
     * 
     * @return <code>true</code> if owned by server, <code>false</code> else.
     */
    public boolean isServer()
    {
        return server != null && UtilNetwork.SERVER_ID.equals(getClientId());
    }

    /**
     * Check if owned by client.
     * 
     * @return <code>true</code> if owned by client, <code>false</code> else.
     */
    public boolean isClient()
    {
        return client != null && client.getClientId() != null && client.getClientId().equals(getClientId());
    }

    /**
     * Check if can be managed by owner.
     * 
     * @return <code>true</code> if network owner, <code>false</code> else.
     */
    public boolean isOwner()
    {
        return isServer() || isClient() || server == null && client == null;
    }

    /**
     * Send data over owned client.
     * 
     * @param buffer The buffer to send.
     * @throws LionEngineException If unable to send data.
     */
    public void send(ByteBuffer buffer)
    {
        try
        {
            if (isServer())
            {
                server.send(new Data(UtilNetwork.SERVER_ID, getDataId(), buffer));
            }
            else if (isClient())
            {
                client.send(new Data(getClientId(), getDataId(), buffer));
            }
        }
        catch (final IOException exception)
        {
            Verbose.exception(exception);
        }
    }

    /**
     * Send data over owned client.
     * 
     * @param buffer The buffer to send.
     */
    public void send(Supplier<ByteBuffer> buffer)
    {
        send(buffer.get());
    }

    /*
     * Networkable
     */

    /**
     * {@inheritDoc}
     * <p>
     * Destroy {@link Identifiable} by default.
     * </p>
     */
    @Override
    public void onDisconnected()
    {
        getFeature(Identifiable.class).destroy();
    }

    @Override
    public void setClientId(Integer clientId)
    {
        this.clientId = clientId;
    }

    @Override
    public final void setDataId(int dataId)
    {
        this.dataId = dataId;
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
}
