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
package com.b3dgs.lionengine.example.e_shmup.b_shipweapon.weapon;

import com.b3dgs.lionengine.example.e_shmup.b_shipweapon.projectile.FactoryProjectile;
import com.b3dgs.lionengine.example.e_shmup.b_shipweapon.projectile.HandlerProjectile;
import com.b3dgs.lionengine.example.e_shmup.b_shipweapon.projectile.ProjectileType;
import com.b3dgs.lionengine.game.entity.EntityGame;

/**
 * Pulse cannon implementation.
 */
final class MissileLauncher
        extends Weapon
{
    /**
     * @see Weapon#Weapon(FactoryProjectile, HandlerProjectile)
     */
    MissileLauncher(FactoryProjectile factory, HandlerProjectile handler)
    {
        super(factory, handler);
        setRate(400);
    }

    /*
     * Weapon
     */

    @Override
    protected void launchProjectile(EntityGame owner)
    {
        addProjectile(ProjectileType.MISSILE, 1, 136, -4.5, 4.5, -10, -10);
        addProjectile(ProjectileType.MISSILE, 1, 151, 4.5, 4.5, 10, -10);

        addProjectile(ProjectileType.MISSILE, 1, 136, -5, 3.75, -10, -12);
        addProjectile(ProjectileType.MISSILE, 1, 151, 5, 3.75, 10, -12);

        addProjectile(ProjectileType.MISSILE, 1, 136, -6, 3.5, -10, -14);
        addProjectile(ProjectileType.MISSILE, 1, 151, 6, 3.5, 10, -14);
    }
}
