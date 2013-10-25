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
package com.b3dgs.lionengine.example.tyrian.entity.bonus;

import com.b3dgs.lionengine.core.Media;
import com.b3dgs.lionengine.example.tyrian.AppTyrian;
import com.b3dgs.lionengine.example.tyrian.effect.FactoryEffect;
import com.b3dgs.lionengine.example.tyrian.effect.HandlerEffect;
import com.b3dgs.lionengine.example.tyrian.weapon.FactoryWeapon;
import com.b3dgs.lionengine.game.FactoryObjectGame;

/**
 * Factory entity bonus.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public final class FactoryEntityBonus
        extends FactoryObjectGame<EntityBonusType, SetupEntityBonus, Bonus>
{
    /** Factory effect. */
    private final FactoryEffect factoryEffect;
    /** factory weapon. */
    private final FactoryWeapon factoryWeapon;
    /** Handler effect. */
    private final HandlerEffect handlerEffect;

    /**
     * Constructor.
     * 
     * @param factoryEffect The effect factory reference.
     * @param factoryWeapon The weapon factory reference.
     * @param handlerEffect The handler effect reference.
     */
    public FactoryEntityBonus(FactoryEffect factoryEffect, FactoryWeapon factoryWeapon, HandlerEffect handlerEffect)
    {
        super(EntityBonusType.class, AppTyrian.BONUS_DIR);
        this.factoryEffect = factoryEffect;
        this.factoryWeapon = factoryWeapon;
        this.handlerEffect = handlerEffect;
        load();
    }

    /*
     * FactoryObjectGame
     */

    @Override
    protected SetupEntityBonus createSetup(EntityBonusType type, Media config)
    {
        return new SetupEntityBonus(config, type, factoryEffect, factoryWeapon, handlerEffect);
    }
}
