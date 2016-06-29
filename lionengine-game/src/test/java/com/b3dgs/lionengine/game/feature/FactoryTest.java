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
package com.b3dgs.lionengine.game.feature;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.core.Medias;

/**
 * Test the factory class.
 */
public class FactoryTest
{
    /** Object configuration file name. */
    private static final String OBJECT_XML = "object.xml";

    /**
     * Prepare test.
     */
    @BeforeClass
    public static void setUp()
    {
        Medias.setLoadFromJar(FactoryTest.class);
    }

    /**
     * Clean up test.
     */
    @AfterClass
    public static void cleanUp()
    {
        Medias.setLoadFromJar(null);
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

        final Featurable featurable1 = factory.create(Medias.create(OBJECT_XML));
        final Featurable featurable2 = factory.create(Medias.create(OBJECT_XML), FeaturableModel.class);

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
        final Setup setup = factory.getSetup(Medias.create(OBJECT_XML));

        Assert.assertEquals(Medias.create(OBJECT_XML), setup.getMedia());

        Assert.assertEquals(setup, factory.getSetup(Medias.create(OBJECT_XML)));
        Assert.assertEquals(setup, factory.getSetup(Medias.create(OBJECT_XML)));
    }
}
