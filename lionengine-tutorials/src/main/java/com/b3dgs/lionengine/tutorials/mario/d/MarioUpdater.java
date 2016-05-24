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

import com.b3dgs.lionengine.core.awt.Keyboard;
import com.b3dgs.lionengine.game.collision.object.Collidable;
import com.b3dgs.lionengine.game.collision.object.CollidableListener;
import com.b3dgs.lionengine.game.collision.tile.TileCollidable;
import com.b3dgs.lionengine.game.feature.Featurable;
import com.b3dgs.lionengine.game.feature.Service;
import com.b3dgs.lionengine.game.feature.Services;
import com.b3dgs.lionengine.game.object.feature.transformable.Transformable;
import com.b3dgs.lionengine.game.state.StateAnimationBased;

/**
 * Mario specific implementation.
 */
public class MarioUpdater extends EntityUpdater implements CollidableListener
{
    @Service private Transformable transformable;
    @Service private TileCollidable tileCollidable;
    @Service private Collidable collidable;

    @Service private Keyboard keyboard;

    /**
     * Constructor.
     * 
     * @param model The model reference.
     */
    public MarioUpdater(EntityModel model)
    {
        super(model);
    }

    @Override
    public void prepare(Featurable owner, Services services)
    {
        StateAnimationBased.Util.loadStates(MarioState.values(), factory, owner, configurer);

        super.prepare(owner, services);

        setControl(keyboard);
        respawn(160);
    }

    @Override
    public void update(double extrp)
    {
        if (transformable.getY() < 0)
        {
            respawn(160);
        }
        super.update(extrp);
    }

    @Override
    public void notifyCollided(Collidable other)
    {
        final Entity entity = other.getOwner();
        if (transformable.getY() >= transformable.getOldY()
            && !entity.getFeature(EntityUpdater.class).isState(GoombaState.DEATH))
        {
            collidable.setEnabled(false);
            tileCollidable.setEnabled(false);
            changeState(MarioState.DEATH);
        }
    }
}
