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

import org.junit.Assert;
import org.junit.Test;

import com.b3dgs.lionengine.game.Featurable;
import com.b3dgs.lionengine.game.FeaturableModel;
import com.b3dgs.lionengine.game.Feature;
import com.b3dgs.lionengine.game.Services;

/**
 * Test the feature model class.
 */
public class FeatureModelTest
{
    private final Feature feature = new FeatureModel();

    /**
     * Test the feature model.
     */
    @Test
    public void testModel()
    {
        final Featurable featurable = new FeaturableModel();
        final Transformable transformable = new TransformableModel();
        featurable.addFeature(transformable);
        feature.prepare(featurable, new Services());

        Assert.assertEquals(featurable.getFeature(Transformable.class), feature.getFeature(Transformable.class));
        Assert.assertEquals(transformable, feature.getFeature(Transformable.class));
        Assert.assertEquals(transformable, feature.getFeatures().iterator().next());
        for (final Class<? extends Feature> type : feature.getFeaturesType())
        {
            Assert.assertTrue(type == Transformable.class
                              || type == TransformableModel.class
                              || type == Recyclable.class);
        }
        Assert.assertTrue(feature.hasFeature(Transformable.class));
    }

    /**
     * Test the feature not prepared.
     */
    @Test(expected = NullPointerException.class)
    public void testNotPrepared()
    {
        Assert.assertNotNull(feature.getFeatures());
    }
}
