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
package com.b3dgs.lionengine;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicReference;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import com.b3dgs.lionengine.core.Medias;
import com.b3dgs.lionengine.core.drawable.Drawable;
import com.b3dgs.lionengine.graphic.FactoryGraphicMock;
import com.b3dgs.lionengine.graphic.Graphics;
import com.b3dgs.lionengine.graphic.Image;
import com.b3dgs.lionengine.util.UtilTests;

/**
 * Test the resource loader class.
 */
public class ResourceLoaderTest
{
    /**
     * Test type.
     */
    private static enum Type
    {
        /** Test type. */
        TEST;
    }

    /**
     * Prepare test.
     */
    @BeforeClass
    public static void setUp()
    {
        Medias.setLoadFromJar(ResourceLoaderTest.class);
        Graphics.setFactoryGraphic(new FactoryGraphicMock());
    }

    /**
     * Clean up test.
     */
    @AfterClass
    public static void cleanUp()
    {
        Medias.setLoadFromJar(null);
        Graphics.setFactoryGraphic(null);
    }

    /**
     * Test the resource loader.
     */
    @Test
    public void testResourceLoader()
    {
        final ResourceLoader<Type> resourceLoader = new ResourceLoader<>();
        final Image resource = Drawable.loadImage(Medias.create("image.png"));

        resourceLoader.add(Type.TEST, resource);

        Assert.assertFalse(resource.isLoaded());
        Assert.assertNull(resource.getSurface());
        Assert.assertFalse(resourceLoader.isFinished());

        resourceLoader.start();
        resourceLoader.await();

        Assert.assertTrue(resourceLoader.isFinished());

        resource.prepare();
        Assert.assertTrue(resource.isLoaded());
        Assert.assertNotNull(resource.getSurface());
        Assert.assertEquals(resourceLoader.get().get(Type.TEST), resource);
    }

    /**
     * Test the resource loader already started.
     */
    @Test(expected = LionEngineException.class)
    public void testResourceLoaderFailStart()
    {
        final ResourceLoader<Type> resourceLoader = new ResourceLoader<>();
        resourceLoader.start();
        resourceLoader.start();
    }

    /**
     * Test the resource loader not started get.
     */
    @Test(expected = LionEngineException.class)
    public void testResourceLoaderFailNotStartedGet()
    {
        final ResourceLoader<Type> resourceLoader = new ResourceLoader<>();
        Assert.assertNull(resourceLoader.get());
    }

    /**
     * Test the resource loader not started await.
     */
    @Test(expected = LionEngineException.class)
    public void testResourceLoaderFailNotStartedAwait()
    {
        final ResourceLoader<Type> resourceLoader = new ResourceLoader<>();
        resourceLoader.await();
    }

    /**
     * Test the resource loader already started add.
     */
    @Test(expected = LionEngineException.class)
    public void testResourceLoaderFailStartedAdd()
    {
        final ResourceLoader<Type> resourceLoader = new ResourceLoader<>();
        resourceLoader.start();
        resourceLoader.add(null, null);
    }

    /**
     * Test the resource loader skip.
     * 
     * @throws InterruptedException If error.
     */
    @Test(expected = LionEngineException.class, timeout = 500)
    public void testResourceLoaderSkip() throws InterruptedException
    {
        final CountDownLatch startedLatch = new CountDownLatch(1);
        final Thread thread = new Thread()
        {
            @Override
            public void run()
            {
                startedLatch.countDown();

                final ResourceLoader<Type> resourceLoader = new ResourceLoader<>();
                resourceLoader.add(Type.TEST, new SlowResource());
                resourceLoader.start();
                resourceLoader.await();
            }
        };

        final CountDownLatch exceptionLatch = new CountDownLatch(1);
        final AtomicReference<LionEngineException> exception = new AtomicReference<>();
        thread.setUncaughtExceptionHandler((t, throwable) ->
        {
            if (throwable instanceof LionEngineException)
            {
                exception.set((LionEngineException) throwable);
                exceptionLatch.countDown();
            }
        });

        thread.start();
        startedLatch.await();
        thread.interrupt();
        exceptionLatch.await();

        throw exception.get();
    }

    /**
     * Slow resource test case.
     */
    private static class SlowResource implements Resource
    {
        /**
         * Create resource.
         */
        SlowResource()
        {
            super();
        }

        @Override
        public void load()
        {
            UtilTests.pause(Constant.THOUSAND);
        }

        @Override
        public boolean isLoaded()
        {
            return true;
        }

        @Override
        public void dispose()
        {
            // Mock
        }
    }
}
