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
package com.b3dgs.lionengine.example.tyrian.entity.bonus;

import com.b3dgs.lionengine.example.tyrian.effect.FactoryEffect;
import com.b3dgs.lionengine.example.tyrian.effect.HandlerEffect;
import com.b3dgs.lionengine.example.tyrian.entity.Entity;
import com.b3dgs.lionengine.example.tyrian.entity.ship.Ship;
import com.b3dgs.lionengine.game.SetupSurfaceGame;

/**
 * Bonus entity implementation.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public abstract class Bonus
        extends Entity
{
    /**
     * {@link Entity#Entity(SetupSurfaceGame, FactoryEffect, HandlerEffect)}
     */
    public Bonus(SetupSurfaceGame setup, FactoryEffect factoryEffect, HandlerEffect handlerEffect)
    {
        super(setup, factoryEffect, handlerEffect);
    }

    /*
     * Entity
     */

    @Override
    public void update(double extrp)
    {
        super.update(extrp);
        moveLocation(extrp, 0, -0.5);
    }

    @Override
    protected void onHit(Ship ship)
    {
        super.onHit(ship);
        destroy();
    }
}
