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
package com.b3dgs.lionengine.test;

import org.junit.Assert;
import org.junit.Test;

import com.b3dgs.lionengine.network.message.NetworkMessageChat;

/**
 * Test network.
 */
public class TestNetwork
{
    /**
     * Test the message encoding and decoding.
     */
    @Test
    public void testMessageEncodeDecode()
    {
        final NetworkMessageChat in = new NetworkMessageChat(TypeMessage.MESSAGE_CHAT, (byte) 1, (byte) 2, "Test");
        final NetworkMessageChat out = new NetworkMessageChat();

        // out.decode(ByteBuffer.wrap(in.encode()));

        Assert.assertEquals(in.getClientId(), out.getClientId());
        Assert.assertEquals(in.getClientDestId(), out.getClientDestId());
        Assert.assertEquals(in.getMessage(), out.getMessage());
    }
}

/**
 * Messages enum type.
 */
enum TypeMessage
{
    /** Chat message. */
    MESSAGE_CHAT;
}
