/*
 * Copyright (C) 2013-2023 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.example.prototype.dungeon;

import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Medias;
import com.b3dgs.lionengine.game.feature.Featurable;
import com.b3dgs.lionengine.game.feature.LayerableModel;
import com.b3dgs.lionengine.game.feature.Services;
import com.b3dgs.lionengine.game.feature.tile.map.MapTileGame;
import com.b3dgs.lionengine.helper.DeviceControllerConfig;
import com.b3dgs.lionengine.helper.MapTileHelper;
import com.b3dgs.lionengine.helper.WorldHelper;

/**
 * World game representation.
 */
public final class World extends WorldHelper
{
    private final MapTileGame mapWall = new MapTileHelper(services);

    /**
     * Create the world.
     * 
     * @param services The services reference (must not be <code>null</code>).
     * @throws LionEngineException If invalid argument.
     */
    public World(Services services)
    {
        super(services);

        services.add(DeviceControllerConfig.create(services, Medias.create("input.xml")));
        mapWall.addFeature(new LayerableModel(3));

        map.create(Medias.create("map", "LevelGround.png"));
        mapWall.create(Medias.create("map", "LevelWall.png"));

        handler.add(mapWall);

        camera.setLimits(map);

        final Featurable featurable = spawn(Medias.create("hero", "Knight.xml"), 128, 64);
        tracker.track(featurable);
        handler.add(tracker);
    }
}
