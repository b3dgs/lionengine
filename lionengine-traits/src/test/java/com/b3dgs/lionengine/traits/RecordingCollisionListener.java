/*
 * Copyright (C) 2013-2026 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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

package com.b3dgs.lionengine.traits;

import java.util.ArrayList;
import java.util.List;

/**
 * A collision listener recording its events as text, {@code "enter a b"} and {@code "exit a b"}, in order.
 */
final class RecordingCollisionListener implements CollisionListener
{
    /** The events, in order. */
    private final List<String> events = new ArrayList<>();

    /**
     * Creates the listener.
     */
    RecordingCollisionListener()
    {
        super();
    }

    @Override
    public void onEnter(long objectA, long objectB)
    {
        events.add("enter " + objectA + " " + objectB);
    }

    @Override
    public void onExit(long objectA, long objectB)
    {
        events.add("exit " + objectA + " " + objectB);
    }

    /**
     * Returns the events and forgets them.
     *
     * @return The events since the last call, in order.
     */
    List<String> take()
    {
        final List<String> taken = new ArrayList<>(events);
        events.clear();
        return taken;
    }
}
