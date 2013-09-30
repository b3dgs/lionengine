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
package com.b3dgs.lionengine.example.d_rts.e_skills.skill;

import java.awt.Color;

import com.b3dgs.lionengine.example.d_rts.e_skills.Cursor;
import com.b3dgs.lionengine.example.d_rts.e_skills.CursorType;
import com.b3dgs.lionengine.example.d_rts.e_skills.EntityType;
import com.b3dgs.lionengine.example.d_rts.e_skills.FactoryProduction;
import com.b3dgs.lionengine.example.d_rts.e_skills.ProducibleEntity;
import com.b3dgs.lionengine.example.d_rts.e_skills.SkillType;
import com.b3dgs.lionengine.example.d_rts.e_skills.entity.UnitWorker;
import com.b3dgs.lionengine.game.purview.Configurable;
import com.b3dgs.lionengine.game.rts.ControlPanelModel;
import com.b3dgs.lionengine.game.rts.CursorRts;

/**
 * Build skill implementation.
 */
final class BuildBarracks
        extends Skill
{
    /** Production factory. */
    private final FactoryProduction factoryProduction;
    /** Production width in tile. */
    private final int width;
    /** Production height in tile. */
    private final int height;
    /** Cursor reference. */
    private final Cursor cursor;

    /**
     * Constructor.
     * 
     * @param setup The setup skill reference.
     * @param cursor The cursor reference.
     */
    BuildBarracks(SetupSkill setup, Cursor cursor)
    {
        super(SkillType.BUILD_BARRACKS_ORC, setup);
        this.cursor = cursor;
        factoryProduction = setup.factoryProduction;
        final Configurable config = factoryProduction.getConfig(EntityType.BARRACKS_ORC);
        width = config.getDataInteger("widthInTile", "size");
        height = config.getDataInteger("heightInTile", "size");
        setOrder(true);
    }

    /*
     * Skill
     */

    @Override
    public void action(ControlPanelModel<?> panel, CursorRts cursor)
    {
        if (owner instanceof UnitWorker)
        {
            final ProducibleEntity produce = factoryProduction.createProducible(EntityType.BARRACKS_ORC, destX, destY);
            ((UnitWorker) owner).addToProductionQueue(produce);
        }
    }

    @Override
    public void onClicked(ControlPanelModel<?> panel)
    {
        cursor.setType(CursorType.BOX);
        cursor.setBoxColor(Color.GREEN);
        cursor.setBoxSize(width, height);
    }
}
