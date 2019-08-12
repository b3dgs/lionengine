/*
 * Copyright (C) 2013-2019 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.game.feature.tile.map.viewer;

import static com.b3dgs.lionengine.UtilAssert.assertFalse;
import static com.b3dgs.lionengine.UtilAssert.assertThrows;
import static com.b3dgs.lionengine.UtilAssert.assertTrue;

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicBoolean;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.b3dgs.lionengine.ViewerMock;
import com.b3dgs.lionengine.game.feature.Camera;
import com.b3dgs.lionengine.game.feature.Services;
import com.b3dgs.lionengine.game.feature.tile.map.MapTileGame;
import com.b3dgs.lionengine.game.feature.tile.map.MapTileRenderer;
import com.b3dgs.lionengine.graphic.Graphic;
import com.b3dgs.lionengine.graphic.GraphicMock;
import com.b3dgs.lionengine.graphic.ImageBufferMock;
import com.b3dgs.lionengine.graphic.drawable.Drawable;

/**
 * Test {@link MapTileViewerModel}.
 */
public final class MapTileViewerModelTest
{
    private final Services services = new Services();
    private final Graphic g = new GraphicMock();
    private ViewerMock viewer;
    private MapTileGame map;
    private MapTileViewer mapViewer;

    /**
     * Prepare test.
     */
    @BeforeEach
    public void prepare()
    {
        services.add(new Camera());
        viewer = services.add(new ViewerMock());
        viewer.set(-20, -20);

        map = services.add(new MapTileGame());
        mapViewer = new MapTileViewerModel(services);
        mapViewer.prepare(map);
    }

    /**
     * Test constructor with null services.
     */
    @Test
    public void testConstructorNullServices()
    {
        assertThrows(() -> new MapTileViewerModel(null), "Unexpected null argument !");
    }

    /**
     * Test the viewer functions.
     */
    @Test
    public void testViewer()
    {
        map.loadSheets(Arrays.asList(Drawable.loadSpriteTiled(new ImageBufferMock(80, 80), 80, 80)));

        mapViewer.render(g);

        map.create(80, 80, 2, 2);
        map.setTile(0, 0, Integer.valueOf(0), 0);
        map.setTile(1, 1, Integer.valueOf(0), 1);

        final AtomicBoolean rendered = new AtomicBoolean();
        final MapTileRenderer renderer = (g, map, tile, x, y) -> rendered.set(true);

        mapViewer.render(g);

        assertFalse(rendered.get());

        mapViewer.addRenderer(renderer);

        mapViewer.render(g);

        assertTrue(rendered.get());

        rendered.set(false);
        mapViewer.removeRenderer(renderer);
        mapViewer.render(g);

        assertFalse(rendered.get());

        mapViewer.addRenderer(renderer);
        mapViewer.render(g);

        assertTrue(rendered.get());

        rendered.set(false);
        mapViewer.clear();
        mapViewer.render(g);

        assertFalse(rendered.get());

        map.clear();
        mapViewer.render(g);
    }
}
