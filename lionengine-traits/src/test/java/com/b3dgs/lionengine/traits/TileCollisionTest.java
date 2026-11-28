/*
 * Copyright (C) 2013-2026 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.traits;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import com.b3dgs.lionengine.ecs.GameSystem;
import com.b3dgs.lionengine.ecs.TraitStorage;
import com.b3dgs.lionengine.ecs.World;
import com.b3dgs.lionengine.physics.Box;
import com.b3dgs.lionengine.physics.BoxListQuery;
import com.b3dgs.lionengine.physics.BroadPhase;
import com.b3dgs.lionengine.physics.ShapeQuery;

/**
 * Tests of the tile push-out of {@link CollisionSystem} and of the grounded flag of {@link Jumpable}: floor, wall,
 * corner and ceiling, the seams of a floor, grounded set and cleared, triggers and objects without {@link Movable}
 * never pushed, excluded types, offsets, the result buffer, the shape query replaced, at steps of 1/30, 1/60 and 1/120
 * s with gravity and movement (REQ-STD-008, REQ-STD-010, REQ-STD-011, REQ-MAP-008, ADR-0030, ADR-0083).
 */
final class TileCollisionTest
{
    /** Gravity, in meters per second squared. */
    private static final double GRAVITY = -9.81;
    /** The step of the single-rate tests, in seconds. */
    private static final double STEP = 1.0 / 60.0;
    /** Size of the object boxes, in meters. */
    private static final double SIZE = 0.5;
    /** Half of {@link #SIZE}. */
    private static final double HALF = SIZE / 2.0;
    /** Top of the floor, in meters. */
    private static final double FLOOR = 1.0;

    /** The world. */
    private final World world = new World();
    /** The transformables. */
    private final TraitStorage<Transformable> transformables = world.register(Transformable.TYPE);
    /** The movables. */
    private final TraitStorage<Movable> movables = world.register(Movable.TYPE);
    /** The gravities. */
    private final TraitStorage<Gravity> gravities = world.register(Gravity.TYPE);
    /** The collidables. */
    private final TraitStorage<Collidable> collidables = world.register(Collidable.TYPE);
    /** The jumpables. */
    private final TraitStorage<Jumpable> jumpables = world.register(Jumpable.TYPE);
    /** The grid: a floor of 16 tiles on row 0, a wall at column 10 on rows 1 and 2, a ceiling at row 6. */
    private final TileGrid grid = new TileGrid(16, 8).fill(0, 0, 16, 1).fill(10, 1, 1, 2).fill(3, 6, 4, 1);

    /**
     * Adds the standard pipeline: capture, gravity, movement and collision with the tiles.
     *
     * @param tiles The shapes.
     * @return The collision system.
     */
    private CollisionSystem pipeline(ShapeQuery tiles)
    {
        world.addSystem(new TransformCaptureSystem(world, transformables), TransformCaptureSystem.SLOT);
        world.addSystem(new GravitySystem(world, movables, gravities), GravitySystem.SLOT);
        world.addSystem(new MovementSystem(world, transformables, movables), MovementSystem.SLOT);
        final CollisionSystem collision = new CollisionSystem(world,
                                                              transformables,
                                                              collidables,
                                                              movables,
                                                              jumpables,
                                                              tiles);
        world.addSystem(collision, CollisionSystem.SLOT);
        return collision;
    }

    /**
     * Creates a falling jumper of {@link #SIZE}, flushed.
     *
     * @param x The center X, in meters.
     * @param y The center Y, in meters.
     * @param velocityX The horizontal velocity, in meters per second.
     * @param velocityY The vertical velocity, in meters per second.
     * @return The object.
     */
    private long jumper(double x, double y, double velocityX, double velocityY)
    {
        final long id = world.create();
        final Transformable transformable = transformables.add(id);
        transformable.setPosition(x, y);
        transformable.setPreviousPosition(x, y);
        transformable.setSize(SIZE, SIZE);
        movables.add(id).setVelocity(velocityX, velocityY);
        gravities.add(id).setAcceleration(GRAVITY);
        collidables.add(id).setSize(SIZE, SIZE);
        jumpables.add(id);
        world.flush();
        return id;
    }

    /**
     * Runs steps.
     *
     * @param steps The steps.
     * @param stepSeconds The step, in seconds.
     */
    private void run(int steps, double stepSeconds)
    {
        for (int i = 0; i < steps; i++)
        {
            world.update(stepSeconds);
        }
    }

    /**
     * Returns the bottom of the box of an object.
     *
     * @param id The object.
     * @return The bottom, in meters.
     */
    private double bottom(long id)
    {
        return transformables.get(id).getY() + collidables.get(id).getOffsetY() - collidables.get(id).getHeight() * 0.5;
    }

