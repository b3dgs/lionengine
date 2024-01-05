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
package com.b3dgs.lionengine.example.game.fog;

import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.Medias;
import com.b3dgs.lionengine.Tick;
import com.b3dgs.lionengine.UtilRandom;
import com.b3dgs.lionengine.game.feature.FeatureInterface;
import com.b3dgs.lionengine.game.feature.FeatureModel;
import com.b3dgs.lionengine.game.feature.Routine;
import com.b3dgs.lionengine.game.feature.Services;
import com.b3dgs.lionengine.game.feature.rasterable.SetupSurfaceRastered;
import com.b3dgs.lionengine.game.feature.tile.map.pathfinding.Pathfindable;

/**
 * Soldier implementation.
 */
@FeatureInterface
public final class Soldier extends FeatureModel implements Routine
{
    /** Media reference. */
    public static final Media MEDIA = Medias.create("OrcSoldier.xml");

    private final Tick tick = new Tick();

    private final Pathfindable pathfindable;

    /**
     * Create a soldier.
     * 
     * @param services The services reference.
     * @param setup The setup reference.
     * @param pathfindable The pathfindable feature.
     */
    public Soldier(Services services, SetupSurfaceRastered setup, Pathfindable pathfindable)
    {
        super(services, setup);

        this.pathfindable = pathfindable;
        pathfindable.setLocation(UtilRandom.getRandomInteger(19), UtilRandom.getRandomInteger(14));

        tick.start();
    }

    @Override
    public void update(double extrp)
    {
        tick.update(extrp);
        if (tick.elapsed(Scene.NATIVE.getRate()))
        {
            pathfindable.setLocation(UtilRandom.getRandomInteger(19), UtilRandom.getRandomInteger(14));
            tick.restart();
        }
    }
}
