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

import com.b3dgs.lionengine.core.Media;
import com.b3dgs.lionengine.game.FactoryGame;
import com.b3dgs.lionengine.game.ObjectType;
import com.b3dgs.lionengine.game.SetupSurfaceGame;

/**
 * Handle projectile factory. Projectiles are instantiated from a list. This way it is easy to define different kind of
 * projectile.
 * 
 * @param <T> The enum containing all projectile type.
 * @param <P> The projectile type used.
 * @param <S> setup entity type.
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public abstract class FactoryProjectileGame<T extends Enum<T> & ObjectType, P extends ProjectileGame<?, ?>, S extends SetupSurfaceGame>
        extends FactoryGame<T, S>
{
    /** Entities folder. */
    private final String folder;

    /**
     * Constructor.
     * 
     * @param keyType The class of the enum type defined.
     * @param types The projectile types.
     * @param folder The projectiles folder.
     */
    public FactoryProjectileGame(Class<T> keyType, T[] types, String folder)
    {
        super(keyType, types);
        this.folder = folder;
    }

    /**
     * Create a projectile.
     * 
     * @param type The projectile enum.
     * @return The created projectile.
     */
    public abstract P createProjectile(T type);

    /**
     * Create a setup from its media.
     * 
     * @param type The projectile type.
     * @param config The setup media config file.
     * @return The setup instance.
     */
    protected abstract S createSetup(T type, Media config);

    /*
     * FactoryGame
     */

    @Override
    protected S createSetup(T type)
    {
        final Media config = Media.get(folder, type.asPathName() + ".xml");
        return createSetup(type, config);
    }
}
