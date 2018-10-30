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

import static com.b3dgs.lionengine.UtilAssert.assertEquals;
import static com.b3dgs.lionengine.UtilAssert.assertFalse;
import static com.b3dgs.lionengine.UtilAssert.assertThrows;
import static com.b3dgs.lionengine.UtilAssert.assertTrue;

import org.junit.jupiter.api.Test;

import com.b3dgs.lionengine.game.Feature;

/**
 * Test {@link Features}.
 */
public final class FeaturesTest
{
    private final Features features = new Features();

    /**
     * Test the feature not annotated.
     */
    @Test
    public void testFeatureNotAnnotated()
    {
        assertThrows(() -> features.add(new FeatureModel()), Features.ERROR_FEATURE_NOT_ANNOTATED + FeatureModel.class);
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

        for (final Feature current : features.getFeatures())
        {
            assertEquals(identifiable, current);
        }
        for (final Class<? extends Feature> type : features.getFeaturesType())
        {
            assertTrue(Feature.class.isAssignableFrom(type));
        }
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
        features.add(new FeatureLevel2Model());

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
        final Features features = new Features();
        features.add(new FeatureLevel1Model());

        final String error = Features.ERROR_FEATURE_EXISTS
                             + FeatureLevel1Model.class
                             + Features.WITH
                             + FeatureLevel1Model.class;

        assertThrows(() -> features.add(new FeatureLevel1Model()), error);
    }

    /**
     * Test add feature already referenced in depth.
     */
    @Test
    public void testAddExistsDepth()
    {
        final Features features = new Features();
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

        assertThrows(() -> features.add(new FeatureLevel1Model()), error);
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
    private static interface FeatureLevel2 extends FeatureLevel1
    {
        // Mock
    }

    /**
     * Mock feature.
     */
    private static class FeatureLevel1Model extends FeatureModel implements FeatureLevel1
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
    private static class FeatureLevel2Model extends FeatureLevel1Model implements FeatureLevel2
    {
        // Mock
    }
}
