/*
 * Copyright (C) 2013-2015 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.example.game.strategy.ability.entity;

import com.b3dgs.lionengine.core.Media;
import com.b3dgs.lionengine.example.game.strategy.ability.weapon.Axe;
import com.b3dgs.lionengine.example.game.strategy.ability.weapon.FactoryWeapon;
import com.b3dgs.lionengine.example.game.strategy.ability.weapon.Weapon;
import com.b3dgs.lionengine.game.object.Services;
import com.b3dgs.lionengine.game.object.SetupSurface;

/**
 * Grunt implementation.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public final class Grunt
        extends UnitAttacker
{
    /** Class media. */
    public static final Media MEDIA = Entity.getConfig(Grunt.class);

    /**
     * Constructor.
     * 
     * @param setup The setup reference.
     */
    public Grunt(SetupSurface setup)
    {
        super(setup);
    }

    /*
     * UnitAttacker
     */

    @Override
    public void prepareEntity(Services context)
    {
        super.prepareEntity(context);
        final Weapon weapon = context.get(FactoryWeapon.class).create(Axe.MEDIA);
        addWeapon(weapon, 0);
    }
}
