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
package com.b3dgs.lionengine.tutorials.mario.f;

import com.b3dgs.lionengine.core.Media;
import com.b3dgs.lionengine.game.SetupSurfaceGame;

/**
 * Setup entity implementation.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
final class SetupEntity
        extends SetupSurfaceGame
{
    /** Map. */
    final Map map;
    /** Desired fps. */
    final int desiredFps;

    /**
     * Constructor.
     * 
     * @param config The media config.
     * @param map The map reference.
     * @param desiredFps The desired fps.
     */
    SetupEntity(Media config, Map map, int desiredFps)
    {
        super(config);
        this.map = map;
        this.desiredFps = desiredFps;
    }
}
