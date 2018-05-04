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
package com.b3dgs.lionengine.game.feature.producible;

import static com.b3dgs.lionengine.UtilAssert.assertEquals;
import static com.b3dgs.lionengine.UtilAssert.assertFalse;
import static com.b3dgs.lionengine.UtilAssert.assertNotNull;
import static com.b3dgs.lionengine.UtilAssert.assertNull;
import static com.b3dgs.lionengine.UtilAssert.assertThrows;
import static com.b3dgs.lionengine.UtilAssert.assertTrue;

import java.lang.reflect.Field;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.b3dgs.lionengine.Medias;
import com.b3dgs.lionengine.UtilEnum;
import com.b3dgs.lionengine.UtilReflection;
import com.b3dgs.lionengine.game.feature.Featurable;
import com.b3dgs.lionengine.game.feature.FeaturableModel;
import com.b3dgs.lionengine.game.feature.Handler;
import com.b3dgs.lionengine.game.feature.Identifiable;
import com.b3dgs.lionengine.game.feature.Services;

/**
 * Test {@link ProducerModel}.
 */
public final class ProducerModelTest
{
    /** Hack enum. */
    private static final UtilEnum<ProducerState> HACK = new UtilEnum<>(ProducerState.class, ProducerModel.class);

    /**
     * Prepare test.
     */
    @BeforeAll
    public static void beforeTests()
    {
        Medias.setResourcesDirectory(System.getProperty("java.io.tmpdir"));
        HACK.addByValue(HACK.make("FAIL"));
    }

    /**
     * Clean up test.
     */
    @AfterAll
    public static void afterTests()
    {
        Medias.setResourcesDirectory(null);
        HACK.restore();
    }

    private final Services services = new Services();
    private final ProducerObject object = new ProducerObject();
    private ProducerModel producer;

    /**
     * Prepare test.
     */
    @BeforeEach
    public void prepare()
    {
        services.add(new Handler(services));
        services.add(Integer.valueOf(50));
        producer = new ProducerModel(services);
        producer.prepare(object);
    }

    /**
     * Clean test.
     */
    @AfterEach
    public void clean()
    {
        object.getFeature(Identifiable.class).notifyDestroyed();
    }

    /**
     * Test the default production with default checker.
     */
    @Test
    public void testDefaultProduction()
    {
        final ProducerModel producer = new ProducerModel(services);
        producer.prepare(new FeaturableModel());

        producer.setStepsPerSecond(25.0);

        final AtomicReference<Featurable> start = new AtomicReference<>();
        final AtomicReference<Featurable> current = new AtomicReference<>();
        final AtomicReference<Featurable> done = new AtomicReference<>();
        final AtomicReference<Featurable> cant = new AtomicReference<>();
        producer.addListener(UtilProducible.createProducerListener(start, current, done, cant));

        producer.update(1.0);

        assertNull(producer.getProducingElement());
        assertEquals(-1.0, producer.getProgress());
        assertEquals(-1, producer.getProgressPercent());
        assertEquals(0, producer.getQueueLength());
        assertFalse(producer.isProducing());

        final Featurable featurable = UtilProducible.createProducible(services);
        producer.addToProductionQueue(featurable);

        assertEquals(0, producer.getQueueLength());
        assertNull(start.get());
        assertFalse(producer.isProducing());

        producer.update(1.0);

        assertEquals(0.0, producer.getProgress());
        assertEquals(0, producer.getProgressPercent());
        assertEquals(0, producer.getQueueLength());
        assertEquals(featurable, start.get());
        assertNull(current.get());
        assertTrue(producer.isProducing());
        assertEquals(featurable.getFeature(Producible.class).getMedia(), producer.getProducingElement());

        producer.update(1.0);

        assertEquals(0.5, producer.getProgress());
        assertEquals(50, producer.getProgressPercent());
        assertEquals(featurable, current.get());
        assertNull(done.get());
        assertTrue(producer.isProducing());

        producer.update(1.0);

        assertEquals(1.0, producer.getProgress());
        assertEquals(100, producer.getProgressPercent());
        assertEquals(featurable, current.get());
        assertNull(done.get());
        assertFalse(producer.isProducing());

        producer.update(1.0);

        assertEquals(featurable, done.get());
        assertNull(cant.get());
        assertFalse(producer.isProducing());
    }

