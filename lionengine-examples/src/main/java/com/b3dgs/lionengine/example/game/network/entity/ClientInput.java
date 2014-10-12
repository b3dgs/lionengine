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

import com.b3dgs.lionengine.core.awt.Keyboard;
import com.b3dgs.lionengine.network.purview.NetworkedInput;

/**
 * Client input listener.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
final class ClientInput
        extends NetworkedInput
{
    /**
     * Constructor.
     */
    ClientInput()
    {
        super();
    }

    /*
     * NetworkedInput
     */

    @Override
    protected void sendKey(int code, boolean pressed)
    {
        if (!(code == Keyboard.RIGHT.intValue() || code == Keyboard.LEFT.intValue() || code == Keyboard.UP.intValue()))
        {
            return;
        }

        final MessageEntity message = new MessageEntity(getClientId());
        if (code == Keyboard.RIGHT.intValue())
        {
            message.addAction(MessageEntityElement.RIGHT, pressed);
        }
        if (code == Keyboard.LEFT.intValue())
        {
            message.addAction(MessageEntityElement.LEFT, pressed);
        }
        if (code == Keyboard.UP.intValue())
        {
            message.addAction(MessageEntityElement.UP, pressed);
        }
        addNetworkMessage(message);
    }
}
