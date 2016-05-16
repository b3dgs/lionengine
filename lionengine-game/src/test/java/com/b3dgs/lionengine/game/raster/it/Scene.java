/*
 * Copyright (C) 2013-2016 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
 */
package com.b3dgs.lionengine.game.raster.it;

import com.b3dgs.lionengine.Timing;
import com.b3dgs.lionengine.core.Context;
import com.b3dgs.lionengine.core.Engine;
import com.b3dgs.lionengine.core.Medias;
import com.b3dgs.lionengine.core.Resolution;
import com.b3dgs.lionengine.core.Sequence;
import com.b3dgs.lionengine.game.camera.Camera;
import com.b3dgs.lionengine.game.handler.Services;
import com.b3dgs.lionengine.game.map.MapTile;
import com.b3dgs.lionengine.game.map.MapTileGame;
import com.b3dgs.lionengine.game.map.feature.viewer.MapTileViewer;
import com.b3dgs.lionengine.game.map.feature.viewer.MapTileViewerModel;
import com.b3dgs.lionengine.game.raster.MapTileRastered;
import com.b3dgs.lionengine.game.raster.MapTileRasteredModel;
import com.b3dgs.lionengine.graphic.Graphic;

/**
 * Integration test for map tile rastered model.
 */
public class Scene extends Sequence
{
    /** Timing value. */
    private final Timing timing = new Timing();
    /** Services reference. */
    private final Services services = new Services();
    /** Camera reference. */
    private final Camera camera = services.create(Camera.class);
    /** Map reference. */
    private final MapTile map = services.create(MapTileGame.class);
    /** Map viewer. */
    private final MapTileViewer mapViewer = map.createFeature(MapTileViewerModel.class);
    /** Map raster reference. */
    private final MapTileRastered raster = services.create(MapTileRasteredModel.class);
    /** Renderable selection (false = default, true = raster). */
    private boolean useRaster;
    /** Count. */
    private int count;

    /**
     * Constructor.
     * 
     * @param context The context reference.
     */
    public Scene(Context context)
    {
        super(context, new Resolution(320, 240, 60));
    }

    @Override
    public void load()
    {
        map.create(Medias.create("level.png"), 16, 16, 16);

        raster.loadSheets(Medias.create("raster.xml"), false);

        camera.setView(0, 0, getWidth(), getHeight());
        camera.setLimits(map);

        timing.start();
    }

    @Override
    public void update(double extrp)
    {
        if (timing.isStarted() && timing.elapsed(300))
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
            timing.restart();
            count++;
        }
        if (count > 5)
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
