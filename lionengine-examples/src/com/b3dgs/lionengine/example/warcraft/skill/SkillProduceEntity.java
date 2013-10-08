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
package com.b3dgs.lionengine.example.warcraft.skill;

import com.b3dgs.lionengine.example.warcraft.FactoryProduction;
import com.b3dgs.lionengine.example.warcraft.ProducibleEntity;
import com.b3dgs.lionengine.example.warcraft.entity.BuildingProducer;
import com.b3dgs.lionengine.example.warcraft.type.TypeEntity;
import com.b3dgs.lionengine.example.warcraft.type.TypeSkill;
import com.b3dgs.lionengine.game.purview.Configurable;
import com.b3dgs.lionengine.game.rts.ControlPanelModel;
import com.b3dgs.lionengine.game.rts.CursorRts;

/**
 * Skill build implementation.
 */
public class SkillProduceEntity
        extends Skill
{
    /** Production factory. */
    protected final FactoryProduction factoryProduction;
    /** Entity type to produce. */
    private final TypeEntity entity;
    /** The production cost gold. */
    private final int gold;
    /** The production cost wood. */
    private final int wood;

    /**
     * Constructor.
     * 
     * @param id The skill id.
     * @param setup The setup skill reference.
     * @param entity The entity type to produce.
     */
    protected SkillProduceEntity(TypeSkill id, SetupSkill setup, TypeEntity entity)
    {
        super(id, setup);
        this.entity = entity;
        factoryProduction = setup.factoryProduction;
        final Configurable config = factoryProduction.getConfig(TypeEntity.barracks_orc);
        gold = config.getDataInteger("gold", "cost");
        wood = config.getDataInteger("wood", "cost");
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
            final ProducibleEntity producible = factoryProduction.createProducible(entity, destX, destY);
            ((BuildingProducer) owner).addToProductionQueue(producible);
        }
    }

    @Override
    public String getDescription()
    {
        final StringBuilder description = new StringBuilder(super.getDescription());
        description.append(":     ").append(gold).append("      ").append(wood);
        return description.toString();
    }
}
