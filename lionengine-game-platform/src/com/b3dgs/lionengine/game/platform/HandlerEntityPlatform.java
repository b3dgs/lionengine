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
package com.b3dgs.lionengine.game.platform;

import com.b3dgs.lionengine.Graphic;
import com.b3dgs.lionengine.game.entity.HandlerEntityGame;

/**
 * Default platform entity handler.
 * 
 * @param <E> Entity type used.
 */
public abstract class HandlerEntityPlatform<E extends EntityPlatform>
        extends HandlerEntityGame<E>
{
    /** Camera reference. */
    private final CameraPlatform camera;

    /**
     * Create a new handler.
     * 
     * @param camera The camera reference.
     */
    public HandlerEntityPlatform(CameraPlatform camera)
    {
        super();
        this.camera = camera;
    }

    /**
     * Check if entity can be updated.
     * 
     * @param entity The entity to check.
     * @return <code>true</code> if can be updated, <code>false</code> else.
     */
    protected abstract boolean canUpdateEntity(E entity);

    /**
     * Check if entity can be rendered.
     * 
     * @param entity The entity to check.
     * @return <code>true</code> if can be rendered, <code>false</code> else.
     */
    protected abstract boolean canRenderEntity(E entity);

    /**
     * Update this entity ({@link EntityPlatform#update(double)} is already called before).
     * 
     * @param entity The current updating entity.
     * @param extrp The extrapolation value.
     */
    protected abstract void updatingEntity(E entity, double extrp);

    /**
     * Render this entity ({@link EntityPlatform#render(Graphic, CameraPlatform)} is already called before).
     * 
     * @param entity The current rendering entity.
     * @param g The graphic output.
     * @param camera The camera reference.
     */
    protected abstract void renderingEntity(E entity, Graphic g, CameraPlatform camera);

    /*
     * HandlerEntityGame
     */

    @Override
    protected void update(double extrp, E entity)
    {
        if (canUpdateEntity(entity))
        {
            entity.update(extrp);
            updatingEntity(entity, extrp);
        }
        if (entity.isDestroyed())
        {
            remove(entity);
        }
    }

    @Override
    protected void render(Graphic g, E entity)
    {
        if (canRenderEntity(entity))
        {
            entity.render(g, camera);
            renderingEntity(entity, g, camera);
        }
    }
}
