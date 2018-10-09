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
public final class FactoryTest
{
    /**
     * Prepare test.
     */
    @BeforeAll
    public static void beforeTests()
    {
        Medias.setLoadFromJar(FactoryTest.class);
        Graphics.setFactoryGraphic(new FactoryGraphicMock());
    }

    /**
     * Clean up test.
     */
    @AfterAll
    public static void afterTests()
    {
        Medias.setLoadFromJar(null);
        Graphics.setFactoryGraphic(null);
    }

    private final Services services = new Services();
    private final Factory factory = new Factory(services);

    /**
     * Test the object creation.
     */
    @Test
    public void testCreate()
    {
        factory.setClassLoader(ClassLoader.getSystemClassLoader());

        final Featurable featurable1 = factory.create(Medias.create("object.xml"));
        final Featurable featurable2 = factory.create(Medias.create("object.xml"), FeaturableModel.class);

        assertNotNull(featurable1);
        assertNotNull(featurable2);
    }

    /**
     * Test the object creation with existing identifiable.
     */
    @Test
    public void testPrepareWithIdentifiable()
    {
        final Featurable featurable1 = factory.create(Medias.create("object_identifiable.xml"));
        final Featurable featurable2 = factory.create(Medias.create("object_identifiable.xml"),
                                                      ObjectWithIdentifiable.class);

        assertNotNull(featurable1);
        assertNotNull(featurable2);
    }

    /**
     * Test the object creation with features.
     */
    @Test
    public void testCreateFeatures()
    {
        final Featurable featurable = factory.create(Medias.create("object_features.xml"));

        assertTrue(featurable.hasFeature(Mirrorable.class));
    }

    /**
     * Test the object creation with unknown feature.
     */
    @Test
    public void testCreateUnknownFeature()
    {
        final Media media = Medias.create("object_unknown_feature.xml");

        assertThrows(() -> factory.create(media), FeaturableConfig.ERROR_CLASS_PRESENCE + "Unknown");
    }

    /**
     * Test the object creation without constructor.
     */
    @Test
    public void testCreateNoConstructorClass()
    {
        final Media media = Medias.create("no_constructor.xml");

        assertThrows(() -> factory.create(media, Featurable.class), Factory.ERROR_CONSTRUCTOR_MISSING + media);
    }

    /**
     * Test the object creation without class.
     */
    @Test
    public void testCreateNoClass()
    {
        assertCause(() -> factory.create(Medias.create("no_class.xml")), ClassNotFoundException.class);
    }

    /**
     * Test the object creation without class.
     */
    @Test
    public void testCreateNoSetupClass()
    {
        assertCause(() -> factory.create(Medias.create("no_setup.xml")), ClassNotFoundException.class);
    }

    /**
     * Test the object creation without setup constructor.
     */
    @Test
    public void testCreateNoSetupConstructor()
    {
        assertCause(() -> factory.create(Medias.create("no_setup_constructor.xml")), NoSuchMethodException.class);
    }

    /**
     * Test the get setup.
     */
    @Test
    public void testGetSetup()
    {
        final Setup setup = factory.getSetup(Medias.create("object.xml"));

        assertEquals(Medias.create("object.xml"), setup.getMedia());

        assertEquals(setup, factory.getSetup(Medias.create("object.xml")));
        assertEquals(setup, factory.getSetup(Medias.create("object.xml")));
    }

    /**
     * Test the object recycling.
     */
    @Test
    public void testRecycle()
    {
        final Featurable featurable = factory.create(Medias.create("object.xml"), ObjectWithIdentifiable.class);
        featurable.addFeature(new Recycler());

        assertNotEquals(featurable, factory.create(Medias.create("object.xml"), ObjectWithIdentifiable.class));

        factory.notifyHandlableRemoved(featurable);

        assertEquals(featurable, factory.create(Medias.create("object.xml"), ObjectWithIdentifiable.class));

        final Media media = UtilSetup.createMedia(ObjectWithIdentifiable.class);
        final Featurable featurable2 = factory.create(media);

        factory.notifyHandlableRemoved(featurable2);

        assertNotEquals(featurable2, factory.create(media));

        featurable2.addFeature(new Recycler());
        factory.notifyHandlableRemoved(featurable2);

        assertEquals(featurable2, factory.create(media));
    }

    /**
     * Test the object recycling without recyclable.
     */
    @Test
    public void testRecycleWithoutRecyclable()
    {
        final Featurable featurable = factory.create(Medias.create("object.xml"), ObjectWithIdentifiable.class);

        assertNotEquals(featurable, factory.create(Medias.create("object.xml"), ObjectWithIdentifiable.class));

        factory.notifyHandlableRemoved(featurable);

        assertNotEquals(featurable, factory.create(Medias.create("object.xml"), ObjectWithIdentifiable.class));
    }

    /**
     * Test with handler notification.
     */
    @Test
    public void testWithHandler()
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
        final Featurable featurable = factory.create(Medias.create("object.xml"), ObjectWithIdentifiable.class);
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
