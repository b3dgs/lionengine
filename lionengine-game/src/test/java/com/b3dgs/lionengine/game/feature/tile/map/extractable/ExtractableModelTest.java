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
package com.b3dgs.lionengine.game.feature.tile.map.extractable;

import static com.b3dgs.lionengine.UtilAssert.assertEquals;
import static com.b3dgs.lionengine.UtilAssert.assertThrows;

import org.junit.jupiter.api.Test;

import com.b3dgs.lionengine.game.feature.Featurable;
import com.b3dgs.lionengine.game.feature.FeaturableModel;
import com.b3dgs.lionengine.game.feature.Identifiable;
import com.b3dgs.lionengine.game.feature.Services;
import com.b3dgs.lionengine.game.feature.TransformableModel;
import com.b3dgs.lionengine.game.feature.tile.map.MapTileGame;

/**
 * Test {@link ExtractableModel}.
 */
public final class ExtractableModelTest
{
    /**
     * Test constructor with null services.
     */
    @Test
    public void testConstructorNullServices()
    {
        assertThrows(() -> new ExtractableModel(null), "Unexpected null argument !");
    }

    /**
     * Test the extraction config.
     */
    @Test
    public void testConfig()
    {
        final Extractable extractable = UtilExtractable.createExtractable();

        assertEquals(10, extractable.getResourceQuantity());
        assertEquals(ResourceType.WOOD, extractable.getResourceType());

        assertEquals(0, extractable.getInTileX());
        assertEquals(0, extractable.getInTileY());
        assertEquals(0, extractable.getInTileWidth());
        assertEquals(0, extractable.getInTileHeight());

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
        featurable.addFeature(new TransformableModel());

        final Extractable extractable = new ExtractableModel(services);
        extractable.prepare(featurable);
        extractable.setResourcesQuantity(10);

        assertEquals(10, extractable.getResourceQuantity());

        extractable.extractResource(5);

        assertEquals(5, extractable.getResourceQuantity());

        extractable.getFeature(Identifiable.class).notifyDestroyed();
    }
}
