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
package com.b3dgs.lionengine.example.warcraft.launcher;

import com.b3dgs.lionengine.example.warcraft.projectile.ProjectileType;
import com.b3dgs.lionengine.game.ObjectType;
import com.b3dgs.lionengine.game.ObjectTypeUtility;

/**
 * List of launcher types.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public enum LauncherType implements ObjectType
{
    /** Bow. */
    BOW(Bow.class, ProjectileType.ARROW),
    /** Spear. */
    SPEAR_LAUNCHER(SpearLauncher.class, ProjectileType.SPEAR);

    /** Target class. */
    private final Class<?> target;
    /** Path name. */
    private final String path;
    /** Projectile type. */
    private final ProjectileType type;

    /**
     * Constructor.
     * 
     * @param target The class target.
     * @param type The projectile type.
     */
    private LauncherType(Class<?> target, ProjectileType type)
    {
        this.target = target;
        path = ObjectTypeUtility.getPathName(this);
        this.type = type;
    }

    /**
     * Get the projectile type.
     * 
     * @return The projectile type.
     */
    public ProjectileType getType()
    {
        return type;
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
        return path;
    }
}
