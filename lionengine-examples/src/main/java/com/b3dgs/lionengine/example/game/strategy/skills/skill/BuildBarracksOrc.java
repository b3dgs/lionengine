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
import com.b3dgs.lionengine.core.Media;
import com.b3dgs.lionengine.example.game.strategy.skills.Cursor;
import com.b3dgs.lionengine.example.game.strategy.skills.CursorType;
import com.b3dgs.lionengine.example.game.strategy.skills.entity.BarracksOrc;
import com.b3dgs.lionengine.example.game.strategy.skills.entity.FactoryProduction;
import com.b3dgs.lionengine.example.game.strategy.skills.entity.ProducibleEntity;
import com.b3dgs.lionengine.example.game.strategy.skills.entity.UnitWorker;
import com.b3dgs.lionengine.game.ContextGame;
import com.b3dgs.lionengine.game.configurer.ConfigSize;
import com.b3dgs.lionengine.game.configurer.Configurer;
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
    /** Class media. */
    public static final Media MEDIA = Skill.getConfig(BuildBarracksOrc.class);

    /** Production factory. */
    private FactoryProduction factoryProduction;
    /** Production width in tile. */
    private int width;
    /** Production height in tile. */
    private int height;
    /** Cursor reference. */
    private Cursor cursor;

    /**
     * Constructor.
     * 
     * @param setup The setup skill reference.
     */
    public BuildBarracksOrc(SetupSkill setup)
    {
        super(setup);
        setOrder(true);
    }

    /*
     * Skill
     */

    @Override
    public void prepare(ContextGame context)
    {
        super.prepare(context);
        cursor = context.getService(Cursor.class);
        factoryProduction = context.getService(FactoryProduction.class);
        final Configurer configurer = factoryProduction.getSetup(BarracksOrc.MEDIA).getConfigurer();
        final ConfigSize sizeData = ConfigSize.create(configurer);
        width = sizeData.getWidth();
        height = sizeData.getHeight();
    }

    @Override
    public void action(ControlPanelModel<?> panel, CursorStrategy cursor)
    {
        if (owner instanceof UnitWorker)
        {
            final ProducibleEntity produce = factoryProduction.create(BarracksOrc.MEDIA, destX, destY);
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
