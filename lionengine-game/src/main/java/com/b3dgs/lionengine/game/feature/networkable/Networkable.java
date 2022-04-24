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

import java.nio.ByteBuffer;
import java.util.function.Supplier;

import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.game.Feature;
import com.b3dgs.lionengine.game.feature.FeatureInterface;

/**
 * Represents network layer ability.
 */
@FeatureInterface
public interface Networkable extends Feature, Syncable
{
    /**
     * Send data over owned client.
     * 
     * @param buffer The buffer to send.
     * @throws LionEngineException If unable to send data.
     */
    void send(ByteBuffer buffer);

    /**
     * Send data over owned client.
     * 
     * @param buffer The buffer to send.
     */
    default void send(Supplier<ByteBuffer> buffer)
    {
        send(buffer.get());
    }

    /**
     * Set the network id (0 is server).
     * 
     * @param clientId The network id.
     */
    void setClientId(Integer clientId);

    /**
     * Set the network id (0 is server).
     * 
     * @param dataId The network id.
     */
    void setDataId(int dataId);

    /**
     * Set synced on server.
     * 
     * @param synced <code>true</code> if synced, <code>false</code> else.
     */
    void setSynced(boolean synced);

    /**
     * Get get the network id (0 is server).
     * 
     * @return The network id.
     */
    Integer getClientId();

    /**
     * Get get the network id (0 is server).
     * 
     * @return The network id.
     */
    int getDataId();

    /**
     * Check if synced on server.
     * 
     * @return <code>true</code> if synced, <code>false</code> else.
     */
    boolean isSynced();

    /**
     * Check if owned by server.
     * 
     * @return <code>true</code> if owned by server, <code>false</code> else.
     */
    boolean isServer();

    /**
     * Check if owned by client.
     * 
     * @return <code>true</code> if owned by client, <code>false</code> else.
     */
    boolean isClient();

    /**
     * Check if can be managed by owner.
     * 
     * @return <code>true</code> if network owner, <code>false</code> else.
     */
    boolean isOwner();

    /**
     * Check if handled by server and owned by client.
     * 
     * @return <code>true</code> if managed by server, <code>false</code> else.
     */
    boolean isServerHandleClient();
}
