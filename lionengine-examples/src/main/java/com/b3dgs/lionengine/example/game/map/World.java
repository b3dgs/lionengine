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
package com.b3dgs.lionengine.example.game.map;

import com.b3dgs.lionengine.Medias;
import com.b3dgs.lionengine.Origin;
import com.b3dgs.lionengine.game.feature.Services;
import com.b3dgs.lionengine.game.feature.tile.map.Minimap;
import com.b3dgs.lionengine.graphic.ColorRgba;
import com.b3dgs.lionengine.graphic.Graphic;
import com.b3dgs.lionengine.helper.WorldHelper;

/**
 * World representation.
 */
public final class World extends WorldHelper
{
    private final Minimap minimap = new Minimap(map);
    private final int size;

    private double speed = 1.0;

    /**
     * Create world.
     * 
     * @param services The services reference.
     */
    public World(Services services)
    {
        super(services);

        map.create(Medias.create("level.png"), 16, 16, 16);

        minimap.load();
        minimap.automaticColor();
        minimap.prepare();

        camera.setView(source, 0, 0, Origin.TOP_LEFT);
        camera.setLimits(map);

        size = map.getWidth() - camera.getWidth();
    }

    @Override
    public void update(double extrp)
    {
        super.update(extrp);

        if (camera.getX() >= size)
        {
            camera.setLocation(size, 0);
            speed *= -1;
        }
        if (camera.getX() <= 0)
        {
            camera.setLocation(0, 0);
            speed *= -1;
        }
        camera.moveLocation(extrp, speed, 0.0);
    }

    @Override
    public void render(Graphic g)
    {
        g.clear(0, 0, source.getWidth(), source.getHeight());

        super.render(g);

        minimap.render(g);
        g.setColor(ColorRgba.RED);
        g.drawRect((int) (camera.getX() / map.getTileWidth()),
                   (int) (camera.getY() / map.getTileHeight()),
                   camera.getWidth() / map.getTileWidth(),
                   camera.getHeight() / map.getTileWidth(),
                   false);
    }
}
