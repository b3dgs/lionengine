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
package com.b3dgs.lionengine.game.handler;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import com.b3dgs.lionengine.Constant;
import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.Updatable;
import com.b3dgs.lionengine.core.Medias;
import com.b3dgs.lionengine.game.Services;
import com.b3dgs.lionengine.game.object.ObjectGame;
import com.b3dgs.lionengine.game.object.Setup;
import com.b3dgs.lionengine.game.object.UtilSetup;
import com.b3dgs.lionengine.game.object.feature.mirrorable.Mirrorable;
import com.b3dgs.lionengine.game.object.feature.mirrorable.MirrorableModel;
import com.b3dgs.lionengine.game.object.feature.transformable.Transformable;
import com.b3dgs.lionengine.game.object.feature.transformable.TransformableModel;

/**
 * Test the handled objects.
 */
public class HandledObjectsImplTest
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
        Medias.setResourcesDirectory(Constant.EMPTY_STRING);
    }

    /** Handled objects test. */
    private final HandledHandlablesImpl handled = new HandledHandlablesImpl();
    /** Object test. */
    private final ObjectGame object = new ObjectGame(new Setup(config), new Services());

    /**
     * Clean test.
     */
    @After
    public void after()
    {
        object.notifyDestroyed();
    }

    /**
     * Test ID manipulation.
     */
    @Test
    public void testId()
    {
        handled.add(object);

        Assert.assertEquals(object, handled.get(object.getId()));
        Assert.assertEquals(object, handled.values().iterator().next());
        Assert.assertEquals(1, handled.getIds().size());

        handled.remove(object.getId());

        Assert.assertTrue(handled.getIds().isEmpty());
        Assert.assertFalse(handled.values().iterator().hasNext());
        try
        {
            Assert.assertNull(handled.get(object.getId()));
            Assert.fail();
        }
        catch (final LionEngineException exception)
        {
            // Success
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
        object.prepareFeatures(object, new Services());
        handled.add(object);

        Assert.assertEquals(mirrorable, handled.get(Mirrorable.class).iterator().next());
        Assert.assertFalse(handled.get(Transformable.class).iterator().hasNext());

        handled.remove(object.getId());

        Assert.assertFalse(handled.get(Mirrorable.class).iterator().hasNext());
    }

    /**
     * Test type with complex object manipulation.
     */
    @Test
    public void testFeatureComplex()
    {
        final ObjectGame complex = new ObjectComplex(new Setup(config), new Services());
        complex.addFeature(new MirrorableModel());
        complex.addFeature(new TransformableModel(complex.getConfigurer()));
        complex.prepareFeatures(complex, new Services());
        handled.add(complex);

        int i = 0;
        for (final Updatable updatable : handled.get(Updatable.class))
        {
            Assert.assertEquals(complex, updatable);
            i++;
        }
        Assert.assertEquals(1, i);
    }

    /**
     * Complex object with interface.
     */
    private static class ObjectComplex extends ObjectGame implements Updatable
    {
        /**
         * Create object.
         * 
         * @param setup The setup reference.
         * @param services The services used.
         */
        public ObjectComplex(Setup setup, Services services)
        {
            super(setup, services);
        }

        @Override
        public void update(double extrp)
        {
            // Mock
        }
    }
}
