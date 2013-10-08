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
import com.b3dgs.lionengine.game.projectile.FactoryLauncherGame;

/**
 * Launcher factory.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 * @see com.b3dgs.lionengine.example.game.factory
 */
final class FactoryLauncher
        extends FactoryLauncherGame<LauncherType, Launcher>
{
    /** Factory projectile. */
    private final FactoryProjectile factory;
    /** Handler projectile. */
    private final HandlerProjectile handler;

    /**
     * Constructor.
     * 
     * @param factory The factory reference.
     * @param handler The handler reference.
     */
    FactoryLauncher(FactoryProjectile factory, HandlerProjectile handler)
    {
        super();
        this.factory = factory;
        this.handler = handler;
    }

    /*
     * FactoryProjectileGame
     */

    @Override
    public Launcher createLauncher(LauncherType type)
    {
        switch (type)
        {
            case PULSE_CANON:
                return new PulseCanon(factory, handler);
            default:
                throw new LionEngineException("Unknown type: " + type);
        }
    }
}
