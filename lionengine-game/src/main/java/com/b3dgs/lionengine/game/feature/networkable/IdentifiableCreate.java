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
import java.nio.charset.StandardCharsets;

import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.UtilConversion;
import com.b3dgs.lionengine.network.MessageAbstract;
import com.b3dgs.lionengine.network.MessageType;

/**
 * Identifiable create message.
 */
public class IdentifiableCreate extends MessageAbstract
{
    private final int clientSourceId;
    private final Media media;
    private final int dataId;

    /**
     * Create message.
     * 
     * @param clientId The client id.
     * @param clientSourceId The client source id.
     * @param dataId The data id.
     * @param media The media reference.
     */
    public IdentifiableCreate(Integer clientId, Integer clientSourceId, int dataId, Media media)
    {
        super(MessageType.DIRECT, clientId);

        this.clientSourceId = clientSourceId.intValue();
        this.dataId = dataId;
        this.media = media;
    }

    @Override
    protected ByteBuffer content()
    {
        final ByteBuffer file = StandardCharsets.UTF_8.encode(media.getPath());
        final ByteBuffer buffer = ByteBuffer.allocate(2 + Integer.BYTES + Integer.BYTES + file.capacity());

        buffer.put(UtilConversion.fromUnsignedByte(ComponentNetwork.MODE_IDENTIFIABLE_CREATE));
        buffer.put(UtilConversion.fromUnsignedByte(clientSourceId));
        buffer.putInt(dataId);
        buffer.putInt(file.capacity() - 1);
        buffer.put(file);

        return buffer;
    }
}
