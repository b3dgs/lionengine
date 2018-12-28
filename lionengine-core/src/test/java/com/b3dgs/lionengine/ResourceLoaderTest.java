/*
 * Copyright (C) 2013-2018 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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

import static com.b3dgs.lionengine.UtilAssert.assertEquals;
import static com.b3dgs.lionengine.UtilAssert.assertFalse;
import static com.b3dgs.lionengine.UtilAssert.assertNotNull;
import static com.b3dgs.lionengine.UtilAssert.assertNull;
import static com.b3dgs.lionengine.UtilAssert.assertThrows;
import static com.b3dgs.lionengine.UtilAssert.assertTimeout;
import static com.b3dgs.lionengine.UtilAssert.assertTrue;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicReference;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.b3dgs.lionengine.graphic.FactoryGraphicMock;
import com.b3dgs.lionengine.graphic.Graphics;
import com.b3dgs.lionengine.graphic.drawable.Drawable;
import com.b3dgs.lionengine.graphic.drawable.Image;

/**
 * Test {@link ResourceLoader}.
 */
public final class ResourceLoaderTest
{
    /**
     * Prepare tests.
     */
    @BeforeAll
    public static void beforeTests()
    {
        Medias.setLoadFromJar(ResourceLoaderTest.class);
        Graphics.setFactoryGraphic(new FactoryGraphicMock());
    }

    /**
     * Clean up tests.
     */
    @AfterAll
    public static void afterTests()
    {
        Medias.setLoadFromJar(null);
        Graphics.setFactoryGraphic(null);
    }

    /**
     * Test resource loader.
     */
    @Test
    public void testResourceLoader()
    {
        final ResourceLoader<Type> resourceLoader = new ResourceLoader<>();
        final Image resource = Drawable.loadImage(Medias.create("image.png"));

        resourceLoader.add(Type.TEST, resource);

        assertFalse(resource.isLoaded());
        assertNull(resource.getSurface());
        assertFalse(resourceLoader.isFinished());

        resourceLoader.start();
        resourceLoader.await();

        assertTrue(resource.isLoaded());
        assertNotNull(resource.getSurface());
        assertTrue(resourceLoader.isFinished());
        assertEquals(resourceLoader.get().get(Type.TEST), resource);
    }

    /**
     * Test already started.
     */
    @Test
    public void testAlreadyStarted()
    {
        final ResourceLoader<Type> resourceLoader = new ResourceLoader<>();
        resourceLoader.start();

        assertThrows(() -> resourceLoader.start(), ResourceLoader.ERROR_STARTED);
    }

    /**
     * Test not started get.
     */
    @Test
    public void testNotStartedGet()
    {
        final ResourceLoader<Type> resourceLoader = new ResourceLoader<>();

        assertThrows(() -> resourceLoader.get(), ResourceLoader.ERROR_NOT_FINISHED);
    }

    /**
     * Test not started await.
     */
    @Test
    public void testNotStartedAwait()
    {
        final ResourceLoader<Type> resourceLoader = new ResourceLoader<>();

        assertThrows(() -> resourceLoader.await(), ResourceLoader.ERROR_NOT_STARTED);
    }

    /**
     * Test add already started.
     */
    @Test
    public void testAddAlreadyStarted()
    {
        final ResourceLoader<Type> resourceLoader = new ResourceLoader<>();
        resourceLoader.start();

        assertThrows(() -> resourceLoader.add(Type.TEST, new SlowResource()), ResourceLoader.ERROR_STARTED);
    }

    /**
     * Test add <code>null</code> key.
     */
    @Test
    public void testAddNullKey()
    {
        final ResourceLoader<Type> resourceLoader = new ResourceLoader<>();
        resourceLoader.start();

        assertThrows(() -> resourceLoader.add(null, new SlowResource()), Check.ERROR_NULL);
    }

    /**
     * Test add <code>null</code> resource.
     */
    @Test
    public void testAddNullResource()
    {
        final ResourceLoader<Type> resourceLoader = new ResourceLoader<>();
        resourceLoader.start();

        assertThrows(() -> resourceLoader.add(Type.TEST, null), Check.ERROR_NULL);
    }

    /**
     * Test skip.
     * 
     * @throws InterruptedException If error.
     */
    @Test
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

        assertTimeout(1_000L, () -> ResourceLoader.ERROR_SKIPPED.equals(exception.get().getMessage()));
    }

    /**
     * Test type.
     */
    private static enum Type
    {
        /** Test type. */
        TEST;
    }

    /**
     * Slow resource test case.
     */
    private static final class SlowResource implements Resource
    {
        /**
         * Create resource.
         */
        private SlowResource()
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
