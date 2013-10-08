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
import com.b3dgs.lionengine.example.tyrian.projectile.FactoryProjectile;
import com.b3dgs.lionengine.example.tyrian.projectile.HandlerProjectile;
import com.b3dgs.lionengine.example.tyrian.projectile.ProjectileType;

/**
 * Pulse cannon implementation.
 */
final class PulseCannon
        extends Weapon
{
    /**
     * @see Weapon#Weapon(FactoryProjectile, HandlerProjectile)
     */
    PulseCannon(FactoryProjectile factory, HandlerProjectile handler)
    {
        super(factory, handler);
        setRate(175);
        level.setMax(6);
    }

    /*
     * Weapon
     */

    @Override
    protected void launchProjectile(Entity owner)
    {
        int dmg;
        final int lvl = level.getCurrent();
        final int speed = 6;
        final int frame = 1;
        switch (level.getCurrent())
        {
            case 4:
                dmg = 60;
                setConsume(65);
                addProjectile(ProjectileType.BULLET, dmg, frame, 0, speed, -6, -3);
                addProjectile(ProjectileType.BULLET, dmg, frame, 0, speed, 6, -3);
                break;
            case 5:
                dmg = 70;
                setConsume(85);
                addProjectile(ProjectileType.BULLET, dmg, frame, 0, speed, -12, 6);
                addProjectile(ProjectileType.BULLET, dmg, frame, 0, speed, -8, 4);
                addProjectile(ProjectileType.BULLET, dmg, frame, 0, speed, -4, 2);
                addProjectile(ProjectileType.BULLET, dmg, frame, 0, speed, 0, 0);
                addProjectile(ProjectileType.BULLET, dmg, frame, 0, speed, 4, 2);
                addProjectile(ProjectileType.BULLET, dmg, frame, 0, speed, 8, 4);
                addProjectile(ProjectileType.BULLET, dmg, frame, 0, speed, 12, 6);
                break;
            case 6:
                dmg = 80;
                setConsume(105);
                addProjectile(ProjectileType.BULLET, dmg, frame, 0, speed, -23, 7);
                addProjectile(ProjectileType.BULLET, dmg, frame, 0, speed, -18, 7);
                addProjectile(ProjectileType.BULLET, dmg, frame, 0, speed, -13, 5);
                addProjectile(ProjectileType.BULLET, dmg, frame, 0, speed, -8, 3);
                addProjectile(ProjectileType.BULLET, dmg, frame, 0, speed, 0, 0);
                addProjectile(ProjectileType.BULLET, dmg, frame, 0, speed, 8, 3);
                addProjectile(ProjectileType.BULLET, dmg, frame, 0, speed, 13, 5);
                addProjectile(ProjectileType.BULLET, dmg, frame, 0, speed, 18, 7);
                addProjectile(ProjectileType.BULLET, dmg, frame, 0, speed, 23, 7);
                break;
            default:
                dmg = 20 + lvl * 10;
                setConsume(40 + lvl * 8);
                addProjectile(ProjectileType.BULLET, dmg, frame + lvl - 1, 0, speed, 0, -3);
                break;
        }
    }
}
