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
package com.b3dgs.lionengine.game.map;

import java.util.Collection;

/**
 * Collision tile category.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public interface CollisionTileCategory
{
    /**
     * Get the list of collisions to test.
     * 
     * @return The collisions list.
     */
    Collection<CollisionTile> getCollisions();

    /**
     * Get the axis where slide should be performed.
     * 
     * @return The slide axis (<code>null</code> if none).
     */
    CollisionRefential getSlide();

    /**
     * Get the horizontal tile collision check offset.
     * 
     * @return The horizontal tile collision check offset.
     */
    int getOffsetX();

    /**
     * Get the vertical tile collision check offset.
     * 
     * @return The vertical tile collision check offset.
     */
    int getOffsetY();
}
