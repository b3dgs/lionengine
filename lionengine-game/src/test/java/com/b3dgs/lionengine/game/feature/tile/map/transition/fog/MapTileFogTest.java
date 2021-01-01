/*
 * Copyright (C) 2013-2021 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
import static com.b3dgs.lionengine.UtilAssert.assertTrue;

import java.util.concurrent.atomic.AtomicInteger;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.Medias;
import com.b3dgs.lionengine.game.feature.FeaturableModel;
import com.b3dgs.lionengine.game.feature.Services;
import com.b3dgs.lionengine.game.feature.Setup;
import com.b3dgs.lionengine.game.feature.Transformable;
import com.b3dgs.lionengine.game.feature.TransformableModel;
import com.b3dgs.lionengine.game.feature.UtilTransformable;
import com.b3dgs.lionengine.game.feature.tile.map.MapTile;
import com.b3dgs.lionengine.game.feature.tile.map.MapTileSurface;
import com.b3dgs.lionengine.game.feature.tile.map.UtilMap;
import com.b3dgs.lionengine.game.feature.tile.map.transition.UtilMapTransition;

/**
 * Test {@link MapTileFog}.
 */
final class MapTileFogTest
{
    /** Object config test. */
    private static Media media;
    /** Test configuration. */
    private static Media config;

    /**
     * Prepare test.
     */
    @BeforeAll
    public static void setUp()
    {
        Medias.setResourcesDirectory(System.getProperty("java.io.tmpdir"));
        media = UtilTransformable.createMedia(MapTileFogTest.class);
        config = UtilMapTransition.createTransitions();
    }

    /**
     * Clean up test.
     */
    @AfterAll
    public static void cleanUp()
    {
        assertTrue(media.getFile().delete());
        assertTrue(config.getFile().delete());
        Medias.setResourcesDirectory(null);
    }

    private final Services services = new Services();
    private final Setup setup = new Setup(config);

    /**
     * Test the fog.
     */
    @Test
    void testFog()
    {
        final MapTile map = UtilMap.createMap(5);
        services.add(map);

        final FovableModel fovable = new FovableModel(services, setup);

        final FeaturableModel object = new FeaturableModel(services, setup);
        final Transformable transformable = object.addFeatureAndGet(new TransformableModel(services, setup));
        transformable.teleport(3, 3);
        fovable.prepare(object);
        fovable.setFov(1);

        final MapTileFog fog = new MapTileFog();
        Medias.setLoadFromJar(MapTileFog.class);
        fog.create(map.getFeature(MapTileSurface.class), Medias.create("fog.xml"), null);
        Medias.setLoadFromJar(null);

        final AtomicInteger rtx = new AtomicInteger();
        final AtomicInteger rty = new AtomicInteger();
        final AtomicInteger count = new AtomicInteger();
        final RevealedListener listener = (tx, ty) ->
        {
            rtx.set(tx);
            rty.set(ty);
            count.incrementAndGet();
        };
        fog.addListener(listener);

        assertEquals(16, fog.getTile(2, 3).getNumber());
        assertEquals(16, fog.getTile(3, 3).getNumber());
        assertEquals(16, fog.getTile(4, 3).getNumber());

        fog.updateFov(fovable);

        assertEquals(10, fog.getTile(2, 2).getNumber());
        assertEquals(1, fog.getTile(3, 2).getNumber());
        assertEquals(11, fog.getTile(4, 2).getNumber());
        assertEquals(2, fog.getTile(2, 3).getNumber());
        assertEquals(17, fog.getTile(3, 3).getNumber());
        assertEquals(3, fog.getTile(4, 3).getNumber());
        assertEquals(8, fog.getTile(2, 4).getNumber());
        assertEquals(0, fog.getTile(3, 4).getNumber());
        assertEquals(9, fog.getTile(4, 4).getNumber());

        assertEquals(1, count.get());
        assertEquals(3, rtx.get());
        assertEquals(3, rty.get());

        fog.reset(fovable);
        fog.removeListener(listener);
        count.set(0);

        assertEquals(16, fog.getTile(2, 3).getNumber());
        assertEquals(16, fog.getTile(3, 3).getNumber());
        assertEquals(16, fog.getTile(4, 3).getNumber());

        fog.updateFov(fovable);

        assertEquals(0, count.get());
    }
}
