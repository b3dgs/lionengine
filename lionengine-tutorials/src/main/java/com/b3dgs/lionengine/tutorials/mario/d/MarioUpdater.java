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

import com.b3dgs.lionengine.game.FeatureGet;
import com.b3dgs.lionengine.game.FeatureProvider;
import com.b3dgs.lionengine.game.Services;
import com.b3dgs.lionengine.game.Setup;
import com.b3dgs.lionengine.game.feature.Transformable;
import com.b3dgs.lionengine.game.feature.collidable.Collidable;
import com.b3dgs.lionengine.game.feature.collidable.CollidableListener;
import com.b3dgs.lionengine.game.feature.tile.map.collision.TileCollidable;
import com.b3dgs.lionengine.game.state.StateAnimationBased;
import com.b3dgs.lionengine.io.InputDeviceDirectional;

/**
 * Mario specific implementation.
 */
class MarioUpdater extends EntityUpdater implements CollidableListener
{
    private final InputDeviceDirectional keyboard;

    @FeatureGet private Transformable transformable;
    @FeatureGet private TileCollidable tileCollidable;
    @FeatureGet private Collidable collidable;

    /**
     * Constructor.
     * 
     * @param setup The setup reference.
     * @param services The services reference.
     */
    public MarioUpdater(Services services, Setup setup)
    {
        super(services, setup);

        keyboard = services.get(InputDeviceDirectional.class);
    }

    @Override
    public void prepare(FeatureProvider provider)
    {
        StateAnimationBased.Util.loadStates(MarioState.values(), factory, provider, setup);

        super.prepare(provider);

        collidable.setGroup(0);
        collidable.addAccept(1);
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
