/*
 * Copyright (C) 2013 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.example.c_platform.e_lionheart.entity.player;

import com.b3dgs.lionengine.file.XmlNode;
import com.b3dgs.lionengine.game.CollisionData;

/**
 * Attacks data.
 */
class Attack
{
    /** The frame index. */
    private final int frame;
    /** The collision data. */
    private final CollisionData collision;

    /**
     * Constructor.
     * 
     * @param attack The attack node.
     */
    Attack(XmlNode attack)
    {
        frame = attack.readInteger("frame");
        collision = new CollisionData(attack.readInteger("x"), attack.readInteger("y"), attack.readInteger("width"),
                attack.readInteger("height"), true);
    }

    /**
     * Get the frame that contain a collision.
     * 
     * @return The frame containing a collision.
     */
    public int getFrame()
    {
        return frame;
    }

    /**
     * Get the collision data.
     * 
     * @return The collision data.
     */
    public CollisionData getCollision()
    {
        return collision;
    }
}
