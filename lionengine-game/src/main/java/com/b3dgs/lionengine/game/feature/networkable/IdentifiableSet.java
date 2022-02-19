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

import com.b3dgs.lionengine.UtilConversion;
import com.b3dgs.lionengine.network.MessageAbstract;
import com.b3dgs.lionengine.network.MessageType;

/**
 * Set new identifiable.
 */
public class IdentifiableSet extends MessageAbstract
{
    private final int clientSourceId;
    private final int clientDataId;
    private final int dataId;

    /**
     * Create message.
     * 
     * @param clientId The client id.
     * @param clientSourceId The client source id.
     * @param clientDataId The client data id.
     * @param dataId The data id.
     */
    public IdentifiableSet(Integer clientId, Integer clientSourceId, int clientDataId, int dataId)
    {
        super(MessageType.DIRECT, clientId);

        this.clientSourceId = clientSourceId.intValue();
        this.clientDataId = clientDataId;
        this.dataId = dataId;
    }

    @Override
    protected ByteBuffer content()
    {
        final ByteBuffer buffer = ByteBuffer.allocate(2 + Integer.BYTES * 2);
        buffer.put(UtilConversion.fromUnsignedByte(ComponentNetwork.MODE_IDENTIFIABLE_SET));
        buffer.put(UtilConversion.fromUnsignedByte(clientSourceId));
        buffer.putInt(clientDataId);
        buffer.putInt(dataId);
        return buffer;
    }
}
