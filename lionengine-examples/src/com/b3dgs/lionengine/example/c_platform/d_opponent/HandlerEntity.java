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
package com.b3dgs.lionengine.example.c_platform.d_opponent;

import com.b3dgs.lionengine.core.Graphic;
import com.b3dgs.lionengine.game.platform.CameraPlatform;
import com.b3dgs.lionengine.game.platform.entity.HandlerEntityPlatform;

/**
 * Handler implementation. All of our entity will be handled here.
 */
final class HandlerEntity
        extends HandlerEntityPlatform<Entity>
{
    /** Mario reference. */
    private final Mario mario;

    /**
     * Constructor.
     * 
     * @param camera The camera reference.
     * @param mario The mario reference.
     */
    HandlerEntity(CameraPlatform camera, Mario mario)
    {
        super(camera);
        this.mario = mario;
    }

    /*
     * HandlerEntityPlatform
     */

    @Override
    protected boolean canUpdateEntity(Entity entity)
    {
        return true;
    }

    @Override
    protected void updatingEntity(Entity entity, double extrp)
    {
        if (!(mario.isDead() || entity.isDead()) && entity.collide(mario))
        {
            // Mario hit entity if coming from its top
            if (mario.isFalling() && mario.getLocationOldY() > entity.getLocationOldY())
            {
                entity.onHurtBy(mario);
                mario.onHitThat(entity);
            }
            else
            {
                mario.onHurtBy(entity);
                entity.onHitThat(mario);
            }
        }
    }

    @Override
    protected void renderingEntity(Entity entity, Graphic g, CameraPlatform camera)
    {
        // Nothing to do
    }
}
