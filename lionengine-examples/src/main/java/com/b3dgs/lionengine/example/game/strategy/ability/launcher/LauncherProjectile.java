/*
 * Copyright (C) 2013-2014 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.example.game.strategy.ability.launcher;

import com.b3dgs.lionengine.core.Core;
import com.b3dgs.lionengine.core.Media;
import com.b3dgs.lionengine.example.game.strategy.ability.entity.Entity;
import com.b3dgs.lionengine.example.game.strategy.ability.projectile.Projectile;
import com.b3dgs.lionengine.example.game.strategy.ability.weapon.Weapon;
import com.b3dgs.lionengine.game.ContextGame;
import com.b3dgs.lionengine.game.FactoryObjectGame;
import com.b3dgs.lionengine.game.SetupGame;
import com.b3dgs.lionengine.game.projectile.LauncherProjectileGame;

/**
 * Launcher base implementation.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public class LauncherProjectile
        extends LauncherProjectileGame<Entity, Weapon, Projectile>
{
    /**
     * Get an entity configuration file.
     * 
     * @param type The config associated class.
     * @return The media config.
     */
    protected static Media getConfig(Class<? extends LauncherProjectile> type)
    {
        return Core.MEDIA.create(FactoryLauncher.LAUNCHER_DIR, type.getSimpleName() + "."
                + FactoryObjectGame.FILE_DATA_EXTENSION);
    }

    /** Type projectile. */
    private final Media type;

    /**
     * Constructor.
     * 
     * @param setup The setup reference.
     * @param projectileType The projectile type used.
     */
    public LauncherProjectile(SetupGame setup, Media projectileType)
    {
        super(setup);
        type = projectileType;
    }

    /*
     * LauncherProjectileGame
     */

    @Override
    protected void prepareProjectile(ContextGame context)
    {
        // Nothing to do
    }

    @Override
    protected void launchProjectile(Weapon owner)
    {
        // Nothing to do
    }

    @Override
    protected void launchProjectile(Weapon owner, Entity target)
    {
        final Projectile projectile = addProjectile(type, owner.getAttackDamages(), target, 2.5, 0, 0);
        projectile.setFrame(owner.getFrame());
    }
}
