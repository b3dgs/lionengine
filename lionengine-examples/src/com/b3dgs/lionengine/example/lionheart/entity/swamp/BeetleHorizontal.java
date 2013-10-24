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
package com.b3dgs.lionengine.example.lionheart.entity.swamp;

import com.b3dgs.lionengine.example.lionheart.entity.Entity;
import com.b3dgs.lionengine.example.lionheart.entity.EntityState;
import com.b3dgs.lionengine.example.lionheart.entity.SetupEntity;
import com.b3dgs.lionengine.example.lionheart.entity.patrol.Patrol;

/**
 * Beetle horizontal implementation.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public final class BeetleHorizontal
        extends EntityBeetle
{
    /**
     * Constructor.
     * 
     * @param setup The setup reference.
     */
    public BeetleHorizontal(SetupEntity setup)
    {
        super(setup);
        setFrameOffsets(0, -6);
        enableMovement(Patrol.HORIZONTAL);
    }

    /*
     * EntityBeetle
     */

    @Override
    protected void handleActions(double extrp)
    {
        if (status.getState() == EntityState.WALK)
        {
            final int x = getLocationIntX();
            if (x > getPositionMax())
            {
                teleportX(getPositionMax());
            }
            if (x < getPositionMin())
            {
                teleportX(getPositionMin());
            }
        }
        super.handleActions(extrp);
    }

    @Override
    protected void updateStates()
    {
        super.updateStates();
        final double diffHorizontal = getDiffHorizontal();
        final int x = getLocationIntX();
        if (hasPatrol() && (x == getPositionMin() || x == getPositionMax()))
        {
            status.setState(EntityState.TURN);
        }
        else if (diffHorizontal != 0.0)
        {
            status.setState(EntityState.WALK);
        }
        else
        {
            status.setState(EntityState.IDLE);
        }
    }

    @Override
    protected void onCollide(Entity entity)
    {
        super.onCollide(entity);
        entity.teleportX(entity.getLocationX() + getDiffHorizontal());
    }
}
