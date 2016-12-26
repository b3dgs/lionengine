/*
 * Copyright (C) 2013-2016 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.example.game.chat;

import java.util.Iterator;
import java.util.LinkedList;

import com.b3dgs.lionengine.Align;
import com.b3dgs.lionengine.Constant;
import com.b3dgs.lionengine.graphic.ColorRgba;
import com.b3dgs.lionengine.graphic.Graphic;
import com.b3dgs.lionengine.graphic.Graphics;
import com.b3dgs.lionengine.graphic.Text;
import com.b3dgs.lionengine.graphic.TextStyle;
import com.b3dgs.lionengine.io.Keyboard;
import com.b3dgs.lionengine.network.ConnectionListener;
import com.b3dgs.lionengine.network.message.NetworkMessageChat;
import com.b3dgs.lionengine.network.purview.NetworkChat;

/**
 * Chat implementation.
 */
class Chat extends NetworkChat implements ConnectionListener
{
    /** Background. */
    private static final ColorRgba BACKGROUND = new ColorRgba(128, 128, 128, 255);
    /** Background writing. */
    private static final ColorRgba BACKGROUND_WRITING = new ColorRgba(64, 64, 64, 255);
    /** Command marker. */
    private static final String COMMAND = Constant.SLASH;

    /** Text. */
    private final Text text;
    /** World reference. */
    private final World<?> world;

    /**
     * Constructor.
     * 
     * @param world The world reference.
     */
    public Chat(World<?> world)
    {
        super(TypeMessage.MESSAGE_CHAT.getId());
        this.world = world;
        text = Graphics.createText(Text.DIALOG, 9, TextStyle.NORMAL);
        setKeyValidate(Keyboard.ENTER.intValue());
        setKeyBackSpace(Keyboard.BACK_SPACE.intValue());
    }

    /*
     * NetworkChat
     */

    @Override
    public void render(Graphic g)
    {
        g.setColor(Chat.BACKGROUND);
        g.drawRect(0, 180, 320, 60, true);
        g.setColor(Chat.BACKGROUND_WRITING);
        g.drawRect(0, 228, 320, 12, true);
        final Iterator<String> messages = new LinkedList<>(getMessages()).descendingIterator();
        int i = 1;
        while (messages.hasNext())
        {
            text.draw(g, 0, 230 - i * 12, Align.LEFT, messages.next());
            i++;
        }
        text.draw(g, 0, 230, Align.LEFT, ">:" + getWriting());
    }

    @Override
    protected boolean canAddChar(char c)
    {
        return true;
    }

    @Override
    protected String getMessage(NetworkMessageChat message)
    {
        final StringBuilder builder = new StringBuilder(world.getClientName(message.getClientId()));
        builder.append(" says: ").append(message.getMessage());
        return builder.toString();
    }

    @Override
    protected boolean canSendMessage(String message)
    {
        if (message.startsWith(Chat.COMMAND))
        {
            world.applyCommand(message);
            return false;
        }
        return true;
    }

    /*
     * ConnectionListener
     */

    @Override
    public void notifyClientConnected(Byte id, String name)
    {
        addMessage(name + " connected");
    }

    @Override
    public void notifyClientDisconnected(Byte id, String name)
    {
        addMessage(name + " disconnected");
    }

    @Override
    public void notifyClientNameChanged(Byte id, String name)
    {
        addMessage(world.getClientName(id.byteValue()) + " renamed as: " + name);
    }

    @Override
    public void notifyConnectionEstablished(Byte id, String name)
    {
        addMessage("Connection established as: " + name);
    }

    @Override
    public void notifyMessageOfTheDay(String messageOfTheDay)
    {
        addMessage(messageOfTheDay);
    }

    @Override
    public void notifyConnectionTerminated(Byte id)
    {
        addMessage("Connection terminated");
    }
}
