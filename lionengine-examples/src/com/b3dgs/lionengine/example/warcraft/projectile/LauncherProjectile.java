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
package com.b3dgs.lionengine.example.warcraft.projectile;

import com.b3dgs.lionengine.example.warcraft.Context;
import com.b3dgs.lionengine.example.warcraft.entity.Entity;
import com.b3dgs.lionengine.example.warcraft.type.TypeProjectile;
import com.b3dgs.lionengine.example.warcraft.weapon.Weapon;
import com.b3dgs.lionengine.game.projectile.LauncherProjectileGame;

/**
 * Launcher base implementation.
 */
public final class LauncherProjectile
        extends LauncherProjectileGame<TypeProjectile, Entity, Weapon, Projectile>
{
    /** Type projectile. */
    private final TypeProjectile type;
    /** The projectile frame. */
    private int frame;

    /**
     * Constructor.
     * 
     * @param type The projectile type.
     * @param context The context reference.
     */
    public LauncherProjectile(TypeProjectile type, Context context)
    {
        super(context.factoryProjectile, context.handlerProjectile);
        this.type = type;
    }

    /**
     * Set the projectile frame.
     * 
     * @param frame The projectile frame.
     */
    public void setFrame(int frame)
    {
        this.frame = frame;
    }

    /*
     * LauncherProjectileGame
     */

    @Override
    protected void launchProjectile(Weapon owner)
    {
        // Nothing to do
    }

    @Override
    protected void launchProjectile(Weapon owner, Entity target)
    {
        addProjectile(type, owner.getAttackDamages(), frame, target, 2.5, 0, 0);
    }
}
