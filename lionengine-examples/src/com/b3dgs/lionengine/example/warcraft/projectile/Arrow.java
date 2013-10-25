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
package com.b3dgs.lionengine.example.warcraft.projectile;

import com.b3dgs.lionengine.example.warcraft.entity.Entity;
import com.b3dgs.lionengine.game.CollisionData;
import com.b3dgs.lionengine.game.SetupSurfaceGame;

/**
 * Arrow projectile implementation.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public final class Arrow
        extends Projectile
{
    /**
     * Constructor.
     * 
     * @param setup The entity setup.
     */
    public Arrow(SetupSurfaceGame setup)
    {
        super(setup);
        setCollision(new CollisionData(0, 1, 0, 1, false));
    }

    /*
     * Projectile
     */

    @Override
    public void onHit(Entity entity, int damages)
    {
        entity.decreaseLife(damages, getOwner().getUser());
        destroy(); // Destroy projectile on hit
    }
}
