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
package com.b3dgs.lionengine.network.server;

import java.nio.ByteBuffer;
import java.util.Set;

import com.b3dgs.lionengine.UtilConversion;
import com.b3dgs.lionengine.network.MessageAbstract;
import com.b3dgs.lionengine.network.MessageType;

/**
 * Message clients list implementation.
 */
public class ClientsList extends MessageAbstract
{
    private final Set<Integer> clients;

    /**
     * Create message.
     * 
     * @param clientId The client id.
     * @param clients The clients list.
     */
    public ClientsList(Integer clientId, Set<Integer> clients)
    {
        super(MessageType.CLIENTS_LIST, clientId);

        this.clients = clients;
    }

    @Override
    public ByteBuffer content()
    {
        final ByteBuffer buffer = ByteBuffer.allocate(clients.size());

        for (final Integer clientId : clients)
        {
            buffer.put(UtilConversion.fromUnsignedByte(clientId.intValue()));
        }
        return buffer;
    }
}
