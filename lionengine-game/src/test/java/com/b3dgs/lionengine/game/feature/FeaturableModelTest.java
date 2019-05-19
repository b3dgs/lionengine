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
package com.b3dgs.lionengine.game.feature;

import static com.b3dgs.lionengine.UtilAssert.assertEquals;
import static com.b3dgs.lionengine.UtilAssert.assertFalse;
import static com.b3dgs.lionengine.UtilAssert.assertThrows;
import static com.b3dgs.lionengine.UtilAssert.assertTrue;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.concurrent.atomic.AtomicReference;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.Medias;
import com.b3dgs.lionengine.UtilFile;
import com.b3dgs.lionengine.UtilReflection;
import com.b3dgs.lionengine.Xml;
import com.b3dgs.lionengine.game.Feature;
import com.b3dgs.lionengine.game.FeatureProvider;

/**
 * Test {@link FeaturableModel}.
 */
public final class FeaturableModelTest
{
    /**
     * Prepare test.
     */
    @BeforeAll
    public static void setUp()
    {
        Medias.setResourcesDirectory(System.getProperty("java.io.tmpdir"));
    }

    /**
     * Clean up test.
     */
    @AfterAll
    public static void cleanUp()
    {
        Medias.setResourcesDirectory(null);
        FeaturableConfig.clearCache();
    }

    /**
     * Test the featurable model with compatible feature.
     */
    @Test
    public void testFeaturableCompatible()
    {
        final Featurable featurable = new FeaturableModel();
        final MyFeatureInterface feature = new MyFeature();
        featurable.addFeature(feature);

        assertTrue(featurable.hasFeature(MyFeatureInterface.class));
        assertTrue(featurable.hasFeature(MyFeature.class));

        assertEquals(feature, featurable.getFeature(MyFeature.class));
        for (final Feature current : featurable.getFeatures())
        {
            assertTrue(feature.getClass().equals(current.getClass())
                       || Identifiable.class.isAssignableFrom(current.getClass())
                       || Recycler.class.isAssignableFrom(current.getClass()),
                       current.getClass().getName());
        }
        for (final Class<? extends Feature> type : featurable.getFeaturesType())
        {
            assertTrue(MyFeatureInterface.class.isAssignableFrom(type)
                       || Identifiable.class.isAssignableFrom(type)
                       || Recycler.class.isAssignableFrom(type),
                       type.getName());
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

        assertTrue(featurable.hasFeature(MyFeatureNotCompatible.class));

        assertEquals(feature, featurable.getFeature(MyFeatureNotCompatible.class));
        for (final Feature current : featurable.getFeatures())
        {
            assertTrue(feature.getClass().equals(current.getClass())
                       || Identifiable.class.isAssignableFrom(current.getClass())
                       || Recycler.class.isAssignableFrom(current.getClass()),
                       current.getClass().getName());
        }
        for (final Class<? extends Feature> type : featurable.getFeaturesType())
        {
            assertTrue(MyFeatureNotCompatible.class.isAssignableFrom(type)
                       || Identifiable.class.isAssignableFrom(type)
                       || Recycler.class.isAssignableFrom(type),
                       type.getName());
        }
    }

    /**
     * Test the service with annotation.
     */
    @Test
    public void testServiceAnnotation()
    {
        final Featurable featurable = new FeaturableModel();

        final MyFeatureInterface featureModel = new MyFeature();
        featurable.addFeature(featureModel);

        final MyFeatureModel feature = featurable.addFeatureAndGet(new MyFeatureModel());

        assertEquals(featureModel, feature.feature);
    }

    /**
     * Test the service with annotation and service not found.
     */
    @Test
    public void testServiceAnnotationNotFound()
    {
        final Featurable featurable = new FeaturableModel();
        final AtomicReference<String> unfilledType = new AtomicReference<>();
        final Feature feature = new FeatureModel()
        {
            private @FeatureGet String type;

            @Override
            public void prepare(FeatureProvider provider)
            {
                super.prepare(provider);
                unfilledType.set(type);
            }
        };
        assertThrows(() -> featurable.addFeature(feature), "Class not found: " + String.class + " in " + feature);
    }

    /**
     * Test the feature itself.
     */
    @Test
    public void testFeatureItself()
    {
        final FeatureItself object = new FeatureItself();
        object.prepare(object);

        assertFalse(object.hasFeature(FeatureItself.class));
    }

    /**
     * Test the set field not accessible.
     * 
     * @throws ReflectiveOperationException If error.
     */
    @Test
    public void testSetFieldNotAccessible() throws ReflectiveOperationException
    {
        final FeatureItself featurable = new FeatureItself();
        final Method method = FeaturableModel.class.getDeclaredMethod("setField",
                                                                      Field.class,
                                                                      Object.class,
                                                                      Class.class);
        UtilReflection.setAccessible(method, true);
        assertThrows(InvocationTargetException.class,
                     () -> method.invoke(featurable,
                                         featurable.getClass().getDeclaredField("object"),
                                         featurable,
                                         Object.class),
                     null);

    }

    /**
     * Test the add features.
     */
    @Test
    public void testAddFeatures()
    {
        final Media media = Medias.create("Features.xml");

        final Xml root = new Xml(FeaturableConfig.NODE_FEATURABLE);
        final Xml unknown = root.createChild(FeaturableConfig.NODE_FEATURE);
        unknown.setText(MyFeature.class.getName());

        root.save(media);

        Featurable featurable = new FeaturableModel(new Services(), new Setup(media));
        featurable.checkListener(featurable);

        assertEquals(media, featurable.getMedia());

        for (final Feature next : featurable.getFeatures())
        {
            assertTrue(MyFeature.class.equals(next.getClass())
                       || Identifiable.class.isAssignableFrom(next.getClass())
                       || Recycler.class.isAssignableFrom(next.getClass()),
                       next.getClass().getName());
        }

        featurable = new FeaturableModel(new Services(), new Setup(media));

        UtilFile.deleteFile(media.getFile());
    }

    @FeatureInterface
    private static class MyFeatureModel extends FeatureModel
    {
        private @FeatureGet MyFeatureInterface feature;
    }

    /**
     * Mock feature.
     */
    @FeatureInterface
    private static interface MyFeatureInterface extends Feature
    {
        // Mock
    }

    /**
     * Mock feature.
     */
    @FeatureInterface
    private static class MyFeature extends FeatureModel implements MyFeatureInterface
    {
        // Mock
    }

    /**
     * Mock feature.
     */
    @FeatureInterface
    private static class MyFeatureNotCompatible extends FeatureModel
    {
        // Mock
    }

    /**
     * Mock feature itself.
     */
    @FeatureInterface
    private static class FeatureItself extends FeaturableModel implements Feature
    {
        @SuppressWarnings("unused") private Object object;

        @Override
        public void prepare(FeatureProvider provider)
        {
            // Mock
        }

        @Override
        public void checkListener(Object listener)
        {
            // Mock
        }
    }
}
