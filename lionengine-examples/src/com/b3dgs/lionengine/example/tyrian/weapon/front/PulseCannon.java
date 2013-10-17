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
package com.b3dgs.lionengine.example.tyrian.weapon.front;

import com.b3dgs.lionengine.example.tyrian.entity.Entity;
import com.b3dgs.lionengine.example.tyrian.projectile.FactoryProjectile;
import com.b3dgs.lionengine.example.tyrian.projectile.HandlerProjectile;
import com.b3dgs.lionengine.example.tyrian.projectile.ProjectileType;
import com.b3dgs.lionengine.example.tyrian.weapon.Weapon;

/**
 * Pulse cannon implementation.
 */
final class PulseCannon
        extends Weapon
{
    /**
     * @see Weapon#Weapon(FactoryProjectile, HandlerProjectile)
     */
    public PulseCannon(FactoryProjectile factory, HandlerProjectile handler)
    {
        super(factory, handler);
        setOffsetY(-5);
        setRate(175);
    }

    /*
     * Weapon
     */

    @Override
    protected void launchProjectile(Entity owner)
    {
        int dmg;
        final int speed = 6;
        switch (level.getCurrent())
        {
            case 0:
                dmg = 60;
                addProjectile(ProjectileType.PULSE, dmg, 0, speed, -3, 0);
                addProjectile(ProjectileType.PULSE, dmg, 0, speed, 3, 0);
                break;
            case 1:
                dmg = 60;
                addProjectile(ProjectileType.PULSE, dmg, 0, speed, -3, 0);
                addProjectile(ProjectileType.PULSE, dmg, 0, speed, 0, 3);
                addProjectile(ProjectileType.PULSE, dmg, 0, speed, 3, 0);
                break;
            case 2:
                dmg = 60;
                addProjectile(ProjectileType.PULSE, dmg, 0, speed, -9, 0);
                addProjectile(ProjectileType.PULSE, dmg, 0, speed, -3, 0);
                addProjectile(ProjectileType.PULSE, dmg, 0, speed, 3, 0);
                addProjectile(ProjectileType.PULSE, dmg, 0, speed, 9, 0);
                break;
            case 3:
                dmg = 60;
                addProjectile(ProjectileType.PULSE, dmg, 0, speed, -8, 0);
                addProjectile(ProjectileType.PULSE, dmg, 0, speed, -4, 2);
                addProjectile(ProjectileType.PULSE, dmg, 0, speed, 0, 4);
                addProjectile(ProjectileType.PULSE, dmg, 0, speed, 4, 2);
                addProjectile(ProjectileType.PULSE, dmg, 0, speed, 8, 0);
                break;
            case 4:
                dmg = 60;
                addProjectile(ProjectileType.PULSE, dmg, 0, speed, -17, 0);
                addProjectile(ProjectileType.PULSE, dmg, 0, speed, -13, 0);
                addProjectile(ProjectileType.PULSE, dmg, 0, speed, -9, 1);
                addProjectile(ProjectileType.PULSE, dmg, 0, speed, -4, 3);
                addProjectile(ProjectileType.PULSE, dmg, 0, speed, 0, 5);
                addProjectile(ProjectileType.PULSE, dmg, 0, speed, 4, 3);
                addProjectile(ProjectileType.PULSE, dmg, 0, speed, 9, 1);
                addProjectile(ProjectileType.PULSE, dmg, 0, speed, 13, 0);
                addProjectile(ProjectileType.PULSE, dmg, 0, speed, 17, 0);
                break;
            case 5:
                dmg = 60;
                addProjectile(ProjectileType.PULSE, dmg, 0, speed, -19, 0);
                addProjectile(ProjectileType.PULSE, dmg, 0, speed, -16, 0);
                addProjectile(ProjectileType.PULSE, dmg, 0, speed, -13, 0);
                addProjectile(ProjectileType.PULSE, dmg, 0, speed, -10, 2);
                addProjectile(ProjectileType.PULSE, dmg, 0, speed, -6, 3);
                addProjectile(ProjectileType.PULSE, dmg, 0, speed, -3, 5);
                addProjectile(ProjectileType.PULSE, dmg, 0, speed, 0, 6);
                addProjectile(ProjectileType.PULSE, dmg, 0, speed, 3, 5);
                addProjectile(ProjectileType.PULSE, dmg, 0, speed, 6, 3);
                addProjectile(ProjectileType.PULSE, dmg, 0, speed, 10, 2);
                addProjectile(ProjectileType.PULSE, dmg, 0, speed, 13, 0);
                addProjectile(ProjectileType.PULSE, dmg, 0, speed, 16, 0);
                addProjectile(ProjectileType.PULSE, dmg, 0, speed, 19, 0);
                break;
            default:
                break;
        }
    }
}
