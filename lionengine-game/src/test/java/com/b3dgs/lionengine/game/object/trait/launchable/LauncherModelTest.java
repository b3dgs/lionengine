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
package com.b3dgs.lionengine.game.object.trait.launchable;

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicReference;

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
import com.b3dgs.lionengine.game.object.Factory;
import com.b3dgs.lionengine.game.object.Handler;
import com.b3dgs.lionengine.game.object.ObjectGame;
import com.b3dgs.lionengine.game.object.ObjectGameTest;
import com.b3dgs.lionengine.game.object.Services;
import com.b3dgs.lionengine.game.object.Setup;
import com.b3dgs.lionengine.game.object.trait.transformable.TransformableModel;
import com.b3dgs.lionengine.stream.Xml;
import com.b3dgs.lionengine.stream.XmlNode;
import com.b3dgs.lionengine.util.UtilTests;

/**
 * Test the launcher trait.
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

    /**
     * Create the media.
     * 
     * @param launchableMedia The launchable.
     * @return The media.
     */
    private static Media createLauncherMedia(Media launchableMedia)
    {
        final Media media = Medias.create("launcher.xml");
        final LaunchableConfig launchableConfig = new LaunchableConfig(launchableMedia.getPath(),
                                                                       10,
                                                                       new Force(1.0, 2.0));
        final LauncherConfig launcherConfig = new LauncherConfig(10, Arrays.asList(launchableConfig));

        final XmlNode root = Xml.create("test");
        root.add(LauncherConfig.exports(launcherConfig));
        Xml.save(root, media);

        return media;
    }

    /**
     * Create launcher.
     * 
     * @param services The services.
     * @param object The object.
     * @return The extractable.
     */
    private static Launcher createLauncher(Services services, ObjectGame object)
    {
        final Launcher launcher = new LauncherModel();
        services.add(new Factory(services));
        services.add(new Handler());
        object.addType(new TransformableModel());

        launcher.prepare(object, services);
        launcher.setOffset(1, 2);
        launcher.setRate(10);

        return launcher;
    }

    /**
     * Create a localizable.
     * 
     * @return Localizable.
     */
    private static Localizable createLocalizable()
    {
        return new Localizable()
        {
            @Override
            public double getY()
            {
                return 0;
            }

            @Override
            public double getX()
            {
                return 0;
            }
        };
    }

    /**
     * Create a listener.
     * 
     * @param fired The fired flag.
     * @return The listener.
     */
    private static LauncherListener createListener(final AtomicReference<ObjectGame> fired)
    {
        return new LauncherListener()
        {
            @Override
            public void notifyFired(ObjectGame object)
            {
                fired.set(object);
            }
        };
    }

    /**
     * Test the config.
     */
    @Test
    public void testConfig()
    {
        final Media launchableMedia = Medias.create("launchable.xml");
        final Media launcherMedia = createLauncherMedia(launchableMedia);
        final Services services = new Services();
        final ObjectGame object = new ObjectGame(new Setup(launcherMedia), services);
        final Launcher launcher = createLauncher(services, object);

        Assert.assertEquals(1.0, launcher.getOffsetX(), UtilTests.PRECISION);
        Assert.assertEquals(2.0, launcher.getOffsetY(), UtilTests.PRECISION);

        Assert.assertTrue(launcherMedia.getFile().delete());
    }

    /**
     * Test the launcher.
     * 
     * @throws InterruptedException If error.
     */
    @Test
    public void testLauncher() throws InterruptedException
    {
        final Media launchableMedia = ObjectGameTest.createMedia(LaunchableObject.class);
        final Media launcherMedia = createLauncherMedia(launchableMedia);
        final Services services = new Services();
        final ObjectGame object = new ObjectGame(new Setup(launcherMedia), services);
        final Launcher launcher = createLauncher(services, object);

        final AtomicReference<ObjectGame> fired = new AtomicReference<ObjectGame>();
        launcher.addListener(createListener(fired));

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
        launcher.fire(createLocalizable());

        Assert.assertNotNull(fired.get());

        handler.update(1.0);

        Assert.assertEquals(3, handler.size());

        handler.removeAll();
        handler.update(1.0);
        Assert.assertEquals(0, handler.size());

        Assert.assertTrue(launchableMedia.getFile().delete());
        Assert.assertTrue(launcherMedia.getFile().delete());
    }

    /**
     * Test the launcher with listener itself.
     * 
     * @throws InterruptedException If error.
     */
    @Test
    public void testLauncherSelfListener() throws InterruptedException
    {
        final Media launchableMedia = ObjectGameTest.createMedia(LaunchableObjectSelf.class);
        final Media launcherMedia = createLauncherMedia(launchableMedia);
        final Services services = new Services();
        final LaunchableObjectSelf object = new LaunchableObjectSelf(new Setup(launcherMedia), services);
        final Launcher launcher = createLauncher(services, object);
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

        Assert.assertTrue(launchableMedia.getFile().delete());
        Assert.assertTrue(launcherMedia.getFile().delete());
    }

    /**
     * Test the launcher failure.
     * 
     * @throws InterruptedException If error.
     */
    @Test(expected = LionEngineException.class)
    public void testLauncherFailure() throws InterruptedException
    {
        final Media launchableMedia = ObjectGameTest.createMedia(ObjectGame.class);
        final Media launcherMedia = createLauncherMedia(launchableMedia);
        final Services services = new Services();
        final ObjectGame object = new ObjectGame(new Setup(launcherMedia), services);
        final Launcher launcher = createLauncher(services, object);

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

            Assert.assertTrue(launcherMedia.getFile().delete());
            Assert.assertTrue(launchableMedia.getFile().delete());
        }
    }

    /**
     * Launchable object test.
     */
    private static class LaunchableObject extends ObjectGame
    {
        /**
         * Constructor.
         * 
         * @param setup The setup.
         * @param services The services.
         */
        public LaunchableObject(Setup setup, Services services)
        {
            super(setup, services);
            addTrait(new TransformableModel());
            addTrait(new LaunchableModel());
        }
    }

    /**
     * Launchable object test self listener.
     */
    private static class LaunchableObjectSelf extends LaunchableObject implements LauncherListener
    {
        /** Fired flag. */
        private final AtomicReference<ObjectGame> fired = new AtomicReference<ObjectGame>();

        /**
         * Constructor.
         * 
         * @param setup The setup.
         * @param services The services.
         */
        public LaunchableObjectSelf(Setup setup, Services services)
        {
            super(setup, services);
        }

        @Override
        public void notifyFired(ObjectGame object)
        {
            fired.set(object);
        }
    }
}
