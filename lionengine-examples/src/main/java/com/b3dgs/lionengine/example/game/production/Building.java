/*
 * Copyright (C) 2013-2024 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */
package com.b3dgs.lionengine.example.game.production;

import com.b3dgs.lionengine.game.feature.Animatable;
import com.b3dgs.lionengine.game.feature.FeatureInterface;
import com.b3dgs.lionengine.game.feature.FeatureModel;
import com.b3dgs.lionengine.game.feature.Services;
import com.b3dgs.lionengine.game.feature.Setup;
import com.b3dgs.lionengine.game.feature.producible.Producer;
import com.b3dgs.lionengine.game.feature.producible.Producible;
import com.b3dgs.lionengine.game.feature.producible.ProducibleListener;
import com.b3dgs.lionengine.game.feature.rasterable.Rasterable;
import com.b3dgs.lionengine.game.feature.tile.map.pathfinding.Pathfindable;

/**
 * Building implementation.
 */
@FeatureInterface
public final class Building extends FeatureModel implements ProducibleListener
{
    private final Rasterable rasterable;
    private final Pathfindable pathfindable;
    private final Producible producible;

    /**
     * Create a building.
     * 
     * @param services The services reference.
     * @param setup The setup reference.
     * @param rasterable The rasterable feature.
     * @param animatable The animatable feature.
     * @param producible The producible feature.
     * @param pathfindable The pathfindable feature.
     */
    public Building(Services services,
                    Setup setup,
                    Rasterable rasterable,
                    Animatable animatable,
                    Producible producible,
                    Pathfindable pathfindable)
    {
        super(services, setup);

        this.rasterable = rasterable;
        this.producible = producible;
        this.pathfindable = pathfindable;

        rasterable.setVisibility(false);
    }

    @Override
    public void notifyProductionStarted(Producer producer)
    {
        pathfindable.setLocation((int) producible.getX() / 16, (int) producible.getY() / 16);
        rasterable.setVisibility(true);
    }

    @Override
    public void notifyProductionProgress(Producer producer)
    {
        // Nothing to do
    }

    @Override
    public void notifyProductionEnded(Producer producer)
    {
        // Nothing to do
    }
}
