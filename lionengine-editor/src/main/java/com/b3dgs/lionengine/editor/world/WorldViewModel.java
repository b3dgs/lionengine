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
package com.b3dgs.lionengine.editor.world;

import com.b3dgs.lionengine.game.CameraGame;
import com.b3dgs.lionengine.game.map.MapTile;

/**
 * Contains the objects of the world.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public enum WorldViewModel
{
    /** Instance. */
    INSTANCE;

    /** Camera reference. */
    private final CameraGame camera;
    /** Map reference. */
    private MapTile<?, ?> map;

    /**
     * Constructor.
     */
    private WorldViewModel()
    {
        camera = new CameraGame();
    }

    /**
     * Set the map reference.
     * 
     * @param map The map reference.
     */
    public void setMap(MapTile<?, ?> map)
    {
        this.map = map;
    }

    /**
     * Get the camera reference.
     * 
     * @return The camera reference.
     */
    public CameraGame getCamera()
    {
        return camera;
    }

    /**
     * Get the map reference.
     * 
     * @return The map reference.
     */
    public MapTile<?, ?> getMap()
    {
        return map;
    }
}
