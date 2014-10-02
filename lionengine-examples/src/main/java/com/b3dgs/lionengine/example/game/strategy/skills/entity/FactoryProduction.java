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

import com.b3dgs.lionengine.core.Media;
import com.b3dgs.lionengine.example.game.strategy.skills.map.Map;
import com.b3dgs.lionengine.game.SetupGame;
import com.b3dgs.lionengine.game.configurer.ConfigSize;
import com.b3dgs.lionengine.game.configurer.Configurer;
import com.b3dgs.lionengine.game.strategy.ability.producer.FactoryProductionStrategy;

/**
 * The production factory.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public final class FactoryProduction
        extends FactoryProductionStrategy<Entity, ProductionCost, ProducibleEntity>
{
    /**
     * Constructor.
     */
    public FactoryProduction()
    {
        super();
    }

    /*
     * FactoryProductionStrategy
     */

    @Override
    public ProducibleEntity create(Media config)
    {
        final Configurer configurer = getSetup(config).getConfigurer();
        final int step = configurer.getInteger("steps", "cost");
        final int gold = configurer.getInteger("gold", "cost");
        final int wood = configurer.getInteger("wood", "cost");
        final ConfigSize sizeData = ConfigSize.create(configurer);

        final ProductionCost cost = new ProductionCost(step, gold, wood);
        final ProducibleEntity producible = new ProducibleEntity(config, cost, sizeData.getWidth() / Map.TILE_WIDTH,
                sizeData.getHeight() / Map.TILE_HEIGHT);

        return producible;
    }

    @Override
    public ProducibleEntity create(Media config, int tx, int ty)
    {
        final ProducibleEntity producible = create(config);

        producible.setLocation(tx, ty);

        return producible;
    }

    @Override
    protected SetupGame createSetup(Media config)
    {
        return new SetupGame(config);
    }
}
