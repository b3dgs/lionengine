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
package com.b3dgs.lionengine.example.game.raster;

import com.b3dgs.lionengine.Medias;
import com.b3dgs.lionengine.Origin;
import com.b3dgs.lionengine.awt.Mouse;
import com.b3dgs.lionengine.awt.MouseAwt;
import com.b3dgs.lionengine.game.feature.Featurable;
import com.b3dgs.lionengine.game.feature.Services;
import com.b3dgs.lionengine.graphic.Graphic;
import com.b3dgs.lionengine.graphic.Graphics;
import com.b3dgs.lionengine.helper.WorldHelper;

/**
 * World representation.
 */
public final class World extends WorldHelper
{
    private final Mouse mouse = getInputDevice(Mouse.class);

    private boolean useRaster = true;

    /**
     * Create world.
     * 
     * @param services The services reference.
     */
    public World(Services services)
    {
        super(services);

        map.create(Medias.create("level.png"), 16, 16, 16);

        camera.setView(source, 0, 0, Origin.TOP_LEFT);
        camera.setLimits(map);

        final Featurable featurable = factory.create(Medias.create("Dino.xml"));
        handler.add(featurable);

        rasterbar.setRasterbarOffset(-24, 16);
        rasterbar.clearRasterbarColor();
        rasterbar.addRasterbarColor(Graphics.getImageBuffer(Medias.create("tiles.png")));
    }

    @Override
    public void update(double extrp)
    {
        super.update(extrp);

        if (mouse.isPushedOnce(MouseAwt.LEFT))
        {
            useRaster = !useRaster;
            rasterbar.clearRasterbarColor();
            if (useRaster)
            {
                rasterbar.addRasterbarColor(Graphics.getImageBuffer(Medias.create("tiles.png")));
            }
        }
    }

    @Override
    public void render(Graphic g)
    {
        g.clear(0, 0, source.getWidth(), source.getHeight());
        super.render(g);
        rasterbar.renderRasterbar();
    }
}
