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
package com.b3dgs.lionengine.game.feature.tile.map.collision;

import static com.b3dgs.lionengine.UtilAssert.assertEquals;
import static com.b3dgs.lionengine.UtilAssert.assertPrivateConstructor;
import static com.b3dgs.lionengine.UtilAssert.assertThrows;
import static com.b3dgs.lionengine.UtilAssert.assertTrue;

import java.util.Arrays;
import java.util.Collection;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.b3dgs.lionengine.Constant;
import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.Medias;
import com.b3dgs.lionengine.Xml;
import com.b3dgs.lionengine.game.Configurer;
import com.b3dgs.lionengine.game.feature.Camera;
import com.b3dgs.lionengine.game.feature.Services;
import com.b3dgs.lionengine.game.feature.tile.map.MapTile;
import com.b3dgs.lionengine.game.feature.tile.map.MapTileGame;
import com.b3dgs.lionengine.game.feature.tile.map.MapTileGroupModel;

/**
 * Test {@link CollisionCategoryConfig}.
 */
public final class CollisionCategoryConfigTest
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

    /** Formula test. */
    private final CollisionFormula formula = new CollisionFormula("formula",
                                                                  new CollisionRange(Axis.X, 0, 1, 2, 3),
                                                                  new CollisionFunctionLinear(1.0, 2.0),
                                                                  new CollisionConstraint());
    /** Group test. */
    private final CollisionGroup group = new CollisionGroup("group", Arrays.asList(formula));
    /** Category test. */
    private final CollisionCategory category = new CollisionCategory("name", Axis.X, 1, 2, Arrays.asList(group));
    /** Map collision. */
    private MapTileCollision mapCollision;

    /**
     * Prepare test.
     */
    @BeforeEach
    public void prepare()
    {
        final Services services = new Services();
        services.add(new Camera());

        final MapTile map = services.add(new MapTileGame());
        map.addFeature(new MapTileGroupModel());
        mapCollision = new MapTileCollisionModel(services);
        mapCollision.prepare(map);
    }

    /**
     * Test constructor.
     */
    @Test
    public void testConstructor()
    {
        assertPrivateConstructor(CollisionCategoryConfig.class);
    }

    /**
     * Test exports imports.
     */
    @Test
    public void testExportsImports()
    {
        final Media formulasConfig = UtilConfig.createFormulaConfig(formula);
        final Media groupsConfig = UtilConfig.createGroupsConfig(group);
        mapCollision.loadCollisions(formulasConfig, groupsConfig);

        final Xml root = new Xml("categories");
        CollisionCategoryConfig.exports(root, category);

        final Media media = Medias.create("object.xml");
        root.save(media);

        final Collection<CollisionCategory> imported = CollisionCategoryConfig.imports(new Configurer(media),
                                                                                       mapCollision);

        assertEquals(category, imported.iterator().next());

        assertTrue(formulasConfig.getFile().delete());
        assertTrue(groupsConfig.getFile().delete());
        assertTrue(media.getFile().delete());
    }

    /**
     * Test category.
     */
    @Test
    public void testCategory()
    {
        final Xml root = new Xml("categories");
        CollisionCategoryConfig.exports(root, category);
        CollisionCategoryConfig.exports(root, new CollisionCategory("name2", Axis.X, 1, 2, Arrays.asList(group)));

        final Collection<CollisionCategory> imported = CollisionCategoryConfig.imports(root);

        assertEquals(category, imported.iterator().next());
    }

    /**
     * Test from map.
     */
    @Test
    public void testMap()
    {
        final Media formulasConfig = UtilConfig.createFormulaConfig(formula);
        final Media groupsConfig = UtilConfig.createGroupsConfig(group);

        mapCollision.loadCollisions(formulasConfig, groupsConfig);

        final Xml root = new Xml("categories");
        CollisionCategoryConfig.exports(root, category);
        final CollisionCategory imported = CollisionCategoryConfig.imports(root.getChild(Constant.XML_PREFIX
                                                                                         + "category"),
                                                                           mapCollision);

        assertEquals(category, imported);

        assertTrue(formulasConfig.getFile().delete());
        assertTrue(groupsConfig.getFile().delete());
    }

    /**
     * Test with invalid axis.
     */
    @Test
    public void testMapInvalidAxis()
    {
        final Media formulasConfig = UtilConfig.createFormulaConfig(formula);
        final Media groupsConfig = UtilConfig.createGroupsConfig(group);

        mapCollision.loadCollisions(formulasConfig, groupsConfig);

        final Xml root = new Xml("categories");
        CollisionCategoryConfig.exports(root, category);

        root.getChild(Constant.XML_PREFIX + "category").writeString(CollisionCategoryConfig.ATT_AXIS, "void");
        assertThrows(() -> CollisionCategoryConfig.imports(root.getChild(Constant.XML_PREFIX + "category"),
                                                           mapCollision),
                     "Unknown axis: void");

        assertTrue(formulasConfig.getFile().delete());
        assertTrue(groupsConfig.getFile().delete());
    }
}
