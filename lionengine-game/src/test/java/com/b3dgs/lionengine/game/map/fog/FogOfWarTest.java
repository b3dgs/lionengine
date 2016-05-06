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
package com.b3dgs.lionengine.game.map.fog;

import java.util.ArrayList;
import java.util.Arrays;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import com.b3dgs.lionengine.Constant;
import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.core.Graphics;
import com.b3dgs.lionengine.core.Medias;
import com.b3dgs.lionengine.game.Services;
import com.b3dgs.lionengine.game.map.MapTile;
import com.b3dgs.lionengine.game.map.UtilMap;
import com.b3dgs.lionengine.game.object.ObjectGame;
import com.b3dgs.lionengine.game.object.Setup;
import com.b3dgs.lionengine.game.object.UtilSetup;
import com.b3dgs.lionengine.game.object.trait.transformable.Transformable;
import com.b3dgs.lionengine.game.object.trait.transformable.TransformableModel;
import com.b3dgs.lionengine.graphic.Graphic;
import com.b3dgs.lionengine.mock.FactoryGraphicMock;

/**
 * Test the fog of war class.
 */
public class FogOfWarTest
{
    /** Test configuration. */
    private static Media config;

    /**
     * Prepare test.
     */
    @BeforeClass
    public static void setUp()
    {
        Graphics.setFactoryGraphic(new FactoryGraphicMock());
        Medias.setResourcesDirectory(System.getProperty("java.io.tmpdir"));
        config = UtilSetup.createConfig();
    }

    /**
     * Clean up test.
     */
    @AfterClass
    public static void cleanUp()
    {
        Assert.assertTrue(config.getFile().delete());
        Graphics.setFactoryGraphic(null);
        Medias.setResourcesDirectory(Constant.EMPTY_STRING);
    }

    /**
     * Test the fog of war.
     */
    @Test
    public void testFogOfWar()
    {
        final FovableModel fovable = new FovableModel();
        final Services services = new Services();
        final MapTile map = UtilMap.createMap(5);
        UtilMap.fill(map, UtilMap.TILE_GROUND);
        services.add(map);

        final ObjectGame object = new ObjectGame(new Setup(config), services);
        final Transformable transformable = new TransformableModel();
        transformable.teleport(3, 3);
        object.addType(transformable);
        fovable.prepare(object, services);
        fovable.setFov(1);

        final FogOfWar fog = new FogOfWar();
        Medias.setLoadFromJar(MapTileFog.class);
        fog.create(map, Medias.create("fog.xml"));
        Medias.setLoadFromJar(null);

        Assert.assertFalse(fog.isFogged(2, 3));
        Assert.assertFalse(fog.isFogged(3, 3));
        Assert.assertFalse(fog.isFogged(4, 3));
        Assert.assertTrue(fog.isFogged(map.getTile(2, 3)));
        Assert.assertTrue(fog.isFogged(map.getTile(3, 3)));
        Assert.assertTrue(fog.isFogged(map.getTile(4, 3)));
        Assert.assertFalse(fog.isVisited(2, 3));
        Assert.assertFalse(fog.isVisited(3, 3));
        Assert.assertFalse(fog.isVisited(4, 3));

        fog.update(new ArrayList<Fovable>(Arrays.asList(fovable)));

        Assert.assertTrue(fog.isFogged(2, 3));
        Assert.assertFalse(fog.isFogged(3, 3));
        Assert.assertTrue(fog.isFogged(4, 3));
        Assert.assertTrue(fog.isFogged(map.getTile(2, 3)));
        Assert.assertTrue(fog.isFogged(map.getTile(3, 3)));
        Assert.assertTrue(fog.isFogged(map.getTile(4, 3)));
        Assert.assertFalse(fog.isVisited(2, 3));
        Assert.assertTrue(fog.isVisited(3, 3));
        Assert.assertFalse(fog.isVisited(4, 3));

        transformable.teleport(6, 6);
        fog.update(new ArrayList<Fovable>(Arrays.asList(fovable)));

        Assert.assertFalse(fog.isFogged(2, 3));
        Assert.assertFalse(fog.isFogged(3, 3));
        Assert.assertFalse(fog.isFogged(4, 3));
        Assert.assertTrue(fog.isFogged(map.getTile(2, 3)));
        Assert.assertTrue(fog.isFogged(map.getTile(3, 3)));
        Assert.assertTrue(fog.isFogged(map.getTile(4, 3)));
        Assert.assertFalse(fog.isVisited(2, 3));
        Assert.assertTrue(fog.isVisited(3, 3));
        Assert.assertFalse(fog.isVisited(4, 3));
    }

    /**
     * Test the fog of war render.
     */
    @Test
    public void testRender()
    {
        final FogOfWar fog = new FogOfWar();
        final Services services = new Services();
        final MapTile map = UtilMap.createMap(5);
        UtilMap.fill(map, UtilMap.TILE_GROUND);
        services.add(map);
        Medias.setLoadFromJar(MapTileFog.class);
        fog.create(map, Medias.create("fog.xml"));
        Medias.setLoadFromJar(null);

        Assert.assertFalse(fog.hasFogOfWar());

        final Graphic g = Graphics.createGraphic();

        fog.renderTile(g, map.getTile(0, 0), 0, 0);

        fog.setTilesheet(new SpriteTiledMock(), null);
        fog.renderTile(g, map.getTile(0, 0), 0, 0);

        Assert.assertFalse(fog.hasFogOfWar());

        fog.setTilesheet(null, new SpriteTiledMock());
        fog.renderTile(g, map.getTile(0, 0), 0, 0);

        Assert.assertFalse(fog.hasFogOfWar());

        fog.setTilesheet(new SpriteTiledMock(), new SpriteTiledMock());
        fog.setEnabled(true, false);
        fog.renderTile(g, map.getTile(0, 0), 0, 0);

        Assert.assertTrue(fog.hasFogOfWar());

        fog.setTilesheet(new SpriteTiledMock(), new SpriteTiledMock());
        fog.setEnabled(false, true);
        fog.renderTile(g, map.getTile(0, 0), 0, 0);

        Assert.assertTrue(fog.hasFogOfWar());

        fog.setTilesheet(new SpriteTiledMock(), new SpriteTiledMock());
        fog.setEnabled(true, true);
        fog.renderTile(g, map.getTile(0, 0), 0, 0);

        Assert.assertTrue(fog.hasFogOfWar());
    }
}
