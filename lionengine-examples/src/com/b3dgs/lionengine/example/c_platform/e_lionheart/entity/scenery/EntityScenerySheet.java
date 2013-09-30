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
package com.b3dgs.lionengine.example.c_platform.e_lionheart.entity.scenery;

import com.b3dgs.lionengine.example.c_platform.e_lionheart.Level;
import com.b3dgs.lionengine.example.c_platform.e_lionheart.entity.Entity;
import com.b3dgs.lionengine.example.c_platform.e_lionheart.entity.EntityType;
import com.b3dgs.lionengine.utility.UtilityMath;

/**
 * Sheet base scenery implementation.
 */
public class EntityScenerySheet
        extends EntityScenery
{
    /** Half circle. */
    protected static final int HALF_CIRCLE = 180;
    /** Effect speed. */
    private static final int EFFECT_SPEED = 9;
    /** Effect amplitude. */
    private static final int AMPLITUDE = 6;
    /** Initial vertical location, default sheet location y. */
    protected int initialY = Integer.MIN_VALUE;
    /** Effect start flag, <code>true</code> when effect is occurring, <code>false</code> else. */
    protected boolean effectStart;
    /** Effect counter, represent the value used to calculate the effect. */
    protected int effectCounter;
    /** Effect side, -1 to decrease, 1 to increase. */
    protected int effectSide;
    /** First hit flag, when sheet is hit for the first time. */
    protected boolean firstHit;

    /**
     * @see Entity#Entity(Level, EntityType)
     */
    public EntityScenerySheet(Level level, EntityType type)
    {
        super(level, type);
    }

    /*
     * EntityScenery
     */

    @Override
    public void update(double extrp)
    {
        // Keep original location y
        if (initialY == Integer.MIN_VALUE)
        {
            initialY = getLocationIntY();
        }
        super.update(extrp);
        if (effectStart)
        {
            effectCounter += EntityScenerySheet.EFFECT_SPEED * effectSide;
            // Detect end
            if (effectCounter >= EntityScenerySheet.HALF_CIRCLE)
            {
                effectCounter = 0;
                effectSide = 0;
            }
            if (effectCounter <= 0)
            {
                effectCounter = 0;
                effectSide = 0;
            }
            setLocationY(initialY - UtilityMath.sin(effectCounter) * EntityScenerySheet.AMPLITUDE);
        }
    }

    @Override
    protected void onCollide(Entity entity)
    {
        if (!effectStart)
        {
            effectSide = 1;
            effectStart = true;
        }
        if (!firstHit)
        {
            firstHit = true;
            if (effectCounter > EntityScenerySheet.HALF_CIRCLE / 2)
            {
                effectSide = -1;
            }
            else
            {
                effectSide = 1;
            }
            effectStart = true;
        }
    }

    @Override
    protected void onLostCollision()
    {
        firstHit = false;
        if (effectStart)
        {
            // Go back to 0 as effect is lower than its half way
            if (effectCounter < EntityScenerySheet.HALF_CIRCLE / 2)
            {
                effectSide = -1;
            }
            if (effectCounter == 0)
            {
                effectStart = false;
            }
        }
    }
}
