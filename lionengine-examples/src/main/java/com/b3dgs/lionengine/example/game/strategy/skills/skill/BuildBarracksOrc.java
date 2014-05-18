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
package com.b3dgs.lionengine.example.game.strategy.skills.skill;

import com.b3dgs.lionengine.ColorRgba;
import com.b3dgs.lionengine.example.game.strategy.skills.Cursor;
import com.b3dgs.lionengine.example.game.strategy.skills.CursorType;
import com.b3dgs.lionengine.example.game.strategy.skills.entity.BarracksOrc;
import com.b3dgs.lionengine.example.game.strategy.skills.entity.FactoryProduction;
import com.b3dgs.lionengine.example.game.strategy.skills.entity.ProducibleEntity;
import com.b3dgs.lionengine.example.game.strategy.skills.entity.UnitWorker;
import com.b3dgs.lionengine.game.purview.Configurable;
import com.b3dgs.lionengine.game.strategy.ControlPanelModel;
import com.b3dgs.lionengine.game.strategy.CursorStrategy;

/**
 * Build barracks implementation.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public final class BuildBarracksOrc
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
     */
    public BuildBarracksOrc(SetupSkill setup)
    {
        super(setup);
        cursor = setup.cursor;
        factoryProduction = setup.factoryProduction;
        final Configurable config = factoryProduction.getSetup(BarracksOrc.class).configurable;
        width = config.getDataInteger("widthInTile", "lionengine:tileSize");
        height = config.getDataInteger("heightInTile", "lionengine:tileSize");
        setOrder(true);
    }

    /*
     * Skill
     */

    @Override
    public void action(ControlPanelModel<?> panel, CursorStrategy cursor)
    {
        if (owner instanceof UnitWorker)
        {
            final ProducibleEntity produce = factoryProduction.create(BarracksOrc.class, destX, destY);
            ((UnitWorker) owner).addToProductionQueue(produce);
        }
    }

    @Override
    public void onClicked(ControlPanelModel<?> panel)
    {
        cursor.setType(CursorType.BOX);
        cursor.setBoxColor(ColorRgba.GREEN);
        cursor.setBoxSize(width, height);
    }
}
