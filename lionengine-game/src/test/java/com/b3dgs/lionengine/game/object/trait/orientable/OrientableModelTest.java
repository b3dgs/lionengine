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
package com.b3dgs.lionengine.game.object.trait.orientable;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import com.b3dgs.lionengine.Constant;
import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.core.Medias;
import com.b3dgs.lionengine.game.Orientation;
import com.b3dgs.lionengine.game.map.MapTileGame;
import com.b3dgs.lionengine.game.object.ObjectGame;
import com.b3dgs.lionengine.game.object.ObjectGameTest;
import com.b3dgs.lionengine.game.object.Services;
import com.b3dgs.lionengine.game.object.Setup;
import com.b3dgs.lionengine.game.object.trait.transformable.TransformableModel;
import com.b3dgs.lionengine.game.tile.Tiled;
import com.b3dgs.lionengine.util.UtilTests;

/**
 * Test the orientable trait.
 */
public class OrientableModelTest
{
    /**
     * Prepare test.
     */
    @BeforeClass
    public static void setUp()
    {
        Medias.setResourcesDirectory(System.getProperty("java.io.tmpdir"));
    }

    /**
     * Clean up test.
     */
    @AfterClass
    public static void cleanUp()
    {
        Medias.setResourcesDirectory(Constant.EMPTY_STRING);
    }

    /**
     * Point to a tiled.
     * 
     * @param orientable The orientable.
     * @param tx The horizontal location.
     * @param ty The vertical location.
     */
    private static void pointToTiled(Orientable orientable, final int tx, final int ty)
    {
        orientable.pointTo(new Tiled()
        {
            @Override
            public int getInTileX()
            {
                return tx;
            }

            @Override
            public int getInTileY()
            {
                return ty;
            }

            @Override
            public int getInTileWidth()
            {
                return 0;
            }

            @Override
            public int getInTileHeight()
            {
                return 0;
            }
        });
    }

    /**
     * Test the orientations enum.
     * 
     * @throws Exception If error.
     */
    @Test
    public void testEnum() throws Exception
    {
        UtilTests.testEnum(Orientation.class);
    }

    /**
     * Test the orientations.
     */
    @Test
    public void testOrientable()
    {
        final OrientableModel orientable = new OrientableModel();
        final Media media = ObjectGameTest.createMedia(ObjectGame.class);
        final Services services = new Services();
        services.add(new MapTileGame());
        final ObjectGame object = new ObjectGame(new Setup(media), services);
        object.addType(new TransformableModel());
        orientable.prepare(object, services);

        Assert.assertEquals(Orientation.NORTH, orientable.getOrientation());

        orientable.setOrientation(Orientation.SOUTH);

        Assert.assertEquals(Orientation.SOUTH, orientable.getOrientation());

        orientable.pointTo(0, 0);
        Assert.assertEquals(Orientation.SOUTH, orientable.getOrientation());

        orientable.pointTo(-1, 0);
        Assert.assertEquals(Orientation.WEST, orientable.getOrientation());

        orientable.pointTo(1, 0);
        Assert.assertEquals(Orientation.EAST, orientable.getOrientation());

        orientable.pointTo(0, 1);
        Assert.assertEquals(Orientation.NORTH, orientable.getOrientation());

        orientable.pointTo(0, -1);
        Assert.assertEquals(Orientation.SOUTH, orientable.getOrientation());

        orientable.pointTo(-1, 1);
        Assert.assertEquals(Orientation.NORTH_WEST, orientable.getOrientation());

        orientable.pointTo(1, 1);
        Assert.assertEquals(Orientation.NORTH_EAST, orientable.getOrientation());

        orientable.pointTo(-1, -1);
        Assert.assertEquals(Orientation.SOUTH_WEST, orientable.getOrientation());

        orientable.pointTo(1, -1);
        Assert.assertEquals(Orientation.SOUTH_EAST, orientable.getOrientation());

        ObjectGameTest.freeId(object);
        Assert.assertTrue(media.getFile().delete());
    }

    /**
     * Test the orientations
     */
    @Test
    public void testOrientableTiled()
    {
        final OrientableModel orientable = new OrientableModel();
        final Media media = ObjectGameTest.createMedia(ObjectGame.class);
        final Services services = new Services();
        services.add(new MapTileGame());
        final ObjectGame object = new ObjectGame(new Setup(media), services);
        object.addType(new TransformableModel());
        orientable.prepare(object, services);

        Assert.assertEquals(Orientation.NORTH, orientable.getOrientation());

        orientable.setOrientation(Orientation.SOUTH);

        Assert.assertEquals(Orientation.SOUTH, orientable.getOrientation());

        pointToTiled(orientable, 0, 0);
        Assert.assertEquals(Orientation.SOUTH, orientable.getOrientation());

        pointToTiled(orientable, -1, 0);
        Assert.assertEquals(Orientation.WEST, orientable.getOrientation());

        pointToTiled(orientable, 1, 0);
        Assert.assertEquals(Orientation.EAST, orientable.getOrientation());

        pointToTiled(orientable, 0, 1);
        Assert.assertEquals(Orientation.NORTH, orientable.getOrientation());

        pointToTiled(orientable, 0, -1);
        Assert.assertEquals(Orientation.SOUTH, orientable.getOrientation());

        pointToTiled(orientable, -1, 1);
        Assert.assertEquals(Orientation.NORTH_WEST, orientable.getOrientation());

        pointToTiled(orientable, 1, 1);
        Assert.assertEquals(Orientation.NORTH_EAST, orientable.getOrientation());

        pointToTiled(orientable, -1, -1);
        Assert.assertEquals(Orientation.SOUTH_WEST, orientable.getOrientation());

        pointToTiled(orientable, 1, -1);
        Assert.assertEquals(Orientation.SOUTH_EAST, orientable.getOrientation());

        ObjectGameTest.freeId(object);
        Assert.assertTrue(media.getFile().delete());
    }
}
