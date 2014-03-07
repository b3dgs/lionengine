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
package com.b3dgs.lionengine.example.game.strategy.fog;

import com.b3dgs.lionengine.anim.Animation;
import com.b3dgs.lionengine.game.Orientation;
import com.b3dgs.lionengine.game.strategy.entity.EntityStrategy;

/**
 * Abstract entity implementation.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 * @see com.b3dgs.lionengine.example.game.entity
 */
abstract class Entity
        extends EntityStrategy
{
    /** Idle animation. */
    protected final Animation animIdle;
    /** Map reference. */
    protected final Map map;

    /**
     * Constructor.
     * 
     * @param setup The setup reference.
     */
    protected Entity(SetupEntity setup)
    {
        super(setup, setup.map);
        map = setup.map;
        animIdle = getDataAnimation("idle");
        play(animIdle);
    }

    /*
     * EntityRts
     */

    @Override
    public void update(double extrp)
    {
        mirror(getOrientation().ordinal() > Orientation.ORIENTATIONS_NUMBER_HALF);
        super.update(extrp);
    }

    @Override
    public void stop()
    {
        // Nothing for the moment
    }
}