    /**
     * Test the production.
     */
    @Test
    public void testProduction()
    {
        producer.setStepsPerSecond(25.0);

        final AtomicReference<Featurable> start = new AtomicReference<>();
        final AtomicReference<Featurable> current = new AtomicReference<>();
        final AtomicReference<Featurable> done = new AtomicReference<>();
        final AtomicReference<Featurable> cant = new AtomicReference<>();
        producer.addListener(UtilProducible.createProducerListener(start, current, done, cant));
        producer.setChecker(object);

        producer.update(1.0);

        assertNull(producer.getProducingElement());
        assertEquals(-1.0, producer.getProgress());
        assertEquals(-1, producer.getProgressPercent());
        assertEquals(0, producer.getQueueLength());
        assertFalse(producer.isProducing());

        final Featurable featurable = UtilProducible.createProducible(services);
        producer.addToProductionQueue(featurable);

        assertEquals(0, producer.getQueueLength());
        assertNull(start.get());
        assertFalse(producer.isProducing());

        producer.update(1.0);

        assertEquals(0.0, producer.getProgress());
        assertEquals(0, producer.getProgressPercent());
        assertEquals(0, producer.getQueueLength());
        assertEquals(featurable, start.get());
        assertNull(current.get());
        assertTrue(producer.isProducing());
        assertEquals(featurable.getFeature(Producible.class).getMedia(), producer.getProducingElement());

        producer.update(1.0);

        assertEquals(0.5, producer.getProgress());
        assertEquals(50, producer.getProgressPercent());
        assertEquals(featurable, current.get());
        assertNull(done.get());
        assertTrue(producer.isProducing());

        producer.update(1.0);

        assertEquals(1.0, producer.getProgress());
        assertEquals(100, producer.getProgressPercent());
        assertEquals(featurable, current.get());
        assertNull(done.get());
        assertFalse(producer.isProducing());

        producer.update(1.0);

        assertEquals(featurable, done.get());
        assertNull(cant.get());
        assertFalse(producer.isProducing());
    }

    /**
     * Test the production with self listener.
     */
    @Test
    public void testProductionListenerSelf()
    {
        final ProducerObjectSelf object = new ProducerObjectSelf();
        final ProducerModel producer = new ProducerModel(services);
        producer.prepare(object);
        producer.setStepsPerSecond(50.0);
        producer.addListener(object);

        final Featurable featurable = UtilProducible.createProducible(services);
        producer.addToProductionQueue(featurable);

        assertEquals(0, object.flag.get());

        producer.update(1.0);

        assertEquals(1, object.flag.get());

        producer.update(1.0);

        assertEquals(2, object.flag.get());

        producer.update(1.0);

        assertEquals(3, object.flag.get());

        producer.update(1.0);
    }

    /**
     * Test the production pending.
     */
    @Test
    public void testPending()
    {
        producer.setStepsPerSecond(50.0);

        final AtomicReference<Featurable> start = new AtomicReference<>();
        final AtomicReference<Featurable> skip = new AtomicReference<>();
        producer.addListener(UtilProducible.createProducerListener(start, skip, skip, skip));

        final Featurable featurable = UtilProducible.createProducible(services);
        producer.addToProductionQueue(featurable);
        producer.addToProductionQueue(featurable);

        assertEquals(1, producer.getQueueLength());
        assertNull(start.get());

        producer.update(1.0);

        assertTrue(producer.iterator().hasNext());
        assertEquals(1, producer.getQueueLength());
        assertNotNull(start.get());

        start.set(null);
        producer.update(1.0);
        producer.update(1.0);
        producer.update(1.0);
        producer.update(1.0);

        assertEquals(0, producer.getQueueLength());
        assertNotNull(start.get());
        assertFalse(producer.iterator().hasNext());
    }

    /**
     * Test the production pending and cannot.
     */
    @Test
    public void testPendingCannot()
    {
        producer.setStepsPerSecond(50.0);

        final AtomicReference<Featurable> start = new AtomicReference<>();
        final AtomicReference<Featurable> skip = new AtomicReference<>();
        producer.addListener(UtilProducible.createProducerListener(start, skip, skip, skip));

        final Featurable featurable = UtilProducible.createProducible(services);
        producer.addToProductionQueue(featurable);
        producer.addToProductionQueue(featurable);

        producer.update(1.0);
        producer.update(1.0);
        object.check.set(false);
        producer.update(1.0);
        producer.update(1.0);

        assertEquals(1, producer.getQueueLength());
        assertNotNull(start.get());
    }

