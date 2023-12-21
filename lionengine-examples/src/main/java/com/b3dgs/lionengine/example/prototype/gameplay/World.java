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
package com.b3dgs.lionengine.example.prototype.gameplay;

import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Medias;
import com.b3dgs.lionengine.UtilRandom;
import com.b3dgs.lionengine.game.feature.Services;
import com.b3dgs.lionengine.helper.DeviceControllerConfig;
import com.b3dgs.lionengine.helper.WorldHelper;

/**
 * World game representation.
 */
public final class World extends WorldHelper
{
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
        spawn(Medias.create("Knight.xml"), Scene.NATIVE_RESOLUTION.getWidth() / 3, 0);

    }

    @Override
    public void update(double extrp)
    {
        super.update(extrp);

        if (UtilRandom.getRandomInteger(200) == 0)
        {
            spawn(Medias.create("Zombi.xml"),
                  Scene.NATIVE_RESOLUTION.getWidth() / 2 + UtilRandom.getRandomInteger(-100, 100),
                  0);
        }
    }
}
