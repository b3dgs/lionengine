/*
 * Copyright (C) 2013-2020 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.game.feature.tile.map.transition.circuit;

import static com.b3dgs.lionengine.UtilAssert.assertEquals;
import static com.b3dgs.lionengine.UtilAssert.assertPrivateConstructor;
import static com.b3dgs.lionengine.UtilAssert.assertTrue;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.Medias;
import com.b3dgs.lionengine.game.feature.tile.TileGroupsConfig;
import com.b3dgs.lionengine.game.feature.tile.map.MapTile;
import com.b3dgs.lionengine.game.feature.tile.map.MapTileGame;
import com.b3dgs.lionengine.game.feature.tile.map.MapTileGroupModel;
import com.b3dgs.lionengine.game.feature.tile.map.TileSheetsConfig;
import com.b3dgs.lionengine.game.feature.tile.map.UtilMap;
import com.b3dgs.lionengine.game.feature.tile.map.transition.MapTileTransition;
import com.b3dgs.lionengine.game.feature.tile.map.transition.UtilMapTransition;
import com.b3dgs.lionengine.graphic.FactoryGraphicMock;
import com.b3dgs.lionengine.graphic.Graphics;

/**
 * Test {@link CircuitsConfig}.
 */
final class CircuitsConfigTest
{
    /**
     * Prepare test.
     */
    @BeforeAll
    public static void beforeTests()
    {
        Medias.setResourcesDirectory(System.getProperty("java.io.tmpdir"));
        Medias.setLoadFromJar(CircuitsConfigTest.class);
        Graphics.setFactoryGraphic(new FactoryGraphicMock());
    }

    /**
     * Clean up test.
     */
    @AfterAll
    public static void afterTests()
    {
        Medias.setResourcesDirectory(null);
        Medias.setLoadFromJar(null);
        Graphics.setFactoryGraphic(null);
    }

    /**
     * Test constructor.
     */
    @Test
    void testConstructor()
    {
        assertPrivateConstructor(CircuitsConfig.class);
    }

    /**
     * Test exports imports.
     * 
     * @throws IOException If error.
     */
    @Test
    void testExportsImports() throws IOException
    {
        final Media config = UtilMapTransition.createTransitions();

        final MapTile map = UtilMap.createMap(7);
        map.getFeature(MapTileTransition.class).loadTransitions(config);
        UtilMap.fill(map, UtilMap.TILE_GROUND);
        UtilMap.fill(map, UtilMap.TILE_ROAD, UtilMap.TILE_ROAD, 3);

        final MapTileGame map3 = new MapTileGame();
        map3.addFeature(new MapTileGroupModel());
        map3.create(1, 1, 3, 3);

        final CircuitsExtractor extractor = new CircuitsExtractorImpl();
        final Map<Circuit, Collection<Integer>> circuits = extractor.getCircuits(Arrays.asList(map, map3));

        final Media media = Medias.create("circuit.xml");

        CircuitsConfig.exports(media, circuits);
        final Map<Circuit, Collection<Integer>> imported = CircuitsConfig.imports(media);

        assertEquals(circuits, imported);

        assertTrue(media.getFile().delete());
        assertTrue(config.getFile().delete());

        final Media sheets = Medias.create("sheets.xml");
        TileSheetsConfig.exports(sheets, 1, 1, Arrays.asList("sheet.png"));

        final Media groups = Medias.create("groups.xml");
        TileGroupsConfig.exports(groups, Collections.emptyList());

        CircuitsConfig.exports(media, Arrays.asList(Medias.create("level.png")), sheets, groups);

        assertTrue(sheets.getFile().delete());
        assertTrue(groups.getFile().delete());
        assertTrue(media.getFile().delete());
    }
}
