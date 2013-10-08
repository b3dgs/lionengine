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
package com.b3dgs.lionengine.example.e_shmup.c_tyrian.projectile;

import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.core.Media;
import com.b3dgs.lionengine.example.e_shmup.c_tyrian.effect.FactoryEffect;
import com.b3dgs.lionengine.example.e_shmup.c_tyrian.effect.HandlerEffect;
import com.b3dgs.lionengine.game.SetupSurfaceGame;
import com.b3dgs.lionengine.game.projectile.FactoryProjectileGame;

/**
 * Factory projectile.
 */
public final class FactoryProjectile
        extends FactoryProjectileGame<ProjectileType, Projectile, SetupSurfaceGame>
{
    /** Weapon surfaces. */
    private final SetupSurfaceGame setup;
    /** Factory effect. */
    private final FactoryEffect factoryEffect;
    /** Handler effect. */
    private final HandlerEffect handlerEffect;

    /**
     * Constructor.
     * 
     * @param factoryEffect The factory effect reference.
     * @param handlerEffect The handler effect reference.
     */
    public FactoryProjectile(FactoryEffect factoryEffect, HandlerEffect handlerEffect)
    {
        super(ProjectileType.class);
        this.factoryEffect = factoryEffect;
        this.handlerEffect = handlerEffect;
        setup = new SetupSurfaceGame(Media.get("sprites", "weapons.xml"));
        loadAll(ProjectileType.values());
    }

    /*
     * FactoryProjectileGame
     */

    @Override
    public Projectile createProjectile(ProjectileType type)
    {
        switch (type)
        {
            case BULLET:
                return new Bullet(getSetup(type));
            case MISSILE:
                return new Missile(factoryEffect, handlerEffect, getSetup(type));
            default:
                throw new LionEngineException("Unknown type: " + type);
        }
    }

    @Override
    protected SetupSurfaceGame createSetup(ProjectileType id)
    {
        return setup;
    }
}
