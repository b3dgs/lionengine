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
package com.b3dgs.lionengine.example.game.state;

import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Medias;
import com.b3dgs.lionengine.game.feature.Services;
import com.b3dgs.lionengine.graphic.ColorRgba;
import com.b3dgs.lionengine.graphic.Graphic;
import com.b3dgs.lionengine.helper.DeviceControllerConfig;
import com.b3dgs.lionengine.helper.WorldHelper;
import com.b3dgs.lionengine.io.DeviceController;

/**
 * World game representation.
 */
public final class World extends WorldHelper
{
    private final DeviceController device = services.add(DeviceControllerConfig.create(services,
                                                                                       Medias.create("input.xml")));

    /**
     * Create the world.
     * 
     * @param services The services reference (must not be <code>null</code>).
     * @throws LionEngineException If invalid argument.
     */
    public World(Services services)
    {
        super(services);

        spawn(Medias.create("Hero.xml"), 160, 32);
    }

    @Override
    public void update(double extrp)
    {
        super.update(extrp);

        device.update(extrp);
    }

    @Override
    public void render(Graphic g)
    {
        g.clear(0, 0, camera.getWidth(), camera.getHeight());
        g.setColor(ColorRgba.GRAY);
        g.drawLine(camera, 0, 32, camera.getWidth(), 32);

        super.render(g);
    }
}
