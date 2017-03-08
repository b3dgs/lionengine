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
package com.b3dgs.lionengine.game;

import org.junit.Assert;
import org.junit.Test;

import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Viewer;
import com.b3dgs.lionengine.game.feature.Factory;
import com.b3dgs.lionengine.game.feature.tile.map.MapTile;
import com.b3dgs.lionengine.game.feature.tile.map.MapTileGame;
import com.b3dgs.lionengine.game.feature.tile.map.MapTileGroup;
import com.b3dgs.lionengine.game.feature.tile.map.MapTileGroupModel;

/**
 * Test the services class.
 */
public class ServicesTest
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

        Assert.assertEquals(services, services.get(Services.class));
        Assert.assertEquals(camera, services.get(Viewer.class));
        Assert.assertEquals(factory, services.get(Factory.class));
        Assert.assertEquals(map, services.get(MapTile.class));
        Assert.assertEquals(mapGroup, map.getFeature(MapTileGroup.class));
    }

    /**
     * Test the service add.
     */
    @Test
    public void testAddGet()
    {
        final Services services = new Services();
        final Camera camera = services.add(new Camera());
        Assert.assertEquals(camera, services.get(Camera.class));
    }

    /**
     * Test the service without constructor.
     */
    @Test
    public void testServiceNoConstructor()
    {
        final Services services = new Services();
        try
        {
            services.create(NoConstructorService.class);
        }
        catch (final LionEngineException exception)
        {
            Assert.assertEquals(IllegalAccessException.class, exception.getCause().getClass());
        }
    }

    /**
     * Test the service with invalid constructor.
     */
    @Test
    public void testServiceInvalidConstructor()
    {
        final Services services = new Services();
        try
        {
            services.create(InvalidConstructorService.class);
        }
        catch (final LionEngineException exception)
        {
            Assert.assertEquals(InstantiationException.class, exception.getCause().getClass());
        }
    }

    /**
     * Test the service not found.
     */
    @Test(expected = LionEngineException.class)
    public void testNotFound()
    {
        final Services services = new Services();
        Assert.assertNotNull(services.get(Camera.class));
    }

    /**
     * Test the service <code>null</code>.
     */
    @Test(expected = LionEngineException.class)
    public void testNull()
    {
        final Services services = new Services();
        Assert.assertNotNull(services.get(null));
    }

    /**
     * Service without constructor.
     */
    public static class NoConstructorService
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
