/*
 * Copyright (C) 2013-2017 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.network.purview;

import java.util.Collection;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import com.b3dgs.lionengine.Constant;
import com.b3dgs.lionengine.InputDeviceKeyListener;
import com.b3dgs.lionengine.graphic.Graphic;
import com.b3dgs.lionengine.network.message.NetworkMessage;
import com.b3dgs.lionengine.network.message.NetworkMessageChat;

/**
 * Basic chat implementation.
 */
public abstract class NetworkChat implements Networkable, InputDeviceKeyListener
{
    /** Default queue max. */
    private static final int DEFAULT_QUEUE_MAX = 4;

    /** Networkable model. */
    private final NetworkableModel networkable;
    /** Message type. */
    private final byte type;
    /** List of received message. */
    private final Queue<String> messages;
    /** Current message. */
    private final StringBuilder message;
    /** Messages number. */
    private int messageCount;
    /** Messages max queue. */
    private int messagesQueueMax;
    /** Writing message. */
    private String display;
    /** Validate key. */
    private int keyValidate;
    /** Space key. */
    private int keyBackSpace;

    /**
     * Constructor base.
     * 
     * @param type The message type enum.
     */
    public NetworkChat(byte type)
    {
        this.type = type;
        networkable = new NetworkableModel();
        message = new StringBuilder();
        messages = new ConcurrentLinkedQueue<>();
        messagesQueueMax = DEFAULT_QUEUE_MAX;
        display = Constant.EMPTY_STRING;
    }

    /**
     * Render the chat.
     * 
     * @param g The graphic output.
     */
    public abstract void render(Graphic g);

    /**
     * Get the message string from the network message.
     * 
     * @param message The network message.
     * @return The message string.
     */
    protected abstract String getMessage(NetworkMessageChat message);

    /**
     * Check if the message can be sent. Can be used for example to intercept a command (name changing...) to not send
     * it to the network.
     * 
     * @param message The message to check.
     * @return <code>true</code> if can be sent, <code>false</code> else.
     */
    protected abstract boolean canSendMessage(String message);

    /**
     * Check if the input char can be added.
     * 
     * @param c The input char.
     * @return <code>true</code> if can be added, <code>false</code> else.
     */
    protected abstract boolean canAddChar(char c);

    /**
     * Set the key that allow to validate a message.
     * 
     * @param keyValidate The key that allow to validate a message.
     */
    public void setKeyValidate(int keyValidate)
    {
        this.keyValidate = keyValidate;
    }

    /**
     * Set the key that insert a space in a message.
     * 
     * @param keyBackSpace The key that insert a backspace in a message.
     */
    public void setKeyBackSpace(int keyBackSpace)
    {
        this.keyBackSpace = keyBackSpace;
    }

    /**
     * Set the message queue max length.
     * 
     * @param max The maximum number of messages that can be kept.
     */
    public void setMessagesMax(int max)
    {
        messagesQueueMax = max;
    }

    /**
     * Get the list of messages.
     * 
     * @return The list of messages.
     */
    public Queue<String> getMessages()
    {
        return messages;
    }

    /**
     * Get the current writing string.
     * 
     * @return The writing string.
     */
    public String getWriting()
    {
        return display;
    }

    /**
     * Add a new message.
     * 
     * @param message The message to add.
     */
    protected void addMessage(String message)
    {
        messages.add(message);
        messageCount++;
        if (messageCount > messagesQueueMax)
        {
            messages.remove();
            messageCount--;
        }
    }

    /**
     * Send validated message.
     */
    private void sendValidatedMessage()
    {
        final String msg = message.toString();
        if (canSendMessage(msg))
        {
            addNetworkMessage(new NetworkMessageChat(type, getClientId().byteValue(), msg));
        }
        message.delete(0, message.length());
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
        if (!(message instanceof NetworkMessageChat))
        {
            return;
        }
        final NetworkMessageChat messageChat = (NetworkMessageChat) message;
        addMessage(getMessage(messageChat));
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

    /*
     * KeyboardListener
     */

    @Override
    public void keyPressed(int keyCode, char keyChar)
    {
        // Validate message
        if (keyCode == keyValidate)
        {
            if (message.length() > 0)
            {
                sendValidatedMessage();
            }
        }
        else if (keyCode == keyBackSpace)
        {
            if (message.length() > 0)
            {
                message.deleteCharAt(message.length() - 1);
            }
        }
        else if (Character.isDefined(keyChar) && canAddChar(keyChar))
        {
            message.append(keyChar);
        }
        display = message.toString();
    }

    @Override
    public void keyReleased(int keyCode, char keyChar)
    {
        // Nothing to do
    }
}
