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

import org.junit.Assert;
import org.junit.Test;

import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.game.object.feature.Refreshable;
import com.b3dgs.lionengine.game.object.feature.displayable.Displayable;

/**
 * Test the features class.
 */
public class FeaturesTest
{
    /**
     * Test the features.
     */
    @Test
    public void testFeatures()
    {
        final Features features = new Features();

        Assert.assertFalse(features.contains(Feature.class));

        final Feature feature = new FeatureModel()
        {
            // Mock
        };
        features.add(feature);

        Assert.assertTrue(features.contains(Feature.class));
        Assert.assertEquals(feature, features.get(Feature.class));

        for (final Feature current : features.getFeatures())
        {
            Assert.assertEquals(feature, current);
        }
        for (final Class<? extends Feature> type : features.getFeaturesType())
        {
            Assert.assertTrue(Feature.class.isAssignableFrom(type));
        }
    }

    /**
     * Test the feature not found.
     */
    @Test(expected = LionEngineException.class)
    public void testFeatureNotFound()
    {
        final Features features = new Features();
        Assert.assertNotNull(features.get(Feature.class));
    }

    /**
     * Test the feature with not compatible interface.
     */
    @Test
    public void testInterfaceNotCompatible()
    {
        final Features features = new Features();
        features.add(new FeatureNotCompatible());

        Assert.assertTrue(features.contains(Feature.class));
        Assert.assertTrue(features.contains(Refreshable.class));
        Assert.assertFalse(features.contains(Displayable.class));
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
}
