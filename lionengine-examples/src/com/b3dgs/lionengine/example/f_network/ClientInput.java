package com.b3dgs.lionengine.example.f_network;

import java.awt.event.KeyEvent;

import com.b3dgs.lionengine.network.purview.NetworkedInput;

/**
 * Client input listener.
 */
public class ClientInput
        extends NetworkedInput
{
    /**
     * Constructor.
     */
    public ClientInput()
    {
        super();
    }

    @Override
    protected void sendKey(int code, boolean pressed)
    {
        if (!(code == KeyEvent.VK_RIGHT || code == KeyEvent.VK_LEFT || code == KeyEvent.VK_UP))
        {
            return;
        }

        final MessageEntity message = new MessageEntity(getClientId());
        if (code == KeyEvent.VK_RIGHT)
        {
            message.addAction(MessageEntityElement.RIGHT, pressed);
        }
        if (code == KeyEvent.VK_LEFT)
        {
            message.addAction(MessageEntityElement.LEFT, pressed);
        }
        if (code == KeyEvent.VK_UP)
        {
            message.addAction(MessageEntityElement.UP, pressed);
        }
        addNetworkMessage(message);
    }
}
