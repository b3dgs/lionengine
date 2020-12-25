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
package com.b3dgs.lionengine.game.feature;

import static com.b3dgs.lionengine.UtilAssert.assertEquals;
import static com.b3dgs.lionengine.UtilAssert.assertFalse;
import static com.b3dgs.lionengine.UtilAssert.assertThrows;
import static com.b3dgs.lionengine.UtilAssert.assertTrue;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.b3dgs.lionengine.Localizable;
import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.Medias;
import com.b3dgs.lionengine.Origin;
import com.b3dgs.lionengine.Shape;
import com.b3dgs.lionengine.Surface;
import com.b3dgs.lionengine.SurfaceTile;
import com.b3dgs.lionengine.game.Cursor;
import com.b3dgs.lionengine.geom.Geom;
import com.b3dgs.lionengine.graphic.GraphicMock;
import com.b3dgs.lionengine.graphic.engine.SourceResolutionProvider;

/**
 * Test {@link Camera}.
 */
final class CameraTest
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
        config = UtilTransformable.createMedia(CameraTest.class);
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

    private final Camera camera = new Camera();
    private final Surface surface = new Surface()
    {
        @Override
        public int getWidth()
        {
            return 10;
        }

        @Override
        public int getHeight()
        {
            return 10;
        }
    };

    /**
     * Test the camera default values.
     */
    @Test
    void testCameraDefault()
    {
        assertEquals(0, camera.getViewX());
        assertEquals(0, camera.getViewY());
        assertEquals(Integer.MAX_VALUE, camera.getWidth());
        assertEquals(Integer.MAX_VALUE, camera.getHeight());
        assertEquals(0, camera.getScreenHeight());
        assertEquals(0.0, camera.getX());
        assertEquals(0.0, camera.getY());
        assertEquals(0.0, camera.getMovementHorizontal());
        assertEquals(0.0, camera.getMovementVertical());
    }

    /**
     * Test the camera center.
     */
    @Test
    void testCameraCenter()
    {
        final Shape shape = Geom.createArea(60, 50, 20, 10);
        camera.setView(0, 0, 120, 100, 100);
        camera.center(shape);

        assertEquals(10.0, camera.getX());
        assertEquals(5.0, camera.getY());
    }

    /**
     * Test the camera round.
     */
    @Test
    void testCameraRound()
    {
        camera.teleport(11.1, 12.2);
        camera.round(new SurfaceTile()
        {
            @Override
            public int getWidth()
            {
                return 100;
            }

            @Override
            public int getHeight()
            {
                return 200;
            }

            @Override
            public int getTileWidth()
            {
                return 10;
            }

            @Override
            public int getTileHeight()
            {
                return 20;
            }
        });

        assertEquals(10.0, camera.getX());
        assertEquals(20.0, camera.getY());
    }

    /**
     * Test the camera teleport.
     */
    @Test
    void testCameraTeleport()
    {
        camera.setLimits(surface);
        camera.teleport(1.0, 2.0);

        assertEquals(0.0, camera.getMovementHorizontal());
        assertEquals(0.0, camera.getMovementVertical());
        assertEquals(1.0, camera.getX());
        assertEquals(2.0, camera.getY());
    }

    /**
     * Test the camera movement.
     */
    @Test
    void testCameraMovement()
    {
        camera.moveLocation(1.0, 2.0, 3.0);

        assertEquals(2.0, camera.getMovementHorizontal());
        assertEquals(3.0, camera.getMovementVertical());
        assertEquals(2.0, camera.getX());
        assertEquals(3.0, camera.getY());
    }

    /**
     * Test the camera viewpoint.
     */
    @Test
    void testCameraViewpoint()
    {
        camera.setView(0, 0, 16, 32, 32);

        assertEquals(0.0, camera.getViewpointX(0.0));
        assertEquals(32.0, camera.getViewpointY(0.0));

        camera.setLocation(1.0, 2.0);

        assertEquals(-1.0, camera.getViewpointX(0.0));
        assertEquals(34.0, camera.getViewpointY(0.0));
    }

    /**
     * Test the camera view.
     */
    @Test
    void testCameraView()
    {
        camera.setView(1, 2, 3, 4, 4);

        assertEquals(1, camera.getViewX());
        assertEquals(2, camera.getViewY());
        assertEquals(3, camera.getWidth());
        assertEquals(4, camera.getHeight());

        camera.drawFov(new GraphicMock(), 0, 0, 1, 1, surface);

        assertThrows(() -> camera.setView(null, 0, 0, null), "Unexpected null argument !");

        camera.setView(new SourceResolutionProvider()
        {
            @Override
            public int getWidth()
            {
                return 320;
            }

            @Override
            public int getHeight()
            {
                return 240;
            }

            @Override
            public int getRate()
            {
                return 60;
            }
        }, 10, 20, Origin.TOP_LEFT);

        assertEquals(10, camera.getViewX());
        assertEquals(20, camera.getViewY());
        assertEquals(310, camera.getWidth());
        assertEquals(220, camera.getHeight());
    }

    /**
     * Test the camera viewable.
     */
    @Test
    void testIsViewable()
    {
        camera.setView(0, 0, 2, 2, 2);

        final Cursor cursor = new Cursor();
        cursor.setArea(-2, -2, 4, 4);
        cursor.setLocation(0, 0);
        cursor.update(1.0);

        assertTrue(camera.isViewable((Localizable) cursor, 0, 0));
        assertFalse(camera.isViewable((Localizable) cursor, 0, -1));
        assertFalse(camera.isViewable((Localizable) cursor, -1, 0));
        assertFalse(camera.isViewable((Localizable) cursor, -1, -1));

        assertTrue(camera.isViewable(cursor, 0, 0));
        assertFalse(camera.isViewable(cursor, 0, -2));
        assertFalse(camera.isViewable(cursor, -2, 0));
        assertFalse(camera.isViewable(cursor, -2, -2));

        cursor.setLocation(3, 3);
        cursor.update(1.0);

        assertFalse(camera.isViewable((Localizable) cursor, 0, 0));
        assertFalse(camera.isViewable((Localizable) cursor, 0, 1));
        assertFalse(camera.isViewable((Localizable) cursor, 1, 0));
        assertTrue(camera.isViewable((Localizable) cursor, 1, 1));

        assertFalse(camera.isViewable(cursor, -1, -1));
        assertFalse(camera.isViewable(cursor, 0, -2));
        assertFalse(camera.isViewable(cursor, -2, 0));
        assertTrue(camera.isViewable(cursor, 0, 0));

        assertTrue(camera.isViewable(cursor, 0, 0));
        assertTrue(camera.isViewable(cursor, 1, 3));

        cursor.setLocation(-2, -2);
        cursor.update(1.0);

        assertFalse(camera.isViewable(cursor, 0, 0));
        assertFalse(camera.isViewable(cursor, 0, -1));
        assertFalse(camera.isViewable(cursor, -1, 0));
        assertTrue(camera.isViewable(cursor, 1, 1));

        cursor.setLocation(0, -2);
        cursor.update(1.0);

        assertFalse(camera.isViewable(cursor, 0, 0));
        assertFalse(camera.isViewable(cursor, -1, 0));
        assertFalse(camera.isViewable(cursor, 0, -1));
        assertTrue(camera.isViewable(cursor, 1, 1));

        cursor.setLocation(-2, 0);
        cursor.update(1.0);

        assertFalse(camera.isViewable(cursor, 0, 0));
        assertFalse(camera.isViewable(cursor, 0, -1));
        assertFalse(camera.isViewable(cursor, -1, 0));
        assertTrue(camera.isViewable(cursor, 1, 1));

        cursor.setLocation(-2, 2);
        cursor.update(1.0);

        assertFalse(camera.isViewable(cursor, 0, 0));
        assertFalse(camera.isViewable(cursor, -1, 0));
        assertFalse(camera.isViewable(cursor, 0, -1));
        assertTrue(camera.isViewable(cursor, 1, 1));

        cursor.setLocation(2, -2);
        cursor.update(1.0);

        assertFalse(camera.isViewable(cursor, 0, 0));
        assertTrue(camera.isViewable(cursor, 1, 1));
    }

    /**
     * Test the camera interval.
     */
    @Test
    void testInterval()
    {
        final Transformable transformable = new TransformableModel(new Services(), new Setup(config));
        transformable.teleport(1.0, 2.0);

        camera.setView(0, 0, 1, 1, 1);
        camera.setLimits(surface);
        camera.resetInterval(transformable);

        assertEquals(1.0, camera.getX());
        assertEquals(2.0, camera.getY());

        camera.setIntervals(1, 1);
        camera.moveLocation(1.0, 3.0, 3.0);

        assertEquals(4.0, camera.getX());
        assertEquals(5.0, camera.getY());

        camera.moveLocation(1.0, -2.0, -2.0);

        assertEquals(2.0, camera.getX());
        assertEquals(3.0, camera.getY());

        camera.setIntervals(10, 10);
        camera.resetInterval(transformable);

        camera.moveLocation(1.0, -2.0, -2.0);

        assertEquals(1.0, camera.getX());
        assertEquals(2.0, camera.getY());
    }

    /**
     * Test the camera interval.
     */
    @Test
    void testIntervalLimit()
    {
        camera.setView(0, 0, 0, 0, 0);
        camera.setLimits(surface);

        // Limit right
        camera.moveLocation(1.0, 5.0, 0.0);

        assertEquals(5.0, camera.getX());
        assertEquals(0.0, camera.getY());

        camera.moveLocation(1.0, 20.0, 0.0);

        assertEquals(10.0, camera.getX());
        assertEquals(0.0, camera.getY());

        // Limit left
        camera.moveLocation(1.0, -5.0, 0.0);

        assertEquals(5.0, camera.getX());
        assertEquals(0.0, camera.getY());

        camera.moveLocation(1.0, -20.0, 0.0);

        assertEquals(0.0, camera.getX());
        assertEquals(0.0, camera.getY());

        // Limit top
        camera.moveLocation(1.0, 0.0, 5.0);

        assertEquals(0.0, camera.getX());
        assertEquals(5.0, camera.getY());

        camera.moveLocation(1.0, 0.0, 20.0);

        assertEquals(0.0, camera.getX());
        assertEquals(10.0, camera.getY());

        // Limit bottom
        camera.moveLocation(1.0, 0.0, -5.0);

        assertEquals(0.0, camera.getX());
        assertEquals(5.0, camera.getY());

        camera.moveLocation(1.0, 0.0, -20.0);

        assertEquals(0.0, camera.getX());
        assertEquals(0.0, camera.getY());

        camera.setLimits(new SurfaceTile()
        {
            @Override
            public int getWidth()
            {
                return 0;
            }

            @Override
            public int getHeight()
            {
                return 0;
            }

            @Override
            public int getTileWidth()
            {
                return 0;
            }

            @Override
            public int getTileHeight()
            {
                return 0;
            }
        });

        // Limit right
        camera.moveLocation(1.0, 50.0, 0.0);

        assertEquals(0.0, camera.getX());
        assertEquals(0.0, camera.getY());

        // Limit left
        camera.moveLocation(1.0, 50.0, 0.0);

        assertEquals(0.0, camera.getX());
        assertEquals(0.0, camera.getY());

        // Limit top
        camera.moveLocation(1.0, 0.0, 50.0);

        assertEquals(0.0, camera.getX());
        assertEquals(0.0, camera.getY());

        // Limit bottom
        camera.moveLocation(1.0, 0.0, -50.0);

        assertEquals(0.0, camera.getX());
        assertEquals(0.0, camera.getY());
    }

    /**
     * Test the camera interval.
     */
    @Test
    void testIntervalNoLimit()
    {
        // Limit right
        camera.moveLocation(1.0, Integer.MAX_VALUE + 1.0, 0.0);

        assertEquals(Integer.MAX_VALUE + 1.0, camera.getX());
        assertEquals(0.0, camera.getY());

        // Limit left
        camera.moveLocation(1.0, Integer.MIN_VALUE * 2.0 - 1.0, 0.0);

        assertEquals(Integer.MIN_VALUE - 1.0, camera.getX());
        assertEquals(0.0, camera.getY());

        // Limit top
        camera.moveLocation(1.0, 0.0, Integer.MAX_VALUE + 1.0);

        assertEquals(Integer.MIN_VALUE - 1.0, camera.getX());
        assertEquals(Integer.MAX_VALUE + 1.0, camera.getY());

        // Limit bottom
        camera.moveLocation(1.0, 0.0, Integer.MIN_VALUE * 2.0 - 1);

        assertEquals(Integer.MIN_VALUE - 1.0, camera.getX());
        assertEquals(Integer.MIN_VALUE - 1.0, camera.getY());
    }

    /**
     * Test the shake effect.
     */
    @Test
    void testShake()
    {
        camera.setLocation(5.0, 10.0);

        assertEquals(5.0, camera.getX());
        assertEquals(10.0, camera.getY());

        camera.setShake(1, 2);

        assertEquals(6.0, camera.getX());
        assertEquals(12.0, camera.getY());
    }
}
