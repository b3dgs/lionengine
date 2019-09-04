/*
 * Copyright (C) 2013-2019 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
import static com.b3dgs.lionengine.UtilAssert.assertThrows;
import static com.b3dgs.lionengine.UtilAssert.assertTrue;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.Medias;
import com.b3dgs.lionengine.game.Feature;

/**
 * Test {@link Features}.
 */
public final class FeaturesTest
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
        config = UtilTransformable.createMedia(FeaturesTest.class);
    }

    /**
     * Clean up test.
     */
    @AfterAll
    public static void afterTests()
    {
        assertTrue(config.getFile().delete());
        Medias.setResourcesDirectory(null);
    }

    private final Services services = new Services();
    private final Setup setup = new Setup(config);
    private final Features features = new Features();

    /**
     * Test the feature not annotated.
     */
    @Test
    public void testFeatureNotAnnotated()
    {
        assertThrows(() -> features.add(new FeatureModel(services, setup)),
                     Features.ERROR_FEATURE_NOT_ANNOTATED + FeatureModel.class);
    }

    /**
     * Test the features.
     */
    @Test
    public void testFeatures()
    {
        assertFalse(features.contains(Feature.class));

        final Identifiable identifiable = new IdentifiableModel();
        features.add(identifiable);

        assertEquals(identifiable, features.get(Identifiable.class));
        assertEquals(identifiable, features.get(IdentifiableModel.class));

        int i = 0;
        for (final Feature current : features.getFeatures())
        {
            assertEquals(identifiable, current);
            i++;
        }
        assertEquals(1, i);

        i = 0;
        for (final Class<? extends Feature> type : features.getFeaturesType())
        {
            assertTrue(Feature.class.isAssignableFrom(type));
            i++;
        }
        assertEquals(2, i);
    }

    /**
     * Test the feature not found.
     */
    @Test
    public void testFeatureNotFound()
    {
        assertThrows(() -> features.get(Feature.class), "Feature not found: " + Feature.class.getName());
    }

    /**
     * Test the feature with inheritance.
     */
    @Test
    public void testInheritance()
    {
        features.add(new FeatureLevel2Model(services, setup));

        assertTrue(features.contains(FeatureLevel1.class));
        assertTrue(features.contains(FeatureLevel2.class));
        assertTrue(features.contains(Refreshable.class));
    }

    /**
     * Test add feature already referenced.
     */
    @Test
    public void testAddExists()
    {
        features.add(new FeatureLevel1Model(services, setup));

        final String error = Features.ERROR_FEATURE_EXISTS
                             + FeatureLevel1Model.class
                             + Features.WITH
                             + FeatureLevel1Model.class;

        assertThrows(() -> features.add(new FeatureLevel1Model(services, setup)), error);
    }

    /**
     * Test add feature already referenced in depth.
     */
    @Test
    public void testAddExistsDepth()
    {
        features.add(new RefreshableModel(extrp ->
        {
            // Nothing to do
        }));

        final String error = Features.ERROR_FEATURE_EXISTS
                             + FeatureLevel1Model.class
                             + Features.AS
                             + Refreshable.class
                             + Features.WITH
                             + RefreshableModel.class;

        assertThrows(() -> features.add(new FeatureLevel1Model(services, setup)), error);
    }

    /**
     * Mock feature.
     */
    @FeatureInterface
    private static interface FeatureLevel1 extends Refreshable
    {
        // Mock
    }

    /**
     * Mock feature.
     */
    @FeatureInterface
    private static interface FeatureLevel2 extends Feature
    {
        // Mock
    }

    /**
     * Mock feature.
     */
    @FeatureInterface
    private static class FeatureLevel1Model extends FeatureModel implements FeatureLevel1
    {
        private FeatureLevel1Model(Services services, Setup setup)
        {
            super(services, setup);
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
    @FeatureInterface
    private static class FeatureLevel2Model extends FeatureLevel1Model implements FeatureLevel2
    {
        private FeatureLevel2Model(Services services, Setup setup)
        {
            super(services, setup);
        }

        @Override
        public void update(double extrp)
        {
            // Mock
        }
    }
}
