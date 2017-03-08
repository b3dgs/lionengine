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
package com.b3dgs.lionengine.game;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;

import com.b3dgs.lionengine.Timing;
import com.b3dgs.lionengine.Updatable;
import com.b3dgs.lionengine.graphic.Graphic;
import com.b3dgs.lionengine.graphic.Renderable;
import com.b3dgs.lionengine.graphic.Text;

/**
 * Class representing a timed message handler. This allows to prepare a set of timed message that will disappear at the
 * end of their timer.
 */
public final class TimedMessage implements Updatable, Renderable
{
    /** List of active messages. */
    private final Collection<MessageData> messages;
    /** List of messages to delete. */
    private final Collection<MessageData> delete;
    /** Text reference. */
    private final Text text;
    /** Has to delete something. */
    private boolean deleted;
    /** Has message flag. */
    private boolean hasMessage;

    /**
     * Create a timed message.
     * 
     * @param text The text renderer to use.
     */
    public TimedMessage(Text text)
    {
        this.text = text;
        messages = new HashSet<MessageData>(1);
        delete = new ArrayList<MessageData>(1);
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
     * Check if there are existing message.
     * 
     * @return <code>true</code> if there are messages, <code>false</code> else.
     */
    public boolean hasMessage()
    {
        return hasMessage;
    }

    /*
     * Updatable
     */

    @Override
    public void update(double extrp)
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

    /*
     * Renderable
     */

    @Override
    public void render(Graphic g)
    {
        for (final MessageData messageData : messages)
        {
            text.draw(g, messageData.x, messageData.y, messageData.message);
        }
    }

    /**
     * Message data class.
     */
    private static final class MessageData
    {
        /** The message content. */
        final String message;
        /** The vertical location. */
        final int x;
        /** The vertical location. */
        final int y;
        /** The timer. */
        private final Timing timer;
        /** Max time. */
        private final int time;

        /**
         * Internal constructor.
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
            return timer.elapsed(time);
        }
    }
}