    /**
     * A falling object lands on the floor and stays on it, grounded, its vertical velocity zeroed, its box touching the
     * floor exactly, at each step duration.
     *
     * @param updatesPerSecond The step rate.
     */
    @ParameterizedTest
    @ValueSource(ints =
    {
        30, 60, 120
    })
    void testFloor(int updatesPerSecond)
    {
        final double step = 1.0 / updatesPerSecond;
        final CollisionSystem collision = pipeline(grid);
        final long id = jumper(2.5, 3.0, 0.0, 0.0);

        world.update(step);
        assertFalse(jumpables.get(id).isGrounded());
        assertTrue(transformables.get(id).getY() < 3.0);

        run(updatesPerSecond * 2, step);

        assertEquals(FLOOR, bottom(id));
        assertEquals(FLOOR + HALF, transformables.get(id).getY());
        assertEquals(2.5, transformables.get(id).getX());
        assertEquals(0.0, movables.get(id).getVelocityY());
        assertTrue(jumpables.get(id).isGrounded());
        assertEquals(1, collision.getTileQuery().getCount());
        assertSame(grid, collision.getShapeQuery());

        run(updatesPerSecond, step);
        assertEquals(FLOOR + HALF, transformables.get(id).getY());
        assertTrue(jumpables.get(id).isGrounded());
    }

    /**
     * An object walking on a floor of several tiles crosses their seams without being stopped, grounded at each step;
     * it is stopped by the wall, its horizontal velocity zeroed, its box touching the wall, still grounded.
     *
     * @param updatesPerSecond The step rate.
     */
    @ParameterizedTest
    @ValueSource(ints =
    {
        30, 60, 120
    })
    void testWalkIntoWall(int updatesPerSecond)
    {
        final double step = 1.0 / updatesPerSecond;
        pipeline(grid);
        final long id = jumper(1.5, FLOOR + HALF, 2.0, 0.0);

        double x = transformables.get(id).getX();
        while (x + HALF < 10.0 - 2.0 * step)
        {
            world.update(step);
            assertEquals(x + 2.0 * step, transformables.get(id).getX(), 1e-9);
            assertEquals(2.0, movables.get(id).getVelocityX());
            assertEquals(FLOOR + HALF, transformables.get(id).getY());
            assertTrue(jumpables.get(id).isGrounded());
            x = transformables.get(id).getX();
        }
        run(updatesPerSecond, step);

        assertEquals(10.0, transformables.get(id).getX() + HALF);
        assertEquals(0.0, movables.get(id).getVelocityX());
        assertEquals(FLOOR + HALF, transformables.get(id).getY());
        assertTrue(jumpables.get(id).isGrounded());
    }

    /**
     * An object walking left is stopped by the right side of the wall.
     */
    @Test
    void testWallFromTheRight()
    {
        pipeline(grid);
        final long id = jumper(12.5, FLOOR + HALF, -3.0, 0.0);

        run(120, STEP);

        assertEquals(11.0, transformables.get(id).getX() - HALF);
        assertEquals(0.0, movables.get(id).getVelocityX());
        assertTrue(jumpables.get(id).isGrounded());
    }

    /**
     * An object moving up is stopped under the ceiling, its upward velocity zeroed, not grounded; it then falls back.
     *
     * @param updatesPerSecond The step rate.
     */
    @ParameterizedTest
    @ValueSource(ints =
    {
        30, 60, 120
    })
    void testCeiling(int updatesPerSecond)
    {
        final double step = 1.0 / updatesPerSecond;
        pipeline(grid);
        final long id = jumper(4.5, 5.0, 0.0, 6.0);
        gravities.get(id).setAcceleration(0.0);

        run(updatesPerSecond, step);

        assertEquals(6.0, transformables.get(id).getY() + HALF);
        assertEquals(0.0, movables.get(id).getVelocityY());
        assertFalse(jumpables.get(id).isGrounded());

        gravities.get(id).setAcceleration(GRAVITY);
        gravities.get(id).setTerminalVelocity(7.0);
        run(updatesPerSecond * 3, step);
        assertEquals(FLOOR, bottom(id));
        assertTrue(jumpables.get(id).isGrounded());
    }

    /**
     * Corners: an object falling while moving right above the top of the wall lands on it, keeping its horizontal
     * velocity; an object moving right into the side of the wall while falling is stopped on X and keeps falling.
     */
    @Test
    void testCorner()
    {
        pipeline(grid);
        final long above = jumper(10.0, 3.5, 1.0, -2.0);
        final long side = jumper(9.7, 2.0, 6.0, 0.0);

        world.update(STEP);
        assertEquals(10.0, transformables.get(side).getX() + HALF);
        assertEquals(0.0, movables.get(side).getVelocityX());
        assertTrue(transformables.get(side).getY() < 2.0);
        assertFalse(jumpables.get(side).isGrounded());

        run(60, STEP);

        assertEquals(3.0, bottom(above));
        assertTrue(jumpables.get(above).isGrounded());
        assertEquals(1.0, movables.get(above).getVelocityX());
        assertEquals(FLOOR, bottom(side));
        assertEquals(10.0, transformables.get(side).getX() + HALF);
        assertTrue(jumpables.get(side).isGrounded());
    }

