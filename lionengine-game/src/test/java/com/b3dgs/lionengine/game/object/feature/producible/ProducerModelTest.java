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
package com.b3dgs.lionengine.game.object.feature.producible;

import java.lang.reflect.Field;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.b3dgs.lionengine.Constant;
import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.core.Medias;
import com.b3dgs.lionengine.game.handler.Handler;
import com.b3dgs.lionengine.game.handler.Services;
import com.b3dgs.lionengine.game.object.ObjectGame;
import com.b3dgs.lionengine.game.object.Setup;
import com.b3dgs.lionengine.game.object.UtilSetup;
import com.b3dgs.lionengine.test.UtilEnum;
import com.b3dgs.lionengine.test.UtilTests;
import com.b3dgs.lionengine.util.UtilReflection;

/**
 * Test the producer model trait.
 */
public class ProducerModelTest
{
    /** Hack enum. */
    private static final UtilEnum<ProducerState> HACK = new UtilEnum<ProducerState>(ProducerState.class,
                                                                                    ProducerModel.class);

    /**
     * Prepare test.
     */
    @BeforeClass
    public static void setUp()
    {
        Medias.setResourcesDirectory(System.getProperty("java.io.tmpdir"));
        HACK.addByValue(HACK.make("FAIL"));
    }

    /**
     * Clean up test.
     */
    @AfterClass
    public static void cleanUp()
    {
        Medias.setResourcesDirectory(Constant.EMPTY_STRING);
        HACK.restore();
    }

    private final Services services = new Services();
    private final Media media = UtilSetup.createMedia(ProducerObject.class);
    private final ProducerObject object = new ProducerObject(new Setup(media));
    private final ProducerModel producer = new ProducerModel();

    /**
     * Prepare test.
     */
    @Before
    public void prepare()
    {
        services.add(new Handler(services));
        services.add(Integer.valueOf(50));
        producer.prepare(object, services);
    }

    /**
     * Clean test.
     */
    @After
    public void clean()
    {
        object.notifyDestroyed();
        Assert.assertTrue(media.getFile().delete());
    }

    /**
     * Test the production.
     */
    @Test
    public void testProduction()
    {
        producer.setStepsPerSecond(25.0);

        final AtomicReference<Producible> start = new AtomicReference<Producible>();
        final AtomicReference<Producible> current = new AtomicReference<Producible>();
        final AtomicReference<Producible> done = new AtomicReference<Producible>();
        final AtomicReference<Producible> cant = new AtomicReference<Producible>();
        producer.addListener(UtilProducible.createProducerListener(start, current, done, cant));

        producer.update(1.0);

        Assert.assertNull(producer.getProducingElement());
        Assert.assertEquals(-1.0, producer.getProgress(), UtilTests.PRECISION);
        Assert.assertEquals(-1, producer.getProgressPercent());
        Assert.assertEquals(0, producer.getQueueLength());
        Assert.assertFalse(producer.isProducing());

        final Producible producible = UtilProducible.createProducible(services);
        producer.addToProductionQueue(producible);

        Assert.assertEquals(0, producer.getQueueLength());
        Assert.assertNull(start.get());
        Assert.assertFalse(producer.isProducing());

        producer.update(1.0);

        Assert.assertEquals(0.0, producer.getProgress(), UtilTests.PRECISION);
        Assert.assertEquals(0, producer.getProgressPercent());
        Assert.assertEquals(0, producer.getQueueLength());
        Assert.assertEquals(producible, start.get());
        Assert.assertNull(current.get());
        Assert.assertTrue(producer.isProducing());
        Assert.assertEquals(((ObjectGame) producible.getOwner()).getConfigurer().getMedia(),
                            producer.getProducingElement());

        producer.update(1.0);

        Assert.assertEquals(0.5, producer.getProgress(), UtilTests.PRECISION);
        Assert.assertEquals(50, producer.getProgressPercent());
        Assert.assertEquals(producible, current.get());
        Assert.assertNull(done.get());
        Assert.assertTrue(producer.isProducing());

        producer.update(1.0);

        Assert.assertEquals(1.0, producer.getProgress(), UtilTests.PRECISION);
        Assert.assertEquals(100, producer.getProgressPercent());
        Assert.assertEquals(producible, current.get());
        Assert.assertNull(done.get());
        Assert.assertFalse(producer.isProducing());

        producer.update(1.0);

        Assert.assertEquals(producible, done.get());
        Assert.assertNull(cant.get());
        Assert.assertFalse(producer.isProducing());
    }

