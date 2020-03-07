/*
 * Copyright (C) 2013-2020 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.network.purview;

import java.util.Collection;

import com.b3dgs.lionengine.InputDeviceKeyListener;
import com.b3dgs.lionengine.network.message.NetworkMessage;

/**
 * Networked input listener.
 */
public abstract class NetworkedInput implements Networkable, InputDeviceKeyListener
{
    /** Model reference. */
    private final NetworkableModel networkable;

    /**
     * Constructor base.
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
     * KeyboardListener
     */

    @Override
    public void keyPressed(int keyCode, char keyChar)
    {
        sendKey(keyCode, true);
    }

    @Override
    public void keyReleased(int keyCode, char keyChar)
    {
        sendKey(keyCode, false);
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
