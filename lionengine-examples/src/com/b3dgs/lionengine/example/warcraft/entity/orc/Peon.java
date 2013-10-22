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
package com.b3dgs.lionengine.example.warcraft.entity.orc;

import com.b3dgs.lionengine.example.warcraft.Context;
import com.b3dgs.lionengine.example.warcraft.entity.EntityType;
import com.b3dgs.lionengine.example.warcraft.entity.UnitWorker;
import com.b3dgs.lionengine.example.warcraft.skill.SkillType;

/**
 * Peon implementation.
 */
public final class Peon
        extends UnitWorker
{
    /**
     * Constructor.
     * 
     * @param context The context reference.
     */
    public Peon(Context context)
    {
        super(EntityType.PEON, context);
        addSkill(context, 0, SkillType.MOVE_ORC, 0);
        addSkill(context, 0, SkillType.STOP_ORC, 1);
        addSkill(context, 0, SkillType.BUILDING_STANDARD_ORC, 2);
        addSkill(context, 1, SkillType.BUILD_FARM_ORC, 0);
        addSkill(context, 1, SkillType.BUILD_BARRACKS_ORC, 1);
        addSkill(context, 1, SkillType.CANCEL_ORC, 2);
    }
}