    /**
     * Test the production with self listener.
     */
    @Test
    public void testProductionListenerSelf()
    {
        final ProducerObjectSelf object = new ProducerObjectSelf(new Setup(media));
        final ProducerModel producer = new ProducerModel();
        producer.prepare(object, services);
        producer.setStepsPerSecond(50.0);
        producer.addListener(object);

        final Producible producible = UtilProducible.createProducible(services);
        producer.addToProductionQueue(producible);

        Assert.assertEquals(0, object.flag.get());

        producer.update(1.0);

        Assert.assertEquals(1, object.flag.get());

        producer.update(1.0);

        Assert.assertEquals(2, object.flag.get());

        producer.update(1.0);

        Assert.assertEquals(3, object.flag.get());

        producer.update(1.0);
    }

    /**
     * Test the production pending.
     */
    @Test
    public void testPending()
    {
        producer.setStepsPerSecond(50.0);

        final AtomicReference<Producible> start = new AtomicReference<Producible>();
        final AtomicReference<Producible> skip = new AtomicReference<Producible>();
        producer.addListener(UtilProducible.createProducerListener(start, skip, skip, skip));

        final Producible producible = UtilProducible.createProducible(services);
        producer.addToProductionQueue(producible);
        producer.addToProductionQueue(producible);

        Assert.assertEquals(1, producer.getQueueLength());
        Assert.assertNull(start.get());

        producer.update(1.0);

        Assert.assertTrue(producer.iterator().hasNext());
        Assert.assertEquals(1, producer.getQueueLength());
        Assert.assertNotNull(start.get());

        start.set(null);
        producer.update(1.0);
        producer.update(1.0);
        producer.update(1.0);
        producer.update(1.0);

        Assert.assertEquals(0, producer.getQueueLength());
        Assert.assertNotNull(start.get());
        Assert.assertFalse(producer.iterator().hasNext());
    }

    /**
     * Test the production pending and cannot.
     */
    @Test
    public void testPendingCannot()
    {
        producer.setStepsPerSecond(50.0);

        final AtomicReference<Producible> start = new AtomicReference<Producible>();
        final AtomicReference<Producible> skip = new AtomicReference<Producible>();
        producer.addListener(UtilProducible.createProducerListener(start, skip, skip, skip));

        final Producible producible = UtilProducible.createProducible(services);
        producer.addToProductionQueue(producible);
        producer.addToProductionQueue(producible);

        producer.update(1.0);
        producer.update(1.0);
        object.check.set(false);
        producer.update(1.0);
        producer.update(1.0);

        Assert.assertEquals(1, producer.getQueueLength());
        Assert.assertNotNull(start.get());
    }

    /**
     * Test the skip production.
     */
    @Test
    public void testSkip()
    {
        producer.setStepsPerSecond(50.0);

        final AtomicReference<Producible> done = new AtomicReference<Producible>();
        final AtomicReference<Producible> skip = new AtomicReference<Producible>();
        producer.addListener(UtilProducible.createProducerListener(skip, skip, done, skip));

        producer.skipProduction();

        final Producible producible = UtilProducible.createProducible(services);
        producer.addToProductionQueue(producible);
        producer.addToProductionQueue(producible);

        producer.update(1.0);
        producer.skipProduction();
        producer.update(1.0);
        producer.update(1.0);
        producer.update(1.0);

        Assert.assertNull(done.get());
    }

