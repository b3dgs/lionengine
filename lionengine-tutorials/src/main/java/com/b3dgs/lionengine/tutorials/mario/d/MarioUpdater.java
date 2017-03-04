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

import com.b3dgs.lionengine.game.FeatureProvider;
import com.b3dgs.lionengine.game.Service;
import com.b3dgs.lionengine.game.Services;
import com.b3dgs.lionengine.game.Setup;
import com.b3dgs.lionengine.game.feature.Transformable;
import com.b3dgs.lionengine.game.feature.collidable.Collidable;
import com.b3dgs.lionengine.game.feature.collidable.CollidableListener;
import com.b3dgs.lionengine.game.feature.tile.map.collision.TileCollidable;
import com.b3dgs.lionengine.game.state.StateAnimationBased;
import com.b3dgs.lionengine.io.awt.Keyboard;

/**
 * Mario specific implementation.
 */
public class MarioUpdater extends EntityUpdater implements CollidableListener
{
    private final Setup setup;

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

        setup = model.getSetup();
    }

    @Override
    public void prepare(FeatureProvider provider, Services services)
    {
        StateAnimationBased.Util.loadStates(MarioState.values(), factory, provider, setup);

        super.prepare(provider, services);

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
        if (transformable.getY() >= transformable.getOldY()
            && !other.getFeature(EntityUpdater.class).isState(GoombaState.DEATH))
        {
            collidable.setEnabled(false);
            tileCollidable.setEnabled(false);
            changeState(MarioState.DEATH);
        }
    }
}
