/*
 * Copyright (C) 2013-2024 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.game.feature.collidable;

import com.b3dgs.lionengine.Check;
import com.b3dgs.lionengine.Nameable;

/**
 * Represents the collision data, offsets and size. Should be used in combination with
 * {@link CollisionConfig#getCollision(String)} and {@link Collidable#addCollision(Collision)}.
 * 
 * @param name The collision name.
 * @param offsetX The collision horizontal offset.
 * @param offsetY The collision vertical offset.
 * @param width The collision width.
 * @param height The collision height.
 * @param mirror The mirror flag.
 * 
 * @see ComponentCollision
 */
public record Collision(String name, int offsetX, int offsetY, int width, int height, boolean mirror)
                       implements Nameable
{

    /** Compute automatically collision by using the owner size. */
    public static final Collision AUTOMATIC = new Collision("automatic", 0, 0, 0, 0, false);

    /**
     * Create collision.
     * 
     * @param name The collision name (must not be <code>null</code>).
     * @param offsetX The collision horizontal offset.
     * @param offsetY The collision vertical offset.
     * @param width The collision width.
     * @param height The collision height.
     * @param mirror The mirror flag.
     */
    public Collision
    {
        Check.notNull(name);
    }

    /**
     * Get the horizontal collision offset.
     * 
     * @return The horizontal collision offset.
     */
    public int getOffsetX()
    {
        return offsetX;
    }

    /**
     * Get the vertical collision offset.
     * 
     * @return The vertical collision offset.
     */
    public int getOffsetY()
    {
        return offsetY;
    }

    /**
     * Get the collision width.
     * 
     * @return The collision width.
     */
    public int getWidth()
    {
        return width;
    }

    /**
     * Get the collision height.
     * 
     * @return The collision height.
     */
    public int getHeight()
    {
        return height;
    }

    /**
     * Get the mirror flag.
     * 
     * @return <code>true</code> if has mirror, <code>false</code> else.
     */
    public boolean hasMirror()
    {
        return mirror;
    }

    @Override
    public String getName()
    {
        return name;
    }
}
