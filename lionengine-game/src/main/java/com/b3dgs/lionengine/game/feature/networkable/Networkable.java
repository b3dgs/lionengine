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

import com.b3dgs.lionengine.game.Feature;
import com.b3dgs.lionengine.game.feature.FeatureInterface;
import com.b3dgs.lionengine.network.Packet;

/**
 * Represents network layer ability.
 */
@FeatureInterface
public interface Networkable extends Feature
{
    /**
     * Called on init.
     * 
     * @return The init buffer.
     */
    default ByteBuffer init()
    {
        return ByteBuffer.allocate(0);
    }

    /**
     * Called on disconnected from network.
     */
    void onDisconnected();

    /**
     * Called on packet received.
     * 
     * @param packet The packet received.
     */
    void onReceived(Packet packet);

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
}
