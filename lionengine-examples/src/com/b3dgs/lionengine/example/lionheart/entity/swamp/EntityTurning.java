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

import com.b3dgs.lionengine.Timing;
import com.b3dgs.lionengine.anim.AnimState;
import com.b3dgs.lionengine.core.UtilityMath;
import com.b3dgs.lionengine.example.lionheart.Sfx;
import com.b3dgs.lionengine.example.lionheart.entity.Entity;
import com.b3dgs.lionengine.example.lionheart.entity.EntityState;
import com.b3dgs.lionengine.example.lionheart.entity.SetupEntity;

/**
 * Turning scenery implementation base.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public abstract class EntityTurning
        extends EntityScenerySheet
{
    /** Shake amplitude. */
    protected static final int SHAKE_AMPLITUDE = 5;
    /** Time before start shake. */
    protected static final int TIME_BEFORE_START_SHAKE = 2000;
    /** Time before start turning. */
    protected static final int TIME_BEFORE_TURNING = 1000;
    /** Shake speed. */
    protected static final int SHAKE_SPEED = 30;
    /** Shake timer. */
    protected final Timing timerShake;
    /** Shake counter. */
    protected int shakeCounter;
    /** Shake started. */
    protected boolean shake;

    /**
     * @see Entity#Entity(SetupEntity)
     */
    public EntityTurning(SetupEntity setup)
    {
        super(setup);
        timerShake = new Timing();
    }

    /*
     * EntityScenerySheet
     */

    @Override
    public void update(double extrp)
    {
        super.update(extrp);
        if (!shake && timerShake.elapsed(EntityTurning.TIME_BEFORE_START_SHAKE))
        {
            shake = true;
            timerShake.stop();
            shakeCounter = 0;
            Sfx.BIPBIPBIP.play();
        }
        if (shake)
        {
            if (shakeCounter < 3)
            {
                effectCounter += EntityTurning.SHAKE_SPEED * extrp;
                if (effectCounter > EntityScenerySheet.HALF_CIRCLE)
                {
                    shakeCounter++;
                    effectCounter = 0;
                }
                effectStart = false;
            }
            // Prepare turning
            if (shakeCounter == 3)
            {
                shakeCounter = 4;
                timerShake.start();
            }
            setLocationY(initialY - UtilityMath.sin(effectCounter) * EntityTurning.SHAKE_AMPLITUDE);
        }
    }

    @Override
    public void hitThat(Entity entity)
    {
        if (shakeCounter < 5)
        {
            super.hitThat(entity);
        }
    }

    @Override
    protected void updateStates()
    {
        super.updateStates();
        if (shake)
        {
            // Turning, detect end turning
            if (shakeCounter == 5)
            {
                effectStart = false;
                if (getAnimState() == AnimState.FINISHED)
                {
                    shakeCounter = 0;
                    shake = false;
                    status.setState(EntityState.IDLE);
                    effectSide = 1;
                    timerShake.start();
                }
            }
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
