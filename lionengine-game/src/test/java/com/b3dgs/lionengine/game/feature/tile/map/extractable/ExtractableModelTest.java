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
package com.b3dgs.lionengine.game.feature.tile.map.extractable;

import org.junit.Assert;
import org.junit.Test;

import com.b3dgs.lionengine.game.Featurable;
import com.b3dgs.lionengine.game.FeaturableModel;
import com.b3dgs.lionengine.game.Services;
import com.b3dgs.lionengine.game.feature.Identifiable;
import com.b3dgs.lionengine.game.feature.IdentifiableModel;
import com.b3dgs.lionengine.game.feature.TransformableModel;
import com.b3dgs.lionengine.game.feature.tile.map.MapTileGame;
import com.b3dgs.lionengine.util.UtilTests;

/**
 * Test the extractable.
 */
public class ExtractableModelTest
{
    /**
     * Test the extraction config.
     */
    @Test
    public void testConfig()
    {
        final Extractable extractable = UtilExtractable.createExtractable();

        Assert.assertEquals(10, extractable.getResourceQuantity());
        Assert.assertEquals(ResourceType.WOOD, extractable.getResourceType());

        Assert.assertEquals(0, extractable.getInTileX(), UtilTests.PRECISION);
        Assert.assertEquals(0, extractable.getInTileY(), UtilTests.PRECISION);
        Assert.assertEquals(0, extractable.getInTileWidth(), UtilTests.PRECISION);
        Assert.assertEquals(0, extractable.getInTileHeight(), UtilTests.PRECISION);

        extractable.getFeature(Identifiable.class).notifyDestroyed();
    }

    /**
     * Test the extraction.
     */
    @Test
    public void testExtract()
    {
        final Services services = new Services();
        services.add(new MapTileGame());

        final Featurable featurable = new FeaturableModel();
        featurable.addFeature(new IdentifiableModel());
        featurable.addFeature(new TransformableModel());
        featurable.prepareFeatures(services);

        final Extractable extractable = new ExtractableModel();
        extractable.prepare(featurable, services);
        extractable.setResourcesQuantity(10);

        Assert.assertEquals(10, extractable.getResourceQuantity());

        extractable.extractResource(5);

        Assert.assertEquals(5, extractable.getResourceQuantity());

        extractable.getFeature(Identifiable.class).notifyDestroyed();
    }
}
