/*
 * Copyright (C) 2013-2014 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.example.game.strategy.skills.entity;

import com.b3dgs.lionengine.example.game.strategy.skills.skill.BuildBarracksOrc;
import com.b3dgs.lionengine.example.game.strategy.skills.skill.BuildingStandardOrc;
import com.b3dgs.lionengine.example.game.strategy.skills.skill.CancelOrc;
import com.b3dgs.lionengine.example.game.strategy.skills.skill.MoveOrc;
import com.b3dgs.lionengine.example.game.strategy.skills.skill.StopOrc;

/**
 * Peon implementation.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public final class Peon
        extends UnitWorker
{
    /**
     * Constructor.
     * 
     * @param setup The setup reference.
     */
    public Peon(SetupEntity setup)
    {
        super(setup);
        addSkill(setup.factoryEntity, 0, MoveOrc.class, 0);
        addSkill(setup.factoryEntity, 0, StopOrc.class, 1);
        addSkill(setup.factoryEntity, 0, BuildingStandardOrc.class, 2);
        addSkill(setup.factoryEntity, 1, BuildBarracksOrc.class, 0);
        addSkill(setup.factoryEntity, 1, CancelOrc.class, 1);
    }
}
