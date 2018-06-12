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
package com.b3dgs.lionengine.audio;

import java.util.concurrent.atomic.AtomicReference;

import com.b3dgs.lionengine.Media;

/**
 * Player mock.
 */
public class PlayerMock extends PlayerAbstract
{
    /** Played track. */
    final AtomicReference<String> played = new AtomicReference<>();

    /**
     * Create player.
     * 
     * @param media The media reference.
     */
    protected PlayerMock(Media media)
    {
        super(media);
    }

    @Override
    protected void play(String track)
    {
        played.set(track);
    }

    @Override
    public void setVolume(int volume)
    {
        // Mock
    }

    @Override
    public void stop()
    {
        // Mock
    }
}
