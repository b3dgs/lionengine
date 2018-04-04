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
package com.b3dgs.lionengine.game.feature.tile.map.viewer;

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicBoolean;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

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
 * Test the map tile viewer class.
 */
public class MapTileViewerModelTest
{
    private final Services services = new Services();
    private MapTileViewer mapViewer;

    /**
     * Prepare test.
     */
    @Before
    public void prepare()
    {
        services.add(new Camera());
        services.add(new ViewerMock());

        final MapTileGame map = services.add(new MapTileGame());
        map.create(80, 80, 1, 1);
        map.loadSheets(Arrays.asList(Drawable.loadSpriteTiled(new ImageBufferMock(80, 80), 80, 80)));
        map.setTile(map.createTile(Integer.valueOf(0), 0, 0, 0));
        mapViewer = new MapTileViewerModel(services);
        mapViewer.prepare(map);
    }

    /**
     * Test the viewer functions.
     */
    @Test
    public void testViewer()
    {
        final AtomicBoolean rendered = new AtomicBoolean();
        final MapTileRenderer renderer = (g, map, tile, x, y) -> rendered.set(true);

        final Graphic g = new GraphicMock();

        mapViewer.render(g);

        Assert.assertFalse(rendered.get());

        mapViewer.addRenderer(renderer);

        mapViewer.render(g);

        Assert.assertTrue(rendered.get());

        rendered.set(false);
        mapViewer.removeRenderer(renderer);
        mapViewer.render(g);

        Assert.assertFalse(rendered.get());

        mapViewer.addRenderer(renderer);
        mapViewer.render(g);

        Assert.assertTrue(rendered.get());

        rendered.set(false);
        mapViewer.clear();
        mapViewer.render(g);

        Assert.assertFalse(rendered.get());
    }
}
