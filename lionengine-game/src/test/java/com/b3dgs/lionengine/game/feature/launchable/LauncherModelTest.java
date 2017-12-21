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
package com.b3dgs.lionengine.game.feature.launchable;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import com.b3dgs.lionengine.Constant;
import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Localizable;
import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.core.Medias;
import com.b3dgs.lionengine.game.Force;
import com.b3dgs.lionengine.game.feature.Featurable;
import com.b3dgs.lionengine.game.feature.FeaturableModel;
import com.b3dgs.lionengine.game.feature.Handler;
import com.b3dgs.lionengine.game.feature.Services;
import com.b3dgs.lionengine.game.feature.Setup;
import com.b3dgs.lionengine.game.feature.Transformable;
import com.b3dgs.lionengine.game.feature.TransformableModel;
import com.b3dgs.lionengine.game.feature.UtilSetup;
import com.b3dgs.lionengine.util.UtilTests;

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
        Assert.assertEquals(0, launcher.getLevel());
        Assert.assertEquals(10, launcher.getRate());
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
        final AtomicBoolean fired = new AtomicBoolean();
        final AtomicReference<Launchable> firedLaunchable = new AtomicReference<Launchable>();
        launcher.addListener(UtilLaunchable.createListener(fired));
        launcher.addListener(UtilLaunchable.createListener(firedLaunchable));

        while (!launcher.fire())
        {
            continue;
        }

        Assert.assertTrue(fired.get());
        Assert.assertNotNull(firedLaunchable.get());
        firedLaunchable.set(null);

        final Handler handler = services.get(Handler.class);
        handler.update(1.0);

        Assert.assertEquals(1, handler.size());

        final Transformable transformable = new TransformableModel();
        while (!launcher.fire(transformable))
        {
            continue;
        }

        Assert.assertNotNull(firedLaunchable.get());
        firedLaunchable.set(null);

        handler.update(1.0);

        Assert.assertEquals(2, handler.size());

        final Localizable localizable = UtilLaunchable.createLocalizable();
        while (!launcher.fire(localizable))
        {
            continue;
        }

        Assert.assertNotNull(firedLaunchable.get());

        handler.update(1.0);

        Assert.assertEquals(3, handler.size());

        handler.removeAll();
        handler.update(1.0);

        Assert.assertEquals(0, handler.size());
    }

    /**
     * Test the launcher with initial speed.
     * 
     * @throws InterruptedException If error.
     */
    @Test
    public void testLauncherInitial() throws InterruptedException
    {
        final AtomicBoolean fired = new AtomicBoolean();
        final AtomicReference<Launchable> firedLaunchable = new AtomicReference<Launchable>();
        launcher.addListener(UtilLaunchable.createListener(fired));
        launcher.addListener(UtilLaunchable.createListener(firedLaunchable));

        final Force force = new Force(1.0, 2.0);
        while (!launcher.fire(force))
        {
            continue;
        }

        Assert.assertTrue(fired.get());
        Assert.assertNotNull(firedLaunchable.get());

        final Transformable transformable = firedLaunchable.get().getFeature(Transformable.class);

        Assert.assertEquals(2.0, transformable.getX(), UtilTests.PRECISION);
        Assert.assertEquals(4.0, transformable.getY(), UtilTests.PRECISION);

        final Handler handler = services.get(Handler.class);
        handler.update(1.0);

        Assert.assertEquals(1, handler.size());

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

        final AtomicBoolean fired = new AtomicBoolean();
        final AtomicReference<Launchable> firedLaunchable = new AtomicReference<Launchable>();
        launcher.addListener(UtilLaunchable.createListener(fired));
        launcher.addListener(UtilLaunchable.createListener(firedLaunchable));

        while (!launcher.fire())
        {
            continue;
        }

        final Handler handler = services.get(Handler.class);
        launcher.update(1.0);
        handler.update(1.0);

        Assert.assertEquals(0, handler.size());
        Assert.assertTrue(fired.get());
        Assert.assertNull(firedLaunchable.get());

        Thread.sleep(50);
        launcher.update(1.0);
        handler.update(1.0);

        Assert.assertNotNull(firedLaunchable.get());
        Assert.assertEquals(1, handler.size());

        firedLaunchable.set(null);
        handler.removeAll();
        handler.update(1.0);

        Assert.assertEquals(0, handler.size());
    }

    /**
     * Test the launcher level.
     * 
     * @throws InterruptedException If error.
     */
    @Test
    public void testLauncherLevel() throws InterruptedException
    {
        final AtomicBoolean fired = new AtomicBoolean();
        final AtomicReference<Launchable> firedLaunchable = new AtomicReference<Launchable>();
        launcher.addListener(UtilLaunchable.createListener(fired));
        launcher.addListener(UtilLaunchable.createListener(firedLaunchable));

        while (!launcher.fire())
        {
            continue;
        }

        Assert.assertTrue(fired.get());
        Assert.assertNotNull(firedLaunchable.get());
        firedLaunchable.set(null);

        final Handler handler = services.get(Handler.class);
        handler.update(1.0);

        Assert.assertEquals(1, handler.size());

        launcher.setLevel(1);

        Assert.assertNull(firedLaunchable.get());

        while (!launcher.fire())
        {
            continue;
        }
        while (!launcher.fire())
        {
            continue;
        }

        Assert.assertNotNull(firedLaunchable.get());

        handler.update(1.0);

        Assert.assertEquals(3, handler.size());

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
        launcher.addListener((LauncherListener) object);
        launcher.addListener((LaunchableListener) object);

        Assert.assertFalse(object.fired.get());
        Assert.assertNull(object.firedLaunchable.get());

        while (!launcher.fire())
        {
            continue;
        }
        final Handler handler = services.get(Handler.class);
        handler.update(1.0);

        Assert.assertTrue(object.fired.get());
        Assert.assertNotNull(object.firedLaunchable.get());

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

        Assert.assertFalse(object.fired.get());
        Assert.assertNull(object.firedLaunchable.get());

        while (!launcher.fire())
        {
            continue;
        }
        final Handler handler = services.get(Handler.class);
        handler.update(1.0);

        Assert.assertTrue(object.fired.get());
        Assert.assertNotNull(object.firedLaunchable.get());

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
            while (!launcher.fire())
            {
                continue;
            }
            while (!launcher.fire())
            {
                continue;
            }
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
            while (!launcher.fire())
            {
                continue;
            }
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
