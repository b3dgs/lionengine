/*
 * Copyright (C) 2013-2022 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.game.feature.tile;

import static com.b3dgs.lionengine.UtilAssert.assertEquals;
import static com.b3dgs.lionengine.UtilAssert.assertPrivateConstructor;
import static com.b3dgs.lionengine.UtilAssert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.Medias;

/**
 * Test {@link TileGroupsConfig}.
 */
final class TileGroupsConfigTest
{
    /**
     * Prepare test.
     */
    @BeforeAll
    public static void beforeTests()
    {
        Medias.setResourcesDirectory(System.getProperty("java.io.tmpdir"));
    }

    /**
     * Clean up test.
     */
    @AfterAll
    public static void cleanUp()
    {
        Medias.setResourcesDirectory(null);
    }

    /**
     * Test the constructor.
     */
    @Test
    void testConstructor()
    {
        assertPrivateConstructor(TileGroupsConfig.class);
    }

    /**
     * Test exports imports.
     */
    @Test
    void testExportsImports()
    {
        final Collection<TileGroup> groups = new ArrayList<>();
        groups.add(new TileGroup("test", TileGroupType.NONE, new HashSet<>(Arrays.asList(Integer.valueOf(1)))));

        final Media config = Medias.create("tilegroups.xml");
        TileGroupsConfig.exports(config, groups);

        assertEquals(groups, TileGroupsConfig.imports(config));

        assertTrue(config.getFile().delete());
    }
}
