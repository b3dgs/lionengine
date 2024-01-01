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
package com.b3dgs.lionengine.tutorials.mario.d;

import java.io.IOException;

import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.Medias;
import com.b3dgs.lionengine.game.feature.Featurable;
import com.b3dgs.lionengine.game.feature.Services;
import com.b3dgs.lionengine.game.feature.tile.map.TileSheetsConfig;
import com.b3dgs.lionengine.graphic.ColorRgba;
import com.b3dgs.lionengine.graphic.Graphic;
import com.b3dgs.lionengine.helper.DeviceControllerConfig;
import com.b3dgs.lionengine.helper.WorldHelper;
import com.b3dgs.lionengine.io.DeviceController;
import com.b3dgs.lionengine.io.FileReading;

/**
 * World implementation.
 */
public final class World extends WorldHelper
{
    /** Ground height. */
    static final int GROUND = 32;
    private static final ColorRgba BACKGROUND_COLOR = new ColorRgba(107, 136, 255);
    private static final Media MARIO = Medias.create("entity", "Mario.xml");
    private static final Media GOOMBA = Medias.create("entity", "Goomba.xml");

    private final DeviceController device = services.add(DeviceControllerConfig.create(services,
                                                                                       Medias.create("input.xml")));

    /**
     * Create world.
     * 
     * @param services The services reference.
     */
    public World(Services services)
    {
        super(services);

        camera.setIntervals(16, 0);
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
        fill(g, BACKGROUND_COLOR);

        super.render(g);
    }

    @Override
    protected void loading(FileReading file) throws IOException
    {
        map.loadSheets(Medias.create("map", TileSheetsConfig.FILENAME));

        super.loading(file);

        final Featurable mario = factory.create(MARIO);
        handler.add(mario);
        tracker.track(mario);

        for (int i = 0; i < 5; i++)
        {
            final Featurable goomba = spawn(GOOMBA, 500 + i * 50, GROUND);
            handler.add(goomba);
        }
    }
}
