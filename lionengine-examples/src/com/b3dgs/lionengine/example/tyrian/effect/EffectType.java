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
package com.b3dgs.lionengine.example.tyrian.effect;

import com.b3dgs.lionengine.game.ObjectType;
import com.b3dgs.lionengine.game.ObjectTypeUtility;

/**
 * List of effect types.
 */
public enum EffectType implements ObjectType
{
    /** Smoke. */
    SMOKE(Smoke.class),
    /** Bullet hit. */
    BULLET_HIT(BulletHit.class),
    /** Explode1. */
    EXPLODE1(Explode1.class),
    /** Explode2. */
    EXPLODE2(Explode2.class),
    /** Explode3. */
    EXPLODE3(Explode3.class),
    /** Coin10. */
    COIN10(Coin10.class),
    /** Coin25. */
    COIN25(Coin25.class),
    /** Coin50. */
    COIN50(Coin50.class),
    /** Coin75. */
    COIN75(Coin75.class);

    /** Class target. */
    private final Class<?> target;
    /** Path name. */
    private final String pathName;

    /**
     * Constructor.
     * 
     * @param target The target class.
     */
    private EffectType(Class<?> target)
    {
        this.target = target;
        pathName = ObjectTypeUtility.asPathName(this);
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
