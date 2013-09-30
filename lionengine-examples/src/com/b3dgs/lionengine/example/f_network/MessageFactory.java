/*
 * Copyright (C) 2013 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.example.f_network;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.IOException;

import com.b3dgs.lionengine.network.message.NetworkMessageEntity;

/**
 * Factory network message description.
 */
class MessageFactory
        extends NetworkMessageEntity<TypeEntity>
{
    /**
     * Constructor.
     */
    public MessageFactory()
    {
        super();
    }

    /**
     * Constructor.
     * 
     * @param entityId The entity id.
     */
    public MessageFactory(short entityId)
    {
        super(TypeMessage.MESSAGE_FACTORY, entityId);
    }

    /**
     * Constructor.
     * 
     * @param entityId The entity id.
     * @param destId The client destination id.
     */
    public MessageFactory(short entityId, byte destId)
    {
        super(TypeMessage.MESSAGE_FACTORY, entityId, destId);
    }

    @Override
    protected void encode(ByteArrayOutputStream buffer, TypeEntity key) throws IOException
    {
        buffer.write((byte) key.ordinal());
    }

    @Override
    protected void decode(DataInputStream buffer, int i) throws IOException
    {
        final TypeEntity type = TypeEntity.fromOrdinal(buffer.readByte());
        this.addAction(type, true);
    }
}
