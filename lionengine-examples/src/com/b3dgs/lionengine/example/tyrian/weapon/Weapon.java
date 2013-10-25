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
package com.b3dgs.lionengine.example.tyrian.weapon;

import com.b3dgs.lionengine.example.tyrian.entity.Entity;
import com.b3dgs.lionengine.example.tyrian.projectile.Projectile;
import com.b3dgs.lionengine.example.tyrian.projectile.ProjectileType;
import com.b3dgs.lionengine.game.Alterable;
import com.b3dgs.lionengine.game.projectile.LauncherProjectileGame;

/**
 * Weapon base implementation.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public abstract class Weapon
        extends LauncherProjectileGame<ProjectileType, Entity, Entity, Projectile>
{
    /** Energy to consume. */
    private int consume;

    /**
     * Constructor.
     * 
     * @param setup The setup reference.
     */
    protected Weapon(SetupWeapon setup)
    {
        super(setup, setup.factory, setup.handler);
        level.setMax(5);
        consume = 300;
    }

    /**
     * Start shoot.
     * 
     * @param energy The energy reference.
     */
    public void launch(Alterable energy)
    {
        if (energy.isEnough(consume))
        {
            if (launch())
            {
                energy.decrease(consume);
            }
        }
    }

    /**
     * Set the energy to consume.
     * 
     * @param consume The energy to consume.
     */
    protected void setConsume(int consume)
    {
        this.consume = consume;
    }

    /*
     * LauncherProjectileGame
     */

    @Override
    protected void launchProjectile(Entity owner, Entity target)
    {
        // Nothing to do
    }
}
