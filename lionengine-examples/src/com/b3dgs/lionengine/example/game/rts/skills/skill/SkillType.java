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
package com.b3dgs.lionengine.example.game.rts.skills.skill;

import com.b3dgs.lionengine.game.ObjectType;
import com.b3dgs.lionengine.game.ObjectTypeUtility;

/**
 * List of skill types.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public enum SkillType implements ObjectType
{
    /** Move orc skill. */
    MOVE_ORC(MoveOrc.class),
    /** Build orc skill. */
    BUILDING_STANDARD_ORC(BuildingStandardOrc.class),
    /** Stop orc skill. */
    STOP_ORC(StopOrc.class),
    /** Cancel orc skill. */
    CANCEL_ORC(CancelOrc.class),
    /** Build barracks orc skill. */
    BUILD_BARRACKS_ORC(BuildBarracksOrc.class),
    /** Produce peon skill. */
    PRODUCE_PEON(ProducePeon.class);

    /** Class target. */
    private final Class<?> target;
    /** Path name. */
    private final String path;

    /**
     * Constructor.
     * 
     * @param target The target class.
     */
    private SkillType(Class<?> target)
    {
        this.target = target;
        path = ObjectTypeUtility.getPathName(this);
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
