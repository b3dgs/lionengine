/*
 * Copyright (C) 2013-2024 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.game.feature.tile.map;

import static com.b3dgs.lionengine.UtilAssert.assertEquals;
import static com.b3dgs.lionengine.UtilAssert.assertThrows;
import static com.b3dgs.lionengine.UtilAssert.assertTrue;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.Medias;
import com.b3dgs.lionengine.UtilTests;
import com.b3dgs.lionengine.game.Orientation;
import com.b3dgs.lionengine.game.feature.Featurable;
import com.b3dgs.lionengine.game.feature.FeaturableModel;
import com.b3dgs.lionengine.game.feature.Identifiable;
import com.b3dgs.lionengine.game.feature.Services;
import com.b3dgs.lionengine.game.feature.Setup;
import com.b3dgs.lionengine.game.feature.Transformable;
import com.b3dgs.lionengine.game.feature.TransformableModel;
import com.b3dgs.lionengine.game.feature.UtilTransformable;

/**
 * Test {@link OrientableModel}.
 */
final class OrientableModelTest
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
        config = UtilTransformable.createMedia(OrientableModelTest.class);
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
    private final Featurable featurable = new FeaturableModel(services, setup);
    private OrientableModel orientable;

    /**
     * Prepare test.
     */
    @BeforeEach
    public void prepare()
    {
        services.add(new MapTileGame());
        final Transformable transformable = featurable.addFeature(TransformableModel.class, services, setup);

        orientable = new OrientableModel(services, setup, transformable);
        orientable.prepare(featurable);
    }

    /**
     * Clean test.
     */
    @AfterEach
    public void clean()
    {
        featurable.getFeature(Identifiable.class).notifyDestroyed();
    }

    /**
     * Test the orientations enum.
     * 
     * @throws Exception If error.
     */
    @Test
    void testEnum() throws Exception
    {
        UtilTests.testEnum(Orientation.class);
    }

    /**
     * Test the orientations.
     */
    @Test
    void testOrientable()
    {
        assertThrows(() -> orientable.setOrientation(null), "Unexpected null argument !");

        assertEquals(Orientation.NORTH, orientable.getOrientation());

        orientable.recycle();

        assertEquals(Orientation.NORTH, orientable.getOrientation());

        orientable.setOrientation(Orientation.SOUTH);

        assertEquals(Orientation.SOUTH, orientable.getOrientation());

        orientable.pointTo(0, 0);
        assertEquals(Orientation.SOUTH, orientable.getOrientation());

        orientable.pointTo(-1, 0);
        assertEquals(Orientation.WEST, orientable.getOrientation());

        orientable.pointTo(1, 0);
        assertEquals(Orientation.EAST, orientable.getOrientation());

        orientable.pointTo(0, 1);
        assertEquals(Orientation.NORTH, orientable.getOrientation());

        orientable.pointTo(0, -1);
        assertEquals(Orientation.SOUTH, orientable.getOrientation());

        orientable.pointTo(-1, 1);
        assertEquals(Orientation.NORTH_WEST, orientable.getOrientation());

        orientable.pointTo(1, 1);
        assertEquals(Orientation.NORTH_EAST, orientable.getOrientation());

        orientable.pointTo(-1, -1);
        assertEquals(Orientation.SOUTH_WEST, orientable.getOrientation());

        orientable.pointTo(1, -1);
        assertEquals(Orientation.SOUTH_EAST, orientable.getOrientation());
    }

    /**
     * Test the orientations
     */
    @Test
    void testOrientableTiled()
    {
        assertEquals(Orientation.NORTH, orientable.getOrientation());

        orientable.recycle();

        assertEquals(Orientation.NORTH, orientable.getOrientation());

        orientable.setOrientation(Orientation.SOUTH);

        assertEquals(Orientation.SOUTH, orientable.getOrientation());

        UtilOrientable.pointToTiled(orientable, 0, 0);
        assertEquals(Orientation.SOUTH, orientable.getOrientation());

        UtilOrientable.pointToTiled(orientable, -1, 0);
        assertEquals(Orientation.WEST, orientable.getOrientation());

        UtilOrientable.pointToTiled(orientable, 1, 0);
        assertEquals(Orientation.EAST, orientable.getOrientation());

        UtilOrientable.pointToTiled(orientable, 0, 1);
        assertEquals(Orientation.NORTH, orientable.getOrientation());

        UtilOrientable.pointToTiled(orientable, 0, -1);
        assertEquals(Orientation.SOUTH, orientable.getOrientation());

        UtilOrientable.pointToTiled(orientable, -1, 1);
        assertEquals(Orientation.NORTH_WEST, orientable.getOrientation());

        UtilOrientable.pointToTiled(orientable, 1, 1);
        assertEquals(Orientation.NORTH_EAST, orientable.getOrientation());

        UtilOrientable.pointToTiled(orientable, -1, -1);
        assertEquals(Orientation.SOUTH_WEST, orientable.getOrientation());

        UtilOrientable.pointToTiled(orientable, 1, -1);
        assertEquals(Orientation.SOUTH_EAST, orientable.getOrientation());
    }
}
