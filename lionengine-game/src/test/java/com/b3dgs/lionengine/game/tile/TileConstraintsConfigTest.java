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
package com.b3dgs.lionengine.game.tile;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import com.b3dgs.lionengine.Constant;
import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.core.Medias;
import com.b3dgs.lionengine.game.Orientation;
import com.b3dgs.lionengine.test.UtilTests;

/**
 * Test the tile constraint configuration class.
 */
public class TileConstraintsConfigTest
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
     * Test the constructor.
     * 
     * @throws Exception If error.
     */
    @Test(expected = LionEngineException.class)
    public void testConstructor() throws Exception
    {
        UtilTests.testPrivateConstructor(TileConstraintsConfig.class);
    }

    /**
     * Test the configuration import and export.
     */
    @Test
    public void testConfiguration()
    {
        final Map<TileRef, Collection<TileConstraint>> constraints = new HashMap<TileRef, Collection<TileConstraint>>();
        constraints.put(new TileRef(0, 1), Arrays.asList(new TileConstraint(Orientation.NORTH)));

        final TileConstraint constraint = new TileConstraint(Orientation.SOUTH);
        constraint.add(new TileRef(0, 1));
        constraints.put(new TileRef(0, 2), Arrays.asList(constraint));

        final Media config = Medias.create("constraints.xml");

        TileConstraintsConfig.exports(config, constraints);
        final Map<TileRef, Map<Orientation, TileConstraint>> imported = TileConstraintsConfig.imports(config);

        Assert.assertEquals(constraints.keySet(), imported.keySet());

        final Collection<TileConstraint> data = new ArrayList<TileConstraint>();
        for (final Map<Orientation, TileConstraint> map : imported.values())
        {
            data.addAll(map.values());
        }

        final Collection<TileConstraint> expected = new ArrayList<TileConstraint>();
        for (final Collection<TileConstraint> collection : constraints.values())
        {
            expected.addAll(collection);
        }

        Assert.assertEquals(expected, data);

        Assert.assertTrue(config.getFile().delete());
    }
}
