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
package com.b3dgs.lionengine.game.rts.ability.attacker;

import com.b3dgs.lionengine.game.FactoryGame;
import com.b3dgs.lionengine.game.SetupGame;
import com.b3dgs.lionengine.game.rts.EntityRts;

/**
 * @param <T> The weapon enum type used.
 * @param <E> The entity type used.
 * @param <W> The weapon type used.
 * @param <A> The attacker type used.
 */
public abstract class FactoryWeaponRts<T extends Enum<T>, E extends EntityRts, W extends WeaponServices<E>, A extends AttackerUsedServices<E>>
        extends FactoryGame<T, SetupGame>
{
    /**
     * Create a new entity factory.
     * 
     * @param keyType The class of the enum type defined.
     */
    public FactoryWeaponRts(Class<T> keyType)
    {
        super(keyType);
    }

    /**
     * Get the entity instance from its id. It is recommended to use a switch on the id, and throw an exception for the
     * default case (instead of returning a <code>null</code> value).
     * 
     * @param id The entity id (as enumeration).
     * @param user The weapon user.
     * @return The entity instance.
     */
    public abstract W createWeapon(T id, A user);
}
