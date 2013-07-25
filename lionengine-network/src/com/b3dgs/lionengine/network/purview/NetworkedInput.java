package com.b3dgs.lionengine.network.purview;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Collection;

import com.b3dgs.lionengine.network.message.NetworkMessage;

/**
 * Networked input listener.
 */
public abstract class NetworkedInput
        implements Networkable, KeyListener
{
    /** Model reference. */
    private final NetworkableModel networkable;

    /**
     * Constructor.
     */
    public NetworkedInput()
    {
        networkable = new NetworkableModel();
    }

    /**
     * Send the key value.
     * 
     * @param code The key code.
     * @param pressed The key pressed state.
     */
    protected abstract void sendKey(int code, boolean pressed);

    /*
     * KeyListener
     */

    @Override
    public void keyTyped(KeyEvent event)
    {
        // Nothing to do
    }

    @Override
    public void keyPressed(KeyEvent event)
    {
        sendKey(event.getKeyCode(), true);
    }

    @Override
    public void keyReleased(KeyEvent event)
    {
        sendKey(event.getKeyCode(), false);
    }

    /*
     * Networkable
     */

    @Override
    public void addNetworkMessage(NetworkMessage message)
    {
        networkable.addNetworkMessage(message);
    }

    @Override
    public void applyMessage(NetworkMessage message)
    {
        networkable.applyMessage(message);
    }

    @Override
    public Collection<NetworkMessage> getNetworkMessages()
    {
        return networkable.getNetworkMessages();
    }

    @Override
    public void clearNetworkMessages()
    {
        networkable.clearNetworkMessages();
    }

    @Override
    public void setClientId(Byte id)
    {
        networkable.setClientId(id);
    }

    @Override
    public Byte getClientId()
    {
        return networkable.getClientId();
    }
}
