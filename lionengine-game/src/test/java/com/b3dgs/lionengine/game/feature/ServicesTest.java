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

import static com.b3dgs.lionengine.UtilAssert.assertCause;
import static com.b3dgs.lionengine.UtilAssert.assertEquals;
import static com.b3dgs.lionengine.UtilAssert.assertFalse;
import static com.b3dgs.lionengine.UtilAssert.assertThrows;

import org.junit.jupiter.api.Test;

import com.b3dgs.lionengine.Viewer;
import com.b3dgs.lionengine.game.feature.tile.map.MapTile;
import com.b3dgs.lionengine.game.feature.tile.map.MapTileGame;
import com.b3dgs.lionengine.game.feature.tile.map.MapTileGroup;
import com.b3dgs.lionengine.game.feature.tile.map.MapTileGroupModel;

/**
 * Test {@link Services}.
 */
public final class ServicesTest
{
    /**
     * Test the service creation.
     */
    @Test
    public void testCreateGet()
    {
        final Services services = new Services();
        final Camera camera = services.create(Camera.class);
        final Factory factory = services.create(Factory.class);
        final MapTile map = services.create(MapTileGame.class);
        final MapTileGroup mapGroup = map.addFeatureAndGet(new MapTileGroupModel());

        assertEquals(services, services.get(Services.class));
        assertEquals(camera, services.get(Viewer.class));
        assertEquals(factory, services.get(Factory.class));
        assertEquals(map, services.get(MapTile.class));
        assertEquals(mapGroup, map.getFeature(MapTileGroup.class));
    }

    /**
     * Test the service add.
     */
    @Test
    public void testAddGet()
    {
        final Services services = new Services();
        final Camera camera = services.add(new Camera());

        assertEquals(camera, services.get(Camera.class));
    }

    /**
     * Test the service without constructor.
     */
    @Test
    public void testServiceNoConstructor()
    {
        final Services services = new Services();

        assertCause(() -> services.create(NoConstructorService.class), IllegalAccessException.class);
    }

    /**
     * Test the service with invalid constructor.
     */
    @Test
    public void testServiceInvalidConstructor()
    {
        final Services services = new Services();

        assertCause(() -> services.create(InvalidConstructorService.class), InstantiationException.class);
    }

    /**
     * Test the service not found.
     */
    @Test
    public void testNotFound()
    {
        final Services services = new Services();

        assertThrows(() -> services.get(Camera.class), Services.ERROR_SERVICE_GET + Camera.class.getName());
    }

    /**
     * Test the service <code>null</code>.
     */
    @Test
    public void testNull()
    {
        final Services services = new Services();

        assertThrows(() -> services.get(null), "Unexpected null argument !");
    }

    /**
     * Test the service as optional.
     */
    @Test
    public void testOptional()
    {
        final Services services = new Services();
        final Camera camera = services.create(Camera.class);

        assertEquals(services, services.getOptional(Services.class).get());
        assertEquals(camera, services.getOptional(Camera.class).get());
        assertFalse(services.getOptional(String.class).isPresent());
    }

    /**
     * Service without constructor.
     */
    public static final class NoConstructorService
    {
        /**
         * Private.
         */
        private NoConstructorService()
        {
            super();
        }
    }

    /**
     * Service without valid constructor.
     */
    public static abstract class InvalidConstructorService
    {
        /**
         * Private.
         */
        public InvalidConstructorService()
        {
            super();
        }
    }
}
