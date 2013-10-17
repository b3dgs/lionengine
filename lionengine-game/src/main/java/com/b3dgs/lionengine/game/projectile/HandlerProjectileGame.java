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
package com.b3dgs.lionengine.game.projectile;

import com.b3dgs.lionengine.Graphic;
import com.b3dgs.lionengine.game.CameraGame;
import com.b3dgs.lionengine.game.entity.EntityGame;
import com.b3dgs.lionengine.game.entity.HandlerEntityGame;

/**
 * Handle all ship projectiles (the ship represents the main player). Entity projectiles are handled in a separate
 * handler in order to improve performances, avoiding useless checks.
 * 
 * @param <E> The entity type used.
 * @param <P> The projectile type used.
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public class HandlerProjectileGame<E extends EntityGame, P extends ProjectileGame<E, ?>>
        extends HandlerEntityGame<P>
{
    /** Camera reference. */
    protected final CameraGame camera;
    /** The entity handler reference. */
    private final HandlerEntityGame<E>[] handlerEntity;

    /**
     * Create a new player projectile handler.
     * 
     * @param camera The camera reference.
     * @param handlerEntity The entity handlers reference.
     */
    @SafeVarargs
    public HandlerProjectileGame(CameraGame camera, HandlerEntityGame<E>... handlerEntity)
    {
        super();
        this.camera = camera;
        this.handlerEntity = handlerEntity;
    }

    /**
     * Delete all projectiles with this id.
     * 
     * @param id The projectiles id to delete.
     */
    private void deleteID(int id)
    {
        if (id > -1)
        {
            for (final P projectile : list())
            {
                if (projectile.getProjectileId() == id)
                {
                    projectile.destroy();
                }
            }
        }
    }

    /*
     * HandlerEntityGame
     */

    @Override
    protected void update(double extrp, P projectile)
    {
        projectile.update(extrp);
        for (final HandlerEntityGame<E> handler : handlerEntity)
        {
            for (final E entity : handler.list())
            {
                if (!projectile.isDestroyed() && projectile.getOwner() != entity && projectile.collide(entity))
                {
                    if (!projectile.canHitOnlyTarget() || projectile.canHitOnlyTarget()
                            && entity == projectile.getTarget())
                    {
                        projectile.onHit(entity, projectile.damages.getRandom());
                    }
                }
            }
        }
        if (projectile.isDestroyed())
        {
            deleteID(projectile.getProjectileId());
        }
    }

    @Override
    protected void render(Graphic g, P projectile)
    {
        if (camera.isVisible(projectile))
        {
            projectile.render(g, camera);
        }
    }

    @Override
    protected boolean canBeAdded(P projectile)
    {
        return projectile.canBeAdded();
    }
}
