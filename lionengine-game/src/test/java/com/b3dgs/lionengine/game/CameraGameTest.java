/*
 * Copyright (C) 2013 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.game;

import org.junit.Assert;
import org.junit.Test;

import com.b3dgs.lionengine.game.purview.model.LocalizableModel;

/**
 * Test camera class.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public class CameraGameTest
{
    /**
     * Test camera functions.
     */
    @Test
    public void testCamera()
    {
        final CameraGame camera = new CameraGame();
        camera.setView(0, 0, 3, 4);
        Assert.assertTrue(camera.getViewX() == 0);
        Assert.assertTrue(camera.getViewY() == 0);
        Assert.assertTrue(camera.getViewWidth() == 3);
        Assert.assertTrue(camera.getViewHeight() == 4);

        camera.setLocation(2.0, 2.0);
        Assert.assertTrue(camera.getLocationX() == 2.0);
        Assert.assertTrue(camera.getLocationY() == 2.0);
        Assert.assertTrue(camera.getLocationRealX() == 2.0);
        Assert.assertTrue(camera.getLocationRealY() == 2.0);
        Assert.assertTrue(camera.getLocationIntX() == 2);
        Assert.assertTrue(camera.getLocationIntY() == 2);
        Assert.assertTrue(camera.getLocationOldX() == 0.0);
        Assert.assertTrue(camera.getLocationOldY() == 0.0);

        Assert.assertTrue(camera.getLocationOffsetX() == 0);
        Assert.assertTrue(camera.getLocationOffsetY() == 0);

        Assert.assertTrue(camera.getMovementHorizontal() == 2.0);
        Assert.assertTrue(camera.getMovementVertical() == 2.0);

        camera.teleportX(0);
        Assert.assertTrue(camera.getMovementHorizontal() == 0.0);

        camera.teleportY(0);
        Assert.assertTrue(camera.getMovementVertical() == 0.0);

        camera.teleport(1, 1);
        Assert.assertTrue(camera.getMovementHorizontal() == 0.0);
        Assert.assertTrue(camera.getMovementVertical() == 0.0);

        camera.setSize(0, 0);
        Assert.assertTrue(camera.getWidth() == 0);
        Assert.assertTrue(camera.getHeight() == 0);

        Assert.assertTrue(camera.getViewpointX(0) == -1);
        Assert.assertTrue(camera.getViewpointY(0) == 5);

        camera.setIntervals(-15, 15);
        camera.resetInterval(new LocalizableModel());
        camera.moveLocation(1.0, 5, 5);
        camera.moveLocation(1.0, 32, 32);
        camera.moveLocation(1.0, -64, 64);
        camera.moveLocation(1.0, 96, -96);
        camera.moveLocation(1.0, -128, -128);
        camera.moveLocation(1.0, Force.ZERO, Force.ZERO);

        camera.setIntervals(0, 0);
        camera.resetInterval(new LocalizableModel());
        camera.moveLocation(1.0, 5, 5);
        camera.moveLocation(1.0, -5, -5);
        
        camera.setLocationOffset(0, 0);
        camera.setLocationX(0);
        camera.setLocationY(0);
        Assert.assertTrue(camera.getLocationIntX() == 0);
        Assert.assertTrue(camera.getLocationIntY() == 0);

        Assert.assertTrue(camera.isVisible(new LocalizableModel()));
        
        Assert.assertFalse(camera.isVisible(new LocalizableModel(-10, -10), 0, 0));
        Assert.assertFalse(camera.isVisible(new LocalizableModel(-10, -10), 0, 10));
        Assert.assertFalse(camera.isVisible(new LocalizableModel(-10, -10), 10, 0));
        
        Assert.assertFalse(camera.isVisible(new LocalizableModel(10, 10), 0, 0));
        Assert.assertFalse(camera.isVisible(new LocalizableModel(10, 10), 0, 10));
        Assert.assertFalse(camera.isVisible(new LocalizableModel(10, 10), 10, 0));
        
        Assert.assertTrue(camera.isVisible(new LocalizableModel(-10, -10), 10, 10));
        Assert.assertTrue(camera.isVisible(new LocalizableModel(10, 10), 10, 10));
    }
}
