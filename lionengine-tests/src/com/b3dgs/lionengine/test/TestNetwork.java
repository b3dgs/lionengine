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
