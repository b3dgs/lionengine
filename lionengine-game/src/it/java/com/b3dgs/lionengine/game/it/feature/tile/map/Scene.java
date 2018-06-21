/*
 * Copyright (C) 2013-2017 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.game.it.feature.tile.map;

import static com.b3dgs.lionengine.UtilAssert.assertEquals;

import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.atomic.AtomicInteger;

import com.b3dgs.lionengine.Context;
import com.b3dgs.lionengine.Engine;
import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.Medias;
import com.b3dgs.lionengine.Resolution;
import com.b3dgs.lionengine.Timing;
import com.b3dgs.lionengine.game.feature.Camera;
import com.b3dgs.lionengine.game.feature.Services;
import com.b3dgs.lionengine.game.feature.tile.TilesExtractor;
import com.b3dgs.lionengine.game.feature.tile.map.LevelRipConverter;
import com.b3dgs.lionengine.game.feature.tile.map.MapTile;
import com.b3dgs.lionengine.game.feature.tile.map.MapTileGame;
import com.b3dgs.lionengine.game.feature.tile.map.Minimap;
import com.b3dgs.lionengine.game.feature.tile.map.SheetsExtractor;
import com.b3dgs.lionengine.game.feature.tile.map.viewer.MapTileViewer;
import com.b3dgs.lionengine.game.feature.tile.map.viewer.MapTileViewerModel;
import com.b3dgs.lionengine.graphic.ColorRgba;
import com.b3dgs.lionengine.graphic.Graphic;
import com.b3dgs.lionengine.graphic.ImageBuffer;
import com.b3dgs.lionengine.graphic.engine.Sequence;

/**
 * Game loop designed to handle our world.
 */
class Scene extends Sequence
{
    /** Native resolution. */
    private static final Resolution NATIVE = new Resolution(320, 240, 60);

    /** Services reference. */
    private final Services services = new Services();
    /** Camera reference. */
    private final Camera camera = services.create(Camera.class);
    /** Map reference. */
    private final MapTile map = services.create(MapTileGame.class);
    /** Map viewer. */
    private final MapTileViewer mapViewer = map.addFeatureAndGet(new MapTileViewerModel(services));
    /** Minimap reference. */
    private final Minimap minimap = new Minimap(map);
    /** Scrolling speed. */
    private final double speed = 50;
    /** Test timing. */
    private final Timing timing = new Timing();

    /**
     * Constructor.
     * 
     * @param context The context reference.
     */
    public Scene(Context context)
    {
        super(context, NATIVE);
    }

    @Override
    public void load()
    {
        final Media levelrip = Medias.create("level.png");
        final TilesExtractor tilesExtractor = new TilesExtractor();
        final AtomicInteger p = new AtomicInteger();

        tilesExtractor.addListener((percent, tiles) -> p.set(percent));
        Collection<ImageBuffer> buffers = tilesExtractor.extract(16, 16, Arrays.asList(levelrip));
        map.loadSheets(SheetsExtractor.extract(buffers, 16));
        buffers.forEach(ImageBuffer::dispose);

        assertEquals(100, p.get());

        p.set(0);
        tilesExtractor.addListener((percent, tiles) -> p.set(percent));
        buffers = tilesExtractor.extract(() -> true, 16, 16, Arrays.asList(levelrip));
        map.loadSheets(SheetsExtractor.extract(buffers, 16));
        buffers.forEach(ImageBuffer::dispose);

        assertEquals(0, p.get());

        tilesExtractor.addListener((percent, tiles) -> p.set(percent));
        buffers = tilesExtractor.extract(() -> false, 16, 16, Arrays.asList(levelrip));
        map.loadSheets(SheetsExtractor.extract(buffers, 16));
        buffers.forEach(ImageBuffer::dispose);

        assertEquals(100, p.get());

        p.set(0);
        LevelRipConverter.start(levelrip, map, (percent, progressTileX, progressTileY) -> p.set(percent));

        assertEquals(100, p.get());

        mapViewer.prepare(map);

        minimap.load();
        minimap.automaticColor();
        minimap.prepare();

        camera.setView(0, 0, getWidth(), getHeight(), getHeight());
        camera.setLimits(map);

        timing.start();
    }

    @Override
    public void update(double extrp)
    {
        if (timing.elapsed(1000L))
        {
            end();
        }
        camera.moveLocation(extrp, speed, 0.0);
    }

    @Override
    public void render(Graphic g)
    {
        g.clear(0, 0, getWidth(), getHeight());
        mapViewer.render(g);
        minimap.render(g);
        g.setColor(ColorRgba.RED);
        g.drawRect((int) (camera.getX() / map.getTileWidth()),
                   (int) (camera.getY() / map.getTileHeight()),
                   camera.getWidth() / map.getTileWidth(),
                   camera.getHeight() / map.getTileWidth(),
                   false);
    }

    @Override
    public void onTerminated(boolean hasNextSequence)
    {
        Engine.terminate();
    }
}
