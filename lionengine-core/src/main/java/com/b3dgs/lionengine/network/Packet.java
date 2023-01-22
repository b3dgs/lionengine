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
package com.b3dgs.lionengine.network;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.Medias;
import com.b3dgs.lionengine.UtilConversion;

/**
 * Network game packet representation.
 */
public class Packet
{
    private static final int START = MessageAbstract.SIZE_MIN + UtilNetwork.INDEX_CLIENT_ID;

    private final Integer clientId;
    private final Integer clientSourceId;
    private final int dataId;
    private final int mode;
    private final ByteBuffer buffer;

    /**
     * Create packet.
     * 
     * @param clientId The client id.
     * @param clientSourceId The client source id.
     * @param dataId The data id.
     * @param buffer The buffer reference.
     */
    public Packet(Integer clientId, Integer clientSourceId, int dataId, ByteBuffer buffer)
    {
        super();

        this.clientId = clientId;
        this.clientSourceId = clientSourceId;
        this.dataId = dataId;
        this.buffer = buffer;
        mode = UtilConversion.toUnsignedByte(buffer.get(UtilNetwork.INDEX_MODE));
    }

    /**
     * Create packet.
     * 
     * @param clientId The client id.
     * @param dataId The data id.
     * @param mode The mode value.
     */
    public Packet(Integer clientId, int dataId, int mode)
    {
        super();

        this.clientId = clientId;
        clientSourceId = clientId;
        this.dataId = dataId;
        this.mode = mode;
        buffer = null;
    }

    /**
     * Get the client id.
     * 
     * @return The client id.
     */
    public Integer getClientId()
    {
        return clientId;
    }

    /**
     * Get the client source id.
     * 
     * @return The client source id.
     */
    public Integer getClientSourceId()
    {
        return clientSourceId;
    }

    /**
     * Get the data id.
     * 
     * @return The data id.
     */
    public int getDataId()
    {
        return dataId;
    }

    /**
     * Get mode type.
     * 
     * @return The mode type.
     */
    public int getMode()
    {
        return mode;
    }

    /**
     * Get buffer data without header.
     * 
     * @return The buffer data positioned at user data.
     */
    public ByteBuffer buffer()
    {
        return buffer;
    }

    /**
     * Get byte at index.
     * 
     * @param index The index value.
     * @return The byte value.
     */
    public byte getByte(int index)
    {
        return buffer.get(START + index);
    }

    /**
     * Get byte at index.
     * 
     * @param index The index value.
     * @return The byte value.
     */
    public int getInt(int index)
    {
        return buffer.getInt(START + index);
    }

    /**
     * Get media at index.
     * 
     * @param index The index value.
     * @return The media.
     */
    public Media getMedia(int index)
    {
        final int size = readByteUnsigned();
        final byte[] data = new byte[size];
        buffer.get(data, index + 1, size);
        return Medias.create(StandardCharsets.UTF_8.decode(ByteBuffer.wrap(data)).toString());
    }

    /**
     * Read next string.
     * 
     * @return The string.
     */
    public String readString()
    {
        final int size = readByteUnsigned();
        final byte[] data = new byte[size];
        buffer.get(data);
        return StandardCharsets.UTF_8.decode(ByteBuffer.wrap(data)).toString();
    }

    /**
     * Read next media.
     * 
     * @return The media.
     */
    public Media readMedia()
    {
        return Medias.create(readString());
    }

    /**
     * Read next boolean.
     * 
     * @return The value read.
     */
    public boolean readBool()
    {
        return readByteUnsigned() == 1;
    }

    /**
     * Read next byte.
     * 
     * @return The value read.
     */
    public byte readByte()
    {
        return buffer.get();
    }

    /**
     * Read next byte.
     * 
     * @return The value read.
     */
    public int readByteUnsigned()
    {
        return UtilConversion.toUnsignedByte(buffer.get());
    }

    /**
     * Read next integer.
     * 
     * @return The value read.
     */
    public int readInt()
    {
        return buffer.getInt();
    }

    /**
     * Read next long.
     * 
     * @return The value read.
     */
    public double readLong()
    {
        return buffer.getLong();
    }

    /**
     * Read next float.
     * 
     * @return The value read.
     */
    public float readFloat()
    {
        return buffer.getFloat();
    }

    /**
     * Read next double.
     * 
     * @return The value read.
     */
    public double readDouble()
    {
        return buffer.getDouble();
    }
}
