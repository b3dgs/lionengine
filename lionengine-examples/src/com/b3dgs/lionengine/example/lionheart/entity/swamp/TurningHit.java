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

/**
 * Turning hit scenery implementation.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public final class TurningHit
        extends EntityTurning
{
    /**
     * Constructor.
     * 
     * @param setup The setup reference.
     */
    public TurningHit(SetupEntity setup)
    {
        super(setup);
        shakeCounter = 5;
    }

    /*
     * EntityTurning
     */

    @Override
    public void hitBy(Entity entity)
    {
        super.hitBy(entity);
        if (shakeCounter == 6)
        {
            shakeCounter = 7;
        }
    }

    @Override
    protected void updateStates()
    {
        super.updateStates();
        // Turning
        if (shakeCounter == 5)
        {
            status.setState(EntityState.TURN);
            shakeCounter = 6;
            effectStart = false;
        }
        // Detect end turning
        if (shakeCounter == 7)
        {
            effectStart = false;
            if (getFrameAnim() == 1 || getFrameAnim() == 16)
            {
                shakeCounter = 0;
                shake = false;
                status.setState(EntityState.IDLE);
                effectSide = 1;
                timerShake.start();
            }
        }
        if (shake)
        {
            // Start turning
            if (shakeCounter == 4 && timerShake.elapsed(EntityTurning.TIME_BEFORE_TURNING))
            {
                shakeCounter = 5;
                timerShake.stop();
                status.setState(EntityState.TURN);
            }
        }
    }
}
