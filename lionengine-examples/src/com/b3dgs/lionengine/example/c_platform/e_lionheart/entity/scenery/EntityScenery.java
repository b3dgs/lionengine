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

import java.awt.geom.Rectangle2D;

import com.b3dgs.lionengine.example.c_platform.e_lionheart.Level;
import com.b3dgs.lionengine.example.c_platform.e_lionheart.entity.Entity;
import com.b3dgs.lionengine.example.c_platform.e_lionheart.entity.EntityCollisionTile;
import com.b3dgs.lionengine.example.c_platform.e_lionheart.entity.EntityMover;
import com.b3dgs.lionengine.example.c_platform.e_lionheart.entity.EntityType;
import com.b3dgs.lionengine.example.c_platform.e_lionheart.entity.player.Valdyn;

/**
 * Entity scenery base implementation.
 */
public abstract class EntityScenery
        extends Entity
{
    /** Collide state. */
    private boolean collide;
    /** Collide old state. */
    private boolean collideOld;

    /**
     * @see Entity#Entity(Level, EntityType)
     */
    EntityScenery(Level level, EntityType type)
    {
        super(level, type);
    }

    /**
     * Called when collision occurred.
     * 
     * @param entity The entity reference.
     */
    protected abstract void onCollide(Entity entity);

    /**
     * Called when lost collision.
     */
    protected abstract void onLostCollision();

    /*
     * Entity
     */

    @Override
    public void hitBy(Entity entity)
    {
        // Nothing to do
    }

    @Override
    public void hitThat(Entity entity)
    {
        if (entity instanceof EntityMover)
        {
            final EntityMover mover = (EntityMover) entity;
            if (!mover.isJumping() && mover.getLocationY() > getLocationY())
            {
                onCollide(entity);
                mover.checkCollisionVertical(Double.valueOf(getLocationY() + getCollisionData().getOffsetY()),
                        EntityCollisionTile.GROUND);
                collide = true;
            }
        }
        if (entity instanceof Valdyn)
        {
            final Valdyn valdyn = (Valdyn) entity;
            final Rectangle2D collision = getCollisionBounds();
            if (valdyn.getLocationX() < collision.getMinX() - valdyn.getWidth() / 2 + Valdyn.TILE_EXTREMITY_WIDTH * 2)
            {
                valdyn.updateExtremity(true);
            }
            else if (valdyn.getLocationX() > collision.getMaxX() + valdyn.getWidth() / 2 - Valdyn.TILE_EXTREMITY_WIDTH
                    * 2)
            {
                valdyn.updateExtremity(false);
            }
        }
    }

    @Override
    public void onUpdated()
    {
        if (!collide && collideOld)
        {
            onLostCollision();
            collideOld = false;
        }
    }

    @Override
    protected void updateStates()
    {
        collideOld = collide;
        collide = false;
    }

    @Override
    protected void updateDead()
    {
        // Nothing to do
    }

    @Override
    protected void updateCollisions()
    {
        // Nothing to do
    }

    @Override
    protected void updateAnimations(double extrp)
    {
        // Nothing to do
    }
}
