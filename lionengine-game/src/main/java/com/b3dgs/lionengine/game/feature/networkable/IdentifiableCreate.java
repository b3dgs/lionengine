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

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.UtilConversion;
import com.b3dgs.lionengine.game.feature.Featurable;
import com.b3dgs.lionengine.game.feature.Identifiable;
import com.b3dgs.lionengine.game.feature.Transformable;
import com.b3dgs.lionengine.network.MessageAbstract;
import com.b3dgs.lionengine.network.MessageType;
import com.b3dgs.lionengine.network.UtilNetwork;

/**
 * Identifiable create message.
 */
public class IdentifiableCreate extends MessageAbstract
{
    private final int sourceId;
    private final int dataId;
    private final Media media;
    private final float x;
    private final float y;

    /**
     * Create message.
     * 
     * @param sourceId The source id.
     * @param featurable The featurable reference.
     */
    public IdentifiableCreate(Integer sourceId, Featurable featurable)
    {
        super(MessageType.DIRECT, UtilNetwork.SERVER_ID);

        this.sourceId = sourceId.intValue();
        dataId = featurable.getFeature(Identifiable.class).getId().intValue();
        media = featurable.getMedia();
        if (featurable.hasFeature(Transformable.class))
        {
            final Transformable transformable = featurable.getFeature(Transformable.class);
            x = (float) transformable.getX();
            y = (float) transformable.getY();
        }
        else
        {
            x = 0.0f;
            y = 0.0f;
        }
    }

    /**
     * Create message.
     * 
     * @param sourceId The source id.
     * @param dataId The data id.
     * @param media The media reference.
     * @param x The horizontal location.
     * @param y The vertical location.
     */
    public IdentifiableCreate(Integer sourceId, int dataId, Media media, float x, float y)
    {
        super(MessageType.DIRECT, UtilNetwork.SERVER_ID);

        this.sourceId = sourceId.intValue();
        this.dataId = dataId;
        this.media = media;
        this.x = x;
        this.y = y;
    }

    @Override
    protected ByteBuffer content()
    {
        final String path = media.getPath();
        final int length = path.length();
        final ByteBuffer file = StandardCharsets.UTF_8.encode(path);
        final ByteBuffer buffer = ByteBuffer.allocate(3 + Integer.BYTES * 3 + Float.BYTES * 2 + length);

        buffer.put(UtilConversion.fromUnsignedByte(ComponentNetwork.MODE_IDENTIFIABLE_CREATE));
        buffer.put(UtilConversion.fromUnsignedByte(sourceId));
        buffer.putInt(dataId);
        buffer.putFloat(x);
        buffer.putFloat(y);
        buffer.put(UtilConversion.fromUnsignedByte(length));
        buffer.put(file);

        return buffer;
    }
}
