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
package com.b3dgs.lionengine.game;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.b3dgs.lionengine.Graphic;
import com.b3dgs.lionengine.Text;
import com.b3dgs.lionengine.Timing;

/**
 * Class representing a timed message handler. This allows to prepare a set of timed message that will disappear at the
 * end of their timer.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public final class TimedMessage
{
    /** Text drawer reference. */
    private final Text text;
    /** List of active messages. */
    private final Set<MessageData> messages;
    /** List of messages to delete. */
    private final List<MessageData> delete;
    /** Has to delete something. */
    private boolean deleted;
    /** Has message flag. */
    private boolean hasMessage;

    /**
     * Constructor.
     * 
     * @param text The text drawer reference.
     */
    public TimedMessage(Text text)
    {
        this.text = text;
        messages = new HashSet<>(1);
        delete = new ArrayList<>(1);
        deleted = false;
    }

    /**
     * Add a timed message.
     * 
     * @param message The message string.
     * @param x The horizontal location.
     * @param y The vertical location.
     * @param time The remaining time.
     */
    public void addMessage(String message, int x, int y, int time)
    {
        messages.add(new MessageData(message, x, y, time));
        hasMessage = true;
    }

    /**
     * Update the message list.
     */
    public void update()
    {
        for (final MessageData messageData : messages)
        {
            if (messageData.elapsed())
            {
                delete.add(messageData);
                deleted = true;
            }
        }
        if (deleted)
        {
            for (final MessageData messageData : delete)
            {
                messages.remove(messageData);
            }
            hasMessage = !messages.isEmpty();
            deleted = false;
        }
    }

    /**
     * Render the list of active messages.
     * 
     * @param g The graphics output.
     */
    public void render(Graphic g)
    {
        for (final MessageData messageData : messages)
        {
            text.draw(g, messageData.x, messageData.y, messageData.message);
        }
    }

    /**
     * Check if there are existing message.
     * 
     * @return <code>true</code> if there are messages, <code>false</code> else.
     */
    public boolean hasMessage()
    {
        return hasMessage;
    }
}

/**
 * Message data class.
 */
final class MessageData
{
    /** The message content. */
    public final String message;
    /** The vertical location. */
    public final int x;
    /** The vertical location. */
    public final int y;
    /** The timer. */
    private final Timing timer;
    /** Max time. */
    private final int time;

    /**
     * Constructor.
     * 
     * @param message The message string.
     * @param x The horizontal location.
     * @param y The vertical location.
     * @param time The remaining time.
     */
    MessageData(String message, int x, int y, int time)
    {
        this.message = message;
        this.x = x;
        this.y = y;
        this.time = time;
        timer = new Timing();
        timer.start();
    }

    /**
     * Check if the message is still alive.
     * 
     * @return <code>true</code> if elapsed, <code>false</code> else.
     */
    boolean elapsed()
    {
        return timer.elapsed(time * 1000000L);
    }
}
