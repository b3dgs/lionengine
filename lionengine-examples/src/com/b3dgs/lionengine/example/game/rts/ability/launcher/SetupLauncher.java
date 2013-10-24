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
package com.b3dgs.lionengine.example.game.rts.ability.launcher;

import com.b3dgs.lionengine.core.Media;
import com.b3dgs.lionengine.example.game.rts.ability.projectile.FactoryProjectile;
import com.b3dgs.lionengine.example.game.rts.ability.projectile.HandlerProjectile;
import com.b3dgs.lionengine.example.game.rts.ability.projectile.ProjectileType;
import com.b3dgs.lionengine.game.SetupGame;

/**
 * Setup launcher implementation.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public class SetupLauncher
        extends SetupGame
{
    /** Type. */
    final ProjectileType type;
    /** Factory projectile. */
    final FactoryProjectile factoryProjectile;
    /** Handler projectile. */
    final HandlerProjectile handlerProjectile;

    /**
     * Constructor.
     * 
     * @param config The config media.
     * @param type The projectile type.
     * @param factoryProjectile The factory projectile.
     * @param handlerProjectile The handler projectile.
     */
    public SetupLauncher(Media config, LauncherType type, FactoryProjectile factoryProjectile,
            HandlerProjectile handlerProjectile)
    {
        super(config);
        this.type = type.getType();
        this.factoryProjectile = factoryProjectile;
        this.handlerProjectile = handlerProjectile;
    }
}
