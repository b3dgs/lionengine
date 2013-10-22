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
package com.b3dgs.lionengine.example.game.rts.ability.weapon;

import com.b3dgs.lionengine.core.Media;
import com.b3dgs.lionengine.example.game.rts.ability.Context;
import com.b3dgs.lionengine.example.game.rts.ability.entity.UnitAttacker;
import com.b3dgs.lionengine.game.FactoryObjectGame;
import com.b3dgs.lionengine.game.SetupGame;

/**
 * Weapons factory.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public final class FactoryWeapon
        extends FactoryObjectGame<WeaponType, SetupGame, Weapon>
{
    /** Weapons path. */
    private static final String WEAPONS_DIR = "weapons";
    /** Context reference. */
    private Context context;

    /**
     * Constructor.
     */
    public FactoryWeapon()
    {
        super(WeaponType.class, WeaponType.values(), FactoryWeapon.WEAPONS_DIR);
        load();
    }

    /**
     * Set the context.
     * 
     * @param context The context
     */
    public void setContext(Context context)
    {
        this.context = context;
    }

    /**
     * Create a weapon from its type.
     * 
     * @param type The weapon type.
     * @param user The user reference.
     * @return The weapon instance.
     */
    public Weapon create(WeaponType type, UnitAttacker user)
    {
        return create(type, user, context);
    }

    /*
     * FactoryObjectGame
     */

    @Override
    public <W extends Weapon> W create(WeaponType id)
    {
        return null;
    }

    @Override
    protected SetupGame createSetup(WeaponType type, Media config)
    {
        return new SetupGame(config);
    }
}
