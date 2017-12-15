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

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.core.Medias;
import com.b3dgs.lionengine.game.Featurable;
import com.b3dgs.lionengine.game.FeaturableModel;
import com.b3dgs.lionengine.game.Services;
import com.b3dgs.lionengine.game.Setup;
import com.b3dgs.lionengine.graphic.FactoryGraphicMock;
import com.b3dgs.lionengine.graphic.Graphics;

/**
 * Test the factory class.
 */
public class FactoryTest
{
    /**
     * Prepare test.
     */
    @BeforeClass
    public static void setUp()
    {
        Medias.setLoadFromJar(FactoryTest.class);
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

        Assert.assertNotNull(featurable1);
        Assert.assertNotNull(featurable2);
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

        Assert.assertNotNull(featurable1);
        Assert.assertNotNull(featurable2);
    }

    /**
     * Test the object creation without constructor.
     */
    @Test(expected = LionEngineException.class)
    public void testCreateNoConstructor()
    {
        Assert.assertNotNull(factory.create(Medias.create("no_constructor.xml")));
    }

    /**
     * Test the object creation without constructor.
     */
    @Test(expected = LionEngineException.class)
    public void testCreateNoConstructorClass()
    {
        Assert.assertNotNull(factory.create(Medias.create("no_constructor.xml"), ObjectNoConstructor.class));
    }

    /**
     * Test the object creation without class.
     */
    @Test
    public void testCreateNoClass()
    {
        try
        {
            Assert.assertNotNull(factory.create(Medias.create("no_class.xml")));
            Assert.fail();
        }
        catch (final LionEngineException exception)
        {
            Assert.assertEquals(exception.getMessage(), ClassNotFoundException.class, exception.getCause().getClass());
        }
    }

    /**
     * Test the object creation without class.
     */
    @Test
    public void testCreateNoSetupClass()
    {
        try
        {
            Assert.assertNotNull(factory.create(Medias.create("no_setup.xml")));
            Assert.fail();
        }
        catch (final LionEngineException exception)
        {
            Assert.assertEquals(exception.getMessage(), ClassNotFoundException.class, exception.getCause().getClass());
        }
    }

    /**
     * Test the object creation without setup constructor.
     */
    @Test
    public void testCreateNoSetupConstructor()
    {
        try
        {
            Assert.assertNotNull(factory.create(Medias.create("no_setup_constructor.xml")));
            Assert.fail();
        }
        catch (final LionEngineException exception)
        {
            Assert.assertEquals(exception.getMessage(), NoSuchMethodException.class, exception.getCause().getClass());
        }
    }

    /**
     * Test the get setup.
     */
    @Test
    public void testGetSetup()
    {
        final Setup setup = factory.getSetup(Medias.create("object.xml"));

        Assert.assertEquals(Medias.create("object.xml"), setup.getMedia());

        Assert.assertEquals(setup, factory.getSetup(Medias.create("object.xml")));
        Assert.assertEquals(setup, factory.getSetup(Medias.create("object.xml")));
    }

    /**
     * Test the object recycling.
     */
    @Test
    public void testRecycle()
    {
        final Featurable featurable = factory.create(Medias.create("object.xml"), ObjectWithIdentifiable.class);
        featurable.addFeature(new Recycler());

        Assert.assertNotEquals(featurable, factory.create(Medias.create("object.xml"), ObjectWithIdentifiable.class));

        factory.notifyHandlableRemoved(featurable);

        Assert.assertEquals(featurable, factory.create(Medias.create("object.xml"), ObjectWithIdentifiable.class));

        final Media media = UtilSetup.createMedia(ObjectWithIdentifiable.class);
        final Featurable featurable2 = factory.create(media);

        factory.notifyHandlableRemoved(featurable2);

        Assert.assertNotEquals(featurable2, factory.create(media));

        featurable2.addFeature(new Recycler());
        factory.notifyHandlableRemoved(featurable2);

        Assert.assertEquals(featurable2, factory.create(media));
    }

    /**
     * Test the object recycling without recyclable.
     */
    @Test
    public void testRecycleWithoutRecyclable()
    {
        final Featurable featurable = factory.create(Medias.create("object.xml"), ObjectWithIdentifiable.class);

        Assert.assertNotEquals(featurable, factory.create(Medias.create("object.xml"), ObjectWithIdentifiable.class));

        factory.notifyHandlableRemoved(featurable);

        Assert.assertNotEquals(featurable, factory.create(Medias.create("object.xml"), ObjectWithIdentifiable.class));
    }
}
