package com.b3dgs.lionengine.example.f_network;

import com.b3dgs.lionengine.network.message.NetworkMessage;
import com.b3dgs.lionengine.network.message.NetworkMessageChat;
import com.b3dgs.lionengine.network.message.NetworkMessageDecoder;

/**
 * The decoder implementation.
 */
public class MessageDecoder
        implements NetworkMessageDecoder
{
    @Override
    public NetworkMessage getNetworkMessageFromType(int type)
    {
        final TypeMessage message = TypeMessage.fromOrdinal(type);
        switch (message)
        {
            case MESSAGE_CHAT:
                return new NetworkMessageChat();
            case MESSAGE_ENTITY:
                return new MessageEntity();
            case MESSAGE_FACTORY:
                return new MessageFactory();
            default:
                return null;
        }
    }
}
