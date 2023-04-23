/*
 * Copyright (C) 2013-2023 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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

import static com.b3dgs.lionengine.UtilAssert.assertTrue;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.Medias;
import com.b3dgs.lionengine.UtilAssert;
import com.b3dgs.lionengine.game.feature.FeaturableModel;
import com.b3dgs.lionengine.game.feature.Services;
import com.b3dgs.lionengine.game.feature.Setup;
import com.b3dgs.lionengine.game.feature.Transformable;
import com.b3dgs.lionengine.game.feature.TransformableModel;
import com.b3dgs.lionengine.game.feature.UtilSetup;
import com.b3dgs.lionengine.game.feature.tile.TileGroup;
import com.b3dgs.lionengine.game.feature.tile.TileGroupType;
import com.b3dgs.lionengine.game.feature.tile.map.MapTileGame;
import com.b3dgs.lionengine.game.feature.tile.map.MapTileGroup;
import com.b3dgs.lionengine.game.feature.tile.map.MapTileGroupModel;

/**
 * Test {@link MapTileCollisionModel} with complex map.
 */
final class MapTileCollisionModelComplexTest
{
    private static final int TILE_GROUND = 0;
    private static final int TILE_SLOPE_LEFT_0 = 1;
    private static final int TILE_SLOPE_LEFT_1 = 2;
    private static final int TILE_SLOPE_RIGHT_0 = 3;
    private static final int TILE_SLOPE_RIGHT_1 = 4;

    private static Stream<Arguments> speedX()
    {
        return Stream.of(Arguments.of(Double.valueOf(0.1)),
                         Arguments.of(Double.valueOf(1.0)),
                         Arguments.of(Double.valueOf(2.0)),
                         Arguments.of(Double.valueOf(5.0)),
                         Arguments.of(Double.valueOf(15.0)));
    }

    /** Test configuration. */
    private static Media config;

    /**
     * Prepare test.
     */
    @BeforeAll
    public static void beforeTests()
    {
        Medias.setResourcesDirectory(System.getProperty("java.io.tmpdir"));
        config = UtilSetup.createConfig(MapTileCollisionModelComplexTest.class);
    }

    /**
     * Clean up test.
     */
    @AfterAll
    public static void afterTests()
    {
        assertTrue(config.getFile().delete());
        Medias.setResourcesDirectory(null);
    }

    private static CollisionFormula createFormula(String name, double a, double b)
    {
        return new CollisionFormula(name,
                                    new CollisionRange(Axis.Y, 0, 16, 0, 0),
                                    new CollisionFunctionLinear(a, b),
                                    new CollisionConstraint());
    }

    private final CollisionFormula f_ground = createFormula("ground", 0.0, 0.0);
    private final CollisionFormula f_slope_left_0 = createFormula("slope_left_0", 0.5, 0.0);
    private final CollisionFormula f_slope_left_1 = createFormula("slope_left_1", 0.5, 8.0);
    private final CollisionFormula f_slope_right_0 = createFormula("slope_right_0", -0.5, 16.0);
    private final CollisionFormula f_slope_right_1 = createFormula("slope_right_1", -0.5, 8.0);

    private final CollisionGroup g_ground = new CollisionGroup("ground", Arrays.asList(f_ground));
    private final CollisionGroup g_slope_left_0 = new CollisionGroup("slope_left_0", Arrays.asList(f_slope_left_0));
    private final CollisionGroup g_slope_left_1 = new CollisionGroup("slope_left_1", Arrays.asList(f_slope_left_1));
    private final CollisionGroup g_slope_right_0 = new CollisionGroup("slope_right_0", Arrays.asList(f_slope_right_0));
    private final CollisionGroup g_slope_right_1 = new CollisionGroup("slope_right_1", Arrays.asList(f_slope_right_1));

    private final List<CollisionGroup> groupsList = Arrays.asList(g_ground,
                                                                  g_slope_left_0,
                                                                  g_slope_left_1,
                                                                  g_slope_right_0,
                                                                  g_slope_right_1);

    private final CollisionCategory category = new CollisionCategory("y", Axis.Y, 0, 0, true, groupsList);

    private final Services services = new Services();
    private final MapTileGame map = services.add(new MapTileGame());
    private final MapTileGroup mapGroup = map.addFeatureAndGet(new MapTileGroupModel());
    private final MapTileCollision mapCollision = map.addFeatureAndGet(new MapTileCollisionModel());
    private Transformable transformable;

    /**
     * Prepare test.
     */
    @BeforeEach
    public void prepare()
    {
        mapGroup.loadGroups(Arrays.asList(createGroup("ground", TILE_GROUND),
                                          createGroup("slope_left_0", TILE_SLOPE_LEFT_0),
                                          createGroup("slope_left_1", TILE_SLOPE_LEFT_1),
                                          createGroup("slope_right_0", TILE_SLOPE_RIGHT_0),
                                          createGroup("slope_right_1", TILE_SLOPE_RIGHT_1)));
        map.create(16, 16, 2, 2);
    }

    private TileGroup createGroup(String group, int number)
    {
        return new TileGroup(group, TileGroupType.NONE, new HashSet<>(Arrays.asList(Integer.valueOf(number))));
    }

    /**
     * Load map collisions.
     */
    private void loadCollisions()
    {
        final Map<String, CollisionFormula> formulas = new HashMap<>();
        for (final CollisionFormula formula : Arrays.asList(f_ground,
                                                            f_slope_left_0,
                                                            f_slope_left_1,
                                                            f_slope_right_0,
                                                            f_slope_right_1))
        {
            formulas.put(formula.getName(), formula);
        }

        final Map<String, CollisionGroup> groups = new HashMap<>();
        for (final CollisionGroup group : groupsList)
        {
            groups.put(group.getName(), group);
        }

        mapCollision.loadCollisions(new CollisionFormulaConfig(formulas), new CollisionGroupConfig(groups));
    }

