/*
 * Copyright (C) 2013-2014 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.tutorials.mario.e;

import com.b3dgs.lionengine.core.Core;
import com.b3dgs.lionengine.core.InputDeviceDirectional;
import com.b3dgs.lionengine.core.Media;
import com.b3dgs.lionengine.game.Axis;
import com.b3dgs.lionengine.game.Services;
import com.b3dgs.lionengine.game.component.ComponentCollisionListener;
import com.b3dgs.lionengine.game.factory.SetupSurface;
import com.b3dgs.lionengine.game.handler.ObjectGame;
import com.b3dgs.lionengine.game.map.Tile;
import com.b3dgs.lionengine.game.trait.Collidable;
import com.b3dgs.lionengine.game.trait.TileCollidableListener;
import com.b3dgs.lionengine.game.trait.Transformable;

/**
 * Goomba specific implementation.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
class Goomba
        extends Entity
        implements InputDeviceDirectional, TileCollidableListener, ComponentCollisionListener
{
    /** Goomba media. */
    public static final Media CONFIG = Core.MEDIA.create("entity", "Goomba.xml");

    /** Side movement. */
    private double side;

    /**
     * {@link Entity#Entity(SetupSurface, Services)}
     */
    public Goomba(SetupSurface setup, Services services)
    {
        super(setup, services);
        setControl(this);
        tileCollidable.addListener(this);
        collidable.addListener(this);
        side = 0.25;
    }

    @Override
    public void setHorizontalControlPositive(Integer code)
    {
        // Nothing to do
    }

    @Override
    public void setHorizontalControlNegative(Integer code)
    {
        // Nothing to do
    }

    @Override
    public void setVerticalControlPositive(Integer code)
    {
        // Nothing to do
    }

    @Override
    public void setVerticalControlNegative(Integer code)
    {
        // Nothing to do
    }

    @Override
    public double getHorizontalDirection()
    {
        return side;
    }

    @Override
    public double getVerticalDirection()
    {
        return 0;
    }

    @Override
    public void notifyTileCollided(Tile tile, Axis axis)
    {
        if (Axis.X == axis)
        {
            side = -side;
        }
    }

    @Override
    public void notifyCollided(Collidable collidable)
    {
        final ObjectGame target = collidable.getOwner();
        final Transformable collider = target.getTrait(Transformable.class);
        if (collider.getY() < collider.getOldY() && collider.getY() > transformable.getY())
        {
            collider.teleportY(transformable.getY() + transformable.getHeight());
            ((Entity) target).jump();
            this.collidable.setEnabled(false);
            changeState(factory.getState(EntityState.DEATH_GOOMBA));
            Sfx.CRUSH.play();
        }
    }
}
