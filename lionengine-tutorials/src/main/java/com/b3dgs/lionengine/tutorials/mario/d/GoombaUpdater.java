/*
 * Copyright (C) 2013-2017 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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

import com.b3dgs.lionengine.game.feature.FeatureGet;
import com.b3dgs.lionengine.game.feature.FeatureProvider;
import com.b3dgs.lionengine.game.feature.Services;
import com.b3dgs.lionengine.game.feature.Setup;
import com.b3dgs.lionengine.game.feature.StateAnimationBased;
import com.b3dgs.lionengine.game.feature.Transformable;
import com.b3dgs.lionengine.game.feature.collidable.Collidable;
import com.b3dgs.lionengine.game.feature.collidable.CollidableListener;
import com.b3dgs.lionengine.game.feature.tile.Tile;
import com.b3dgs.lionengine.game.feature.tile.map.collision.Axis;
import com.b3dgs.lionengine.game.feature.tile.map.collision.TileCollidable;
import com.b3dgs.lionengine.io.InputDeviceDirectional;

/**
 * Goomba specific implementation.
 */
class GoombaUpdater extends EntityUpdater implements InputDeviceDirectional, CollidableListener
{
    @FeatureGet private Transformable transformable;
    @FeatureGet private TileCollidable tileCollidable;
    @FeatureGet private Collidable collidable;

    /** Side movement. */
    private double side = 0.25;

    /**
     * Constructor.
     * 
     * @param services The services reference.
     * @param setup The setup reference.
     */
    public GoombaUpdater(Services services, Setup setup)
    {
        super(services, setup);
    }

    @Override
    public void prepare(FeatureProvider provider)
    {
        StateAnimationBased.Util.loadStates(GoombaState.values(), factory, provider, setup);

        super.prepare(provider);
        collidable.setGroup(1);
        collidable.addAccept(0);
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
        final Transformable collider = other.getFeature(Transformable.class);
        if (collider.getY() < collider.getOldY() && collider.getY() > transformable.getY())
        {
            collider.teleportY(transformable.getY() + transformable.getHeight());
            other.getFeature(EntityUpdater.class).jump();
            collidable.setEnabled(false);
            changeState(GoombaState.DEATH);
            Sfx.CRUSH.play();
        }
    }
}
