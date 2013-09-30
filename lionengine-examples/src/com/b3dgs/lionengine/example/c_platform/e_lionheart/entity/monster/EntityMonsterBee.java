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
package com.b3dgs.lionengine.example.c_platform.e_lionheart.entity.monster;

import com.b3dgs.lionengine.example.c_platform.e_lionheart.Level;
import com.b3dgs.lionengine.example.c_platform.e_lionheart.entity.Entity;
import com.b3dgs.lionengine.example.c_platform.e_lionheart.entity.EntityState;
import com.b3dgs.lionengine.example.c_platform.e_lionheart.entity.EntityType;
import com.b3dgs.lionengine.example.c_platform.e_lionheart.entity.patrol.Patrol;

/**
 * Bee monster base implementation.
 */
public abstract class EntityMonsterBee
        extends EntityMonster
{
    /**
     * @see Entity#Entity(Level, EntityType)
     */
    public EntityMonsterBee(Level level, EntityType type)
    {
        super(level, type);
        enableMovement(Patrol.HORIZONTAL);
        enableMovement(Patrol.VERTICAL);
    }

    /*
     * EntityMonster
     */

    @Override
    protected void updateStates()
    {
        super.updateStates();
        mirror(false);
        if (status.getState() == EntityState.IDLE)
        {
            status.setState(EntityState.WALK);
        }
    }

    @Override
    protected void updateCollisions()
    {
        // Nothing to do
    }
}
