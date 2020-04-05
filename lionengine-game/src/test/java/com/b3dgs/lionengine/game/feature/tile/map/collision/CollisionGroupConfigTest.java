/*
 * Copyright (C) 2013-2020 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.game.feature.tile.map.collision;

import static com.b3dgs.lionengine.UtilAssert.assertEquals;
import static com.b3dgs.lionengine.UtilAssert.assertFalse;
import static com.b3dgs.lionengine.UtilAssert.assertTrue;

import java.util.Arrays;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.Medias;
import com.b3dgs.lionengine.Xml;
import com.b3dgs.lionengine.game.feature.Camera;
import com.b3dgs.lionengine.game.feature.Services;
import com.b3dgs.lionengine.game.feature.tile.map.MapTile;
import com.b3dgs.lionengine.game.feature.tile.map.MapTileGame;
import com.b3dgs.lionengine.game.feature.tile.map.MapTileGroupModel;

/**
 * Test {@link CollisionGroupConfig}.
 */
public final class CollisionGroupConfigTest
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
    public static void afterTests()
    {
        Medias.setResourcesDirectory(null);
    }

    private final Services services = new Services();
    private final CollisionFormula formula = new CollisionFormula("formula",
                                                                  new CollisionRange(Axis.X, 0, 1, 2, 3),
                                                                  new CollisionFunctionLinear(1.0, 2.0),
                                                                  new CollisionConstraint());
    private final CollisionGroup group = new CollisionGroup("group", Arrays.asList(formula));
    private MapTileCollision mapCollision;

    /**
     * Prepare test.
     */
    @BeforeEach
    public void prepare()
    {
        services.add(new Camera());

        final MapTile map = services.add(new MapTileGame());
        map.addFeature(new MapTileGroupModel());
        mapCollision = new MapTileCollisionModel();
        mapCollision.prepare(map);
    }

    /**
     * Test exports imports.
     */
    @Test
    public void testExportsImports()
    {
        final Xml root = new Xml("groups");
        CollisionGroupConfig.exports(root, group);

        final Media config = Medias.create("groups.xml");
        root.save(config);

        final CollisionGroupConfig imported = CollisionGroupConfig.imports(config);

        assertEquals(group, imported.getGroups().values().iterator().next());
        assertEquals(group, imported.getGroup("group"));

        assertTrue(config.getFile().delete());
    }

    /**
     * Test with map.
     */
    @Test
    public void testExportsImportsMap()
    {
        final Xml root = new Xml("groups");
        CollisionGroupConfig.exports(root, group);

        final Media groupsConfig = Medias.create("groups.xml");
        root.save(groupsConfig);

        final Media formulasConfig = UtilConfig.createFormulaConfig(formula);

        mapCollision.loadCollisions(formulasConfig, groupsConfig);

        final CollisionGroupConfig groups = CollisionGroupConfig.imports(groupsConfig);

        assertEquals(group, groups.getGroups().values().iterator().next());

        assertTrue(groupsConfig.getFile().delete());
        assertTrue(formulasConfig.getFile().delete());
    }

    /**
     * Test has and remove functions.
     */
    @Test
    public void testHasRemove()
    {
        final Xml root = new Xml("groups");
        CollisionGroupConfig.exports(root, group);

        assertFalse(CollisionGroupConfig.has(root, "void"));
        assertTrue(CollisionGroupConfig.has(root, "group"));

        CollisionGroupConfig.remove(root, "void");
        CollisionGroupConfig.remove(root, "group");

        assertFalse(CollisionGroupConfig.has(root, "group"));
    }
}
