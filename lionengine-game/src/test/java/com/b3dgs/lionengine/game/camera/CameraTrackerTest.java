/*
 * Copyright (C) 2013-2016 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.game.camera;

import org.junit.Assert;
import org.junit.Test;

import com.b3dgs.lionengine.game.feature.Featurable;
import com.b3dgs.lionengine.game.feature.FeaturableModel;
import com.b3dgs.lionengine.game.feature.Services;
import com.b3dgs.lionengine.game.feature.transformable.Transformable;
import com.b3dgs.lionengine.game.feature.transformable.TransformableModel;
import com.b3dgs.lionengine.test.UtilTests;

/**
 * Test the camera tracker class.
 */
public class CameraTrackerTest
{
    /**
     * Test the tracker feature.
     */
    @Test
    public void testTracker()
    {
        final CameraTracker tracker = new CameraTracker();
        final Services services = new Services();
        final Camera camera = new Camera();
        services.add(camera);
        camera.setView(0, 0, 16, 32);
        tracker.prepare(new FeaturableModel(), services);
        tracker.update(1.0);

        Assert.assertEquals(0.0, camera.getX(), UtilTests.PRECISION);
        Assert.assertEquals(0.0, camera.getY(), UtilTests.PRECISION);

        final Transformable transformable = new TransformableModel();
        transformable.teleport(1.0, 2.0);

        tracker.track(transformable);
        tracker.update(1.0);

        Assert.assertEquals(-7.0, camera.getX(), UtilTests.PRECISION);
        Assert.assertEquals(-14.0, camera.getY(), UtilTests.PRECISION);

        final Featurable featurable = new FeaturableModel();
        featurable.addFeature(transformable);

        transformable.teleport(2.0, 3.0);

        tracker.track(featurable);
        tracker.update(1.0);

        Assert.assertEquals(-6.0, camera.getX(), UtilTests.PRECISION);
        Assert.assertEquals(-13.0, camera.getY(), UtilTests.PRECISION);
    }
}
