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

import com.b3dgs.lionengine.core.UtilityMath;
import com.b3dgs.lionengine.example.tyrian.Sfx;
import com.b3dgs.lionengine.example.tyrian.entity.Entity;
import com.b3dgs.lionengine.example.tyrian.projectile.FactoryProjectile;
import com.b3dgs.lionengine.example.tyrian.projectile.HandlerProjectile;
import com.b3dgs.lionengine.example.tyrian.projectile.ProjectileType;
import com.b3dgs.lionengine.example.tyrian.weapon.Weapon;

/**
 * Machine gun implementation.
 */
public final class MachineGun
        extends Weapon
{
    /** Offset x. */
    private int offsetX;

    /**
     * @see Weapon#Weapon(FactoryProjectile, HandlerProjectile)
     */
    public MachineGun(FactoryProjectile factory, HandlerProjectile handler)
    {
        super(factory, handler);
        setOffsetY(-5);
    }

    /*
     * Weapon
     */

    @Override
    protected void launchProjectile(Entity owner)
    {
        int dmg;
        final int speed = 12;
        Sfx.WEAPON_MACHINE_GUN.play();
        switch (level.getCurrent())
        {
            case 0:
                dmg = 60;
                offsetX += 80.0;
                setRate(100);
                addProjectile(ProjectileType.BULLET, dmg, 0, speed, (int) (UtilityMath.sin(offsetX) * 3), 0);
                break;
            case 1:
                dmg = 60;
                offsetX += 90.0;
                setRate(90);
                addProjectile(ProjectileType.BULLET, dmg, 0, speed, (int) (UtilityMath.sin(offsetX) * 4), 0);
                break;
            case 2:
                dmg = 60;
                offsetX += 100.0;
                setRate(80);
                addProjectile(ProjectileType.BULLET, dmg, 0, speed, (int) (UtilityMath.sin(offsetX) * 5), 0);
                break;
            case 3:
                dmg = 60;
                offsetX += 110.0;
                setRate(70);
                addProjectile(ProjectileType.BULLET, dmg, 0, speed, (int) (UtilityMath.sin(offsetX) * 4), 0).setFrame(
                        125);
                break;
            case 4:
                dmg = 60;
                offsetX += 120.0;
                setRate(60);
                addProjectile(ProjectileType.BULLET, dmg, 0, speed, (int) (UtilityMath.sin(offsetX) * 6), 0).setFrame(
                        125);
                break;
            case 5:
                dmg = 60;
                offsetX += 60.0;
                setRate(50);
                addProjectile(ProjectileType.BULLET, dmg, 0, speed, (int) (UtilityMath.sin(offsetX) * 8), 0).setFrame(
                        125);
                break;
            default:
                break;
        }
    }
}
