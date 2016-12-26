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
package com.b3dgs.lionengine.game.feature.collidable;

import com.b3dgs.lionengine.Nameable;

/**
 * Represents the collision data, offsets and size. Should be used in combination with
 * {@link CollisionConfig#getCollision(String)} and {@link Collidable#addCollision(Collision)}.
 * 
 * @see ComponentCollision
 */
public class Collision implements Nameable
{
    /** Name. */
    private final String name;
    /** Horizontal offset. */
    private final int offsetX;
    /** Vertical offset. */
    private final int offsetY;
    /** Width. */
    private final int width;
    /** Height. */
    private final int height;
    /** Has mirror. */
    private final boolean mirror;

    /**
     * Create a collision.
     * 
     * @param name The collision name.
     * @param offsetX The collision horizontal offset.
     * @param offsetY The collision vertical offset.
     * @param width The collision width.
     * @param height The collision height.
     * @param mirror The mirror flag.
     */
    public Collision(String name, int offsetX, int offsetY, int width, int height, boolean mirror)
    {
        this.name = name;
        this.offsetX = offsetX;
        this.offsetY = offsetY;
        this.width = width;
        this.height = height;
        this.mirror = mirror;
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

    /*
     * Nameable
     */

    @Override
    public String getName()
    {
        return name;
    }
}
