/*
 * Copyright (C) 2013-2016 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.tutorials.mario.d;

import com.b3dgs.lionengine.core.InputDeviceDirectional;
import com.b3dgs.lionengine.game.Axis;
import com.b3dgs.lionengine.game.collision.object.Collidable;
import com.b3dgs.lionengine.game.collision.object.CollidableListener;
import com.b3dgs.lionengine.game.collision.tile.TileCollidable;
import com.b3dgs.lionengine.game.handler.Handlable;
import com.b3dgs.lionengine.game.handler.Service;
import com.b3dgs.lionengine.game.handler.Services;
import com.b3dgs.lionengine.game.object.feature.transformable.Transformable;
import com.b3dgs.lionengine.game.state.StateAnimationBased;
import com.b3dgs.lionengine.game.tile.Tile;

/**
 * Goomba specific implementation.
 */
public class GoombaUpdater extends EntityUpdater implements InputDeviceDirectional, CollidableListener
{
    @Service private Transformable transformable;
    @Service private TileCollidable tileCollidable;
    @Service private Collidable collidable;

    /** Side movement. */
    private double side = 0.25;

    /**
     * Constructor.
     * 
     * @param model The model reference.
     */
    public GoombaUpdater(EntityModel model)
    {
        super(model);
    }

    @Override
    public void prepare(Handlable owner, Services services)
    {
        StateAnimationBased.Util.loadStates(GoombaState.values(), factory, owner, configurer);

        super.prepare(owner, services);

        collidable.addListener(this);
        setControl(this);
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
        super.notifyTileCollided(tile, axis);

        if (Axis.X == axis)
        {
            side = -side;
        }
    }

    @Override
    public void notifyCollided(Collidable other)
    {
        final Entity target = other.getOwner();
        final Transformable collider = target.getFeature(Transformable.class);
        if (collider.getY() < collider.getOldY() && collider.getY() > transformable.getY())
        {
            collider.teleportY(transformable.getY() + transformable.getHeight());
            target.getFeature(EntityUpdater.class).jump();
            collidable.setEnabled(false);
            changeState(GoombaState.DEATH);
            Sfx.CRUSH.play();
        }
    }
}
