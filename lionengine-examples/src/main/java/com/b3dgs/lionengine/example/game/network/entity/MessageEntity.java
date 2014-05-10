/*
 * Copyright (C) 2013-2014 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
 */
package com.b3dgs.lionengine.example.game.network.entity;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.IOException;

import com.b3dgs.lionengine.UtilConversion;
import com.b3dgs.lionengine.network.message.NetworkMessageEntity;

/**
 * Entity network message description.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
final class MessageEntity
        extends NetworkMessageEntity<MessageEntityElement>
{
    /**
     * Get the value in byte of the enum.
     * 
     * @param value The enum value.
     * @return The byte value.
     */
    private static byte getKeyByte(MessageEntityElement value)
    {
        return (byte) value.ordinal();
    }

    /**
     * Get the key value from the byte value.
     * 
     * @param value The byte value.
     * @return The key value.
     */
    private static MessageEntityElement getActionKey(byte value)
    {
        return MessageEntityElement.fromOrdinal(value);
    }

    /**
     * Constructor (used in decoding case).
     */
    MessageEntity()
    {
        super();
    }

    /**
     * Constructor (used in client case).
     * 
     * @param clientId The client id.
     */
    MessageEntity(Byte clientId)
    {
        super(TypeMessage.MESSAGE_ENTITY.getId(), clientId.byteValue());
    }

    /**
     * Constructor (used in entity server case).
     * 
     * @param entityId The entity id.
     */
    MessageEntity(short entityId)
    {
        super(TypeMessage.MESSAGE_ENTITY.getId(), entityId);
    }

    /**
     * Constructor (used in entity server case).
     * 
     * @param entityId The entity id.
     * @param destId The client destination id.
     */
    MessageEntity(short entityId, byte destId)
    {
        super(TypeMessage.MESSAGE_ENTITY.getId(), entityId, destId);
    }

    /*
     * NetworkMessageEntity
     */

    @Override
    protected void encode(ByteArrayOutputStream buffer, MessageEntityElement key) throws IOException
    {
        buffer.write(MessageEntity.getKeyByte(key));
        // States
        if (key == MessageEntityElement.DOWN || key == MessageEntityElement.LEFT || key == MessageEntityElement.RIGHT
                || key == MessageEntityElement.UP)
        {
            buffer.write(getActionBoolean(key) ? 1 : 0);
        }
        // Location correction
        if (key == MessageEntityElement.LOCATION_X || key == MessageEntityElement.LOCATION_Y)
        {
            buffer.write(UtilConversion.intToByteArray(getActionInteger(key)));
        }
    }

    @Override
    protected void decode(DataInputStream buffer, int i) throws IOException
    {
        final MessageEntityElement key = MessageEntity.getActionKey(buffer.readByte());

        // States
        if (key == MessageEntityElement.DOWN || key == MessageEntityElement.LEFT || key == MessageEntityElement.RIGHT
                || key == MessageEntityElement.UP)
        {
            addAction(key, buffer.readByte() == 0 ? false : true);
        }
        // Location correction
        if (key == MessageEntityElement.LOCATION_X || key == MessageEntityElement.LOCATION_Y)
        {
            addAction(key, buffer.readInt());
        }
    }
}
