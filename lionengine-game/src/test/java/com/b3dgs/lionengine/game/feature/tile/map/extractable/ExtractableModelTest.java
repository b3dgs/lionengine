/*
 * Copyright (C) 2013-2022 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.game.feature.tile.map.extractable;

import static com.b3dgs.lionengine.UtilAssert.assertEquals;
import static com.b3dgs.lionengine.UtilAssert.assertTrue;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.Medias;
import com.b3dgs.lionengine.game.feature.Featurable;
import com.b3dgs.lionengine.game.feature.FeaturableModel;
import com.b3dgs.lionengine.game.feature.Identifiable;
import com.b3dgs.lionengine.game.feature.Services;
import com.b3dgs.lionengine.game.feature.Setup;
import com.b3dgs.lionengine.game.feature.TransformableModel;
import com.b3dgs.lionengine.game.feature.UtilTransformable;
import com.b3dgs.lionengine.game.feature.tile.map.MapTile;
import com.b3dgs.lionengine.game.feature.tile.map.MapTileGame;

/**
 * Test {@link ExtractableModel}.
 */
final class ExtractableModelTest
{
    /** Object config test. */
    private static Media config;

    /**
     * Prepare test.
     */
    @BeforeAll
    public static void beforeTests()
    {
        Medias.setResourcesDirectory(System.getProperty("java.io.tmpdir"));
        config = UtilTransformable.createMedia(ExtractableModelTest.class);
    }

    /**
     * Clean up test.
     */
    @AfterAll
    public static void afterTests()
    {
        assertTrue(config.getFile().delete());
        Medias.setResourcesDirectory(null);
    }

    private final Services services = new Services();
    private final Setup setup = new Setup(config);

    /**
     * Test the extraction config with node.
     */
    @Test
    void testConfigWithNode()
    {
        final MapTile map = new MapTileGame();
        map.create(16, 16, 4, 4);
        services.add(map);

        final Featurable featurable = new FeaturableModel(services, setup);
        featurable.addFeatureAndGet(new TransformableModel(services, setup)).teleport(16, 32);

        final Media media = UtilExtractable.createMedia();
        final Extractable extractable = new ExtractableModel(services, new Setup(media));
        extractable.prepare(featurable);

        assertEquals(10, extractable.getResourceQuantity());
        assertEquals("gold", extractable.getResourceType());
        assertEquals(1, extractable.getInTileX());
        assertEquals(2, extractable.getInTileY());
        assertEquals(1, extractable.getInTileWidth());
        assertEquals(2, extractable.getInTileHeight());

        extractable.getFeature(Identifiable.class).notifyDestroyed();

        assertTrue(media.getFile().delete());
    }

    /**
     * Test the extraction config.
     */
    @Test
    void testConfig()
    {
        final Extractable extractable = UtilExtractable.createExtractable(services, setup);

        assertEquals(10, extractable.getResourceQuantity());
        assertEquals("wood", extractable.getResourceType());
        assertEquals(0, extractable.getInTileX());
        assertEquals(0, extractable.getInTileY());
        assertEquals(0, extractable.getInTileWidth());
        assertEquals(0, extractable.getInTileHeight());

        extractable.getFeature(Identifiable.class).notifyDestroyed();
    }

    /**
     * Test the extraction.
     */
    @Test
    void testExtract()
    {
        services.add(new MapTileGame());

        final Featurable featurable = new FeaturableModel(services, setup);
        featurable.addFeature(new TransformableModel(services, setup));

        final Extractable extractable = new ExtractableModel(services, setup);
        extractable.prepare(featurable);
        extractable.setResourcesQuantity(10);

        assertEquals(10, extractable.getResourceQuantity());

        extractable.extractResource(5);

        assertEquals(5, extractable.getResourceQuantity());

        extractable.getFeature(Identifiable.class).notifyDestroyed();
    }
}
