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

import com.b3dgs.lionengine.game.FactoryGame;
import com.b3dgs.lionengine.game.SetupSurfaceGame;

/**
 * Handle projectile factory. Projectiles are instantiated from a list. This way it is easy to define different kind of
 * projectile.
 * 
 * @param <T> enum containing all projectile type.
 * @param <P> The projectile type used.
 * @param <S> setup entity type.
 */
public abstract class FactoryProjectileGame<T extends Enum<T>, P extends ProjectileGame<?, ?>, S extends SetupSurfaceGame>
        extends FactoryGame<T, S>
{
    /**
     * Create a new projectile factory.
     * 
     * @param keyType The class of the enum type defined.
     */
    public FactoryProjectileGame(Class<T> keyType)
    {
        super(keyType);
    }

    /**
     * Create a projectile.
     * 
     * @param type The projectile enum.
     * @param id The projectile id (when a projectile is destroyed, all projectiles with this id are also destroyed).
     *            Can be -1 to ignore it.
     * @param frame The projectile tile number (from surface).
     * @return The created projectile.
     */
    public abstract P createProjectile(T type, int id, int frame);
}
