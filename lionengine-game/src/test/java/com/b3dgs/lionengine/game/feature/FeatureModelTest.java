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
import static com.b3dgs.lionengine.UtilAssert.assertThrows;
import static com.b3dgs.lionengine.UtilAssert.assertTrue;

import org.junit.jupiter.api.Test;

import com.b3dgs.lionengine.game.Feature;

/**
 * Test {@link FeatureModel}.
 */
public final class FeatureModelTest
{
    private final Feature feature = new FeatureTest();

    /**
     * Test the feature model.
     */
    @Test
    public void testModel()
    {
        final Featurable featurable = new FeaturableModel();
        final Transformable transformable = new TransformableModel();
        featurable.addFeature(transformable);
        feature.prepare(new FeatureModel()
        {
            @Override
            public boolean hasFeature(Class<? extends Feature> feature)
            {
                return false;
            }
        });
        feature.prepare(featurable);

        assertEquals(featurable.getFeature(Transformable.class), feature.getFeature(Transformable.class));
        assertEquals(transformable, feature.getFeature(Transformable.class));

        final Feature feature = featurable.getFeatures().iterator().next();
        assertTrue(feature.equals(featurable.getFeature(Identifiable.class)) || feature.equals(transformable),
                   feature.getClass().getName());

        for (final Class<? extends Feature> type : feature.getFeaturesType())
        {
            assertTrue(type == Recycler.class
                       || type == Identifiable.class
                       || type == IdentifiableModel.class
                       || type == Transformable.class
                       || type == TransformableModel.class,
                       type.getName());
        }
        assertTrue(feature.hasFeature(Transformable.class));
    }

    /**
     * Test the feature not prepared.
     */
    @Test
    public void testNotPrepared()
    {
        assertThrows(NullPointerException.class, () -> feature.getFeatures(), null);
    }

    private class FeatureTest extends FeatureModel implements IdentifiableListener
    {
        @Override
        public void notifyDestroyed(Integer id)
        {
            // Mock
        }
    }
}
