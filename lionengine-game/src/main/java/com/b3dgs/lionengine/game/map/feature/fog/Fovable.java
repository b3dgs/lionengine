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
package com.b3dgs.lionengine.game.map.feature.fog;

import com.b3dgs.lionengine.game.handler.Feature;
import com.b3dgs.lionengine.game.tile.Tiled;

/**
 * Represents something that have a field of view, able to see until a defined range only.
 */
public interface Fovable extends Feature, Tiled
{
    /**
     * Set the field of view value (in tile).
     * 
     * @param fov The field of view value (in tile).
     */
    void setFov(int fov);

    /**
     * Get the field of view in tile.
     * 
     * @return The field of view in tile.
     */
    int getInTileFov();
}
