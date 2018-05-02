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

import java.io.Serializable;

import org.junit.jupiter.api.Test;

import com.b3dgs.lionengine.game.Feature;

/**
 * Test {@link Features}.
 */
public final class FeaturesTest
{
    /**
     * Test the features.
     */
    @Test
    public void testFeatures()
    {
        final Features features = new Features();

        assertFalse(features.contains(Feature.class));

        final Feature feature = new FeatureModel()
        {
            // Mock
        };
        features.add(feature);

        assertTrue(features.contains(Feature.class));
        assertEquals(feature, features.get(Feature.class));

        for (final Feature current : features.getFeatures())
        {
            assertEquals(feature, current);
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
        final Features features = new Features();

        assertThrows(() -> features.get(Feature.class), "Feature not found: " + Feature.class.getName());
    }

    /**
     * Test the feature with not compatible interface.
     */
    @Test
    public void testInterfaceNotCompatible()
    {
        final Features features = new Features();
        features.add(new FeatureNotCompatible());

        assertTrue(features.contains(Feature.class));
        assertTrue(features.contains(Refreshable.class));
        assertFalse(features.contains(Displayable.class));
    }

    /**
     * Test the feature with inheritance.
     */
    @Test
    public void testInheritance()
    {
        final Features features = new Features();
        features.add(new FeatureLevel2());

        assertTrue(features.contains(FeatureLevel1.class));
        assertTrue(features.contains(FeatureLevel2.class));
        assertTrue(features.contains(Refreshable.class));
    }

    /**
     * Mock feature.
     */
    private static class FeatureNotCompatible extends FeatureModel implements Serializable, Refreshable
    {
        private static final long serialVersionUID = 1L;

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
    private static final class FeatureLevel2 extends FeatureLevel1
    {
        // Mock
    }
}
