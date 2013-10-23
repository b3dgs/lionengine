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
package com.b3dgs.lionengine.example.tyrian.weapon.rear;

import com.b3dgs.lionengine.example.tyrian.Sfx;
import com.b3dgs.lionengine.example.tyrian.entity.Entity;
import com.b3dgs.lionengine.example.tyrian.projectile.FactoryProjectile;
import com.b3dgs.lionengine.example.tyrian.projectile.HandlerProjectile;
import com.b3dgs.lionengine.example.tyrian.projectile.ProjectileType;
import com.b3dgs.lionengine.example.tyrian.weapon.Weapon;
import com.b3dgs.lionengine.game.SetupGame;

/**
 * Missile rear implementation.
 */
public final class MissileLauncherRear
        extends Weapon
{
    /** Side. */
    private boolean side;

    /**
     * {@link Weapon#Weapon(com.b3dgs.lionengine.game.SetupGame, FactoryProjectile, HandlerProjectile)}
     */
    public MissileLauncherRear(SetupGame setup, FactoryProjectile factory, HandlerProjectile handler)
    {
        super(setup, factory, handler);
        setRate(400);
    }

    /*
     * Weapon
     */

    @Override
    protected void launchProjectile(Entity owner)
    {
        final int dmg = 60;
        final int speed = 2;
        Sfx.WEAPON_MISSILE.play();
        switch (level.getCurrent())
        {
            case 0:
                if (side)
                {
                    addProjectile(ProjectileType.MISSILE_REAR, dmg, -speed * 1.25, speed, -10, -10).setFrame(136);
                }
                else
                {
                    addProjectile(ProjectileType.MISSILE_REAR, dmg, speed * 1.25, speed, 10, -10).setFrame(151);
                }
                side = !side;
                break;
            case 1:
                addProjectile(ProjectileType.MISSILE_REAR, dmg, -speed * 1.25, speed, -10, -10).setFrame(136);
                addProjectile(ProjectileType.MISSILE_REAR, dmg, speed * 1.25, speed, 10, -10).setFrame(151);
                break;
            case 2:
                addProjectile(ProjectileType.MISSILE_REAR, dmg, -speed * 1.2, speed, -10, -5).setFrame(136);
                addProjectile(ProjectileType.MISSILE_REAR, dmg, speed * 1.2, speed, 10, -5).setFrame(151);

                addProjectile(ProjectileType.MISSILE_REAR, 100L, dmg, -speed * 1.4, speed, -10, -15).setFrame(137);
                addProjectile(ProjectileType.MISSILE_REAR, 100L, dmg, speed * 1.4, speed, 10, -15).setFrame(152);
                break;
            case 3:
                addProjectile(ProjectileType.MISSILE_REAR, dmg, -speed * 1.2, speed, -10, -5).setFrame(136);
                addProjectile(ProjectileType.MISSILE_REAR, dmg, speed * 1.2, speed, 10, -5).setFrame(151);

                addProjectile(ProjectileType.MISSILE_REAR, 100L, dmg, -speed * 1.4, speed, -10, -15).setFrame(137);
                addProjectile(ProjectileType.MISSILE_REAR, 100L, dmg, speed * 1.4, speed, 10, -15).setFrame(152);

                if (side)
                {
                    addProjectile(ProjectileType.MISSILE_REAR, 200L, dmg, -speed * 1.5, speed, -10, -20).setFrame(138);
                }
                else
                {
                    addProjectile(ProjectileType.MISSILE_REAR, 200L, dmg, speed * 1.5, speed, 10, -20).setFrame(153);
                }
                side = !side;
                break;
            case 4:
                addProjectile(ProjectileType.MISSILE_REAR, dmg, -speed * 1.2, speed, -10, -5).setFrame(137);
                addProjectile(ProjectileType.MISSILE_REAR, dmg, speed * 1.2, speed, 10, -5).setFrame(152);

                addProjectile(ProjectileType.MISSILE_REAR, dmg, -speed * 1.4, speed, -10, -15).setFrame(137);
                addProjectile(ProjectileType.MISSILE_REAR, dmg, speed * 1.4, speed, 10, -15).setFrame(152);
                break;
            case 5:
                addProjectile(ProjectileType.MISSILE_REAR, 50L, dmg, -speed * 1.1, speed, -10, 0).setFrame(138);
                addProjectile(ProjectileType.MISSILE_REAR, 25L, dmg, -speed * 1.2, speed, -10, -10).setFrame(138);
                addProjectile(ProjectileType.MISSILE_REAR, dmg, -speed * 1.3, speed, -10, -20).setFrame(138);

                addProjectile(ProjectileType.MISSILE_REAR, 50L, dmg, speed * 1.1, speed, 10, 0).setFrame(153);
                addProjectile(ProjectileType.MISSILE_REAR, 25L, dmg, speed * 1.2, speed, 10, -10).setFrame(153);
                addProjectile(ProjectileType.MISSILE_REAR, dmg, speed * 1.3, speed, 10, -20).setFrame(153);
                break;
            default:
                break;
        }
    }
}
