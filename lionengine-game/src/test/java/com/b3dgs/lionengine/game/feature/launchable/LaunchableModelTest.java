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
package com.b3dgs.lionengine.game.feature.launchable;

import java.util.concurrent.atomic.AtomicBoolean;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.b3dgs.lionengine.game.feature.Featurable;
import com.b3dgs.lionengine.game.feature.FeaturableModel;
import com.b3dgs.lionengine.game.feature.FeatureModel;
import com.b3dgs.lionengine.game.feature.Services;
import com.b3dgs.lionengine.game.feature.identifiable.Identifiable;
import com.b3dgs.lionengine.game.feature.transformable.Transformable;
import com.b3dgs.lionengine.game.feature.transformable.TransformableModel;
import com.b3dgs.lionengine.game.map.MapTileGame;
import com.b3dgs.lionengine.test.UtilTests;

/**
 * Test the launchable model.
 */
public class LaunchableModelTest
{
    private final Services services = new Services();
    private final Featurable featurable = new FeaturableModel();
    private final Launchable launchable = UtilLaunchable.createLaunchable(services, featurable);
    private final Transformable transformable = featurable.getFeature(Transformable.class);

    /**
     * Prepare test.
     */
    @Before
    public void prepare()
    {
        services.add(new MapTileGame());
    }

    /**
     * Clean test.
     */
    @After
    public void clean()
    {
        featurable.getFeature(Identifiable.class).notifyDestroyed();
    }

    /**
     * Test the launch.
     * 
     * @throws InterruptedException If error.
     */
    @Test
    public void testLaunch() throws InterruptedException
    {
        launchable.launch();

        Assert.assertEquals(0.0, transformable.getOldX(), UtilTests.PRECISION);
        Assert.assertEquals(0.0, transformable.getOldY(), UtilTests.PRECISION);
        Assert.assertEquals(0.0, transformable.getX(), UtilTests.PRECISION);
        Assert.assertEquals(0.0, transformable.getY(), UtilTests.PRECISION);

        launchable.update(1.0);

        Assert.assertEquals(0.0, transformable.getOldX(), UtilTests.PRECISION);
        Assert.assertEquals(0.0, transformable.getOldY(), UtilTests.PRECISION);
        Assert.assertEquals(0.0, transformable.getX(), UtilTests.PRECISION);
        Assert.assertEquals(1.0, transformable.getY(), UtilTests.PRECISION);

        Thread.sleep(11);
        launchable.update(1.0);

        Assert.assertEquals(0.0, transformable.getOldX(), UtilTests.PRECISION);
        Assert.assertEquals(1.0, transformable.getOldY(), UtilTests.PRECISION);
        Assert.assertEquals(0.0, transformable.getX(), UtilTests.PRECISION);
        Assert.assertEquals(2.0, transformable.getY(), UtilTests.PRECISION);

        launchable.update(1.0);

        Assert.assertEquals(0.0, transformable.getOldX(), UtilTests.PRECISION);
        Assert.assertEquals(2.0, transformable.getOldY(), UtilTests.PRECISION);
        Assert.assertEquals(0.0, transformable.getX(), UtilTests.PRECISION);
        Assert.assertEquals(3.0, transformable.getY(), UtilTests.PRECISION);
    }

    /**
     * Test the launch without vector.
     * 
     * @throws InterruptedException If error.
     */
    @Test
    public void testLaunchNoVector() throws InterruptedException
    {
        launchable.setVector(null);
        launchable.launch();
        launchable.update(1.0);

        Assert.assertEquals(0.0, transformable.getOldX(), UtilTests.PRECISION);
        Assert.assertEquals(0.0, transformable.getOldY(), UtilTests.PRECISION);
        Assert.assertEquals(0.0, transformable.getX(), UtilTests.PRECISION);
        Assert.assertEquals(0.0, transformable.getY(), UtilTests.PRECISION);

        Thread.sleep(11);
        launchable.update(1.0);

        Assert.assertEquals(0.0, transformable.getOldX(), UtilTests.PRECISION);
        Assert.assertEquals(0.0, transformable.getOldY(), UtilTests.PRECISION);
        Assert.assertEquals(0.0, transformable.getX(), UtilTests.PRECISION);
        Assert.assertEquals(0.0, transformable.getY(), UtilTests.PRECISION);
    }

    /**
     * Test the launch listener.
     */
    @Test
    public void testListener()
    {
        final AtomicBoolean fired = new AtomicBoolean();
        launchable.addListener(new LaunchableListener()
        {
            @Override
            public void notifyFired(Launchable launchable)
            {
                fired.set(true);
            }
        });

        Assert.assertFalse(fired.get());

        launchable.launch();

        Assert.assertTrue(fired.get());
    }

    /**
     * Test the check listener.
     */
    @Test
    public void testCheck()
    {
        final Featurable featurable = new FeaturableModel();
        featurable.addFeature(new TransformableModel());
        featurable.addFeature(new LaunchableModel());
        featurable.addFeature(new Self());
        featurable.prepareFeatures(new Services());
    }

    /**
     * Self listener.
     */
    private static class Self extends FeatureModel implements LaunchableListener
    {
        /**
         * Create self.
         */
        Self()
        {
            super();
        }

        @Override
        public void notifyFired(Launchable launchable)
        {
            // Nothing to do
        }
    }
}