    /**
     * Grounded is cleared when the object leaves the floor (walking off a ledge, or moved up by a jump) and set again
     * when it lands.
     */
    @Test
    void testGroundedCleared()
    {
        final TileGrid ledge = new TileGrid(16, 8).fill(0, 2, 4, 1).fill(0, 0, 16, 1);
        pipeline(ledge);
        final long id = jumper(3.0, 3.0 + HALF, 2.0, 0.0);
        world.update(STEP);
        assertTrue(jumpables.get(id).isGrounded());

        int steps = 0;
        while (transformables.get(id).getX() - HALF < 4.0)
        {
            world.update(STEP);
            steps++;
            assertTrue(steps < 60);
        }
        world.update(STEP);
        assertFalse(jumpables.get(id).isGrounded());

        run(60, STEP);
        assertEquals(FLOOR, bottom(id));
        assertTrue(jumpables.get(id).isGrounded());

        movables.get(id).setVelocity(0.0, 4.0);
        world.update(STEP);
        assertFalse(jumpables.get(id).isGrounded());
        assertTrue(bottom(id) > FLOOR);
    }

    /**
     * A trigger is never pushed out: it falls through the floor; its grounded flag is not written.
     */
    @Test
    void testTriggerNotPushed()
    {
        pipeline(grid);
        final long id = jumper(2.5, 1.5, 0.0, 0.0);
        collidables.get(id).setTrigger(true);
        jumpables.get(id).setGrounded(true);

        run(30, STEP);

        assertTrue(transformables.get(id).getY() < 0.5);
        assertTrue(jumpables.get(id).isGrounded());
    }

    /**
     * An object without {@link Movable} is not pushed out, and an excluded one neither (REQ-STD-011).
     */
    @Test
    void testNotPushed()
    {
        final TraitStorage<Object> proxies = world.register(new MarkerType());
        world.addSystem(new TransformCaptureSystem(world, transformables), TransformCaptureSystem.SLOT);
        world.addSystem(new GravitySystem(world, movables, gravities), GravitySystem.SLOT);
        world.addSystem(new MovementSystem(world, transformables, movables), MovementSystem.SLOT);
        final CollisionSystem collision = new CollisionSystem(world,
                                                              transformables,
                                                              collidables,
                                                              movables,
                                                              jumpables,
                                                              BroadPhase.DEFAULT_CELL_SIZE,
                                                              new TraitStorage<?>[]
                                                              {
                                                                  proxies
                                                              });
        collision.setShapeQuery(grid);
        world.addSystem(collision, CollisionSystem.SLOT);
        final long still = world.create();
        transformables.add(still).setPosition(2.5, 0.5);
        collidables.add(still).setSize(SIZE, SIZE);
        jumpables.add(still);
        final long proxy = jumper(4.5, 1.5, 0.0, 0.0);
        proxies.add(proxy);
        final long pushed = jumper(6.5, 1.5, 0.0, 0.0);

        run(30, STEP);

        assertEquals(0.5, transformables.get(still).getY());
        assertFalse(jumpables.get(still).isGrounded());
        assertTrue(transformables.get(proxy).getY() < 0.5);
        assertEquals(FLOOR, bottom(pushed));
        assertEquals(1, collision.getTileQuery().getCount());
    }

    /**
     * Without a shape query nothing is pushed and the grounded flags are not written; a query set later applies from
     * the next step, and can be removed again.
     */
    @Test
    void testShapeQueryReplaced()
    {
        final CollisionSystem collision = pipeline(null);
        final long id = jumper(2.5, FLOOR + HALF, 0.0, 0.0);
        jumpables.get(id).setGrounded(true);
        assertNull(collision.getShapeQuery());

        world.update(STEP);
        assertTrue(bottom(id) < FLOOR);
        assertTrue(jumpables.get(id).isGrounded());

        collision.setShapeQuery(grid);
        world.update(STEP);
        assertEquals(FLOOR, bottom(id));
        assertTrue(jumpables.get(id).isGrounded());

        collision.setShapeQuery(null);
        jumpables.get(id).setGrounded(false);
        world.update(STEP);
        assertTrue(bottom(id) < FLOOR);
        assertFalse(jumpables.get(id).isGrounded());
    }