    /**
     * Create object test.
     * 
     * @return The object test.
     */
    private Transformable createObject()
    {
        final Setup setup = new Setup(config);
        CollisionCategoryConfig.exports(setup.getRoot(), category);
        final FeaturableModel object = new FeaturableModel(services, setup);

        final Transformable transformable = object.addFeatureAndGet(new TransformableModel(services, setup));
        transformable.setSize(1, 1);

        final TileCollidable collidable = object.addFeatureAndGet(new TileCollidableModel(services, setup));
        collidable.setEnabled(true);

        return transformable;
    }

    private CollisionResult test(int tx1,
                                 int ty1,
                                 int n1,
                                 int tx2,
                                 int ty2,
                                 int n2,
                                 double x,
                                 double y,
                                 int side,
                                 double speedX)
    {
        map.setTile(tx1, ty1, n1);
        map.setTile(tx2, ty2, n2);
        loadCollisions();
        transformable = createObject();
        transformable.teleport(x, y);

        UtilAssert.assertEquals(map.getTile(tx1, ty1), map.getTile(transformable, 0, 0));

        transformable.moveLocation(1.0, side * speedX, -1.0);
        return mapCollision.computeCollision(transformable, category);
    }

    /**
     * Test collision to left from slope left 0 to slope left 1.
     * 
     * <pre>
     * <----
     * [ ][/]
     * [/][ ]
     * </pre>
     * 
     * @param speedX The horizontal speed used.
     */
    @ParameterizedTest
    @MethodSource("speedX")
    void testToLeftFromSlopeLeft0ToSlopeLeft1(double speedX)
    {
        final CollisionResult res = test(1, 1, TILE_SLOPE_LEFT_0, 0, 0, TILE_SLOPE_LEFT_1, 16.0, 16.0, -1, speedX);

        UtilAssert.assertEquals(Math.floor(16.5 - speedX / 2.0), res.getY());
    }

    /**
     * Test collision to right from slope left 1 to slope left 0.
     * 
     * <pre>
     * ---->
     * [ ][/]
     * [/][ ]
     * </pre>
     * 
     * @param speedX The horizontal speed used.
     */
    @ParameterizedTest
    @MethodSource("speedX")
    void testToRightFromSlopeLeft1ToSlopeLeft0(double speedX)
    {
        final CollisionResult res = test(0, 0, TILE_SLOPE_LEFT_1, 1, 1, TILE_SLOPE_LEFT_0, 15.5, 15.5, 1, speedX);

        UtilAssert.assertEquals(Math.floor(15.5 + speedX / 2.0), res.getY());
    }

    /**
     * Test collision to right from slope left 1 to slope left 0.
     * 
     * <pre>
     * <----
     * [ ][ ]
     * [/][/]
     * </pre>
     * 
     * @param speedX The horizontal speed used.
     */
    @ParameterizedTest
    @MethodSource("speedX")
    void testToLeftFromSlopeLeft1ToSlopeLeft0(double speedX)
    {
        final CollisionResult res = test(1, 0, TILE_SLOPE_LEFT_1, 0, 0, TILE_SLOPE_LEFT_0, 16.0, 8.0, -1, speedX);

        UtilAssert.assertEquals(Math.floor(8.5 - speedX / 2.0), res.getY());
    }

    /**
     * Test collision to right from slope left 0 to slope left 1.
     * 
     * <pre>
     * ---->
     * [ ][ ]
     * [/][/]
     * </pre>
     * 
     * @param speedX The horizontal speed used.
     */
    @ParameterizedTest
    @MethodSource("speedX")
    void testToRightFromSlopeLeft0ToSlopeLeft1(double speedX)
    {
        final CollisionResult res = test(0, 0, TILE_SLOPE_LEFT_0, 1, 0, TILE_SLOPE_LEFT_1, 15.5, 7.9, 1, speedX);

        UtilAssert.assertEquals(Math.floor(7.5 + speedX / 2), res.getY());
    }

    /**
     * Test collision to left from slope right 0 to slope right 1.
     * 
     * <pre>
     * <----
     * [\][ ]
     * [ ][\]
     * </pre>
     * 
     * @param speedX The horizontal speed used.
     */
    @ParameterizedTest
    @MethodSource("speedX")
    void testToLeftFromSlopeRight0ToSlopeRight1(double speedX)
    {
        final CollisionResult res = test(1, 0, TILE_SLOPE_RIGHT_0, 0, 1, TILE_SLOPE_RIGHT_1, 16.0, 15.9, -1, speedX);

        UtilAssert.assertEquals(Math.floor(16.0 + speedX / 2.0), res.getY());
    }

    /**
     * Test collision to right from slope right 0 to slope right 1.
     * 
     * <pre>
     * ---->
     * [\][ ]
     * [ ][\]
     * </pre>
     * 
     * @param speedX The horizontal speed used.
     */
    @ParameterizedTest
    @MethodSource("speedX")
    void testToRightFromSlopeRight1ToSlopeRight0(double speedX)
    {
        final CollisionResult res = test(0, 1, TILE_SLOPE_RIGHT_1, 1, 0, TILE_SLOPE_RIGHT_0, 15.5, 16.0, 1, speedX);

        UtilAssert.assertEquals(Math.round(16.5 - speedX / 2.0), res.getY());
    }
}