    /**
     * Test the stop production.
     */
    @Test
    public void testStop()
    {
        producer.setStepsPerSecond(50.0);

        final AtomicReference<Producible> done = new AtomicReference<Producible>();
        final AtomicReference<Producible> skip = new AtomicReference<Producible>();
        producer.addListener(UtilProducible.createProducerListener(skip, skip, done, skip));

        final Producible producible = UtilProducible.createProducible(services);
        producer.addToProductionQueue(producible);
        producer.addToProductionQueue(producible);

        producer.update(1.0);

        Assert.assertTrue(producer.isProducing());

        producer.stopProduction();

        Assert.assertFalse(producer.iterator().hasNext());
        Assert.assertFalse(producer.isProducing());

        producer.update(1.0);
        producer.update(1.0);
        producer.update(1.0);
        producer.update(1.0);

        Assert.assertNull(done.get());
    }

    /**
     * Test the cannot produce.
     */
    @Test
    public void testCannot()
    {
        object.check.set(false);
        producer.setStepsPerSecond(50.0);

        final AtomicReference<Producible> skip = new AtomicReference<Producible>();
        final AtomicReference<Producible> cant = new AtomicReference<Producible>();
        producer.addListener(UtilProducible.createProducerListener(skip, skip, skip, cant));

        final Producible producible = UtilProducible.createProducible(services);
        producer.addToProductionQueue(producible);
        producer.update(1.0);

        Assert.assertFalse(producer.isProducing());
        Assert.assertEquals(producible, cant.get());
    }

    /**
     * Test the production listener.
     */
    @Test
    public void testProducibleListener()
    {
        producer.setStepsPerSecond(50.0);

        final Producible producible = UtilProducible.createProducible(services);
        final AtomicBoolean start = new AtomicBoolean();
        final AtomicBoolean progress = new AtomicBoolean();
        final AtomicBoolean end = new AtomicBoolean();
        producible.addListener(UtilProducible.createProducibleListener(start, progress, end));

        producer.addToProductionQueue(producible);

        Assert.assertFalse(start.get());
        Assert.assertFalse(progress.get());
        Assert.assertFalse(end.get());

        producer.update(1.0);

        Assert.assertTrue(start.get());
        Assert.assertFalse(progress.get());
        Assert.assertFalse(end.get());

        producer.update(1.0);

        Assert.assertTrue(progress.get());
        Assert.assertFalse(end.get());

        producer.update(1.0);

        Assert.assertTrue(end.get());
    }

    /**
     * Test the production listener auto add.
     */
    @Test
    public void testListenerAutoAdd()
    {
        final ProducerObjectSelf object = new ProducerObjectSelf(new Setup(media));
        final ProducerModel producer = new ProducerModel();
        producer.prepare(object, services);
        producer.setStepsPerSecond(50.0);
        producer.checkListener(object);

        final Producible producible = UtilProducible.createProducible(services);
        producer.addToProductionQueue(producible);

        Assert.assertEquals(0, object.flag.get());

        producer.update(1.0);

        Assert.assertEquals(1, object.flag.get());
    }

    /**
     * Test with enum fail.
     * 
     * @throws NoSuchFieldException If error.
     * @throws IllegalArgumentException If error.
     * @throws IllegalAccessException If error.
     */
    @Test
    public void testEnumFail() throws NoSuchFieldException, IllegalArgumentException, IllegalAccessException
    {
        final ProducerModel producer = new ProducerModel();
        final Field field = producer.getClass().getDeclaredField("state");
        UtilReflection.setAccessible(field, true);
        field.set(producer, ProducerState.values()[5]);
        try
        {
            producer.update(1.0);
            Assert.fail();
        }
        catch (final LionEngineException exception)
        {
            Assert.assertEquals("Unknown enum: FAIL", exception.getMessage());
        }
    }
}