    /**
     * A box offset from the position and an object without {@link Jumpable} are pushed out the same way.
     */
    @Test
    void testOffsetWithoutJumpable()
    {
        pipeline(grid);
        final long id = jumper(2.5, 3.0, 0.0, 0.0);
        collidables.get(id).setOffset(0.1, -0.3);
        jumpables.remove(id);
        world.flush();

        run(120, STEP);

        assertTrue(bottom(id) >= FLOOR);
        assertEquals(FLOOR, bottom(id), 1e-12);
        assertEquals(FLOOR + HALF + 0.3, transformables.get(id).getY(), 1e-12);
        assertEquals(0.0, movables.get(id).getVelocityY());
    }

    /**
     * A wide object overlapping more shapes than the buffer holds is pushed out of all of them (the buffer grows); the
     * tiles of a box list, with odd bounds, are left exactly touching.
     */
    @Test
    void testBufferGrowthAndExactContact()
    {
        final Box[] floor = new Box[12];
        for (int i = 0; i < floor.length; i++)
        {
            floor[i] = new Box(0.1 + i * 0.3, 0.35, 0.3, 0.7);
        }
        pipeline(new BoxListQuery(floor));
        final long id = jumper(1.8, 2.0, 0.0, 0.0);
        transformables.get(id).setSize(3.4, SIZE);
        collidables.get(id).setSize(3.4, SIZE);

        run(120, STEP);

        assertTrue(jumpables.get(id).isGrounded());
        assertTrue(bottom(id) >= 0.7);
        assertEquals(0.7, bottom(id), 1e-15);
        assertEquals(0.0, movables.get(id).getVelocityY());
    }

    /**
     * A velocity pointing away from the shape that stopped the object is kept.
     */
    @Test
    void testVelocityAwayKept()
    {
        pipeline(grid);
        final long id = jumper(9.5, FLOOR + HALF, 0.0, 0.0);
        gravities.get(id).setAcceleration(0.0);
        world.update(STEP);
        transformables.get(id).setPosition(9.9, FLOOR + HALF);
        movables.get(id).setVelocity(-1.0, 0.0);
        world.addSystem(new TeleportSystem(transformables, id), CollisionSystem.SLOT - 1);

        world.update(STEP);

        assertEquals(10.0, transformables.get(id).getX() + HALF);
        assertEquals(-1.0, movables.get(id).getVelocityX());
    }

    /**
     * The object detection sees the pushed positions: an object landing on the floor does not overlap a sensor lying
     * just under the floor top.
     */
    @Test
    void testPairsAfterPushOut()
    {
        final CollisionSystem collision = pipeline(grid);
        final long sensor = world.create();
        transformables.add(sensor).setPosition(2.5, 0.95);
        collidables.add(sensor).setSize(1.0, 0.1);
        final long id = jumper(2.5, 1.5, 0.0, 0.0);

        run(30, STEP);

        assertEquals(FLOOR, bottom(id));
        assertEquals(0, collision.getPairCount());
    }

    /**
     * A system without the tile storages has no tile query; invalid arguments are refused.
     */
    @Test
    void testInvalidArguments()
    {
        final CollisionSystem plain = new CollisionSystem(world, transformables, collidables);
        assertThrows(IllegalStateException.class, plain::getTileQuery);
        assertThrows(IllegalStateException.class, plain::getShapeQuery);
        assertThrows(IllegalStateException.class, () -> plain.setShapeQuery(grid));

        assertThrows(IllegalArgumentException.class,
                     () -> new CollisionSystem(world, transformables, collidables, null, jumpables, grid));
        assertThrows(IllegalArgumentException.class,
                     () -> new CollisionSystem(world, transformables, collidables, movables, null, grid));
        assertThrows(IllegalArgumentException.class,
                     () -> new CollisionSystem(world,
                                               transformables,
                                               collidables,
                                               movables,
                                               jumpables,
                                               BroadPhase.DEFAULT_CELL_SIZE,
                                               new TraitStorage<?>[]
                                               {
                                                   movables
                                               }));
        assertThrows(IllegalArgumentException.class,
                     () -> new CollisionSystem(world,
                                               transformables,
                                               collidables,
                                               movables,
                                               jumpables,
                                               BroadPhase.DEFAULT_CELL_SIZE,
                                               null));
    }

    /**
     * Moves an object before the collision step, after the movement, without its previous position: a game
     * teleporting it.
     */
    private static final class TeleportSystem implements GameSystem
    {
        /** The transformables. */
        private final TraitStorage<Transformable> transformables;
        /** The object. */
        private final long id;

        /**
         * Creates the system.
         *
         * @param transformables The transformables.
         * @param id The object.
         */
        TeleportSystem(TraitStorage<Transformable> transformables, long id)
        {
            this.transformables = transformables;
            this.id = id;
        }

        @Override
        public void update(double stepSeconds)
        {
            final Transformable transformable = transformables.get(id);
            transformable.setPosition(transformable.getPreviousX() + 0.4, transformable.getY());
        }
    }
}
