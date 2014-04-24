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
package com.b3dgs.lionengine.game.purview;

import com.b3dgs.lionengine.core.Graphic;
import com.b3dgs.lionengine.game.CameraGame;
import com.b3dgs.lionengine.game.CollisionData;
import com.b3dgs.lionengine.geom.Line;
import com.b3dgs.lionengine.geom.Rectangle;

/**
 * Purview representing something which can enter in collision with another. Based on a ray casting collision from a
 * bounding box area.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public interface Collidable
{
    /**
     * Update collision with specified area.
     */
    void updateCollision();

    /**
     * Set the collision to use.
     * 
     * @param collision The collision to use (<code>null</code> if none).
     */
    void setCollision(CollisionData collision);

    /**
     * Get the current collision used.
     * 
     * @return The collision data.
     */
    CollisionData getCollisionData();

    /**
     * Check if the entity entered in collision with another one.
     * 
     * @param entity The opponent.
     * @return <code>true</code> if collide, <code>false</code> else.
     */
    boolean collide(Collidable entity);

    /**
     * Check if the entity entered in collision with a specified area.
     * 
     * @param area The area to check.
     * @return <code>true</code> if collide, <code>false</code> else.
     */
    boolean collide(Rectangle area);

    /**
     * Render collision bounding box.
     * 
     * @param g The graphic output.
     * @param camera The camera reference.
     */
    void renderCollision(Graphic g, CameraGame camera);

    /**
     * Get collision representation.
     * 
     * @return The collision representation.
     */
    Rectangle getCollisionBounds();

    /**
     * Get collision ray cast.
     * 
     * @return The collision ray cast.
     */
    Line getCollisionRay();
}
