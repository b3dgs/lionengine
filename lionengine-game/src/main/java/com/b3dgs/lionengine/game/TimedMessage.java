/*
 * Copyright (C) 2013-2023 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
 * Class representing a timed message handler.
 * <p>
 * This allows to prepare a set of timed message that will disappear at the end of their timer.
 * </p>
 */
public final class TimedMessage implements Updatable, Renderable
{
    /** List of active messages. */
    private final Collection<MessageData> messages = new HashSet<>(1);
    /** List of messages to delete. */
    private final Collection<MessageData> delete = new ArrayList<>(1);
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
        super();

        this.text = text;
    }

    /**
     * Add a timed message.
     * 
     * @param message The message string.
     * @param x The horizontal location.
     * @param y The vertical location.
     * @param time The remaining time.
     */
    public void addMessage(String message, int x, int y, long time)
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
     * <p>
     * This class is Thread-Safe.
     * </p>
     */
    private static final class MessageData
    {
        /** The message content. */
        private final String message;
        /** The vertical location. */
        private final int x;
        /** The vertical location. */
        private final int y;
        /** The timer. */
        private final Timing timer = new Timing();
        /** Max time. */
        private final long time;

        /**
         * Internal constructor.
         * 
         * @param message The message string.
         * @param x The horizontal location.
         * @param y The vertical location.
         * @param time The remaining time.
         */
        private MessageData(String message, int x, int y, long time)
        {
            super();

            this.message = message;
            this.x = x;
            this.y = y;
            this.time = time;
            timer.start();
        }

        /**
         * Check if the message is still alive.
         * 
         * @return <code>true</code> if elapsed, <code>false</code> else.
         */
        private boolean elapsed()
        {
            return timer.elapsed(time);
        }
    }
}
