/*
 * Copyright (C) 2013-2015 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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

import com.b3dgs.lionengine.core.Media;
import com.b3dgs.lionengine.core.Medias;
import com.b3dgs.lionengine.game.object.Services;
import com.b3dgs.lionengine.game.object.SetupSurface;
import com.b3dgs.lionengine.game.state.StateAnimationBased;
import com.b3dgs.lionengine.game.trait.collidable.Collidable;
import com.b3dgs.lionengine.game.trait.collidable.CollidableListener;

/**
 * Mario specific implementation.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
class Mario extends Entity implements CollidableListener
{
    /** Mario media. */
    public static final Media CONFIG = Medias.create("entity", "Mario.xml");

    /**
     * Constructor.
     * 
     * @param setup The setup reference.
     * @param services The services reference.
     */
    public Mario(SetupSurface setup, Services services)
    {
        super(setup, services);
    }

    @Override
    protected void onPrepared()
    {
        StateAnimationBased.Util.loadStates(MarioState.values(), factory, this);
        super.onPrepared();
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
    public void notifyCollided(Collidable collidable)
    {
        final Entity entity = collidable.getOwner();
        if (transformable.getY() >= transformable.getOldY() && !entity.isState(GoombaState.DEATH))
        {
            this.collidable.setEnabled(false);
            tileCollidable.setEnabled(false);
            changeState(MarioState.DEATH);
        }
    }
}
