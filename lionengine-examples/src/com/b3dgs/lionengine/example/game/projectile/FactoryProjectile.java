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
package com.b3dgs.lionengine.example.game.projectile;

import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.core.Media;
import com.b3dgs.lionengine.game.SetupSurfaceGame;
import com.b3dgs.lionengine.game.projectile.FactoryProjectileGame;

/**
 * Projectile factory.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 * @see com.b3dgs.lionengine.example.game.factory
 */
final class FactoryProjectile
        extends FactoryProjectileGame<ProjectileType, Projectile, SetupSurfaceGame>
{
    /**
     * Constructor.
     */
    FactoryProjectile()
    {
        super(ProjectileType.class, ProjectileType.values(), "");
    }

    /*
     * FactoryProjectileGame
     */

    @Override
    public Projectile createProjectile(ProjectileType type)
    {
        switch (type)
        {
            case PULSE:
                return new Pulse(getSetup(type));
            default:
                throw new LionEngineException("Unknown type: " + type);
        }
    }

    @Override
    protected SetupSurfaceGame createSetup(ProjectileType type, Media config)
    {
        return new SetupSurfaceGame(config);
    }
}
