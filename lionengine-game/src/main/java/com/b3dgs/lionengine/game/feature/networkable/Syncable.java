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

import com.b3dgs.lionengine.network.Packet;

/**
 * Represents sync layer ability.
 */
public interface Syncable
{
    /**
     * Called on connected to network.
     * <p>
     * Does nothing by default.
     * </p>
     */
    default void onConnected()
    {
        // Nothing
    }

    /**
     * Called on disconnected from network.
     * <p>
     * Does nothing by default.
     * </p>
     */
    default void onDisconnected()
    {
        // Nothing
    }

    /**
     * Called on packet received.
     * 
     * @param packet The packet received.
     */
    void onReceived(Packet packet);

    /**
     * Get the sync id.
     * 
     * @return The sync id.
     */
    default int getSyncId()
    {
        return getClass().getName().hashCode();
    }
}
