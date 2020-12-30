/*
 * Copyright (C) 2013-2020 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */
package com.b3dgs.lionengine.game.feature;

import static com.b3dgs.lionengine.UtilAssert.assertCause;
import static com.b3dgs.lionengine.UtilAssert.assertEquals;
import static com.b3dgs.lionengine.UtilAssert.assertNotEquals;
import static com.b3dgs.lionengine.UtilAssert.assertNotNull;
import static com.b3dgs.lionengine.UtilAssert.assertNull;
import static com.b3dgs.lionengine.UtilAssert.assertThrows;
import static com.b3dgs.lionengine.UtilAssert.assertTrue;

import java.util.concurrent.atomic.AtomicReference;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.Medias;
import com.b3dgs.lionengine.graphic.FactoryGraphicMock;
import com.b3dgs.lionengine.graphic.Graphics;

/**
 * Test {@link Factory}.
 */
final class FactoryTest
{
    /**
     * Prepare test.
     */
    @BeforeAll
    public static void beforeTests()
    {
        Medias.setResourcesDirectory(System.getProperty("java.io.tmpdir"));
        Medias.setLoadFromJar(FactoryTest.class);
        Graphics.setFactoryGraphic(new FactoryGraphicMock());
    }

    /**
     * Clean up test.
     */
    @AfterAll
    public static void afterTests()
    {
        Medias.setResourcesDirectory(null);
        Medias.setLoadFromJar(null);
        Graphics.setFactoryGraphic(null);
    }

    private final Services services = new Services();
    private final Factory factory = new Factory(services);

    /**
     * Test the object creation.
     */
    @Test
    void testCreate()
    {
        final Featurable featurable1 = factory.create(Medias.create("Object.xml"));
        final Featurable featurable2 = factory.create(Medias.create("Object.xml"), FeaturableModel.class);

        assertNotNull(featurable1);
        assertNotNull(featurable2);
    }

    /**
     * Test the object creation with existing identifiable.
     */
    @Test
    void testPrepareWithIdentifiable()
    {
        final Featurable featurable1 = factory.create(Medias.create("ObjectIdentifiable.xml"));
        final Featurable featurable2 = factory.create(Medias.create("ObjectIdentifiable.xml"),
                                                      ObjectWithIdentifiable.class);

        assertNotNull(featurable1);
        assertNotNull(featurable2);
    }

    /**
     * Test the object creation with features.
     */
    @Test
    void testCache()
    {
        factory.createCache(new Spawner()
        {
            @Override
            public Featurable spawn(Media media, double x, double y)
            {
                return factory.create(media);
            }
        }, Medias.create("state"), 2);

        final Featurable featurable = factory.create(Medias.create("ObjectFeatures.xml"));
        assertNotEquals(featurable, factory.create(Medias.create("ObjectFeatures.xml")));
    }

    /**
     * Test the object creation with features.
     */
    @Test
    void testCreateFeatures()
    {
        final Featurable featurable = factory.create(Medias.create("ObjectFeatures.xml"));

        assertTrue(featurable.hasFeature(Mirrorable.class));
    }

    /**
     * Test the object creation with unknown feature.
     */
    @Test
    void testCreateUnknownFeature()
    {
        final Media media = Medias.create("ObjectUnknownFeature.xml");

        assertCause(() -> factory.create(media), FeaturableConfig.ERROR_CLASS_PRESENCE + "Unknown");
    }

    /**
     * Test the object creation without constructor.
     */
    @Test
    void testCreateNoConstructorClass()
    {
        final Media media = Medias.create("ObjectNoConstructor.xml");

        assertThrows(() -> factory.create(media, Featurable.class), Factory.ERROR_CONSTRUCTOR_MISSING + media);
    }

    /**
     * Test the object creation without class.
     */
    @Test
    void testCreateNoClass()
    {
        assertCause(() -> factory.create(Medias.create("ObjectNoClass.xml")), ClassNotFoundException.class);
    }

    /**
     * Test the object creation without class.
     */
    @Test
    void testCreateNoSetupClass()
    {
        assertCause(() -> factory.create(Medias.create("ObjectNoSetup.xml")), ClassNotFoundException.class);
    }

    /**
     * Test the object creation without setup constructor.
     */
    @Test
    void testCreateNoSetupConstructor()
    {
        assertCause(() -> factory.create(Medias.create("ObjectNoSetupConstructor.xml")), NoSuchMethodException.class);
    }

    /**
     * Test the get setup.
     */
    @Test
    void testGetSetup()
    {
        final Setup setup = factory.getSetup(Medias.create("Object.xml"));

        assertEquals(Medias.create("Object.xml"), setup.getMedia());

        assertEquals(setup, factory.getSetup(Medias.create("Object.xml")));
        assertEquals(setup, factory.getSetup(Medias.create("Object.xml")));
    }

    /**
     * Test the object recycling.
     */
    @Test
    void testRecycle()
    {
        final Featurable featurable = factory.create(Medias.create("Object.xml"), ObjectWithIdentifiable.class);

        assertNotEquals(featurable, factory.create(Medias.create("Object.xml"), ObjectWithIdentifiable.class));

        factory.notifyHandlableRemoved(featurable);

        assertEquals(featurable, factory.create(Medias.create("Object.xml"), ObjectWithIdentifiable.class));

        final Media media = UtilSetup.createMedia(ObjectWithIdentifiable.class);
        final Featurable featurable2 = factory.create(media);

        factory.notifyHandlableRemoved(featurable2);

        assertEquals(featurable2, factory.create(media));

        assertTrue(media.getFile().delete());
    }

    /**
     * Test with handler notification.
     */
    @Test
    void testWithHandler()
    {
        final Handler handler = new Handler(services);
        final AtomicReference<Featurable> added = new AtomicReference<>();
        final AtomicReference<Featurable> removed = new AtomicReference<>();
        final Factory factory = new Factory(services)
        {
            @Override
            public void notifyHandlableAdded(Featurable featurable)
            {
                super.notifyHandlableAdded(featurable);
                added.set(featurable);
            }

            @Override
            public void notifyHandlableRemoved(Featurable featurable)
            {
                super.notifyHandlableRemoved(featurable);
                removed.set(featurable);
            }
        };
        handler.addListener(factory);
        final Featurable featurable = factory.create(Medias.create("Object.xml"), ObjectWithIdentifiable.class);
        handler.add(featurable);

        assertNull(added.get());
        assertNull(removed.get());

        handler.update(1.0);

        assertEquals(featurable, added.get());
        assertNull(removed.get());

        handler.remove(featurable);

        assertEquals(featurable, added.get());
        assertNull(removed.get());

        handler.update(1.0);

        assertEquals(featurable, added.get());
        assertEquals(featurable, removed.get());
    }
}
