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
package com.b3dgs.lionengine.game.feature.tile.map.transition.fog;

import static com.b3dgs.lionengine.UtilAssert.assertEquals;
import static com.b3dgs.lionengine.UtilAssert.assertThrows;
import static com.b3dgs.lionengine.UtilAssert.assertTrue;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.Medias;
import com.b3dgs.lionengine.game.feature.Featurable;
import com.b3dgs.lionengine.game.feature.FeaturableModel;
import com.b3dgs.lionengine.game.feature.Services;
import com.b3dgs.lionengine.game.feature.Setup;
import com.b3dgs.lionengine.game.feature.Transformable;
import com.b3dgs.lionengine.game.feature.TransformableModel;
import com.b3dgs.lionengine.game.feature.UtilSetup;
import com.b3dgs.lionengine.game.feature.tile.map.MapTile;
import com.b3dgs.lionengine.game.feature.tile.map.UtilMap;

/**
 * Test {@link FovableModel}.
 */
public final class FovableModelTest
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
        config = UtilSetup.createConfig();
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

    /**
     * Test constructor with null services.
     */
    @Test
    public void testConstructorNullServices()
    {
        assertThrows(() -> new FovableModel(null), "Unexpected null argument !");
    }

    /**
     * Test the fovable model.
     */
    @Test
    public void testFovable()
    {
        final Services services = new Services();
        final MapTile map = UtilMap.createMap(7);
        services.add(map);

        final Fovable fovable = new FovableModel(services);

        final Setup setup = new Setup(config);
        final Featurable featurable = new FeaturableModel();
        final Transformable transformable = featurable.addFeatureAndGet(new TransformableModel(setup));
        transformable.teleport(1, 2);
        transformable.setSize(3, 4);
        fovable.prepare(featurable);
        fovable.setFov(5);

        assertEquals(1, fovable.getInTileX());
        assertEquals(2, fovable.getInTileY());
        assertEquals(3, fovable.getInTileWidth());
        assertEquals(4, fovable.getInTileHeight());
        assertEquals(5, fovable.getInTileFov());
    }
}
