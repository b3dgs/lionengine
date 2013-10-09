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

import com.b3dgs.lionengine.example.game.rts.skills.FactoryProduction;
import com.b3dgs.lionengine.example.game.rts.skills.ProducibleEntity;
import com.b3dgs.lionengine.example.game.rts.skills.entity.BuildingProducer;
import com.b3dgs.lionengine.example.game.rts.skills.entity.EntityType;
import com.b3dgs.lionengine.game.rts.ControlPanelModel;
import com.b3dgs.lionengine.game.rts.CursorRts;

/**
 * Produce peon implementation.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
final class ProducePeon
        extends Skill
{
    /** Production factory. */
    private final FactoryProduction factoryProduction;

    /**
     * Constructor.
     * 
     * @param setup The setup skill reference.
     */
    ProducePeon(SetupSkill setup)
    {
        super(SkillType.PRODUCE_PEON, setup);
        factoryProduction = setup.factoryProduction;
        setOrder(false);
    }

    /*
     * Skill
     */

    @Override
    public void action(ControlPanelModel<?> panel, CursorRts cursor)
    {
        if (owner instanceof BuildingProducer)
        {
            final ProducibleEntity producible = factoryProduction.createProducible(EntityType.PEON);
            ((BuildingProducer) owner).addToProductionQueue(producible);
        }
    }
}
