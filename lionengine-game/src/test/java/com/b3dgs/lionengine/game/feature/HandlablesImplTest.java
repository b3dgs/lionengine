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

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.Medias;
import com.b3dgs.lionengine.Updatable;

/**
 * Test the featurables.
 */
public class HandlablesImplTest
{
    /** Test configuration. */
    private static Media config;

    /**
     * Prepare test.
     */
    @BeforeClass
    public static void setUp()
    {
        Medias.setResourcesDirectory(System.getProperty("java.io.tmpdir"));
        config = UtilSetup.createConfig();
    }

    /**
     * Clean up test.
     */
    @AfterClass
    public static void cleanUp()
    {
        Assert.assertTrue(config.getFile().delete());
        Medias.setResourcesDirectory(null);
    }

    /** Handlables test. */
    private final HandlablesImpl featurables = new HandlablesImpl();
    /** Object test. */
    private final FeaturableModel object = new FeaturableModel();

    /**
     * Prepare test.
     */
    @Before
    public void prepare()
    {
        object.addFeature(new IdentifiableModel());
    }

    /**
     * Clean test.
     */
    @After
    public void clean()
    {
        object.getFeature(Identifiable.class).notifyDestroyed();
    }

    /**
     * Test ID manipulation.
     */
    @Test
    public void testId()
    {
        featurables.add(object);

        Assert.assertEquals(object, featurables.get(object.getFeature(Identifiable.class).getId()));
        Assert.assertEquals(object, featurables.values().iterator().next());
        Assert.assertEquals(1, featurables.getIds().size());

        featurables.remove(object, object.getFeature(Identifiable.class).getId());

        Assert.assertTrue(featurables.getIds().isEmpty());
        Assert.assertFalse(featurables.values().iterator().hasNext());
        try
        {
            Assert.assertNull(featurables.get(object.getFeature(Identifiable.class).getId()));
            Assert.fail();
        }
        catch (final LionEngineException exception)
        {
            // Success
            Assert.assertNotNull(exception);
        }
    }

    /**
     * Test feature manipulation.
     */
    @Test
    public void testFeature()
    {
        final Mirrorable mirrorable = new MirrorableModel();
        object.addFeatureAndGet(mirrorable);
        featurables.add(object);

        Assert.assertEquals(mirrorable, featurables.get(Mirrorable.class).iterator().next());
        Assert.assertFalse(featurables.get(Transformable.class).iterator().hasNext());

        featurables.remove(object, object.getFeature(Identifiable.class).getId());

        Assert.assertFalse(featurables.get(Mirrorable.class).iterator().hasNext());
    }

    /**
     * Test type with complex object manipulation.
     */
    @Test
    public void testFeatureComplex()
    {
        final Setup setup = new Setup(config);
        final Featurable complex = new ObjectComplex();
        complex.addFeature(new MirrorableModel());
        complex.addFeature(new TransformableModel(setup));
        featurables.add(complex);

        int i = 0;
        for (final Updatable updatable : featurables.get(Updatable.class))
        {
            Assert.assertEquals(complex, updatable);
            i++;
        }
        Assert.assertEquals(1, i);
    }

    /**
     * Test type with multiple feature inheritance.
     */
    @Test
    public void testFeatureInheritance()
    {
        final Featurable inheritance = new ObjectComplex();
        inheritance.addFeature(new FeatureLevel2());
        featurables.add(inheritance);

        int i = 0;
        for (final Refreshable refreshable : featurables.get(Refreshable.class))
        {
            Assert.assertNotNull(refreshable);
            i++;
        }
        Assert.assertEquals(1, i);
    }

    /**
     * Complex object with interface.
     */
    private static class ObjectComplex extends FeaturableModel implements Updatable
    {
        /**
         * Create object.
         */
        public ObjectComplex()
        {
            super();
            addFeature(new IdentifiableModel());
        }

        @Override
        public void update(double extrp)
        {
            // Mock
        }
    }

    /**
     * Mock feature.
     */
    private static class FeatureLevel1 extends FeatureModel implements Refreshable
    {
        @Override
        public void update(double extrp)
        {
            // Mock
        }
    }

    /**
     * Mock feature.
     */
    private static class FeatureLevel2 extends FeatureLevel1
    {
        // Mock
    }
}
