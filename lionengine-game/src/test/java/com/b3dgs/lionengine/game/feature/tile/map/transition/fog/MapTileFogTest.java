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
package com.b3dgs.lionengine.game.feature.tile.map.transition.fog;

import java.util.ArrayList;
import java.util.Arrays;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import com.b3dgs.lionengine.Constant;
import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.core.Medias;
import com.b3dgs.lionengine.game.FeaturableModel;
import com.b3dgs.lionengine.game.Services;
import com.b3dgs.lionengine.game.Setup;
import com.b3dgs.lionengine.game.feature.Transformable;
import com.b3dgs.lionengine.game.feature.TransformableModel;
import com.b3dgs.lionengine.game.feature.UtilSetup;
import com.b3dgs.lionengine.game.feature.tile.map.MapTile;
import com.b3dgs.lionengine.game.feature.tile.map.UtilMap;
import com.b3dgs.lionengine.game.feature.tile.map.transition.UtilMapTransition;

/**
 * Test the map tile fog class.
 */
public class MapTileFogTest
{
    /** Test configuration. */
    private static Media config;

    /**
     * Prepare test.
     */
    @BeforeClass
    public static void setUp()
    {
        Medias.setResourcesDirectory(System.getProperty("java.io.tmpdir"));
        config = UtilMapTransition.createTransitions();
    }

    /**
     * Clean up test.
     */
    @AfterClass
    public static void cleanUp()
    {
        Assert.assertTrue(config.getFile().delete());
        Medias.setResourcesDirectory(Constant.EMPTY_STRING);
    }

    /**
     * Test the fog.
     */
    @Test
    public void testFog()
    {
        final FovableModel fovable = new FovableModel();
        final Services services = new Services();
        final MapTile map = UtilMap.createMap(5);
        services.add(map);

        final Setup setup = new Setup(UtilSetup.createConfig());
        final FeaturableModel object = new FeaturableModel();
        final Transformable transformable = object.addFeatureAndGet(new TransformableModel(setup));
        transformable.teleport(3, 3);
        fovable.prepare(object, services);
        fovable.setFov(1);

        final MapTileFog fog = new MapTileFog();
        Medias.setLoadFromJar(MapTileFog.class);
        fog.create(map, Medias.create("fog.xml"), null);
        Medias.setLoadFromJar(null);

        Assert.assertEquals(16, fog.getTile(2, 3).getNumber());
        Assert.assertEquals(16, fog.getTile(3, 3).getNumber());
        Assert.assertEquals(16, fog.getTile(4, 3).getNumber());

        fog.update(new ArrayList<Fovable>(Arrays.asList(fovable)));

        Assert.assertEquals(10, fog.getTile(2, 2).getNumber());
        Assert.assertEquals(1, fog.getTile(3, 2).getNumber());
        Assert.assertEquals(11, fog.getTile(4, 2).getNumber());
        Assert.assertEquals(2, fog.getTile(2, 3).getNumber());
        Assert.assertEquals(17, fog.getTile(3, 3).getNumber());
        Assert.assertEquals(3, fog.getTile(4, 3).getNumber());
        Assert.assertEquals(8, fog.getTile(2, 4).getNumber());
        Assert.assertEquals(0, fog.getTile(3, 4).getNumber());
        Assert.assertEquals(9, fog.getTile(4, 4).getNumber());

        fog.reset();

        Assert.assertEquals(16, fog.getTile(2, 3).getNumber());
        Assert.assertEquals(16, fog.getTile(3, 3).getNumber());
        Assert.assertEquals(16, fog.getTile(4, 3).getNumber());
    }
}
