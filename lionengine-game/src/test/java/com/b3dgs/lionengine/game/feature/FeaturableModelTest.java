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

import java.io.Serializable;
import java.util.concurrent.atomic.AtomicReference;

import org.junit.Assert;
import org.junit.Test;

import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.game.object.Factory;

/**
 * Test the featurable model class.
 */
public class FeaturableModelTest
{
    /**
     * Test the featurable model with compatible feature.
     */
    @Test
    public void testFeaturableCompatible()
    {
        final Featurable featurable = new FeaturableModel();
        final MyFeatureInterface feature = new MyFeature();
        featurable.addFeature(feature);

        Assert.assertTrue(featurable.hasFeature(MyFeatureInterface.class));
        Assert.assertTrue(featurable.hasFeature(MyFeature.class));

        featurable.prepareFeatures(new FeaturableModel(), new Services());

        Assert.assertEquals(feature, featurable.getFeature(MyFeature.class));
        for (final Feature current : featurable.getFeatures())
        {
            Assert.assertEquals(feature, current);
        }
        for (final Class<? extends Feature> type : featurable.getFeaturesType())
        {
            Assert.assertTrue(MyFeatureInterface.class.isAssignableFrom(type));
        }
    }

    /**
     * Test the featurable model with not compatible interface.
     */
    @Test
    public void testFeaturableNotcompatible()
    {
        final Featurable featurable = new FeaturableModel();
        final MyFeatureNotCompatible feature = new MyFeatureNotCompatible();
        featurable.addFeature(feature);

        Assert.assertTrue(featurable.hasFeature(MyFeatureNotCompatible.class));

        featurable.prepareFeatures(new FeaturableModel(), new Services());

        Assert.assertEquals(feature, featurable.getFeature(MyFeatureNotCompatible.class));
        for (final Feature current : featurable.getFeatures())
        {
            Assert.assertEquals(feature, current);
        }
        for (final Class<? extends Feature> type : featurable.getFeaturesType())
        {
            Assert.assertTrue(MyFeatureNotCompatible.class.isAssignableFrom(type));
        }
    }

    /**
     * Test the service with annotation.
     */
    @Test
    public void testServiceAnnotation()
    {
        final Featurable featurable = new FeaturableModel();
        final AtomicReference<Factory> filledService = new AtomicReference<Factory>();
        final AtomicReference<Object> filledObject = new AtomicReference<Object>();
        final AtomicReference<MyFeatureInterface> filledFeature = new AtomicReference<MyFeatureInterface>();
        final Feature feature = new FeatureModel()
        {
            private @Service Factory factory;
            private @Service Object object;
            private @Service MyFeatureInterface feature;

            @Override
            public void prepare(Featurable owner, Services services)
            {
                super.prepare(owner, services);
                filledService.set(factory);
                filledObject.set(object);
                filledFeature.set(feature);
            }
        };
        featurable.addFeature(feature);

        final Services services = new Services();
        final Factory factory = services.create(Factory.class);
        final MyFeatureInterface featureModel = new MyFeature();
        featurable.addFeature(featureModel);
        featurable.prepareFeatures(new FeaturableModel(), services);

        Assert.assertEquals(factory, filledService.get());
        Assert.assertEquals(factory, filledObject.get());
        Assert.assertEquals(featureModel, filledFeature.get());
    }

    /**
     * Test the service with annotation twice loaded.
     */
    @Test
    public void testServiceAnnotationTwice()
    {
        final Featurable featurable = new FeaturableModel();
        final AtomicReference<Factory> filledService = new AtomicReference<Factory>();
        final Feature feature = new FeatureModel()
        {
            private @Service Factory factory;

            @Override
            public void prepare(Featurable owner, Services services)
            {
                super.prepare(owner, services);
                filledService.set(factory);
            }
        };
        featurable.addFeature(feature);

        final Services services = new Services();
        final Factory factory = services.create(Factory.class);
        final MyFeatureInterface featureModel = new MyFeature();
        featurable.addFeature(featureModel);

        featurable.prepareFeatures(featurable, services);

        Assert.assertEquals(factory, filledService.get());

        filledService.set(null);

        featurable.addFeature(feature);
        featurable.prepareFeatures(featurable, services);

        Assert.assertEquals(factory, filledService.get());
    }

    /**
     * Test the service with annotation and service not found.
     */
    @Test(expected = LionEngineException.class)
    public void testServiceAnnotationNotFound()
    {
        final Featurable featurable = new FeaturableModel();
        final AtomicReference<String> unfilledType = new AtomicReference<String>();
        final Feature feature = new FeatureModel()
        {
            private @Service String type;

            @Override
            public void prepare(Featurable owner, Services services)
            {
                super.prepare(owner, services);
                unfilledType.set(type);
            }
        };
        featurable.addFeature(feature);
        featurable.prepareFeatures(new FeaturableModel(), new Services());
    }

    /**
     * Test the feature itself.
     */
    @Test
    public void testFeatureItself()
    {
        final FeatureItself object = new FeatureItself();
        object.prepare(object, new Services());

        Assert.assertFalse(object.hasFeature(FeatureItself.class));
    }

    /**
     * Mock feature.
     */
    private static interface MyFeatureInterface extends Feature
    {
        // Mock
    }

    /**
     * Mock feature.
     */
    private static class MyFeature extends FeatureModel implements MyFeatureInterface
    {
        // Mock
    }

    /**
     * Mock feature.
     */
    private static class MyFeatureNotCompatible extends FeatureModel implements Serializable
    {
        private static final long serialVersionUID = 1L;
    }

    /**
     * Mock feature itself.
     */
    private static class FeatureItself extends FeaturableModel implements Feature
    {
        @Override
        public void prepare(Featurable owner, Services services)
        {
            // Mock
        }

        @Override
        public void checkListener(Object listener)
        {
            // Mock
        }

        @Override
        public <O extends Featurable> O getOwner()
        {
            return null;
        }
    }
}
