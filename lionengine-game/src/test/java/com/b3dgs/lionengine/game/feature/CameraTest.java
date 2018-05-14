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
package com.b3dgs.lionengine.game.feature;

import static com.b3dgs.lionengine.UtilAssert.assertEquals;
import static com.b3dgs.lionengine.UtilAssert.assertFalse;
import static com.b3dgs.lionengine.UtilAssert.assertTrue;

import org.junit.jupiter.api.Test;

import com.b3dgs.lionengine.Localizable;
import com.b3dgs.lionengine.Surface;
import com.b3dgs.lionengine.SurfaceTile;
import com.b3dgs.lionengine.game.Cursor;
import com.b3dgs.lionengine.graphic.GraphicMock;

/**
 * Test {@link Camera}.
 */
public final class CameraTest
{
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
    public void testCameraDefault()
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
     * Test the camera teleport.
     */
    @Test
    public void testCameraTeleport()
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
    public void testCameraMovement()
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
    public void testCameraViewpoint()
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
    public void testCameraView()
    {
        camera.setView(1, 2, 3, 4, 4);

        assertEquals(1, camera.getViewX());
        assertEquals(2, camera.getViewY());
        assertEquals(3, camera.getWidth());
        assertEquals(4, camera.getHeight());

        camera.drawFov(new GraphicMock(), 0, 0, 1, 1, surface);
    }

    /**
     * Test the camera viewable.
     */
    @Test
    public void testIsViewable()
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

        cursor.setLocation(3, 3);
        cursor.update(1.0);

        assertFalse(camera.isViewable((Localizable) cursor, 0, 0));
        assertFalse(camera.isViewable((Localizable) cursor, 0, 1));
        assertFalse(camera.isViewable((Localizable) cursor, 1, 0));
        assertTrue(camera.isViewable((Localizable) cursor, 1, 1));

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
    public void testInterval()
    {
        final Transformable transformable = new TransformableModel();
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
    }

    /**
     * Test the camera interval.
     */
    @Test
    public void testIntervalLimit()
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

        // Limit top
        camera.moveLocation(1.0, 0.0, 50.0);

        assertEquals(0.0, camera.getX());
        assertEquals(0.0, camera.getY());
    }

    /**
     * Test the camera interval.
     */
    @Test
    public void testIntervalNoLimit()
    {
        // Limit right
        camera.moveLocation(1.0, 20.0, 0.0);

        assertEquals(20.0, camera.getX());
        assertEquals(0.0, camera.getY());

        // Limit left
        camera.moveLocation(1.0, -20.0, 0.0);

        assertEquals(0.0, camera.getX());
        assertEquals(0.0, camera.getY());

        // Limit top
        camera.moveLocation(1.0, 0.0, 20.0);

        assertEquals(0.0, camera.getX());
        assertEquals(20.0, camera.getY());

        // Limit bottom
        camera.moveLocation(1.0, 0.0, -20.0);

        assertEquals(0.0, camera.getX());
        assertEquals(0.0, camera.getY());
    }
}
