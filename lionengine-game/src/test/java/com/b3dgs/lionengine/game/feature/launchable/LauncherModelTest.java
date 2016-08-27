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

import java.util.concurrent.atomic.AtomicReference;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import com.b3dgs.lionengine.Constant;
import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.core.Medias;
import com.b3dgs.lionengine.game.feature.Featurable;
import com.b3dgs.lionengine.game.feature.FeaturableModel;
import com.b3dgs.lionengine.game.feature.Services;
import com.b3dgs.lionengine.game.feature.Setup;
import com.b3dgs.lionengine.game.feature.UtilSetup;
import com.b3dgs.lionengine.game.feature.transformable.TransformableModel;
import com.b3dgs.lionengine.game.handler.Handler;
import com.b3dgs.lionengine.test.UtilTests;

/**
 * Test the launcher.
 */
public class LauncherModelTest
{
    /**
     * Prepare test.
     */
    @BeforeClass
    public static void setUp()
    {
        Medias.setResourcesDirectory(System.getProperty("java.io.tmpdir"));
    }

    /**
     * Clean up test.
     */
    @AfterClass
    public static void cleanUp()
    {
        Medias.setResourcesDirectory(Constant.EMPTY_STRING);
    }

    private final Media launchableMedia = UtilSetup.createMedia(LaunchableObject.class);
    private final Media launcherMedia = UtilLaunchable.createLauncherMedia(launchableMedia);
    private final Services services = new Services();
    private final Setup setup = new Setup(launcherMedia);
    private final Featurable featurable = new FeaturableModel();
    private final Launcher launcher = UtilLaunchable.createLauncher(services, setup, featurable);

    /**
     * Clean test.
     */
    @After
    public void clean()
    {
        Assert.assertTrue(launchableMedia.getFile().delete());
        Assert.assertTrue(launcherMedia.getFile().delete());
    }

    /**
     * Test the config.
     */
    @Test
    public void testConfig()
    {
        Assert.assertEquals(1.0, launcher.getOffsetX(), UtilTests.PRECISION);
        Assert.assertEquals(2.0, launcher.getOffsetY(), UtilTests.PRECISION);
    }

    /**
     * Test the launcher.
     * 
     * @throws InterruptedException If error.
     */
    @Test
    public void testLauncher() throws InterruptedException
    {
        final AtomicReference<Featurable> fired = new AtomicReference<Featurable>();
        launcher.addListener(UtilLaunchable.createListener(fired));

        Thread.sleep(11);
        launcher.fire();

        Assert.assertNotNull(fired.get());
        fired.set(null);

        final Handler handler = services.get(Handler.class);
        handler.update(1.0);

        Assert.assertEquals(1, handler.size());

        Thread.sleep(11);
        launcher.fire(new TransformableModel());

        Assert.assertNotNull(fired.get());
        fired.set(null);

        handler.update(1.0);

        Assert.assertEquals(2, handler.size());

        Thread.sleep(11);
        launcher.fire(UtilLaunchable.createLocalizable());

        Assert.assertNotNull(fired.get());

        handler.update(1.0);

        Assert.assertEquals(3, handler.size());

        handler.removeAll();
        handler.update(1.0);

        Assert.assertEquals(0, handler.size());
    }

    /**
     * Test the launcher with delay.
     * 
     * @throws InterruptedException If error.
     */
    @Test
    public void testLauncherDelay() throws InterruptedException
    {
        final Media launcherMedia = UtilLaunchable.createLauncherMedia(launchableMedia, 10);
        final Setup setup = new Setup(launcherMedia);
        final Launcher launcher = UtilLaunchable.createLauncher(services, setup, featurable);

        final AtomicReference<Featurable> fired = new AtomicReference<Featurable>();
        launcher.addListener(UtilLaunchable.createListener(fired));

        Thread.sleep(11);
        launcher.fire();

        final Handler handler = services.get(Handler.class);
        launcher.update(1.0);
        handler.update(1.0);

        Assert.assertEquals(0, handler.size());
        Assert.assertNull(fired.get());

        Thread.sleep(11);
        launcher.update(1.0);
        handler.update(1.0);

        Assert.assertNotNull(fired.get());
        Assert.assertEquals(1, handler.size());

        fired.set(null);
        handler.removeAll();
        handler.update(1.0);

        Assert.assertEquals(0, handler.size());
    }

    /**
     * Test the launcher with listener itself.
     * 
     * @throws InterruptedException If error.
     */
    @Test
    public void testLauncherSelfListener() throws InterruptedException
    {
        final LaunchableObjectSelf object = new LaunchableObjectSelf();
        final Launcher launcher = UtilLaunchable.createLauncher(services, setup, object);
        launcher.addListener(object);

        Assert.assertNull(object.fired.get());

        Thread.sleep(11);
        launcher.fire();
        final Handler handler = services.get(Handler.class);
        handler.update(1.0);

        Assert.assertNotNull(object.fired.get());

        handler.removeAll();
        handler.update(1.0);

        Assert.assertEquals(0, handler.size());
    }

    /**
     * Test the launcher with listener auto add.
     * 
     * @throws InterruptedException If error.
     */
    @Test
    public void testListenerAutoAdd() throws InterruptedException
    {
        final LaunchableObjectSelf object = new LaunchableObjectSelf();
        final Launcher launcher = UtilLaunchable.createLauncher(services, setup, object);
        launcher.checkListener(object);

        Assert.assertNull(object.fired.get());

        Thread.sleep(11);
        launcher.fire();
        final Handler handler = services.get(Handler.class);
        handler.update(1.0);

        Assert.assertNotNull(object.fired.get());

        handler.removeAll();
        handler.update(1.0);

        Assert.assertEquals(0, handler.size());
    }

    /**
     * Test the launcher failure.
     * 
     * @throws InterruptedException If error.
     */
    @Test(expected = LionEngineException.class)
    public void testLauncherFailure() throws InterruptedException
    {
        final Media launchableMedia = UtilSetup.createMedia(Featurable.class);
        final Media launcherMedia = UtilLaunchable.createLauncherMedia(launchableMedia);
        final Setup setup = new Setup(launcherMedia);
        final Launcher launcher = UtilLaunchable.createLauncher(services, setup, featurable);

        try
        {
            launcher.fire();
            Thread.sleep(11);
            launcher.fire();
            Assert.fail();
        }
        finally
        {
            final Handler handler = services.get(Handler.class);
            handler.removeAll();
            handler.update(1.0);

            Assert.assertEquals(0, handler.size());
            Assert.assertTrue(launchableMedia.getFile().delete());
        }
    }

    /**
     * Test the launcher failure.
     * 
     * @throws InterruptedException If error.
     */
    @Test(expected = LionEngineException.class)
    public void testLauncherException() throws InterruptedException
    {
        final Media launchableMedia = UtilSetup.createMedia(LaunchableObjectException.class);
        final Media launcherMedia = UtilLaunchable.createLauncherMedia(launchableMedia);
        final Setup setup = new Setup(launcherMedia);
        final Launcher launcher = UtilLaunchable.createLauncher(services, setup, featurable);

        try
        {
            Thread.sleep(11);
            launcher.fire();
            Assert.fail();
        }
        finally
        {
            final Handler handler = services.get(Handler.class);
            handler.removeAll();
            handler.update(1.0);

            Assert.assertEquals(0, handler.size());
            Assert.assertTrue(launchableMedia.getFile().delete());
        }
    }
}
