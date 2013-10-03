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
package com.b3dgs.lionengine.game.projectile;

/**
 * Handle projectile factory. Projectiles are instantiated from a list. This way it is easy to define different kind of
 * projectile.
 * 
 * @param <T> The enum containing all launchers type.
 * @param <L> The launcher type used.
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public abstract class FactoryLauncherGame<T extends Enum<T>, L extends LauncherProjectileGame<?, ?, ?, ?>>
{
    /**
     * Constructor.
     */
    public FactoryLauncherGame()
    {
        // Nothing to do
    }

    /**
     * Create a projectile.
     * 
     * @param type The launcher enum.
     * @return The created launcher.
     */
    public abstract L createLauncher(T type);
}
