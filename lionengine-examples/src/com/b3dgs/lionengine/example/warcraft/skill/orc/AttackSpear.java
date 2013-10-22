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
package com.b3dgs.lionengine.example.warcraft.skill.orc;

import com.b3dgs.lionengine.example.warcraft.entity.HandlerEntity;
import com.b3dgs.lionengine.example.warcraft.skill.Attack;
import com.b3dgs.lionengine.example.warcraft.skill.SetupSkill;
import com.b3dgs.lionengine.example.warcraft.skill.SkillType;

/**
 * Sword attack skill.
 */
public final class AttackSpear
        extends Attack
{
    /**
     * Constructor.
     * 
     * @param setup The setup skill reference.
     * @param handler The handler reference.
     */
    public AttackSpear(SetupSkill setup, HandlerEntity handler)
    {
        super(SkillType.ATTACK_SPEAR, setup, handler);
    }
}
