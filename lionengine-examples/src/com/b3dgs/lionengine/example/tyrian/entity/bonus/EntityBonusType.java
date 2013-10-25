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

import com.b3dgs.lionengine.example.tyrian.effect.EffectType;
import com.b3dgs.lionengine.game.ObjectType;
import com.b3dgs.lionengine.game.ObjectTypeUtility;

/**
 * List of bonus types.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public enum EntityBonusType implements ObjectType
{
    /*
     * Weapon
     */

    /** Pulse cannon. */
    PULSE_CANNON(PulseCannon.class),
    /** Hyper Pulse. */
    HYPER_PULSE(HyperPulse.class),

    /*
     * Coin.
     */

    /** Coin 10. */
    COIN10(Coin10.class, EffectType.COIN10),
    /** Coin 25. */
    COIN25(Coin25.class, EffectType.COIN25),
    /** Coin 50. */
    COIN50(Coin50.class, EffectType.COIN50),
    /** Coin 75. */
    COIN75(Coin75.class, EffectType.COIN75);

    /** Class target. */
    private final Class<?> target;
    /** Path name. */
    private final String pathName;
    /** Effect. */
    private final EffectType effect;

    /**
     * Constructor.
     * 
     * @param target The target class.
     */
    private EntityBonusType(Class<?> target)
    {
        this(target, null);
    }

    /**
     * Constructor.
     * 
     * @param target The target class.
     * @param effect The effect type.
     */
    private EntityBonusType(Class<?> target, EffectType effect)
    {
        this.target = target;
        pathName = ObjectTypeUtility.getPathName(this);
        this.effect = effect;
    }

    /**
     * Get the effect type.
     * 
     * @return The effect type.
     */
    public EffectType getEffect()
    {
        return effect;
    }

    /*
     * ObjectType
     */

    @Override
    public Class<?> getTargetClass()
    {
        return target;
    }

    @Override
    public String getPathName()
    {
        return pathName;
    }
}
