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
 * Wave cannon implementation.
 */
public final class WaveCannonRear
        extends Weapon
{
    /** Id 1. */
    private int id1;
    /** id 2. */
    private int id2;

    /**
     * @see Weapon#Weapon(SetupGame, FactoryProjectile, HandlerProjectile)
     */
    public WaveCannonRear(SetupGame setup, FactoryProjectile factory, HandlerProjectile handler)
    {
        super(setup, factory, handler);
        setOffsetY(-15);
        setRate(250);
    }

    /*
     * Weapon
     */

    @Override
    protected void launchProjectile(Entity owner)
    {
        final int dmg = 60;
        final int speed = 11;
        Sfx.WEAPON_WAVE.play();
        switch (level.getCurrent())
        {
            case 0:
                addProjectile(ProjectileType.WAVE, dmg, -speed, 1.5, -10, 0).setFrame(39);
                addProjectile(ProjectileType.WAVE, dmg, speed, 1.5, 10, 0).setFrame(54);
                break;
            case 1:
                addProjectile(ProjectileType.WAVE, dmg, -speed, speed, -10, 0).setFrame(70);
                addProjectile(ProjectileType.WAVE, dmg, speed, speed, 10, 0).setFrame(85);
                break;
            case 2:
                addProjectile(ProjectileType.WAVE, dmg, -speed, speed * 0.4, -10, 5).setFrame(69);
                addProjectile(ProjectileType.WAVE, dmg, speed, speed * 0.4, 10, 5).setFrame(84);

                addProjectile(ProjectileType.WAVE, dmg, -speed, -speed * 0.4, -10, -5).setFrame(99);
                addProjectile(ProjectileType.WAVE, dmg, speed, -speed * 0.4, 10, -5).setFrame(114);
                break;
            case 3:
                addProjectile(ProjectileType.WAVE, dmg, -speed, 1, -10, 0).setFrame(40);
                addProjectile(ProjectileType.WAVE, dmg, speed, 1, 10, 0).setFrame(55);

                addProjectile(ProjectileType.WAVE, dmg, -speed, speed * 0.4, -10, 5).setFrame(69);
                addProjectile(ProjectileType.WAVE, dmg, speed, speed * 0.4, 10, 5).setFrame(84);

                addProjectile(ProjectileType.WAVE, dmg, -speed, -speed * 0.4, -10, -5).setFrame(99);
                addProjectile(ProjectileType.WAVE, dmg, speed, -speed * 0.4, 10, -5).setFrame(114);

                addProjectile(ProjectileType.WAVE, dmg, 0, -speed, 0, -10).setFrame(24);
                break;
            case 4:
                id1++;
                addProjectile(ProjectileType.WAVE, id1, dmg, -speed, 1, -10, 6).setFrame(12);
                addProjectile(ProjectileType.WAVE, id1, dmg, -speed, 1, -10, -6).setFrame(27);

                id2++;
                addProjectile(ProjectileType.WAVE, id2, dmg, speed, 1, 10, 6).setFrame(42);
                addProjectile(ProjectileType.WAVE, id2, dmg, speed, 1, 10, -6).setFrame(57);

                addProjectile(ProjectileType.WAVE, dmg, -speed * 0.25, -speed, -3, -5).setFrame(101);
                addProjectile(ProjectileType.WAVE, dmg, speed * 0.25, -speed, 3, -5).setFrame(116);
                break;
            case 5:
                addProjectile(ProjectileType.WAVE, dmg, -speed, speed * 0.4, -10, 5).setFrame(69);
                addProjectile(ProjectileType.WAVE, dmg, speed, speed * 0.4, 10, 5).setFrame(84);

                id1++;
                addProjectile(ProjectileType.WAVE, id1, dmg, -speed, 1, -10, 6).setFrame(12);
                addProjectile(ProjectileType.WAVE, id1, dmg, -speed, 1, -10, -6).setFrame(27);

                id2++;
                addProjectile(ProjectileType.WAVE, id2, dmg, speed, 1, 10, 6).setFrame(42);
                addProjectile(ProjectileType.WAVE, id2, dmg, speed, 1, 10, -6).setFrame(57);

                addProjectile(ProjectileType.WAVE, dmg, -speed, -speed * 0.4, -10, -5).setFrame(99);
                addProjectile(ProjectileType.WAVE, dmg, speed, -speed * 0.4, 10, -5).setFrame(114);

                addProjectile(ProjectileType.WAVE, dmg, -speed * 0.25, -speed, -3, -5).setFrame(101);
                addProjectile(ProjectileType.WAVE, dmg, speed * 0.25, -speed, 3, -5).setFrame(116);
                break;
            default:
                break;
        }
    }
}
