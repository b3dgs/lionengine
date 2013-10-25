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
package com.b3dgs.lionengine.example.tyrian.entity.dynamic;

import com.b3dgs.lionengine.game.ObjectType;
import com.b3dgs.lionengine.game.ObjectTypeUtility;

/**
 * List of dynamic entity types.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public enum EntityDynamicType implements ObjectType
{
    /** Meteor little 1. */
    METEOR_LITTLE_1(MeteorLittle1.class),
    /** Meteor little 2. */
    METEOR_LITTLE_2(MeteorLittle2.class),
    /** Meteor medium 1. */
    METEOR_MEDIUM_1(MeteorMedium1.class),
    /** Meteor medium 2. */
    METEOR_MEDIUM_2(MeteorMedium2.class),
    /** Meteor big. */
    METEOR_BIG(MeteorBig.class);

    /** Class target. */
    private final Class<?> target;
    /** Path name. */
    private final String pathName;

    /**
     * Constructor.
     * 
     * @param target The target class.
     */
    private EntityDynamicType(Class<?> target)
    {
        this.target = target;
        pathName = ObjectTypeUtility.getPathName(this);
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
