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
package com.b3dgs.lionengine.example.f_network;

import java.util.HashMap;

import com.b3dgs.lionengine.Graphic;
import com.b3dgs.lionengine.game.platform.CameraPlatform;
import com.b3dgs.lionengine.game.platform.entity.HandlerEntityPlatform;

/**
 * Handler implementation. All of our entity will be handled here.
 */
class HandlerEntity
        extends HandlerEntityPlatform<Entity>
{
    /** The mario clients. */
    private final HashMap<Byte, Mario> marioClients;

    /**
     * Standard constructor.
     * 
     * @param camera The camera reference.
     * @param marioClients The clients.
     */
    public HandlerEntity(CameraPlatform camera, HashMap<Byte, Mario> marioClients)
    {
        super(camera);
        this.marioClients = marioClients;
    }

    @Override
    protected boolean canUpdateEntity(Entity entity)
    {
        return true;
    }

    @Override
    protected void updatingEntity(Entity entity, double extrp)
    {
        for (final Mario mario : marioClients.values())
        {
            if (!(mario.isDead() || entity.isDead()) && entity.collide(mario))
            {
                // Mario hit entity if coming from its top
                if (mario.getLocationY() - 10 > entity.getLocationY())
                {
                    entity.onHurtBy(mario, 0);
                    mario.onHitThat(entity);
                }
                else
                {
                    mario.onHurtBy(entity, 0);
                    entity.onHitThat(mario);
                }
            }
        }
    }

    @Override
    protected void renderingEntity(Entity entity, Graphic g, CameraPlatform camera)
    {
        // Nothing to do
    }
}
