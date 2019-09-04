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
package com.b3dgs.lionengine.game.feature.tile.map.raster;

import static com.b3dgs.lionengine.UtilAssert.assertEquals;

import java.util.Arrays;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.b3dgs.lionengine.Medias;
import com.b3dgs.lionengine.ViewerMock;
import com.b3dgs.lionengine.game.feature.Camera;
import com.b3dgs.lionengine.game.feature.Services;
import com.b3dgs.lionengine.game.feature.tile.map.MapTileGame;
import com.b3dgs.lionengine.game.feature.tile.map.viewer.MapTileViewer;
import com.b3dgs.lionengine.game.feature.tile.map.viewer.MapTileViewerModel;
import com.b3dgs.lionengine.graphic.FactoryGraphicMock;
import com.b3dgs.lionengine.graphic.Graphic;
import com.b3dgs.lionengine.graphic.GraphicMock;
import com.b3dgs.lionengine.graphic.Graphics;
import com.b3dgs.lionengine.graphic.ImageBufferMock;
import com.b3dgs.lionengine.graphic.drawable.Drawable;
import com.b3dgs.lionengine.graphic.raster.RasterImage;

/**
 * Test {@link MapTileRasteredModel}.
 */
public final class MapTileRasteredModelTest
{
    /**
     * Prepare test.
     */
    @BeforeAll
    public static void beforeTests()
    {
        Medias.setResourcesDirectory(System.getProperty("java.io.tmpdir"));
        Graphics.setFactoryGraphic(new FactoryGraphicMock());
        Medias.setLoadFromJar(MapTileRasteredModelTest.class);
    }

    /**
     * Clean up test.
     */
    @AfterAll
    public static void afterTests()
    {
        Medias.setResourcesDirectory(null);
        Graphics.setFactoryGraphic(null);
        Medias.setLoadFromJar(null);
    }

    private final Services services = new Services();
    private final Graphic g = new GraphicMock();
    private ViewerMock viewer;
    private MapTileGame map;
    private MapTileViewer mapViewer;
    private MapTileRastered mapRastered;

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
        mapRastered = new MapTileRasteredModel(services);
        mapRastered.prepare(map);
    }

    /**
     * Test the viewer functions.
     */
    @Test
    public void testRastered()
    {
        map.loadSheets(Arrays.asList(Drawable.loadSpriteTiled(new ImageBufferMock(80, 80), 40, 40)));
        map.create(40, 40, 2, 2);
        map.setTile(0, 0, 0);
        map.setTile(1, 1, 1);

        mapRastered.loadSheets(Medias.create("raster.xml"), false);
        mapViewer.addRenderer(mapRastered);
        mapViewer.render(g);

        assertEquals(RasterImage.MAX_RASTERS_M - (RasterImage.MAX_RASTERS_R - RasterImage.MAX_RASTERS) + 1,
                     mapRastered.getRasterIndex(RasterImage.MAX_RASTERS_R - 1));

        mapRastered.loadSheets(Medias.create("raster.xml"), true);
        mapViewer.render(g);

        assertEquals(RasterImage.MAX_RASTERS_M, mapRastered.getRasterIndex(RasterImage.MAX_RASTERS_M));
    }
}
