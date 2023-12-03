/*
 * Copyright (C) 2013-2023 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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

import static com.b3dgs.lionengine.UtilAssert.assertEquals;
import static com.b3dgs.lionengine.UtilAssert.assertFalse;
import static com.b3dgs.lionengine.UtilAssert.assertTrue;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.Medias;
import com.b3dgs.lionengine.UtilFile;
import com.b3dgs.lionengine.Xml;
import com.b3dgs.lionengine.game.Feature;
import com.b3dgs.lionengine.game.FeatureProvider;

/**
 * Test {@link FeaturableModel}.
 */
final class FeaturableModelTest
{
    /** Object config test. */
    private static Media config;

    /**
     * Prepare test.
     */
    @BeforeAll
    public static void beforeTests()
    {
        Medias.setResourcesDirectory(System.getProperty("java.io.tmpdir"));
        config = UtilTransformable.createMedia(FeaturableModelTest.class);
    }

    /**
     * Clean up test.
     */
    @AfterAll
    public static void afterTests()
    {
        assertTrue(config.getFile().delete());
        Medias.setResourcesDirectory(null);
        FeaturableConfig.clearCache();
    }

    private final Services services = new Services();
    private final Setup setup = new Setup(config);

    /**
     * Test the featurable model with compatible feature.
     */
    @Test
    void testFeaturableCompatible()
    {
        final Featurable featurable = new FeaturableModel(services, setup);
        final MyFeatureInterface feature = new MyFeature(services, setup);
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
    void testFeaturableNotcompatible()
    {
        final Featurable featurable = new FeaturableModel(services, setup);
        final MyFeatureNotCompatible feature = new MyFeatureNotCompatible(services, setup);
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
     * Test.
     */
    @Test
    void testFeature()
    {
        final Featurable featurable = new FeaturableModel(services, setup);

        final MyFeatureInterface featureModel = new MyFeature(services, setup);
        featurable.addFeature(featureModel);

        final MyFeatureModel feature = featurable.addFeature(MyFeatureModel.class, services, setup);

        assertEquals(featureModel, feature.feature);
    }

    /**
     * Test the feature itself.
     */
    @Test
    void testFeatureItself()
    {
        final FeatureItself object = new FeatureItself(services, setup);
        object.prepare(object);

        assertFalse(object.hasFeature(FeatureItself.class));
    }

    /**
     * Test the add features.
     */
    @Test
    void testAddFeatures()
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

    /**
     * My feature.
     */
    @FeatureInterface
    public static final class MyFeatureModel extends FeatureModel
    {
        private final MyFeatureInterface feature;

        /**
         * Create model.
         * 
         * @param services The services reference.
         * @param setup The setup reference.
         * @param feature The feature.
         */
        public MyFeatureModel(Services services, Setup setup, MyFeatureInterface feature)
        {
            super(services, setup);

            this.feature = feature;
        }
    }

    /**
     * Mock feature.
     */
    @FeatureInterface
    public interface MyFeatureInterface extends Feature
    {
        // Mock
    }

    /**
     * Mock feature.
     */
    @FeatureInterface
    public static final class MyFeature extends FeatureModel implements MyFeatureInterface
    {
        /**
         * Create model.
         * 
         * @param services The services reference.
         * @param setup The setup reference.
         */
        public MyFeature(Services services, Setup setup)
        {
            super(services, setup);
        }
    }

    /**
     * Mock feature.
     */
    @FeatureInterface
    public static final class MyFeatureNotCompatible extends FeatureModel
    {
        /**
         * Create model.
         * 
         * @param services The services reference.
         * @param setup The setup reference.
         */
        public MyFeatureNotCompatible(Services services, Setup setup)
        {
            super(services, setup);
        }
    }

    /**
     * Mock feature itself.
     */
    @FeatureInterface
    public static final class FeatureItself extends FeaturableModel implements Feature
    {
        @SuppressWarnings("unused") private Object object;

        /**
         * Create model.
         * 
         * @param services The services reference.
         * @param setup The setup reference.
         */
        public FeatureItself(Services services, Setup setup)
        {
            super(services, setup);
        }

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