    /**
     * Test the skip production.
     */
    @Test
    public void testSkip()
    {
        producer.setStepsPerSecond(50.0);

        final AtomicReference<Featurable> done = new AtomicReference<>();
        final AtomicReference<Featurable> skip = new AtomicReference<>();
        producer.addListener(UtilProducible.createProducerListener(skip, skip, done, skip));

        producer.skipProduction();

        final Featurable featurable = UtilProducible.createProducible(services);
        producer.addToProductionQueue(featurable);
        producer.addToProductionQueue(featurable);

        producer.update(1.0);
        producer.skipProduction();
        producer.update(1.0);
        producer.update(1.0);
        producer.update(1.0);

        assertNull(done.get());
    }

    /**
     * Test the stop production.
     */
    @Test
    public void testStop()
    {
        producer.setStepsPerSecond(50.0);

        final AtomicReference<Featurable> done = new AtomicReference<>();
        final AtomicReference<Featurable> skip = new AtomicReference<>();
        producer.addListener(UtilProducible.createProducerListener(skip, skip, done, skip));

        final Featurable featurable = UtilProducible.createProducible(services);
        producer.addToProductionQueue(featurable);
        producer.addToProductionQueue(featurable);

        producer.update(1.0);

        assertTrue(producer.isProducing());

        producer.stopProduction();

        assertFalse(producer.iterator().hasNext());
        assertFalse(producer.isProducing());

        producer.update(1.0);
        producer.update(1.0);
        producer.update(1.0);
        producer.update(1.0);

        assertNull(done.get());
    }

    /**
     * Test the cannot produce.
     */
    @Test
    public void testCannot()
    {
        object.check.set(false);
        producer.setStepsPerSecond(50.0);

        final AtomicReference<Featurable> skip = new AtomicReference<>();
        final AtomicReference<Featurable> cant = new AtomicReference<>();
        producer.addListener(UtilProducible.createProducerListener(skip, skip, skip, cant));

        final Featurable featurable = UtilProducible.createProducible(services);
        producer.addToProductionQueue(featurable);
        producer.update(1.0);

        assertFalse(producer.isProducing());
        assertEquals(featurable, cant.get());
    }

    /**
     * Test the production listener.
     */
    @Test
    public void testProducibleListener()
    {
        producer.setStepsPerSecond(50.0);

        final Featurable featurable = UtilProducible.createProducible(services);
        final AtomicBoolean start = new AtomicBoolean();
        final AtomicBoolean progress = new AtomicBoolean();
        final AtomicBoolean end = new AtomicBoolean();
        featurable.getFeature(Producible.class)
                  .addListener(UtilProducible.createProducibleListener(start, progress, end));

        producer.addToProductionQueue(featurable);

        assertFalse(start.get());
        assertFalse(progress.get());
        assertFalse(end.get());

        producer.update(1.0);

        assertTrue(start.get());
        assertFalse(progress.get());
        assertFalse(end.get());

        producer.update(1.0);

        assertTrue(progress.get());
        assertFalse(end.get());

        producer.update(1.0);

        assertTrue(end.get());
    }

    /**
     * Test the production listener auto add.
     */
    @Test
    public void testListenerAutoAdd()
    {
        final ProducerObjectSelf object = new ProducerObjectSelf();
        final ProducerModel producer = new ProducerModel(services);
        producer.prepare(object);
        producer.setStepsPerSecond(50.0);
        producer.checkListener(object);

        final Featurable featurable = UtilProducible.createProducible(services);
        producer.addToProductionQueue(featurable);

        assertEquals(0, object.flag.get());

        producer.update(1.0);

        assertEquals(1, object.flag.get());
    }

    /**
     * Test with enum fail.
     * 
     * @throws ReflectiveOperationException If error.
     */
    @Test
    public void testEnumFail() throws ReflectiveOperationException
    {
        final ProducerModel producer = new ProducerModel(services);
        final Field field = producer.getClass().getDeclaredField("state");
        UtilReflection.setAccessible(field, true);
        field.set(producer, ProducerState.values()[5]);

        assertThrows(() -> producer.update(1.0), "Unknown enum: FAIL");
    }
}
