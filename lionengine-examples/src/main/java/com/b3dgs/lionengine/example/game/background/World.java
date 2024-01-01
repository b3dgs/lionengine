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
package com.b3dgs.lionengine.example.game.background;

import com.b3dgs.lionengine.UtilMath;
import com.b3dgs.lionengine.game.background.BackgroundAbstract;
import com.b3dgs.lionengine.game.feature.Services;
import com.b3dgs.lionengine.graphic.Graphic;
import com.b3dgs.lionengine.graphic.engine.SourceResolutionProvider;
import com.b3dgs.lionengine.helper.WorldHelper;

/**
 * World representation.
 */
public final class World extends WorldHelper
{
    private final ForegroundWater foreground = new ForegroundWater(services,
                                                                   services.get(SourceResolutionProvider.class),
                                                                   false);
    private final BackgroundAbstract background = new Swamp(services.get(SourceResolutionProvider.class), false);

    private double y;

    /**
     * Create world.
     * 
     * @param services The services reference.
     */
    public World(Services services)
    {
        super(services);
    }

    @Override
    public void update(double extrp)
    {
        y = UtilMath.wrapDouble(y + 1.25, 0.0, 360.0);

        final double dy = UtilMath.sin(y) * 90 + 110;
        background.update(extrp, 1.0, 0, dy);
        foreground.update(extrp, 1.0, 0, dy);
    }

    @Override
    public void render(Graphic g)
    {
        background.render(g);
        foreground.render(g);
    }

    @Override
    public void onResolutionChanged(int width, int height)
    {
        camera.setView(0, 0, width, height, height);
        camera.setLimits(map);
        foreground.setScreenSize(width, height);
        background.setScreenSize(width, height);
    }
}
