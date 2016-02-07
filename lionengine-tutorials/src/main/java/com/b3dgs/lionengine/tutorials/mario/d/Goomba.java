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

import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.core.InputDeviceDirectional;
import com.b3dgs.lionengine.core.Medias;
import com.b3dgs.lionengine.game.Axis;
import com.b3dgs.lionengine.game.collision.object.CollidableListener;
import com.b3dgs.lionengine.game.object.ObjectGame;
import com.b3dgs.lionengine.game.object.Services;
import com.b3dgs.lionengine.game.object.SetupSurface;
import com.b3dgs.lionengine.game.object.trait.transformable.Transformable;
import com.b3dgs.lionengine.game.state.StateAnimationBased;
import com.b3dgs.lionengine.game.tile.Tile;

/**
 * Goomba specific implementation.
 */
class Goomba extends Entity implements InputDeviceDirectional, CollidableListener
{
    /** Goomba media. */
    public static final Media CONFIG = Medias.create("entity", "Goomba.xml");

    /** Side movement. */
    private double side;

    /**
     * {@link Entity#Entity(SetupSurface, Services)}
     */
    public Goomba(SetupSurface setup, Services services)
    {
        super(setup, services);
        side = 0.25;
    }

    @Override
    protected void onPrepared()
    {
        StateAnimationBased.Util.loadStates(GoombaState.values(), factory, this);
        super.onPrepared();
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
    public void notifyCollided(ObjectGame object)
    {
        final Entity target = (Entity) object;
        final Transformable collider = target.getTrait(Transformable.class);
        if (collider.getY() < collider.getOldY() && collider.getY() > transformable.getY())
        {
            collider.teleportY(transformable.getY() + transformable.getHeight());
            target.jump();
            collidable.setEnabled(false);
            changeState(GoombaState.DEATH);
            Sfx.CRUSH.play();
        }
    }
}
