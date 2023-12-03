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
package com.b3dgs.lionengine.game.it.feature.tile.map.raster;

import com.b3dgs.lionengine.Context;
import com.b3dgs.lionengine.Engine;
import com.b3dgs.lionengine.Medias;
import com.b3dgs.lionengine.Resolution;
import com.b3dgs.lionengine.Tick;
import com.b3dgs.lionengine.game.feature.Camera;
import com.b3dgs.lionengine.game.feature.Services;
import com.b3dgs.lionengine.game.feature.tile.map.MapTileGame;
import com.b3dgs.lionengine.game.feature.tile.map.raster.MapTileRastered;
import com.b3dgs.lionengine.game.feature.tile.map.raster.MapTileRasteredModel;
import com.b3dgs.lionengine.game.feature.tile.map.viewer.MapTileViewer;
import com.b3dgs.lionengine.game.feature.tile.map.viewer.MapTileViewerModel;
import com.b3dgs.lionengine.graphic.Graphic;
import com.b3dgs.lionengine.graphic.engine.Sequence;

/**
 * Integration test for map tile rastered model.
 */
public final class SceneMapTileRastered extends Sequence
{
    private final Services services = new Services();
    private final Camera camera = services.create(Camera.class);
    private final MapTileGame map = services.create(MapTileGame.class);
    private final MapTileViewer mapViewer = map.addFeature(new MapTileViewerModel(services));
    private final MapTileRastered raster = map.addFeature(new MapTileRasteredModel());
    private final Tick tickRaster = new Tick();
    private final Tick tick = new Tick();

    private boolean useRaster;

    /**
     * Constructor.
     * 
     * @param context The context reference.
     */
    public SceneMapTileRastered(Context context)
    {
        super(context, new Resolution(640, 480, 60));
    }

    @Override
    public void load()
    {
        map.create(Medias.create("level.png"), 16, 16, 16);
        raster.setRaster(Medias.create("tiles.png"), 2, 0);
        raster.loadSheets();

        camera.setView(0, 0, getWidth(), getHeight(), getHeight());
        camera.setLimits(map);

        tickRaster.start();
        tick.start();
    }

    @Override
    public void update(double extrp)
    {
        tick.update(extrp);
        tickRaster.update(extrp);
        if (tick.isStarted() && tickRaster.elapsed(10L))
        {
            useRaster = !useRaster;
            if (useRaster)
            {
                mapViewer.addRenderer(raster);
            }
            else
            {
                mapViewer.removeRenderer(raster);
            }
            tickRaster.restart();
        }
        if (tick.elapsed(20L))
        {
            end();
        }
    }

    @Override
    public void render(Graphic g)
    {
        mapViewer.render(g);
    }

    @Override
    public void onTerminated(boolean hasNextSequence)
    {
        Engine.terminate();
    }
}
